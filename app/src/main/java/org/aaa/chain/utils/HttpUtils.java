package org.aaa.chain.utils;

import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.aaa.chain.Constant;
import org.aaa.chain.entities.ResumeRequestEntity;
import org.aaa.chain.views.ProgressResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtils {

    private static String METHOD_POST = "post";
    private static String METHOD_GET = "get";

    public static String PRE_PAYED = "change_to_pre_payed";
    public static String DELIVERED = "change_to_delivered";
    public static String FINAL_PAYED = "change_to_final_payed";

    public static HttpUtils instance = new HttpUtils();

    public static HttpUtils getInstance() {
        return instance;
    }

    public void AddDataResource(ResumeRequestEntity entity, ServerCallBack callBack) {
        String url = "http://47.99.114.35:9527/dbnode/data/add?chain=aaa";
        JSONObject object = new JSONObject();
        try {
            object.put("source", "it is so beautiful, i love it");
            object.put("account", entity.getAccount());
            object.put("options", entity.getOptions());
            object.put("signature", entity.getSignature());
            object.put("metadata", entity.getMetadata());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(object.toString(), null, "post", url, callBack);
    }

    private void httpRequest(String content, Headers headers, String method, String url, ServerCallBack callBack) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (headers != null && headers.size() > 0) builder.headers(headers);
        if (METHOD_POST.equals(method)) {
            RequestBody body = RequestBody.create(mediaType, content);
            builder.post(body);
        } else {
            builder.get();
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                callBack.onFailure(call, e);
            }

            @Override public void onResponse(Call call, Response response) {
                callBack.onResponse(call, response);
            }
        });
    }

    public void modifyCustomInfo(String hashId, String signature, String modifyContent, ServerCallBack callBack) {
        String url = "http://47.99.114.35:9527/dbnode/" + hashId + "?chain=aaa";
        JSONObject object = new JSONObject();
        try {
            object.put("signature", signature);
            JSONObject object1 = new JSONObject(modifyContent);
            object.put("extra", object1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(object.toString(), null, METHOD_POST, url, callBack);
    }

    public void getBaseInfo(ServerCallBack callBack) {
        String url = "http://47.99.114.35:9527/dbnode/find?page=1&pageSize=1&order=-1";
        JSONObject object = new JSONObject();
        try {
            object.put("account", Constant.getAccount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(object.toString(), null, METHOD_POST, url, callBack);
    }

    public void searchResource(int page, String content, ServerCallBack callBack) {
        String url = "http://47.99.114.35:9527/dbnode/find?page=" + page + "&pageSize=20&sortBy=timestamp&order=-1";
        JSONObject object = new JSONObject();
        try {
            JSONObject object1 = new JSONObject();
            object1.put("$gte", 0);
            object.put("size", object1);
            JSONObject object2 = new JSONObject();
            object2.put("$ne", Constant.getAccount());
            object.put("account", object2);
            if (content != null) {
                JSONObject object3 = new JSONObject();
                object3.put("extra.company", content);
                object.put("stringMatch", object3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(object.toString(), null, METHOD_POST, url, callBack);
    }

    public void addFileResource(ProgressResponseBody.ProgressListener progressListener, ResumeRequestEntity entity, ServerCallBack callBack) {
        if (TextUtils.isEmpty(entity.getFilepath())) {
            AddDataResource(entity, callBack);
            return;
        }
        String url = "http://47.99.114.35:9527/dbnode/file/add?chain=aaa";
        File file = new File(entity.getFilepath());
        MediaType type = MediaType.parse("text/x-markdown;charset=utf-8");
        RequestBody fileBody = RequestBody.create(type, file);

        RequestBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("account", entity.getAccount())
                .addFormDataPart("metadata", entity.getMetadata())
                .addFormDataPart("signature", entity.getSignature())
                .addFormDataPart("options", entity.getOptions())
                .addFormDataPart("file", entity.getFilename(), fileBody)
                .build();

        Request request = new Request.Builder().url(url).post(multipartBody).build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(), progressListener)).build();
            }
        }).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                callBack.onFailure(call, e);
            }

            @Override public void onResponse(Call call, Response response) {
                callBack.onResponse(call, response);
            }
        });
    }

    public interface ServerCallBack {
        void onFailure(Call call, IOException e);

        void onResponse(Call call, Response response);
    }

    public void getMyResource(String signature, String message, ServerCallBack callBack) {
        String url = "http://47.99.114.35:9527/dbnode/mine?chain=aaa&page=1&pageSize=2&sortBy=timestamp";
        Headers headers = new Headers.Builder().add("signature", signature).add("message", message).build();
        httpRequest(null, headers, METHOD_GET, url, callBack);
    }

    public void getAppointResource(String hashId, ServerCallBack callBack) {
        String url = "http://47.99.114.35:9527/dbnode/" + hashId;
        httpRequest(null, null, METHOD_GET, url, callBack);
    }

    public void downlaodFile(String hashId, ServerCallBack callBack) {
        String url = "http://47.99.114.35:8089/ipfs/" + hashId;
        httpRequest(null, null, METHOD_GET, url, callBack);
    }

    public void createOrder(String body, ServerCallBack callBack) {
        String url = "http://47.98.107.96:8080/aaademo-0.9/restapi/order/create";
        httpRequest(body, null, METHOD_POST, url, callBack);
    }

    public void getOrderList(ServerCallBack callBack) {
        String url = "http://47.98.107.96:8080/aaademo-0.9/restapi/order/get";
        httpRequest(null, null, METHOD_GET, url, callBack);
    }

    public void updateOrderStatus(long orderId, String message, String option, ServerCallBack callBack) {
        String url = "http://47.98.107.96:8080/aaademo-0.9/restapi/order/" + option;
        JSONObject object = new JSONObject();
        try {

            if (option.equals(DELIVERED)) {
                object.put("deliverMsg", message);
            } else if (option.equals(PRE_PAYED)) {
                object.put("payMsg", message);
            } else if (option.equals(FINAL_PAYED)) {
            }
            object.put("orderId", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(object.toString(), null, METHOD_POST, url, callBack);
    }
}

package org.aaa.chain.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aaa.chain.ChainApplication;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.entities.ExtraEntity;
import org.aaa.chain.entities.OrderDataEntity;
import org.aaa.chain.entities.ResumeRequestEntity;
import org.aaa.chain.entities.ResumeResponseEntity;
import org.aaa.chain.entities.SearchResponseEntity;
import org.aaa.chain.utils.FileUtils;
import org.aaa.chain.utils.HttpUtils;
import org.aaa.chain.utils.PBEUtils;
import org.aaa.chain.views.ProgressResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResumeDetailsActivity extends BaseActivity {

    Button buy;
    int type = 0;
    Button authorization;
    private String sellerAccount;
    private String goodId;
    private long orderId;
    private String hashId;
    private String price;
    private int status;
    private String buyer;
    private String buyerPublicKey;
    private ResumeResponseEntity resumeResponseEntity;
    private ProgressDialog dialog;
    private String pMessage;
    private File finalDefile;

    @Override public int initLayout() {
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt("type", 0);
        int layoutId = 0;
        if (type == 0) {
            layoutId = R.layout.activity_resume_details;
            goodId = getIntent().getStringExtra("hashId");
            price = getIntent().getStringExtra("price");
            sellerAccount = bundle.getString("seller");
            pMessage = getResources().getString(R.string.order_generation);
        } else if (type == 1) {
            pMessage = getResources().getString(R.string.order_authorization);
            layoutId = R.layout.activity_postion_info;
            OrderDataEntity entity = getIntent().getParcelableExtra("dataEntity");
            orderId = entity.getId();
            if (entity.getDeliverMsg() == null) {
                hashId = entity.getGoodId();
            } else {
                hashId = entity.getDeliverMsg();
            }
            status = entity.getStatus();
            buyer = entity.getBuyer();
            sellerAccount = entity.getSeller();
            buyerPublicKey = entity.getExt1();
        } else {
            layoutId = R.layout.activity_postion_info;
        }
        return layoutId;
    }

    @Override public void getViewById() {
        if (type == 0) {
            buy = $(R.id.btn_resume_buy);
            if (price == null) {
                price = "1";
                buy.setText(String.format(getResources().getString(R.string.buy_a_resume), "1"));
            } else {
                buy.setText(String.format(getResources().getString(R.string.buy_a_resume), price));
            }
            TextView tvName = $(R.id.tv_resume_name);
            tvName.setText(getIntent().getStringExtra("name"));
            buy.setOnClickListener(this);
        } else if (type == 1) {
            ProgressDialog dialog = ProgressDialog.show(ResumeDetailsActivity.this, getResources().getString(R.string.waiting),
                    getResources().getString(R.string.loading));
            authorization = $(R.id.btn_authorization);
            authorization.setOnClickListener(this);
            ((TextView) $(R.id.tv_position_name)).setText(String.format(getResources().getString(R.string.java_engineer), "java"));
            if (Constant.getCurrentAccount().equals(buyer)) {
                if (status == 0 || status == 1) {
                    authorization.setClickable(false);
                    authorization.setText(getResources().getString(R.string.waiting_seller_authorization));
                    authorization.setBackgroundColor(Color.GRAY);
                } else if (status == 2) {
                    authorization.setClickable(true);
                    authorization.setText(getResources().getString(R.string.waiting_receive));
                    authorization.setBackgroundColor(getResources().getColor(R.color.popup_item_text_color));
                } else if (status == 3) {
                    authorization.setClickable(true);
                    authorization.setText(getResources().getString(R.string.resume_received));
                    authorization.setBackgroundColor(getResources().getColor(R.color.popup_item_text_color));
                }
            } else {
                if (status == 0) {
                    authorization.setClickable(false);
                    authorization.setText(getResources().getString(R.string.waiting_buyer_authorization));
                    authorization.setBackgroundColor(Color.GRAY);
                } else if (status == 1) {
                    authorization.setClickable(true);
                    authorization.setText(getResources().getString(R.string.waiting_buyer_authorization));
                    authorization.setBackgroundColor(getResources().getColor(R.color.popup_item_text_color));
                } else if (status == 2) {
                    authorization.setClickable(false);
                    authorization.setText(getResources().getString(R.string.authorization_successful));
                    authorization.setBackgroundColor(Color.GRAY);
                } else {
                    authorization.setClickable(false);
                    authorization.setText(getResources().getString(R.string.transaction_done));
                    authorization.setBackgroundColor(Color.GRAY);
                }
            }
            HttpUtils.getInstance().getAppointResource(hashId, new HttpUtils.ServerCallBack() {
                @Override public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            dialog.dismiss();
                        }
                    });
                }

                @Override public void onResponse(Call call, Response response) {
                    try {
                        ResponseBody body = response.body();
                        if (response.code() == 200) {

                            resumeResponseEntity = new Gson().fromJson(body.string(), ResumeResponseEntity.class);
                        } else {
                            Log.i("info", "getAppointResource error:" + body.string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            });
        } else {
            $(R.id.btn_authorization).setVisibility(View.GONE);
            SearchResponseEntity entity = ChainApplication.getInstance().getBaseInfo();
            TextView name = $(R.id.tv_position_name);
            name.setText(String.format(getResources().getString(R.string.java_engineer), entity.getDocs().get(0).getExtra().getJobType()));
        }
    }

    private String getPrice(String price) {
        String newPrice;
        if (price.contains("AAA")) {
            newPrice = price.substring(0, 1);
        } else {
            newPrice = price;
        }
        DecimalFormat decimalFormat = new DecimalFormat("##0.0000");
        String dd = decimalFormat.format(Float.valueOf(newPrice));
        return dd + " AAA";
    }

    @Override public void onClick(View v) {
        dialog = new ProgressDialog(ResumeDetailsActivity.this);
        switch (v.getId()) {
            case R.id.btn_resume_buy:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("buyer", Constant.getCurrentAccount());
                    jsonObject.put("seller", sellerAccount);
                    jsonObject.put("goodId", goodId);
                    jsonObject.put("price", price);
                    jsonObject.put("ext1", Constant.getCurrentPublicKey());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.setTitle(getResources().getString(R.string.waiting));
                dialog.setMessage(pMessage);
                dialog.show();
                //1.create order
                JSInteraction.getInstance().getBalance(Constant.getCurrentAccount(), new JSInteraction.JSCallBack() {
                    @Override public void onSuccess(String... stringArray) {
                        if (stringArray[0].equals("undefined")) {
                            String balance = stringArray[0].substring(0, stringArray[0].lastIndexOf(" "));
                            String currentPrice = price.substring(0, price.lastIndexOf(" "));
                            Double value = Double.valueOf(balance) - Double.valueOf(currentPrice);
                            if (value < 0) {
                                dialog.dismiss();
                                Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.not_sufficient_funds),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                createOrder(jsonObject.toString());
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.not_sufficient_funds), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override public void onProgress() {

                    }

                    @Override public void onError(String error) {

                    }
                });
                break;

            case R.id.btn_authorization:
                if ((status == 2 || status == 3) && Constant.getCurrentAccount().equals(buyer)) {
                    File[] files = getExternalFilesDir("").listFiles();
                    if (status == 3 && files != null && files.length > 0) {
                        for (File file : files) {
                            if (file.getName()
                                    .substring(0, file.getName().indexOf("encrypt"))
                                    .equals(resumeResponseEntity.getExtra()
                                            .getName()
                                            .substring(0, resumeResponseEntity.getExtra().getName().indexOf("encrypt")))) {
                                finalDefile = file;
                                openFileDialog();
                            }
                        }
                    } else {
                        dialog.setTitle(getResources().getString(R.string.waiting));
                        downloadFile(status, resumeResponseEntity.getExtra().getHashId());
                        dialog.setMessage(getResources().getString(R.string.waiting_receive));
                        dialog.show();
                    }
                } else {

                    downloadFile(0, hashId);
                    dialog.setMessage(pMessage);
                    dialog.show();
                }

                break;
        }
    }

    private void createOrder(String content) {
        HttpUtils.getInstance().createOrder(content, new HttpUtils.ServerCallBack() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.create_order_failure), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override public void onResponse(Call call, Response response) {
                try {
                    String body = response.body().string();
                    if (response.code() == 200) {
                        JSONObject jsonObject = new JSONObject(body);
                        long data = jsonObject.getLong("data");
                        //2.预支付订单
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                dialog.setTitle(getResources().getString(R.string.waiting));
                                dialog.setMessage(pMessage);
                                dialog.show();
                                prepayOrder(data);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.order_failure) + body,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void prepayOrder(long orderId) {
        JSInteraction.getInstance().prepay(orderId, Constant.getCurrentAccount(), sellerAccount, getPrice(price), new JSInteraction.JSCallBack() {
            @Override public void onSuccess(String... stringArray) {

                //3.修改订单
                updateCreateOrder(orderId, "prepay");
            }

            @Override public void onProgress() {

            }

            @Override public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        dialog.dismiss();
                        Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.order_failure), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void downloadFile(int status, String hashId) {
        //1.download file
        HttpUtils.getInstance().downlaodFile(hashId, new HttpUtils.ServerCallBack() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        dialog.dismiss();
                        if (status == 0) {
                            Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.authorization_failure), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.download_failure), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }

            @Override public void onResponse(Call call, Response response) {
                try {
                    ResponseBody body = response.body();
                    if (response.code() == 200) {
                        byte[] bytes = body.bytes();
                        File file = FileUtils.getInstance()
                                .getFile(bytes, getExternalCacheDir().getAbsolutePath(), resumeResponseEntity.getExtra().getName());

                        if (status == 0) {

                            File defile = PBEUtils.getInstance()
                                    .decryptFile(ResumeDetailsActivity.this, file.getAbsolutePath(), Constant.getCurrentPrivateKey(),
                                            Constant.getCurrentPublicKey().getBytes());

                            byte[] salt = new SecureRandom().generateSeed(8);

                            //使用原始秘钥对文件加密
                            File enfile =
                                    PBEUtils.getInstance().encryptFile(ResumeDetailsActivity.this, defile.getAbsolutePath(), buyerPublicKey, salt);

                            ResumeRequestEntity requestEntity = new ResumeRequestEntity();
                            requestEntity.setFilepath(enfile.getAbsolutePath());
                            requestEntity.setFilename(enfile.getName());

                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    dialog.setMessage(getResources().getString(R.string.order_updating));
                                    //对原始秘钥进行加密
                                    JSInteraction.getInstance()
                                            .encryptKey(Constant.getCurrentPrivateKey(), buyerPublicKey, Base64.encodeToString(salt, Base64.DEFAULT),
                                                    new JSInteraction.JSCallBack() {
                                                        @Override public void onSuccess(String... stringArray) {
                                                            try {
                                                                JSONObject object =
                                                                        new JSONObject(new Gson().toJson(resumeResponseEntity.getExtra()));
                                                                JSONObject jsonObject = new JSONObject(stringArray[0]);
                                                                JSONObject jsonObject1 = jsonObject.getJSONObject("nonce");

                                                                object.put("low", jsonObject1.getLong("low"));
                                                                object.put("high", jsonObject1.getLong("high"));
                                                                object.put("unsigned", jsonObject1.getBoolean("unsigned"));

                                                                JSONObject jsonObject2 = jsonObject.getJSONObject("message");
                                                                object.put("type", jsonObject2.getString("type"));
                                                                object.put("data", jsonObject2.get("data"));

                                                                object.put("checksum", jsonObject.getLong("checksum"));
                                                                object.put("name", enfile.getName());
                                                                object.put("sellerPublicKey", Constant.getCurrentPublicKey());

                                                                requestEntity.setAccount(resumeResponseEntity.getAccount());
                                                                JSONObject jsonObject3 = new JSONObject();
                                                                jsonObject3.put("onlyHash", false);
                                                                requestEntity.setOptions(jsonObject3.toString());
                                                                requestEntity.setMetadata(object.toString());
                                                                runOnUiThread(new Runnable() {
                                                                    @Override public void run() {
                                                                        try {
                                                                            JSInteraction.getInstance()
                                                                                    .getSignature(
                                                                                            new JSONObject(requestEntity.getMetadata()).toString(),
                                                                                            Constant.getCurrentPrivateKey(),
                                                                                            new JSInteraction.JSCallBack() {
                                                                                                @Override
                                                                                                public void onSuccess(String... stringArray) {
                                                                                                    requestEntity.setSignature(stringArray[0]);
                                                                                                    //2.upload file
                                                                                                    addEncryptFile(requestEntity);
                                                                                                }

                                                                                                @Override public void onProgress() {

                                                                                                }

                                                                                                @Override public void onError(String error) {

                                                                                                }
                                                                                            });
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                });
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override public void onProgress() {

                                                        }

                                                        @Override public void onError(String error) {

                                                        }
                                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    decryptFile(file);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void decryptFile(File file) {
        try {
            ExtraEntity extraEntity = resumeResponseEntity.getExtra();
            JSONObject object = new JSONObject();
            JSONObject object1 = new JSONObject();
            object1.put("low", extraEntity.getLow());
            object1.put("high", extraEntity.getHigh());
            object1.put("unsigned", extraEntity.isUnsigned());
            object.put("nonce", object1);

            JSONObject object2 = new JSONObject();
            object2.put("type", extraEntity.getType());
            String[] strings = extraEntity.getData().split(",");
            int[] arr = new int[strings.length];
            for (int i = 0; i < strings.length; i++) {
                arr[i] = Integer.parseInt(strings[i]);
            }
            JSONArray jsonArray = new JSONArray(arr);
            object2.put("data", jsonArray);
            object.put("message", object2);
            object.put("checksum", extraEntity.getChecksum());

            JSInteraction.getInstance()
                    .decryptKey(Constant.getCurrentPrivateKey(), resumeResponseEntity.getExtra().getSellerPublicKey(), object.toString(),
                            new JSInteraction.JSCallBack() {
                                @Override public void onSuccess(String... stringArray) {
                                    finalDefile = PBEUtils.getInstance()
                                            .decryptFile(ResumeDetailsActivity.this, file.getAbsolutePath(), Constant.getCurrentPublicKey(),
                                                    Base64.decode(stringArray[0], Base64.DEFAULT));
                                    if (status == 2) {
                                        runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                //确认付款
                                                JSInteraction.getInstance()
                                                        .confirmOrder(Constant.getCurrentAccount(), orderId, new JSInteraction.JSCallBack() {
                                                            @Override public void onSuccess(String... stringArray) {
                                                                //update order
                                                                updateCreateOrder(orderId, "final");
                                                            }

                                                            @Override public void onProgress() {

                                                            }

                                                            @Override public void onError(String error) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        });
                                    } else if (status == 3) {
                                        dialog.dismiss();

                                        openFileDialog();
                                    }
                                }

                                @Override public void onProgress() {

                                }

                                @Override public void onError(String error) {

                                }
                            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResumeDetailsActivity.this);
        builder.setTitle("open the file");
        builder.setMessage(finalDefile.getAbsolutePath());
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    String type = getMIMEType(finalDefile);
                    intent.setDataAndType(Uri.parse(finalDefile.getAbsolutePath()), type);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ResumeDetailsActivity.this, "can not open file", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.show();
    }

    private void addEncryptFile(ResumeRequestEntity requestEntity) {
        HttpUtils.getInstance().addFileResource(new ProgressResponseBody.ProgressListener() {
            @Override public void update(long bytesRead, long contentLength, boolean done) {

            }
        }, requestEntity, new HttpUtils.ServerCallBack() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        dialog.dismiss();
                        Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.authorization_failure), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override public void onResponse(Call call, Response response) {

                ResponseBody responseBody = response.body();
                try {
                    String json = responseBody.string();
                    if (response.code() == 200) {
                        ResumeResponseEntity entity = new Gson().fromJson(json, ResumeResponseEntity.class);
                        ExtraEntity extraEntity = entity.getExtra();
                        extraEntity.setHashId(entity.getHashId());
                        String jsonObject = new Gson().toJson(extraEntity);
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                dialog.setMessage(getResources().getString(R.string.order_sent));

                                //modify hashid
                                modifyInfo(resumeResponseEntity.getHashId(), jsonObject);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                dialog.dismiss();
                                Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.authorization_failure),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void modifyInfo(String hashId, String modifyContent) {
        JSInteraction.getInstance().getSignature(modifyContent, org.aaa.chain.Constant.getCurrentPrivateKey(), new JSInteraction.JSCallBack() {
            @Override public void onSuccess(String... stringArray) {
                HttpUtils.getInstance().modifyCustomInfo(hashId, stringArray[0], modifyContent, new HttpUtils.ServerCallBack() {
                    @Override public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                dialog.dismiss();
                                Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.authorization_failure),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override public void onResponse(Call call, Response response) {

                        ResponseBody body = response.body();
                        try {
                            String json = body.string();
                            if (response.code() == 200) {
                                //4.update order
                                updateCreateOrder(orderId, hashId);
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        dialog.dismiss();
                                        Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.authorization_failure),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override public void onProgress() {

            }

            @Override public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        dialog.dismiss();
                        Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.modify_failure), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateCreateOrder(long orderId, String type) {
        String option;
        if (type.equals("prepay")) {
            option = HttpUtils.PRE_PAYED;
        } else if (type.equals("final")) {
            option = HttpUtils.FINAL_PAYED;
        } else {
            option = HttpUtils.DELIVERED;
        }
        HttpUtils.getInstance().updateOrderStatus(orderId, type, option, new HttpUtils.ServerCallBack() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(ResumeDetailsActivity.this, "failure", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }

            @Override public void onResponse(Call call, Response response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            boolean isSuccess;
                            String message;
                            try {
                                isSuccess = jsonObject.getBoolean("success");
                                message = jsonObject.getString("message");
                                if (isSuccess) {
                                    //买家预支付
                                    if (type.equals("prepay")) {
                                        buy.setText(getResources().getString(R.string.already_ordered));
                                        buy.setEnabled(false);
                                        buy.setClickable(false);
                                        buy.setBackgroundColor(Color.GRAY);
                                        Toast.makeText(ResumeDetailsActivity.this,
                                                getResources().getString(R.string.waiting_transaction_done) + response, Toast.LENGTH_SHORT).show();
                                    } else if (type.equals("final")) {//买家成功接收文件
                                        //authorization.setClickable(false);
                                        //authorization.setText(getResources().getString(R.string.resume_received));
                                        //authorization.setBackgroundColor(Color.GRAY);
                                        openFileDialog();
                                    } else {//卖家授权发货
                                        authorization.setClickable(false);
                                        authorization.setText(getResources().getString(R.string.authorization_successful));
                                        authorization.setBackgroundColor(Color.GRAY);
                                        Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.authorization_successful),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (type.equals("received")) {
                                        Toast.makeText(ResumeDetailsActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                    } else if (type.equals("delivered")) {
                                        Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.authorization_failure),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.order_failure) + message,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            dialog.dismiss();
                        }
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        JSInteraction.getInstance().removeListener();
    }

    private String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) type = MIME_MapTable[i][1];
        }
        return type;
    }

    private final String[][] MIME_MapTable = {
            { ".bmp", "image/bmp" }, { ".doc", "application/msword" },
            { ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }, { ".gif", "image/gif" }, { ".htm", "text/html" },
            { ".html", "text/html" }, { ".jpeg", "image/jpeg" }, { ".jpg", "image/jpeg" }, { ".pdf", "application/pdf" }, { ".png", "image/png" },
            { ".ppt", "application/vnd.ms-powerpoint" }, { ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" },
            { ".txt", "text/plain" }, { "", "*/*" }
    };
}

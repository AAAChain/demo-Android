package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aaa.chain.Constant;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.db.DBManager;
import org.aaa.chain.entities.ResumeRequestEntity;
import org.aaa.chain.entities.ResumeResponseEntity;
import org.aaa.chain.utils.FileUtils;
import org.aaa.chain.utils.HttpUtils;
import org.aaa.chain.utils.PBEUtils;
import org.aaa.chain.views.ProgressResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

public class ResumeDetailsActivity extends BaseActivity {

    Button buy;
    int type = 0;
    Button authorization;
    private String goodId;
    private long orderId;
    private String hashId;
    private String price;
    private int status;
    private String buyer;
    private ResumeResponseEntity resumeResponseEntity;

    @Override public int initLayout() {
        type = getIntent().getIntExtra("type", 0);
        int layoutId;
        if (type == 0) {
            layoutId = R.layout.activity_resume_details;
            goodId = getIntent().getStringExtra("hashId");
            price = getIntent().getStringExtra("price");
        } else {
            layoutId = R.layout.activity_postion_info;
            orderId = getIntent().getLongExtra("id", -1);
            hashId = getIntent().getStringExtra("goodId");
            status = getIntent().getIntExtra("status", -1);
            buyer = getIntent().getStringExtra("buyer");
        }
        return layoutId;
    }

    @Override public void getViewById() {
        if (type == 0) {
            buy = $(R.id.btn_resume_buy);
            if (price == null) {
                buy.setText(String.format(getResources().getString(R.string.buy_a_resume), "1"));
            } else {
                buy.setText(String.format(getResources().getString(R.string.buy_a_resume), price));
            }
            buy.setOnClickListener(this);
        } else {
            ProgressDialog dialog = ProgressDialog.show(ResumeDetailsActivity.this, "waiting...", "loading...");
            authorization = $(R.id.btn_authorization);
            authorization.setOnClickListener(this);
            if (Constant.getAccount().equals(buyer)) {
                if (status == 0 || status == 1) {
                    authorization.setClickable(false);
                    authorization.setText(getResources().getString(R.string.waiting_seller_authorization));
                    authorization.setBackgroundColor(Color.GRAY);
                } else if (status == 2) {
                    authorization.setClickable(true);
                    authorization.setText(getResources().getString(R.string.waiting_receive));
                    authorization.setBackgroundColor(getResources().getColor(R.color.popup_item_text_color));
                } else if (status == 3) {
                    authorization.setClickable(false);
                    authorization.setText(getResources().getString(R.string.transaction_done));
                    authorization.setBackgroundColor(Color.GRAY);
                }
            } else {
                if (status == 0 || status == 1) {
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
            new Thread(new Runnable() {
                @Override public void run() {
                    HttpUtils.getInstance().getAppointResource(hashId, new HttpUtils.ServerCallBack() {
                        @Override public void onFailure(Call call, IOException e) {

                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    dialog.dismiss();
                                }
                            });
                        }

                        @Override public void onResponse(Call call, Response response) {

                            if (response.code() == 200) {
                                ResponseBody body = response.body();
                                try {
                                    resumeResponseEntity = new Gson().fromJson(body.string(), ResumeResponseEntity.class);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }).start();
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
        return dd + " EOS";
    }

    long data = 0;

    @Override public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_resume_buy:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("buyer", Constant.getAccount());
                    jsonObject.put("seller", Constant.getAnotherAccount());
                    jsonObject.put("goodId", goodId);
                    jsonObject.put("price", price);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //1.创建订单
                HttpUtils.getInstance().createOrder(jsonObject.toString(), new HttpUtils.ServerCallBack() {
                    @Override public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                Toast.makeText(ResumeDetailsActivity.this, "onfailure", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override public void onResponse(Call call, Response response) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                if (response.code() == 200) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        data = jsonObject.getLong("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //2.支付订单
                                    ProgressDialog dialog = ProgressDialog.show(ResumeDetailsActivity.this, "waiting...",
                                            getResources().getString(R.string.order_generation));
                                    JSInteraction.getInstance()
                                            .prepay(data, Constant.getAccount(), Constant.getAnotherAccount(), getPrice(price),
                                                    new JSInteraction.JSCallBack() {
                                                        @Override public void onSuccess(String content) {

                                                            //3.修改订单
                                                            HttpUtils.getInstance()
                                                                    .updateOrderStatus(data, "received", HttpUtils.PRE_PAYED,
                                                                            new HttpUtils.ServerCallBack() {
                                                                                @Override public void onFailure(Call call, IOException e) {
                                                                                    dialog.dismiss();
                                                                                }

                                                                                @Override public void onResponse(Call call, Response response) {
                                                                                    try {
                                                                                        JSONObject jsonObject =
                                                                                                new JSONObject(response.body().string());
                                                                                        runOnUiThread(new Runnable() {
                                                                                            @Override public void run() {
                                                                                                boolean isSuccess;
                                                                                                String message;
                                                                                                try {
                                                                                                    isSuccess = jsonObject.getBoolean("success");
                                                                                                    message = jsonObject.getString("message");
                                                                                                    if (isSuccess) {
                                                                                                        buy.setText(getResources().getString(
                                                                                                                R.string.already_ordered));
                                                                                                        buy.setEnabled(false);
                                                                                                        buy.setClickable(false);
                                                                                                        Toast.makeText(ResumeDetailsActivity.this,
                                                                                                                getResources().getString(
                                                                                                                        R.string.waiting_transaction_done)
                                                                                                                        + response.code(),
                                                                                                                Toast.LENGTH_SHORT).show();
                                                                                                    } else {
                                                                                                        Toast.makeText(ResumeDetailsActivity.this,
                                                                                                                getResources().getString(
                                                                                                                        R.string.order_failure)
                                                                                                                        + message, Toast.LENGTH_SHORT)
                                                                                                                .show();
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

                                                        @Override public void onProgress() {

                                                        }

                                                        @Override public void onError(String error) {
                                                            dialog.dismiss();
                                                            Toast.makeText(ResumeDetailsActivity.this,
                                                                    getResources().getString(R.string.order_failure), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                } else {

                                    Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.order_failure) + response.code(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                break;

            case R.id.btn_authorization:
                ProgressDialog dialog =
                        ProgressDialog.show(ResumeDetailsActivity.this, "waiting...", getResources().getString(R.string.order_authorization));
                //1.下载文件
                HttpUtils.getInstance().downlaodFile(hashId, new HttpUtils.ServerCallBack() {
                    @Override public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                dialog.dismiss();
                                Toast.makeText(ResumeDetailsActivity.this, getResources().getString(R.string.download_failure) + e.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override public void onResponse(Call call, Response response) {
                        if (response.code() == 200) {
                            ResponseBody body = response.body();
                            try {

                                File file = FileUtils.getInstance()
                                        .getFile(body.bytes(), getExternalCacheDir().getAbsolutePath(), resumeResponseEntity.getExtra().getName());

                                File defile = PBEUtils.getInstance()
                                        .decryptFile(ResumeDetailsActivity.this, file.getAbsolutePath(), Constant.getPrivateKey(),
                                                Constant.getPublicKey());
                                File enfile = PBEUtils.getInstance()
                                        .encryptFile(ResumeDetailsActivity.this, defile.getAbsolutePath(), Constant.getPrivateKey(),
                                                Constant.getAnotherPublicKey());
                                ResumeRequestEntity requestEntity = DBManager.getInstance().getResumeRequest();
                                requestEntity.setFilepath(enfile.getAbsolutePath());
                                requestEntity.setFilename(enfile.getName());

                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        dialog.setMessage("order updating...");
                                    }
                                });
                                //2.更新订单

                                JSONObject object = new JSONObject(requestEntity.getMetadata());
                                object.put("test", new Random().nextInt(100));
                                requestEntity.setMetadata(object.toString());
                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        JSInteraction.getInstance()
                                                .getSignature(object.toString(), Constant.getPrivateKey(), new JSInteraction.JSCallBack() {
                                                    @Override public void onSuccess(String content) {

                                                        requestEntity.setSignature(content);
                                                        HttpUtils.getInstance().addFileResource(new ProgressResponseBody.ProgressListener() {
                                                            @Override public void update(long bytesRead, long contentLength, boolean done) {

                                                            }
                                                        }, requestEntity, new HttpUtils.ServerCallBack() {
                                                            @Override public void onFailure(Call call, IOException e) {
                                                                runOnUiThread(new Runnable() {
                                                                    @Override public void run() {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(ResumeDetailsActivity.this,
                                                                                getResources().getString(R.string.authorization_failure),
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }

                                                            @Override public void onResponse(Call call, Response response) {

                                                                ResponseBody responseBody = response.body();
                                                                if (response.code() == 200) {
                                                                    try {
                                                                        ResumeResponseEntity responseEntity =
                                                                                new Gson().fromJson(responseBody.string(),
                                                                                        ResumeResponseEntity.class);

                                                                        runOnUiThread(new Runnable() {
                                                                            @Override public void run() {
                                                                                dialog.setMessage("order sent");
                                                                            }
                                                                        });
                                                                        //3.授权订单，发货
                                                                        HttpUtils.getInstance()
                                                                                .updateOrderStatus(orderId, "sent", HttpUtils.DELIVERED,
                                                                                        new HttpUtils.ServerCallBack() {
                                                                                            @Override
                                                                                            public void onFailure(Call call, IOException e) {
                                                                                                runOnUiThread(new Runnable() {
                                                                                                    @Override public void run() {
                                                                                                        dialog.dismiss();
                                                                                                        Toast.makeText(ResumeDetailsActivity.this,
                                                                                                                getResources().getString(
                                                                                                                        R.string.authorization_failure),
                                                                                                                Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });
                                                                                            }

                                                                                            @Override
                                                                                            public void onResponse(Call call, Response response) {
                                                                                                if (response.code() == 200) {
                                                                                                    DBManager.getInstance()
                                                                                                            .updateResumeResponse(responseEntity);
                                                                                                    runOnUiThread(new Runnable() {
                                                                                                        @Override public void run() {
                                                                                                            dialog.dismiss();
                                                                                                            Toast.makeText(ResumeDetailsActivity.this,
                                                                                                                    getResources().getString(
                                                                                                                            R.string.authorization_successful),
                                                                                                                    Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                                } else {
                                                                                                    runOnUiThread(new Runnable() {
                                                                                                        @Override public void run() {
                                                                                                            dialog.dismiss();
                                                                                                            Toast.makeText(ResumeDetailsActivity.this,
                                                                                                                    getResources().getString(
                                                                                                                            R.string.authorization_failure),
                                                                                                                    Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        });
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                } else {
                                                                    try {
                                                                        Log.i("info", "updating 500:" + responseBody.string());
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override public void run() {
                                                                            dialog.dismiss();
                                                                            Toast.makeText(ResumeDetailsActivity.this,
                                                                                    getResources().getString(R.string.authorization_failure),
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override public void onProgress() {

                                                    }

                                                    @Override public void onError(String error) {

                                                    }
                                                });
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        JSInteraction.getInstance().removeListener();
    }
}

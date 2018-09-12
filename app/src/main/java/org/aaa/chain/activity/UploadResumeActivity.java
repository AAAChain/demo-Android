package org.aaa.chain.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import java.io.File;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aaa.chain.JSInteraction;
import org.aaa.chain.R;
import org.aaa.chain.db.DBManager;
import org.aaa.chain.entities.ResumeRequestEntity;
import org.aaa.chain.entities.ResumeResponseEntity;
import org.aaa.chain.utils.HttpUtils;
import org.aaa.chain.utils.PBEUtils;
import org.aaa.chain.views.ProgressResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadResumeActivity extends BaseActivity implements ProgressResponseBody.ProgressListener {

    ConstraintLayout layout1;
    ConstraintLayout layout2;
    ProgressBar progressBar;
    TextView tvPercent;
    EditText inputPrice;
    ProgressDialog dialog;

    private JSONObject object;
    private ResumeRequestEntity requestEntity = new ResumeRequestEntity();

    @Override public int initLayout() {
        return R.layout.activity_upload_resume;
    }

    @Override public void getViewById() {

        layout1 = $(R.id.cl_layout1);
        layout2 = $(R.id.cl_layout2);
        progressBar = $(R.id.progress);
        tvPercent = $(R.id.tv_percent);
        inputPrice = $(R.id.et_resume_price);
        $(R.id.btn_resume_upload).setOnClickListener(this);
        $(R.id.btn_upload_done).setOnClickListener(this);
    }

    @Override public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_resume_upload:
                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("*/*");
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                //startActivityForResult(intent, 100);
                new LFilePicker().withActivity(UploadResumeActivity.this).withRequestCode(100).start();

                break;

            case R.id.btn_upload_done:
                finish();
                break;
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            //Uri imageUri;
            //if (data != null) {
            //    String imagePath = null;
            //    imageUri = data.getData();
            //    if (DocumentsContract.isDocumentUri(UploadResumeActivity.this, imageUri)) {
            //        String docId = DocumentsContract.getDocumentId(imageUri);
            //        if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
            //            String id = docId.split(":")[1];
            //            String selection = MediaStore.Images.Media._ID + "=" + id;
            //            imagePath = getImagePath(UploadResumeActivity.this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            //        } else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
            //            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
            //            imagePath = getImagePath(UploadResumeActivity.this, contentUri, null);
            //        }
            //    } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            //        imagePath = getImagePath(UploadResumeActivity.this, imageUri, null);
            //    } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            //        imagePath = imageUri.getPath();
            //    }
            List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
            try {
                File file = PBEUtils.getInstance()
                        .encryptFile(UploadResumeActivity.this, list.get(0), org.aaa.chain.Constant.getPrivateKey(), org.aaa.chain.Constant.getPublicKey());
                Log.i("info", "file path:" + file.getAbsolutePath());
                requestEntity.setFilepath(file.getAbsolutePath());
                requestEntity.setFilename(file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            uploadFile();
        }
    }

    private void uploadFile() {
        dialog = ProgressDialog.show(UploadResumeActivity.this, "waiting....", "loading...");
        if (TextUtils.isEmpty(inputPrice.getText().toString())) {
            Toast.makeText(UploadResumeActivity.this, getResources().getString(R.string.setting_price), Toast.LENGTH_SHORT).show();
        }
        try {
            JSONObject object1 = new JSONObject();
            object1.put("onlyHash", false);
            requestEntity.setOptions(object1.toString());

            object = new JSONObject(getIntent().getExtras().getString("metadata"));
            object.put("name", requestEntity.getFilename());
            object.put("desc", "space");
            object.put("price", inputPrice.getText().toString());
            requestEntity.setMetadata(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestEntity.setAccount(org.aaa.chain.Constant.getAccount());

        JSInteraction.getInstance().getSignature(object.toString(), org.aaa.chain.Constant.getPrivateKey(), new JSInteraction.JSCallBack() {
            @Override public void onSuccess(String content) {
                requestEntity.setSignature(content);
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        dialog.dismiss();
                        layout1.setVisibility(View.GONE);
                        layout2.setVisibility(View.VISIBLE);
                        HttpUtils.getInstance().addFileResource(UploadResumeActivity.this, requestEntity, new HttpUtils.ServerCallBack() {
                            @Override public void onFailure(Call call, IOException e) {
                                Log.d("info", "onFailure: " + e.getMessage());
                            }

                            @Override public void onResponse(Call call, Response response) {
                                ResponseBody body = response.body();
                                if (response.code() == 200) {
                                    try {
                                        ResumeResponseEntity responseEntity = new Gson().fromJson(body.string(), ResumeResponseEntity.class);
                                        DBManager.getInstance().saveResumeResponse(responseEntity);
                                        requestEntity.set_id(responseEntity.get_id());
                                        DBManager.getInstance().saveResumeRequest(requestEntity);
                                        runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                Toast.makeText(UploadResumeActivity.this, getResources().getString(R.string.upload_successful),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            Toast.makeText(UploadResumeActivity.this, response.code() + "", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }

            @Override public void onProgress() {

            }

            @Override public void onError(String error) {

            }
        });
    }

    private String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public String getNameFromPath(String picturePath) {
        String temp[] = picturePath.replaceAll("\\\\", "/").split("/");
        String fileName = "";
        if (temp.length > 1) {
            fileName = temp[temp.length - 1];
        }
        return fileName;
    }

    @Override public void update(long bytesRead, long contentLength, boolean done) {
        final int percent = (int) (100 * bytesRead / contentLength);
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Log.i("info", "percent:" + percent);
                progressBar.setProgress(percent);
                tvPercent.setText(percent + "%");
            }
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        JSInteraction.getInstance().removeListener();
    }
}

package org.aaa.chain.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.aaa.chain.permissions.PermissionsManager;

public class ImageUtils {

    public static int ALBUM_REQUEST_CODE = 0X01;
    public static int TAKE_PHOTO_REQUEST_CODE = 0X02;
    public static int CROP_IMAGE_REQUEST_CODE = 0X03;
    private static File cropIconDir;
    private Uri imageUri;

    private static ImageUtils mInstance = null;

    public static ImageUtils getInstance() {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();
            String rootDir = "/" + "images";
            cropIconDir = new File(external, rootDir + "/crop");
            if (!cropIconDir.exists()) {
                cropIconDir.mkdirs();
            }
        }
        if (mInstance == null) {
            mInstance = new ImageUtils();
        }
        return mInstance;
    }

    public void choosePhoto(Context context) {
        boolean hasPermission = PermissionsManager.getInstance().hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasPermission) {
            Intent intentToPickPic = new Intent(Intent.ACTION_OPEN_DOCUMENT, null);
            intentToPickPic.setType("image/*");
            ((AppCompatActivity) context).startActivityForResult(intentToPickPic, ALBUM_REQUEST_CODE);
            Toast.makeText(context, "storage permission have been granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "storage permission has been denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void takePhoto(Context context) {
        boolean hasPermission = PermissionsManager.getInstance().hasPermission(context, Manifest.permission.CAMERA);
        if (hasPermission) {
            Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", createCropFile());
                intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                imageUri = Uri.fromFile(createCropFile());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intentToTakePhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            ((AppCompatActivity) context).startActivityForResult(intentToTakePhoto, TAKE_PHOTO_REQUEST_CODE);
            Toast.makeText(context, "camera permission have been granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "camera permission  has been denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleImage(Context context, Intent data) {
        Uri imageUri;
        if (data != null) {
            String imagePath = null;
            imageUri = data.getData();
            if (DocumentsContract.isDocumentUri(context, imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(context, contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
                imagePath = getImagePath(context, imageUri, null);
            } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
                imagePath = imageUri.getPath();
            }
        } else {
            imageUri = this.imageUri;
        }
        cropPhoto(context, imageUri);
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

    private File createCropFile() {
        String fileName = "";
        if (cropIconDir != null) {
            fileName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        }
        return new File(cropIconDir, fileName);
    }

    private void cropPhoto(Context context, Uri imageUri) {
        File file = createCropFile();
        Uri outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);

        ((Activity) context).startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE);
    }
}

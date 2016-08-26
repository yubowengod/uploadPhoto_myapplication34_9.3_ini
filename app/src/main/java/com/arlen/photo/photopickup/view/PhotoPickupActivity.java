package com.arlen.photo.photopickup.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.arlen.photo.photopickup.util.BaseShrPref;
import com.arlen.photo.photopickup.util.MediaUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Tan Linghui on 2015/11/24.
 */
public final class PhotoPickupActivity extends BasePhotoPickupActivity {
    public static final int REQUEST_CODE = 0xA801;
    public final static String LAST_PHOTO_TAKEN_URI = "LAST_PHOTO_TAKEN_URI";

    public static void startForResult(Activity activity, int maxSel, ArrayList<String> selectedImgIds){
        Intent intent = new Intent(activity, PhotoPickupActivity.class);
        formatMultiSelIntent(intent,maxSel,selectedImgIds);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static final int REQUEST_CODE_IMAGE_CAPTURE = 0x1205;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode==REQUEST_CODE_IMAGE_CAPTURE){
            BaseShrPref draft = new BaseShrPref(this);
            String uriStr = draft.getString(LAST_PHOTO_TAKEN_URI);
            if(TextUtils.isEmpty(uriStr)){
                return;// 搞什么飞机啊，
            }
            Uri lastCameraPhotoUri = Uri.parse(uriStr);
            if(resultCode==RESULT_OK){
                MediaUtils.ImageProperty lastImgPro = MediaUtils.getImagePro(this, lastCameraPhotoUri);
                if (lastImgPro != null) {
                    confirmSelectionAndExit(lastImgPro);
                }else {
                    Toast.makeText(this,"无法生成照片，请检查存储空间是否足够",Toast.LENGTH_SHORT).show();
                }
            }else{
                try {
                    getContentResolver().delete(lastCameraPhotoUri, null, null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            draft.remove(LAST_PHOTO_TAKEN_URI);
        }
    }

    @Override
    protected final void onClickCamera(){
        try{
            final Context context = PhotoPickupActivity.this;
            // 必须传入一个位置URI，否则有的系统无法保存拍照后的图片
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            SimpleDateFormat timeStampFormat = new SimpleDateFormat( "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, filename);
            Uri lastCameraPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            // 这里使用数据库把这个新纪录的URI保存下来，当从照相机返回时，万一被杀死了，还可以读取出这个URI
            new BaseShrPref(context).putString(LAST_PHOTO_TAKEN_URI,lastCameraPhotoUri.toString());
            Log.w("","lastCameraPhotoUri:"+ lastCameraPhotoUri.toString());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, lastCameraPhotoUri);
            startActivityForResult(cameraIntent,REQUEST_CODE_IMAGE_CAPTURE);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this,"无法生成照片，请检查存储空间是否足够",Toast.LENGTH_SHORT).show();
        }
    }
}

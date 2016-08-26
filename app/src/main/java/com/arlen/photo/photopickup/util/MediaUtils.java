package com.arlen.photo.photopickup.util;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Tan LingHui on 2015/11/25.
 */
public final class MediaUtils {

    public static final class ImageProperty implements Serializable {
        public  String id;
        public  String thumbnailId;
        public  String bunketId;
        public  String bunketName;
        public  String dateTaken;
        public  String fullPath;
        public String url;
        public  int ori;
        public int state=0; // 0:loading, 1:load ok, -1: load failed
        public String displayName;
        public String desc;
        public boolean isUploadResult = true;
        public ImageProperty(String id, String tid, String bid, String bname, String dt, String fullPath,String url, int ori,boolean isUploadResult){
            this(id,tid,bid,bname,dt,fullPath,ori);
            this.url = url;
            this.isUploadResult = isUploadResult;
        }
        public ImageProperty(String id, String tid, String bid, String bname, String dt, String fullPath, int ori){
            this.id=id;
            this.thumbnailId=tid;
            this.bunketId=bid;
            this.bunketName=bname;
            this.dateTaken=dt;
            this.fullPath=fullPath;
            this.ori=ori;
        }
        public void dump(){
            Log.w("ImageProperty","id:"+id);
            Log.w("ImageProperty","thumbnailId:"+thumbnailId);
            Log.w("ImageProperty","bunketId:"+bunketId);
            Log.w("ImageProperty","bunketName:"+bunketName);
            Log.w("ImageProperty","dateTaken:"+dateTaken);
            Log.w("ImageProperty","fullPath:"+fullPath);
            Log.w("ImageProperty","ori:"+ori);
            Log.w("ImageProperty","displayName:"+displayName);
            Log.w("ImageProperty","desc:"+desc);
        }
        public final boolean compareId(ImageProperty o){
            return this.id.equals(o.id);
        }
    }

    public static ArrayList<ImageProperty> listAllImage(Context context, HashSet<String> selectedImgIds, HashMap<String,ImageProperty> selResult){
        Cursor cursor=null;
        try{
            HashMap<Integer,Integer> tidMap = new HashMap<Integer,Integer>();
            {
                final String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID};
                cursor = MediaStore.Images.Thumbnails.query(
                        context.getContentResolver(),
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection
                );
            }
            if( cursor!=null){
                if (cursor.moveToFirst()) {
                    final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
                    final int imgIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID);
                    do {
                        final int id = cursor.getInt(idColumn);
                        final int imgId = cursor.getInt(imgIdColumn);
                        tidMap.put(imgId,id);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            {
                final String[] projection = {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.ORIENTATION,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DESCRIPTION
                };
                cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.Images.Media._ID + " DESC"
                );
                if( cursor!=null) {
                    if (cursor.moveToFirst()) {
                        final int idx_1 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                        final int idx_2 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                        final int idx_3 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                        final int idx_4 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                        final int idx_5 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        final int idx_6 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);
                        final int idx_7 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                        final int idx_8 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION);
                        ArrayList<ImageProperty> results = new ArrayList<ImageProperty>(cursor.getCount());
                        do {
                            final int id = cursor.getInt(idx_1);
                            final int bid = cursor.getInt(idx_2);

                            // 预防文件不存在
                            final String path = cursor.getString(idx_5);
                           if( TextUtils.isEmpty(path) || FileSizeUtil.isFileExists(path)==false){
                                continue;
                            }


                            final int ori = cursor.getInt(idx_6);
                            final ImageProperty imgPro = new ImageProperty(
                                    String.valueOf(id),
                                    String.valueOf(tidMap.get(id)),
                                    cursor.getString(idx_2),
                                    cursor.getString(idx_3),
                                    cursor.getString(idx_4),
                                    path,
                                    getImageRotation(path,cursor.getInt(idx_6))
                            );
                            imgPro.displayName = cursor.getString(idx_7);
                            imgPro.desc = cursor.getString(idx_8);
                            if (selectedImgIds != null && selectedImgIds.contains(imgPro.id)) {
                                if (selResult != null) {
                                    selResult.put(imgPro.id, imgPro);
                                }
                            }

                            //imgPro.dump();
                            results.add(imgPro);

                        }while (cursor.moveToNext());
                        return results;
                    }

                }
                return new ArrayList<>();//empty result
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (cursor != null && cursor.isClosed()==false) {
                    cursor.close();
                }
            }catch(Exception e){
            }
        }
        return null;
    }

    /**
     * @deprecated
     * @param context
     * @return
     */
    public static int getLastImageId(Context context){
        final String[] imageColumns = { MediaStore.Images.Media._ID };
        final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
        final String imageWhere = null;
        final String[] imageArguments = null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, imageWhere, imageArguments, imageOrderBy);
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            }
            return 0;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (cursor != null && cursor.isClosed()==false) {
                    cursor.close();
                }
            }catch(Exception e){
            }
        }
        return -1;
    }

    public static MediaUtils.ImageProperty getImagePro(Context context, Uri uri){
        Cursor cursor = null;
        try {
            final String[] projection = {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.ORIENTATION
            };
            cursor = context.getContentResolver().query(uri,null,null,null,null);
            if(cursor!=null) {
                if (cursor.moveToFirst()) {
                    final int idx_1 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    final int idx_2 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                    final int idx_3 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    final int idx_4 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                    final int idx_5 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    final int idx_6 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);
                    final int id = cursor.getInt(idx_1);
                    final String path = cursor.getString(idx_5);
                    final int ori = cursor.getInt(idx_6);
                    return new ImageProperty(
                            String.valueOf(id),
                            "",
                            cursor.getString(idx_2),
                            cursor.getString(idx_3),
                            cursor.getString(idx_4),
                            path,
                            getImageRotation(path,ori)
                    );
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (cursor != null && cursor.isClosed()==false) {
                    cursor.close();
                }
            }catch(Exception e){
            }
        }
        return null;
    }

    public static String isImgAvailable(Context context, String imgId){
        final String[] imageColumns = { MediaStore.Images.Media._ID };
        final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
        final String imageWhere = MediaStore.Images.Media._ID + "=" + imgId;
        final String[] imageArguments = null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, imageWhere, imageArguments, imageOrderBy);
            if (cursor.moveToFirst()) {
                return String.valueOf(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (cursor != null && cursor.isClosed()==false) {
                    cursor.close();
                }
            }catch(Exception e){
            }
        }
        return "";
    }

    /**
     * 使用这个方法获取旋转才是最靠谱的
     * @param path
     * @param rotationFromMediaStore
     * @return
     */
    private static int getImageRotation(String path, int rotationFromMediaStore) {
        try {
            ExifInterface exif = new ExifInterface(path);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if (rotation != ExifInterface.ORIENTATION_UNDEFINED){
                return exifToDegrees(rotation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotationFromMediaStore;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }
}

package com.arlen.photo.photopickup.presenter;

import android.graphics.Bitmap;
import android.support.v4.util.Pair;

import com.arlen.photo.photopickup.util.MediaUtils;
import com.arlen.photo.photopickup.view.IPhotoPickupView;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Tan LingHui on 2015/11/26.
 */
public interface IPhotoPickupPresenter {

    void onCreate();

    void onDestroy();

    void setCallback(IPhotoPickupView cb);

    Bitmap getThumbnailBitmap(Object reqKey, MediaUtils.ImageProperty imgPro);

    Bitmap getLargeBitmap(Object reqKey, MediaUtils.ImageProperty imgPro, int targetWidth, int targetHeight);

    void refresh(HashSet<String> selImgIds);

    ArrayList<MediaUtils.ImageProperty> getImgProperties(String bunketName);

    ArrayList<Pair<String, ArrayList<MediaUtils.ImageProperty>>> getImgProsGroup();

    boolean isDefBunketName(String bunketName);

    void cancelThumbnailBitmapGetting(Object reqKey, MediaUtils.ImageProperty imgPro);

    void cancelLargeBitmapGetting(Object reqKey, MediaUtils.ImageProperty imgPro);
}

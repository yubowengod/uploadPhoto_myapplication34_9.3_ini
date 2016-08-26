package com.arlen.photo.photopickup.presenter;


import com.arlen.photo.photopickup.util.MediaUtils;

/**
 * Created by Arlen on 2016/8/10 10:23.
 */
public interface IPhotoResult {
    void showUploadView(String url, MediaUtils.ImageProperty imageProperty);

    void showUploadFailureView(MediaUtils.ImageProperty imageProperty);


}

package com.arlen.photo.ui;

import android.net.Uri;

import java.util.List;

/**
 * Created by Arlen on 2016/6/30 10:48.
 */
public interface IMainPresenter {

    void compressImage(Uri uri);

    void uploadImage(List<String> files);

}

package com.arlen.photo.upload;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.ksoap2.serialization.SoapObject;

import java.util.List;

/**
 * Created by GOD on 2016/9/2.
 */
public class progressbar_thread {




    public void getProgressBar(ProgressBar progressBar){
        int progress = progressBar.getProgress();
        progress = progress + 10;
        progressBar.setProgress(progress);
    }




}

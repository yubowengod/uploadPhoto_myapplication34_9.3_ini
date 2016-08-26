package com.arlen.photo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.arlen.photo.R;
import com.arlen.photo.photopickup.presenter.PhotoPresenter;
import com.arlen.photo.photopickup.view.ImageLookActivity;
import com.arlen.photo.photopickup.view.PhotoPickupActivity;
import com.arlen.photo.photopickup.widget.SimpleGrid;

/**
 * Created by Arlen on 2016/8/12 10:30.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private PhotoPresenter mPhotoPresenter;

    private SimpleGrid mSimpleGrid;

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotoPresenter = new PhotoPresenter(this,"feedback");
        initView();
    }

    private void initView() {

        mSimpleGrid = (SimpleGrid) findViewById(R.id.img_grid);

        mPhotoPresenter.initView(mSimpleGrid);
        mSimpleGrid.setMaxItemPerRow(4);
        mSimpleGrid.setItemMarginHor(7f);
        mSimpleGrid.setItemMarginVer(7f);
        mPhotoPresenter.updateImgGrid();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPickupActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (PhotoPickupActivity.getSelectedImgPros(data) != null) {
                    mPhotoPresenter.pickPhotoResult(data);
                }
            }
        } else if (requestCode == ImageLookActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mPhotoPresenter.lookImageResult(data);
                }
            }
        }
    }
}

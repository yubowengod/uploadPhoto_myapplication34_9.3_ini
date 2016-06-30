package com.arlen.photo.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.arlen.photo.R;
import com.arlen.photo.widget.PhotoSelectDialog;
import com.arlen.photo.widget.SimpleGrid;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , PhotoSelectDialog.CropResultListener, SimpleGrid.Callback, IMainView {

    private List<Bitmap> mImageList;
    private List<String> mFileList;

    private SimpleGrid mSimpleGrid;
    private Button mBtnSubmit;

    private PhotoSelectDialog mDialog;
    private IMainPresenter mMainPresenter;

    private static final int MAX_SEL_PHOTOS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageList = new ArrayList<>();
        mFileList = new ArrayList<>();
        mMainPresenter = new MainPresenter(this, this);

        intView();
    }

    private void intView() {
        mSimpleGrid = (SimpleGrid) findViewById(R.id.simpleGrid);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);

        mDialog = new PhotoSelectDialog(this);
        mSimpleGrid.setMaxItemPerRow(3);
        mSimpleGrid.setItemMarginHor(6f);
        mSimpleGrid.setItemMarginVer(6f);

        mSimpleGrid.setCallback(this);
        mBtnSubmit.setOnClickListener(this);
        mDialog.setCropResultListener(this);

        //要放在setCallBack(this)后面
        updateImgGrid();
    }

    private void updateImgGrid() {
        final int curImgCount = mImageList.size();
        if (curImgCount < MAX_SEL_PHOTOS) {
            mSimpleGrid.createViews(curImgCount + 1); // 未满的时候，要加上1， 照相机的位置
        } else {
            mSimpleGrid.createViews(curImgCount);
        }
    }

    private void prepareDelete(Bitmap bitmap,String fileName) {
        if (bitmap != null) {
            mImageList.remove(bitmap);
            mFileList.remove(fileName);
            updateImgGrid();
            bitmap.recycle();
        }
    }

    @Override
    public void showImageView(Bitmap bitmap,String fileName) {
        if (mImageList.size() < MAX_SEL_PHOTOS) {
            mImageList.add(bitmap);
            mFileList.add(fileName);
            updateImgGrid();
        }
    }

    @Override
    public void onClick(View v) {
        mMainPresenter.uploadImage(mFileList);//
    }

    @Override
    public void cropResult(Uri uri) {
        if (mImageList != null && mImageList.size() < MAX_SEL_PHOTOS) {
            if (uri == null)
                return;
            mMainPresenter.compressImage(uri);
        }
    }

    @Override
    public View onCreateView(ViewGroup viewGroup, int position) {
        // 如果未满,第一个位置显示照相机
        if (position == 0 && mImageList.size() < MAX_SEL_PHOTOS) {
            final View view = LayoutInflater.from(this).inflate(R.layout.item_photo_preview_with_upload, viewGroup,
                    false);
            final ImageView ivAdd = (ImageView) view.findViewById(R.id.iv_add);
            ivAdd.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO点击拍照
                    mDialog.show();
                }
            });
            return view;
        }

        // 获取实际的数据索引，未满的时候需要减去1，因为第一个是照相机
        final int pos = (mImageList.size() < MAX_SEL_PHOTOS) ? (position - 1) : (position);
        final View itemV = LayoutInflater.from(this).inflate(R.layout.item_photo_preview_with_delete, viewGroup, false);
        final ImageView img = (ImageView) itemV.findViewById(R.id.img);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final Bitmap bitmap = mImageList.get(pos);
        final String fileName = mFileList.get(pos);
        img.setImageBitmap(bitmap);
        itemV.findViewById(R.id.del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDelete(bitmap,fileName);
            }
        });
        itemV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO跳到大图浏览
//                ImagePagerActivity.startImagePagerActivity(RefundActivity.this, pos, mImageList);
            }
        });
        return itemV;
    }

    @Override
    public void onRemoveView(int position, View v) {

    }

    /**
     * android 6.0以上拍照权限问题
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            mDialog.takePhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) { // 头像上传,不光要上传到即来社区的头像库中,也需要上传到聊天的头像库中
            mDialog.doPhoto(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

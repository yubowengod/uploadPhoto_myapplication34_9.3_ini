package com.arlen.photo.photopickup.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlen.photo.R;
import com.arlen.photo.photopickup.util.MediaUtils;
import com.arlen.photo.photopickup.widget.PhotoViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arlen on 2016/8/8 17:12.
 */
public class ImageLookActivity extends Activity implements View.OnClickListener, PhotoViewPager.Callback {

    private int mPosition;
    private int mMode;
    private MediaUtils.ImageProperty mCurrentImageProperty;
    private ArrayList<MediaUtils.ImageProperty> mList;
    private List<String> mListUrl;

    private ImageView mIvBack;
    private TextView mTvTitle;
    private ImageView mIvDelete;
    private PhotoViewPager mPhotoViewPager;

    public static final int REQUEST_CODE = 0x0121;
    public static final int MODE_DELETE = 0x001;
    public static final int MODE_LOOK = 0x002;

    private static final String KEY_POSITION = "position";
    private static final String KEY_MODE = "mode";
    public static final String KEY_IMAGE_PROPERTY = "image_property";

    public static void startForResultImageLookActivity(Activity activity, int mode,int position, ArrayList<MediaUtils.ImageProperty> list) {
        Intent intent = new Intent(activity, ImageLookActivity.class);
        intent.putExtra(KEY_POSITION, position);
        intent.putExtra(KEY_MODE,mode);
        intent.putExtra(KEY_IMAGE_PROPERTY, list);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void startImageLookActivity(Context activity, int mode, int position, String[] imageUrl){
        Intent intent = new Intent(activity, ImageLookActivity.class);
        intent.putExtra(KEY_POSITION, position);
        intent.putExtra(KEY_MODE,mode);
        ArrayList<MediaUtils.ImageProperty> list = new ArrayList<>();
        for(String url:imageUrl){
            MediaUtils.ImageProperty imageProperty = new MediaUtils.ImageProperty("", "", "", "", "", "", url, 0, true);
            list.add(imageProperty);
        }
        intent.putExtra(KEY_IMAGE_PROPERTY, list);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_image);

        mPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        mMode = getIntent().getIntExtra(KEY_MODE,MODE_LOOK);
        mList = (ArrayList<MediaUtils.ImageProperty>) getIntent().getSerializableExtra(KEY_IMAGE_PROPERTY);
        initView();
        initData();
    }

    private void initData() {
        if (mList == null)
            return;
        mTvTitle.setText((mPosition + 1) + "/" + mList.size());
        mCurrentImageProperty = mList.get(mPosition);
        mPhotoViewPager.show("照片列表", mList, mPosition);
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvDelete = (ImageView) findViewById(R.id.iv_delete);
        mPhotoViewPager = (PhotoViewPager) findViewById(R.id.photo_view_pager);

        mIvDelete.setVisibility(mMode == MODE_DELETE?View.VISIBLE:View.GONE);

        mIvBack.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);
        mPhotoViewPager.setCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_delete:
                if (mCurrentImageProperty != null) {
                    mPhotoViewPager.removePage(mCurrentImageProperty);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mMode == MODE_LOOK) {
            super.onBackPressed();
        }else {
            Intent intent = new Intent();
            intent.putExtra(KEY_IMAGE_PROPERTY, mList);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onPageSelected(int position, MediaUtils.ImageProperty imgPro) {
        mCurrentImageProperty = mList.get(position);
        mTvTitle.setText((position + 1) + "/" + mList.size());
    }

    @Override
    public void onPageScrolled(int position, MediaUtils.ImageProperty imgPro) {

    }

    @Override
    public void onPageClicked(MediaUtils.ImageProperty imgPro) {

    }
    @Override
    public void onDismissed() {
        onBackPressed();
    }

    @Override
    public void onPreShow(String title, int totalSize, int beginPos) {

    }
}

package com.arlen.photo.photopickup.presenter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlen.photo.R;
import com.arlen.photo.photopickup.util.ImageUtil;
import com.arlen.photo.photopickup.util.MediaUtils;
import com.arlen.photo.photopickup.view.ImageLookActivity;
import com.arlen.photo.photopickup.view.PhotoPickupActivity;
import com.arlen.photo.photopickup.widget.SimpleGrid;

import java.util.ArrayList;

/**
 * Created by Arlen on 2016/8/10 9:55.
 */
public class PhotoPresenter extends BasePhotoPresenter implements SimpleGrid.Callback, IPhotoResult {

    private Activity mContext;
    private String mType;
    private ArrayList<MediaUtils.ImageProperty> mSelectedImgPros;

    private SimpleGrid mSimpleGrid;

    private static final int MAX_SEL_PHOTOS = 10;

    /**
     *
     * @param context
     * @param type 区分不同的地方上传图片比如"goods"--上传商品 "feedback"--意见反馈
     */
    public PhotoPresenter(Activity context, String type) {
        super(context);
        mContext = context;
        mType = type;
        mSelectedImgPros = new ArrayList<>();
        setPhotoResult(this);
    }

    public void initView(SimpleGrid simpleGrid) {
        mSimpleGrid = simpleGrid;
        mSimpleGrid.setCallback(this);
    }

    public void addAllSelectedList(ArrayList<MediaUtils.ImageProperty> propertyArrayList) {
        mSelectedImgPros.addAll(propertyArrayList);
    }

    public ArrayList<MediaUtils.ImageProperty> getSelectedList() {
        return mSelectedImgPros;
    }

    private void prepareDelete(MediaUtils.ImageProperty imageProperty) {
        if (imageProperty != null) {
            if (imageProperty.id.equals("-1")) {
                mSelectedImgPros.remove(imageProperty);
            }
            updateImgGrid();
        }
    }

    public void updateImgGrid() {
        final int curImgCount = mSelectedImgPros.size();
        if (curImgCount < MAX_SEL_PHOTOS) {
            mSimpleGrid.createViews(curImgCount + 1); // 未满的时候，要加上1， 照相机的位置
        } else {
            mSimpleGrid.createViews(curImgCount);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(ViewGroup viewGroup, final int position) {
        if (Build.VERSION.SDK_INT >= 17 && mContext.isDestroyed()) {
            return null;
        }
        // 如果未满,第一个位置显示照相机
        if (position == mSelectedImgPros.size() && mSelectedImgPros.size() < MAX_SEL_PHOTOS) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo_preview_with_upload, viewGroup,
                    false);
            final ImageView ivAdd = (ImageView) view.findViewById(R.id.iv_add);
            ivAdd.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUploading()) {
                        Toast.makeText(mContext,"图片上传中，请稍后...",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // TODO点击拍照
                    ArrayList<String> imgId = new ArrayList<>();
                    for (MediaUtils.ImageProperty imageProperty : mSelectedImgPros) {
                        imgId.add(imageProperty.id);
                    }
                    PhotoPickupActivity.startForResult(mContext, mSelectedImgPros.size(), imgId);
                }
            });
            return view;
        }

        // 获取实际的数据索引，未满的时候需要减去1，因为第一个是照相机
        final View itemV = LayoutInflater.from(mContext).inflate(R.layout.item_photo_preview_with_delete, viewGroup, false);
        ImageView ivBg = (ImageView) itemV.findViewById(R.id.sel_cover);
        final TextView tvUploadStatus = (TextView) itemV.findViewById(R.id.tv_upload_status);
        final ImageView img = (ImageView) itemV.findViewById(R.id.img);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageButton ivDel = (ImageButton) itemV.findViewById(R.id.del);
        final MediaUtils.ImageProperty imageUrl = mSelectedImgPros.get(position);
        if (imageUrl.id.equals("-1")) {
            ivBg.setVisibility(View.GONE);
            tvUploadStatus.setVisibility(View.GONE);
        } else {
            ivBg.setVisibility(View.VISIBLE);
            tvUploadStatus.setVisibility(View.VISIBLE);
            if (imageUrl.isUploadResult) {
                tvUploadStatus.setText("上传中...");
            } else {
                tvUploadStatus.setText("点击重试");
                tvUploadStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvUploadStatus.setText("上传中...");
                        uploadImageUrl(mType, imageUrl);
                    }
                });
            }
        }
        ImageUtil.load(mContext, imageUrl.fullPath, img, R.mipmap.ic_pig_rect);
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUrl.id.equals("-1") || tvUploadStatus.getText().toString().equals("点击重试")) {
                    prepareDelete(imageUrl);
                }
            }
        });
        itemV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploading()) {
                    Toast.makeText(mContext,"图片上传中，请稍后...",Toast.LENGTH_SHORT).show();
                    return;
                }
                ImageLookActivity.startForResultImageLookActivity(mContext
                        , ImageLookActivity.MODE_DELETE, position, mSelectedImgPros);
            }
        });
        return itemV;
    }

    @Override
    public void onRemoveView(int position, View v) {

    }

    public boolean isUploading() {
        for (int i = 0, size = mSelectedImgPros.size(); i < size; i++) {
            if (!mSelectedImgPros.get(i).id.equals("-1")) {
                Toast.makeText(mContext,"图片上传中，请稍后...",Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    @Override
    public void showUploadView(String url, MediaUtils.ImageProperty imageProperty) {
        imageProperty.id = "-1";
        //如果是上传成功把下面的注释去掉，这里只是模拟
//        imageProperty.fullPath = url;
        imageProperty.url = url;
        imageProperty.isUploadResult = true;
        updateImgGrid();
    }

    @Override
    public void showUploadFailureView(MediaUtils.ImageProperty imageProperty) {
        imageProperty.isUploadResult = false;
        updateImgGrid();
    }


    private void uploadImage() {
        for (MediaUtils.ImageProperty imageProperty : mSelectedImgPros) {
            if (!imageProperty.id.equals("-1")) {
                uploadImageUrl(mType, imageProperty);
            }
        }
    }

    public void pickPhotoResult(Intent data) {
        if (mSelectedImgPros.size() < 10) {
            mSelectedImgPros.addAll(PhotoPickupActivity.getSelectedImgPros(data));
            updateImgGrid();
            uploadImage();
        }
    }

    public void lookImageResult(Intent data) {
        ArrayList<MediaUtils.ImageProperty> imageProperties = (ArrayList<MediaUtils.ImageProperty>) data.getSerializableExtra(ImageLookActivity.KEY_IMAGE_PROPERTY);
        if (imageProperties != null) {
            mSelectedImgPros = imageProperties;
            updateImgGrid();
        }
    }
}

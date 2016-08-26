package com.arlen.photo.photopickup.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.arlen.photo.photopickup.util.DensityUtils;
import com.arlen.photo.photopickup.util.FileSizeUtil;
import com.arlen.photo.photopickup.util.MediaUtils;

import java.io.File;

/**
 * Created by Arlen on 2016/8/10 10:18.
 */
public class BasePhotoPresenter{

    private Activity mActivity;
    private IPhotoResult mPhotoResult;

    public BasePhotoPresenter(Activity activity) {
        this.mActivity = activity;
    }

    protected void setPhotoResult(IPhotoResult photoResult) {
        this.mPhotoResult = photoResult;
    }

    protected void uploadImageUrl(final String type, final MediaUtils.ImageProperty imageProperty) {
        if (TextUtils.isEmpty(imageProperty.fullPath))
            return;

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                File file = new File(imageProperty.fullPath);
                BitmapFactory.Options options = FileSizeUtil.getBitmapOptions(file.getPath());
                int screenMax = Math.max(DensityUtils.getWindowWidth(mActivity),
                        DensityUtils.getWindowHeight(mActivity));
                int imgMax = Math.max(options.outWidth, options.outHeight);
                int inSimpleSize = 1;
                if (screenMax <= imgMax) {
                    inSimpleSize = Math.max(screenMax, imgMax) / Math.min(screenMax, imgMax);
                }
                return FileSizeUtil.compressBitmap(mActivity,
                        file.getAbsolutePath(),
                        Bitmap.CompressFormat.JPEG,
                        options.outWidth / inSimpleSize,
                        options.outHeight / inSimpleSize,
                        false);
            }

            @Override
            protected void onPostExecute(final String fileName) {
                final File imageBytes = new File(fileName);
                //TODO 用的retrofit 2.0上传图片一次上传一张
                if(true){
                    mPhotoResult.showUploadView("返回的url", imageProperty);
                }else{
                    mPhotoResult.showUploadFailureView(imageProperty);
                }
//                Map<String, RequestBody> map = new HashMap<>();
//                if (imageBytes != null) {
//                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), imageBytes);
//                    map.put("uploadedFile\"; filename=\"" + imageBytes.getName() + "", requestBody);
//                }
//
//                RequestOperate.instance().getImgUrl(new ReqDataCallBack<HeaderImgUrl>() {
//
//                    @Override
//                    public void onNext(HeaderImgUrl result) {
//                        super.onNext(result);
//                        if (result.isSuccess()) {
//                            mPhotoResult.showUploadView(result.getUrl(), imageProperty);
//                        } else {
//                            mPhotoResult.showUploadFailureView(imageProperty);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mPhotoResult.showUploadFailureView(imageProperty);
//                    }
//                }, type, map);
            }
        }.execute();
    }
}

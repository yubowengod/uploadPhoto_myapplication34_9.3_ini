package com.arlen.photo.photopickup.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlen.photo.R;
import com.arlen.photo.photopickup.presenter.IPhotoPickupPresenter;
import com.arlen.photo.photopickup.presenter.PhotoPickupPresenterImpl;
import com.arlen.photo.photopickup.util.MediaUtils;
import com.arlen.photo.photopickup.widget.PhotoBunketList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class BasePhotoPickupActivity extends Activity {

    private String bunkName="所有图片";
    private LayoutInflater mLayoutInflater;
    private RecyclerView mPhotoThumbnailGrid;
    private ItemAdapter mItemAdapter;
    private PhotoBunketList mPhotoBunketList;
    private TextView mTvPreviewState;
    private IPhotoPickupPresenter mPresenter;
    private TextView mTvCurBunketName;
    private TextView mTvCancel;
    private final LinkedList<MediaUtils.ImageProperty> mSelectedImgPros = new LinkedList<MediaUtils.ImageProperty>();
    private int CURRENT_SEL_PHOTOS;
    private int MAX_SELETED_PHOTOS = 10;
    private ArrayList<String> mInitSelImgIds;
    private final MyContentObserver myContentObserver = new MyContentObserver(new Handler());
    private static final int SEL_MODE_SINGLE = 0;
    private static final int SEL_MODE_MULTI = 1;
    private int mSelMode = SEL_MODE_MULTI;
    private boolean mIsDestroyed;

    private static final String EXTRA_MAX_SEL = "EXTRA_MAX_SEL";
    private static final String EXTRA_SELECTED_IMG_IDS = "EXTRA_SELECTED_IMG_IDS";
    private static final String EXTRA_SEL_MODE = "EXTRA_SEL_MODE";

    public static void formatMultiSelIntent(Intent intent, int maxSel, ArrayList<String> selectedImgIds) {
        intent.putExtra(EXTRA_MAX_SEL, maxSel);
        intent.putExtra(EXTRA_SELECTED_IMG_IDS, selectedImgIds);
        intent.putExtra(EXTRA_SEL_MODE, SEL_MODE_MULTI);
    }

    public static void formatSingleSelIntent(Intent intent) {
        intent.putExtra(EXTRA_SEL_MODE, SEL_MODE_SINGLE);
    }

    public static ArrayList<MediaUtils.ImageProperty> getSelectedImgPros(Intent intent) {
        ArrayList<MediaUtils.ImageProperty> imgPros = new ArrayList<>();
        int idx = 0;
        while (true) {
            final String name = "imgPro_" + String.valueOf(idx++);
            if (intent.hasExtra(name) == false) {
                break;
            }
            imgPros.add((MediaUtils.ImageProperty) intent.getSerializableExtra(name));
        }
        return imgPros;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleInputIntent();
        initViews();
        initPresenters();
        getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false,
                myContentObserver
        );


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroyed = true;
        mPhotoBunketList = null;
        mPhotoThumbnailGrid = null;
        mPresenter.setCallback(null);
        mPresenter.onDestroy();
        getContentResolver().unregisterContentObserver(myContentObserver);
    }

    public void onBackPressed() {
        if (mPhotoBunketList.isShowing()) {
            mPhotoBunketList.hide();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void handleInputIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            CURRENT_SEL_PHOTOS = intent.getIntExtra(EXTRA_MAX_SEL, CURRENT_SEL_PHOTOS);
            mInitSelImgIds = intent.getStringArrayListExtra(EXTRA_SELECTED_IMG_IDS);
            if (intent.hasExtra(EXTRA_SEL_MODE)) {
                mSelMode = intent.getIntExtra(EXTRA_SEL_MODE, SEL_MODE_SINGLE);
            }
        }
    }

    protected void confirmSelectionAndExit(MediaUtils.ImageProperty newImgPro) {
        Intent intent = new Intent();
        if (newImgPro != null) {
            mSelectedImgPros.addLast(newImgPro);
        }

        int idx = 0;
        for (MediaUtils.ImageProperty imgPro : mSelectedImgPros) {
            intent.putExtra("imgPro_" + String.valueOf(idx++), imgPro);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initViews() {
        mLayoutInflater = LayoutInflater.from(this);
        setContentView(R.layout.activity_base_photo_pickup_layout);

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 照片墙
        mPhotoThumbnailGrid = (RecyclerView) findViewById(R.id.photo_thumbnail_list_view);
        mPhotoThumbnailGrid.setLayoutManager(new GridLayoutManager(this, 3));//这里用线性宫格显示 类似于grid view
        mItemAdapter = new ItemAdapter();
        mPhotoThumbnailGrid.setAdapter(mItemAdapter);

        // 相册选择按钮
        mTvCurBunketName = (TextView) findViewById(R.id.tv_look_photo);
        mTvCurBunketName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoBunketList.isShowing()) {
                    mPhotoBunketList.hide();
                } else {
                    mPhotoBunketList.show(bunkName, mPresenter.getImgProsGroup());
                }
            }
        });

        // 相册列表
        mPhotoBunketList = (PhotoBunketList) findViewById(R.id.photo_bunket_list_view);
        mPhotoBunketList.setCallback(new PhotoBunketList.Callback() {
            @Override
            public void onPrepareShow() {
            }

            @Override
            public void onHided() {
            }

            @Override
            public void onBunketSelected(String bunketName) {
                bunkName = bunketName;
                mItemAdapter.setDatas(mPresenter.getImgProperties(bunketName), mPresenter.isDefBunketName(bunketName));
            }

            @Override
            public Bitmap onGetThumbnailBitmap(MediaUtils.ImageProperty imgPro) {
                return mPresenter.getThumbnailBitmap(mPhotoBunketList, imgPro);
            }
        });

        mTvPreviewState = (TextView) findViewById(R.id.tv_preview_state); // 预览状态

        if (mSelMode == SEL_MODE_SINGLE) {

            mTvCurBunketName.setVisibility(View.GONE);
            mTvPreviewState.setVisibility(View.GONE);

        } else if (mSelMode == SEL_MODE_MULTI) {

            mTvPreviewState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmSelectionAndExit(null);
                }
            });
        }//end if SEL_MODE_MULTI

        mTvCancel = (TextView) findViewById(R.id.tv_all_sel_state);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoBunketList.isShowing())
                    mPhotoBunketList.hide();
                mSelectedImgPros.clear();
                updateAllSelState();
                mItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initPresenters() {
        mPresenter = new PhotoPickupPresenterImpl(this, 2);
        mPresenter.onCreate();
        mPresenter.setCallback(myViewCallback);
        HashSet<String> ids = new HashSet<>();
        if (mInitSelImgIds != null) {
            for (String id : mInitSelImgIds) ids.add(id);
        }
        mPresenter.refresh(ids);
    }

    private final class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (mIsDestroyed) {
                return;
            }
            if (mPhotoBunketList.isShowing()) {
                mPhotoBunketList.hide();
            }

            if (mSelMode == SEL_MODE_MULTI) {
                final Context context = BasePhotoPickupActivity.this;
                // 根据当前的选择状态重新设置初始选择列表
                mInitSelImgIds = new ArrayList<>(mSelectedImgPros.size());
                for (MediaUtils.ImageProperty imgPro : mSelectedImgPros) {
                    if (MediaUtils.isImgAvailable(context, imgPro.id).equals(imgPro.id)) {
                        mInitSelImgIds.add(imgPro.id);
                    }
                }
                // 此时不要急于清除mSelectedImgPros，因为可能会两次发出obChange通知
                // 待刷新完毕再重新设置mSelectedImgPros

                // 重新刷新
                mPresenter.refresh(new HashSet<>(mInitSelImgIds));
            } else {
                mPresenter.refresh(null);
            }

        }
    }

    private boolean clickOnImgSelView(View view, MediaUtils.ImageProperty imgPro, boolean notifyDataSet) {
        if (view.isSelected()) {
            view.setSelected(false);
            mSelectedImgPros.remove(imgPro);
        } else {
            if (imgPro.state == 0) {
                showToast("图片正在加载，请稍候...");
                return false;
            } else if (imgPro.state == -1) {
                showToast("图片已失效");
                return false;
            }
            if ((mSelectedImgPros.size() + CURRENT_SEL_PHOTOS) >= MAX_SELETED_PHOTOS) {
                showToast("最多选择" + MAX_SELETED_PHOTOS + "张图片");
                return false;
            }
            view.setSelected(true);
            mSelectedImgPros.add(imgPro);
        }
        updateAllSelState();
        if (notifyDataSet) {
            mItemAdapter.notifyDataSetChanged();
        }
        return view.isSelected();
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private void updateAllSelState() {
        final int curSelCount = mSelectedImgPros.size() + CURRENT_SEL_PHOTOS;
//        if (curSelCount > 0) {
//            mTvCurBunketName.setEnabled(true);
//            mTvPreviewState.setEnabled(true);
//        } else {
//            mTvCurBunketName.setEnabled(false);
//            mTvPreviewState.setEnabled(false);
//        }
        mTvPreviewState.setText("完成(" + String.valueOf(curSelCount) + "/" + String.valueOf(MAX_SELETED_PHOTOS) + ")");
    }

    private final IPhotoPickupView myViewCallback = new IPhotoPickupView() {
        @Override
        public void onRefreshComplete(String defBunketNames, HashMap<String, MediaUtils.ImageProperty> selResult) {
            if (mIsDestroyed) {
                return;
            }
            // 清除已选择
            mSelectedImgPros.clear();
            // 根据刷新结果和初始顺序重新设置已选择列表
            if (mInitSelImgIds != null) {
                for (String imgId : mInitSelImgIds) {
                    MediaUtils.ImageProperty imgPro = selResult.get(imgId);
                    if (imgPro != null) {
                        mSelectedImgPros.add(imgPro);
                    }
                }
            }
            mItemAdapter.setDatas(mPresenter.getImgProperties(defBunketNames), true);
            updateAllSelState();
        }

        @Override
        public void onThumbnailBitmapReady(Object taskKey, MediaUtils.ImageProperty imgPro, Bitmap bm) {
            if (mIsDestroyed) {
                return;
            }
            if (taskKey == mPhotoThumbnailGrid) {
                ImageView img = (ImageView) mPhotoThumbnailGrid.findViewWithTag(imgPro);
                if (img != null && bm != null) {
                    img.setImageBitmap(bm);
                }
            } else if (taskKey == mPhotoBunketList) {
                if (mPhotoBunketList.isShowing()) {
                    mPhotoBunketList.setImageBitmap(imgPro, bm);
                }
            }
        }

        @Override
        public void onLargeBitmapReady(Object taskKey, MediaUtils.ImageProperty imgPro, Bitmap bm) {
            if (mIsDestroyed) {
                return;
            }
        }
    };

    private final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        boolean hasHeader;
        ArrayList<MediaUtils.ImageProperty> datas = new ArrayList<>();

        ItemAdapter() {
        }

        void setDatas(ArrayList<MediaUtils.ImageProperty> d, boolean addHeader) {
            datas.clear();
            hasHeader = addHeader;
            if (addHeader) {
                datas.add(new MediaUtils.ImageProperty(null, null, null, null, null, null, -1));
            }
            if (d != null) {
                datas.addAll(d);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            // TODO Auto-generated method stub
            if (datas == null) {
                return 0;
            }
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {
            MediaUtils.ImageProperty imgPro = datas.get(position);
            if (imgPro == null) {
                return 0;
            }
            if (TextUtils.isEmpty(imgPro.id)) {
                return 0;
            }
            return 1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // TODO Auto-generated method stub
            //Log.w("","onBindViewHolder");
            MediaUtils.ImageProperty imgPro = datas.get(position);
            if (imgPro != null) {
                if (holder instanceof ItemViewHolder) {
                    ((ItemViewHolder) holder).bindData(imgPro);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup rootV, int viewType) {
            // TODO Auto-generated method stub
            if (viewType == 0) {
                return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.photo_preview_item_head, rootV, false));
            } else {
                return new ItemViewHolder(mLayoutInflater.inflate(R.layout.photo_preview_item, rootV, false));
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            //Log.w("", "onViewRecycled");
            if (holder instanceof ItemViewHolder) {
                View v = ((ItemViewHolder) holder).img;
                MediaUtils.ImageProperty imgPro = (MediaUtils.ImageProperty) v.getTag();
                v.setTag(null);
                mPresenter.cancelThumbnailBitmapGetting(mPhotoThumbnailGrid, imgPro);
                ((ItemViewHolder) holder).img.setImageBitmap(null);
            }
            super.onViewRecycled(holder);
        }

        final class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemV) {
                super(itemV);
                itemV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickCamera();
                    }
                });
            }
        }

        final class ItemViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            View cb;

            public ItemViewHolder(View itemV) {
                super(itemV);
                // TODO Auto-generated constructor stub
                img = (ImageView) itemV.findViewById(R.id.img);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                cb = itemV.findViewById(R.id.cb);

                if (mSelMode == SEL_MODE_SINGLE) {
                    cb.setVisibility(View.GONE);
                    itemV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final MediaUtils.ImageProperty imgPro = datas.get(getPosition());
                            onClickPhoto(imgPro);
                        }
                    });
                } else if (mSelMode == SEL_MODE_MULTI) {
                    itemV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MediaUtils.ImageProperty imgPro = datas.get(getPosition());
                            clickOnImgSelView(cb, imgPro, false);
                        }
                    });
                }

            }

            public void bindData(final MediaUtils.ImageProperty imgPro) {
                //Log.w("", "bindData");
                img.setTag(imgPro);

                Bitmap b = mPresenter.getThumbnailBitmap(mPhotoThumbnailGrid, imgPro);
                if (b != null) {
                    img.setImageBitmap(b);
                } else {
                    img.setImageResource(R.mipmap.ic_pig_rect);
                }

                if (mSelMode == SEL_MODE_MULTI) {
                    final boolean isSelected = mSelectedImgPros.contains(imgPro);
                    cb.setSelected(isSelected);
                }

            }// end bind data
        }


    }//end adapter

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // 派生类重载此方法
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 当点击拍照时被调用
     */
    protected void onClickCamera() {
        //Log.w("BasePhotoPickupActivity_v1","onClickCamera");
    }

    /**
     * 当点击一张图片是被调用
     *
     * @param imgPro 图片属性
     */
    protected void onClickPhoto(final MediaUtils.ImageProperty imgPro) {
        //Log.w("BasePhotoPickupActivity_v1","onClickPhoto");
        //imgPro.dump();
    }
}

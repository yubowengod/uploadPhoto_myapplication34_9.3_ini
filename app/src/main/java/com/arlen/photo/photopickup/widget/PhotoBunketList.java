package com.arlen.photo.photopickup.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlen.photo.R;
import com.arlen.photo.photopickup.util.DeferredHandler;
import com.arlen.photo.photopickup.util.MediaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public final class PhotoBunketList extends RelativeLayout {

    public static interface Callback{
        void onPrepareShow();
        void onHided();
        void onBunketSelected(String bunketName);
        Bitmap onGetThumbnailBitmap(MediaUtils.ImageProperty imgPro);
    }

    private final DeferredHandler mHandler = new DeferredHandler();
    private boolean mIsDoingAnimation;
    private View mRootBg;
    private RecyclerView mBunketList;
    private ItemAdapter mItemAdapter;
    private Callback mCallback;
    // 这个界面比较特殊，多个immageView可能会同时关联一个imgePro,因此要采用表格记录
    private final HashMap<MediaUtils.ImageProperty,HashSet<ImageView>> imgProMapImgView = new HashMap<MediaUtils.ImageProperty,HashSet<ImageView>>();

    public PhotoBunketList(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_photo_bunket_list, this);
    }

    @Override
    public final void onFinishInflate(){
        super.onFinishInflate();
        initViews();
    }

    private final void initViews() {
        final Context context = getContext();

        mRootBg = findViewById(R.id.root_layout);
        mRootBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide(null);
            }
        });

        mBunketList = (RecyclerView) findViewById(R.id.bunket_list);
        mBunketList.setLayoutManager(new LinearLayoutManager(context));
        mItemAdapter = new ItemAdapter();
        mBunketList.setAdapter(mItemAdapter);
        mBunketList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide(null);
            }
        });
    }

    public void setCallback(Callback cb){
        mCallback=cb;
    }




    public void setImageBitmap(MediaUtils.ImageProperty imgPro,Bitmap bm){
        HashSet<ImageView> vs = imgProMapImgView.get(imgPro);
        if( vs!=null){
            for(ImageView v:vs){
                v.setImageBitmap(bm);
            }
        }
    }

    public final void show(String name, ArrayList<Pair<String,ArrayList<MediaUtils.ImageProperty>>> d){
        if(isShowing()){
            return;
        }

        if(mCallback!=null){
            mCallback.onPrepareShow();
        }

        mIsDoingAnimation=true;

        setVisibility(VISIBLE);
        mRootBg.setVisibility(INVISIBLE);
        mBunketList.setVisibility(INVISIBLE);
        mItemAdapter.setDatas(name, d);

        mHandler.post(new Runnable() {
            @Override
            public final void run() {
                //初始化
                final Animation translateAnimation = new TranslateAnimation(
                        0,// X无改变
                        0,
                        mBunketList.getMeasuredHeight(),// 开始时，Y在最下面
                       0
                );
                //设置动画时间
                translateAnimation.setDuration(200);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mBunketList.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mIsDoingAnimation = false;
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                AlphaAnimation openAnim = new AlphaAnimation(0.01f, 1.0f);
                openAnim.setDuration(200);
                openAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mRootBg.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mBunketList.clearAnimation();
                        mBunketList.startAnimation(translateAnimation);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mRootBg.clearAnimation();
                mRootBg.startAnimation(openAnim);
            }
        });
    }

    public final void hide(){
        hide(null);
    }

    private final void hide(final String selBunketName){
        if(mIsDoingAnimation){
            return;
        }
        if(getVisibility()!=VISIBLE){
            return;
        }

        mIsDoingAnimation=true;

        mHandler.post(new Runnable() {
            @Override
            public final void run() {
                final AlphaAnimation closeAnim = new AlphaAnimation(1.0f, 0.0f);
                closeAnim.setDuration(200);
                closeAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mRootBg.setVisibility(View.INVISIBLE);
                        setVisibility(INVISIBLE);
                        mIsDoingAnimation=false;
                        if(mCallback!=null){
                            if(selBunketName!=null)mCallback.onBunketSelected(selBunketName);
                            mCallback.onHided();
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                //初始化
                final Animation translateAnimation = new TranslateAnimation(
                        0,// X无改变
                        0,
                        0,
                        mBunketList.getMeasuredHeight()// 结束时，Y在最下面
                );
                //设置动画时间
                translateAnimation.setDuration(200);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mBunketList.setVisibility(View.INVISIBLE);
                        mRootBg.clearAnimation();
                        mRootBg.startAnimation(closeAnim);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {	}
                });
                mBunketList.clearAnimation();
                mBunketList.startAnimation(translateAnimation);
            }
        });
    }

    public final boolean isShowing(){
        if(mIsDoingAnimation){
            return true;
        }
        return getVisibility()==VISIBLE;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////




    private final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        String curBunketName;
        ArrayList<Pair<String,ArrayList<MediaUtils.ImageProperty>>> datas;
        ItemAdapter() {
        }
        void setDatas(String name, ArrayList<Pair<String,ArrayList<MediaUtils.ImageProperty>>> d){
            curBunketName=name;
            datas=d;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            // TODO Auto-generated method stub
            if( datas==null){
                return 0;
            }
            return datas.size();
        }
        @Override
        public int getItemViewType(int position) {
            return 0;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // TODO Auto-generated method stub
            //Log.w("","onBindViewHolder");
            Pair<String,ArrayList<MediaUtils.ImageProperty>> data = datas.get(position);
            ((ItemViewHolder)holder).bindData(data);
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup rootV, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.bunket_item,rootV,false));
        }
        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder){
            //Log.w("", "onViewRecycled");
            final ImageView v = ((ItemViewHolder) holder).imgFirstThumbnail;
            final MediaUtils.ImageProperty imgPro = (MediaUtils.ImageProperty) v.getTag();
            v.setTag(null);
            final HashSet<ImageView> vs = imgProMapImgView.get(imgPro);
            if(vs!=null){
                vs.remove(v);
                if(vs.isEmpty())imgProMapImgView.remove(imgPro);
            }
            super.onViewRecycled(holder);
        }
        final class ItemViewHolder extends RecyclerView.ViewHolder{
            ImageView imgFirstThumbnail;
            TextView tvTitle;
            ImageView imgSel;
            public ItemViewHolder(View itemV) {
                super(itemV);
                // TODO Auto-generated constructor stub
                imgFirstThumbnail = (ImageView)itemV.findViewById(R.id.img_first_thumbnail);
                imgFirstThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tvTitle = (TextView) itemV.findViewById(R.id.tv_title);
                imgSel = (ImageView) itemV.findViewById(R.id.img_sel);
                itemV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mIsDoingAnimation){
                            return;
                        }
                        int pos = getPosition();
                        final String bunketName = datas.get(pos).first;
                        if( bunketName.equals(curBunketName)==false){
                            hide(bunketName);
                        }else{
                            hide(null);
                        }
                             }
                });
            }
            public void bindData(final Pair<String,ArrayList<MediaUtils.ImageProperty>> data){
                if(data==null){
                    return;
                }

                final String bunketName = data.first;
                if(bunketName==null){
                    return;
                }

                final ArrayList<MediaUtils.ImageProperty> imgPros = data.second;
                final String title = bunketName+"("+ String.valueOf(imgPros!=null?imgPros.size():0)+")";
                tvTitle.setText(title);

                if( imgPros!=null && imgPros.isEmpty()==false && imgPros.get(0)!=null) {
                    final MediaUtils.ImageProperty imgPro = imgPros.get(0);
                    imgFirstThumbnail.setTag(imgPro);
                    HashSet<ImageView> vs = imgProMapImgView.get(imgPro);
                    if(vs==null){
                        vs = new HashSet<ImageView>();
                        imgProMapImgView.put(imgPro,vs);
                    }
                    vs.add(imgFirstThumbnail);

                    Bitmap b = mCallback.onGetThumbnailBitmap(imgPro);
                    if (b != null) {
                        imgFirstThumbnail.setImageBitmap(b);
                    } else {
                        imgFirstThumbnail.setImageResource(R.mipmap.ic_pig_rect);
                    }
                }else{
                    imgFirstThumbnail.setImageResource(R.mipmap.ic_pig_rect);
                }

                if( bunketName.equals(curBunketName)){
                    imgSel.setVisibility(VISIBLE);
                }else{
                    imgSel.setVisibility(GONE);
                }
            }// end bind data
        }


    }//end adapter
}

package com.arlen.photo.photopickup.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.arlen.photo.R;
import com.arlen.photo.photopickup.util.DeferredHandler;
import com.arlen.photo.photopickup.util.ImageUtil;
import com.arlen.photo.photopickup.util.MediaUtils;

import java.util.ArrayList;


public class PhotoViewPager extends HackyViewPager {

    public interface Callback{
        void onPageSelected(int position, MediaUtils.ImageProperty imgPro);
        void onPageScrolled(int position, MediaUtils.ImageProperty imgPro);
        void onPageClicked(MediaUtils.ImageProperty imgPro);
        void onDismissed();
        void onPreShow(String title, int totalSize, int beginPos);
    }

    private Context context;
    private Callback mCallback;
    private final DeferredHandler mHandler = new DeferredHandler();
    private MyPagerAdapter myPagerAdapter;
    private String title;

    public PhotoViewPager(Context context) {
        this(context, null);
    }

    public PhotoViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
    }

    private final void initViews() {
        setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mCallback!=null){
                    mCallback.onPageScrolled(position,getImgPro(position));
                }
            }
            @Override
            public void onPageSelected(int position) {
                if(mCallback!=null){
                    mCallback.onPageSelected(position,getImgPro(position));
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onPageClicked(getCurImgPro());
                }
            }
        });
    }

    @Override
    public final void onFinishInflate(){
        super.onFinishInflate();
        initViews();
    }

    public void setImageBitmap(MediaUtils.ImageProperty imgPro,Bitmap bm){
        ImageView photoView = (ImageView)findViewWithTag(imgPro);
        if( photoView!=null){
            setImageBitmap(photoView, imgPro, bm);
        }
    }

    public String getTitle(){
        return title;
    }

    public int getPageSize(){
        if( myPagerAdapter==null){
            return -1;
        }
        return myPagerAdapter.getCount();
    }

    public MediaUtils.ImageProperty getCurImgPro(){
        return getImgPro(getCurrentItem());
    }

    public MediaUtils.ImageProperty getImgPro(int position){
        if( myPagerAdapter==null){
            return null;
        }
        if( myPagerAdapter.datas==null ||  myPagerAdapter.datas.isEmpty()){
            return null;
        }
        if(position<0||position>myPagerAdapter.datas.size()){
            return null;
        }
        return myPagerAdapter.datas.get(position);
    }

    public int removePage(MediaUtils.ImageProperty imgPro){
        if( myPagerAdapter==null){
            return -1;//
        }
        final ArrayList<MediaUtils.ImageProperty> newDatas = myPagerAdapter.datas;
        if( newDatas==null || newDatas.isEmpty()){
            return -1;
        }
        return removePage(newDatas.indexOf(imgPro));
    }

    public int removeCurPage(){
        return removePage(-1);
    }

    public int  removePage(int position){
        if( myPagerAdapter==null){
            return -1;//
        }
        final ArrayList<MediaUtils.ImageProperty> newDatas = myPagerAdapter.datas;
        if( newDatas==null || newDatas.isEmpty()){
            return -1;
        }
        if(position<0){
            position = getCurrentItem();
        }
        if( position<0 || position>=newDatas.size()){
            return -1;
        }
        newDatas.remove(position);
        myPagerAdapter.notifyDataSetChanged();// 记得不要漏了这句，否则会报异常，没有更新数据
        // 因为就算隐藏了，还是有可能会被测量和刷新
        if(newDatas.isEmpty()){
            hide();
            return -1;
        }
        // 一定要更换一个新的适配器，否则数据会落乱
        myPagerAdapter = new MyPagerAdapter();
        myPagerAdapter.setDatas(newDatas);
        setAdapter(myPagerAdapter);
        // 选中被删除页的前一页
        if(position>0){
            --position;
        }
        setCurrentItem(position, false);

        if(position==0){ // 如果本身已经是0位置，在此滚到0位置，VIEWPAGE不会自动发onPageSelect,自己手动补一个
            if(mCallback!=null){
                mCallback.onPageSelected(position,getImgPro(position));
            }
        }

        return position;
    }



    public void show(String t, ArrayList<MediaUtils.ImageProperty> datas, int pos){
        if( datas==null || datas.isEmpty()){
            return;
        }
        if( pos<0||pos>datas.size()){
            return;
        }

        title=t;

        // 必须每次使用新的适配器，否则数据会错乱
        myPagerAdapter = new MyPagerAdapter();
        myPagerAdapter.setDatas(datas);

        if (mCallback!=null){
            mCallback.onPreShow(t,datas.size(),pos);
        }

        setVisibility(VISIBLE);
        setAdapter(myPagerAdapter);
        setCurrentItem(pos, false);
    }

    public void hide(){
        setVisibility(INVISIBLE);
        if(mCallback!=null){
            mCallback.onDismissed();
        }
    }

    public boolean isShowing(){
        return getVisibility()==VISIBLE;
    }

    public void setCallback(Callback cb){
        mCallback=cb;
    }

    private void setImageBitmap(final ImageView photoView, MediaUtils.ImageProperty imgPro, Bitmap bm){

        // 参考微信的做法，对于旋转的图，。。。。
        if( bm.getWidth() < bm.getHeight()){
            photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        photoView.setImageBitmap(bm);

        // 针对之前没有显示的图，做个动画慢慢显示出来，避免闪烁
        // 这里要使用延时的事件，是考虑到setBitmap之后，需要绘制的时间
        if(photoView.getVisibility()!=VISIBLE){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    AlphaAnimation openAnim = new AlphaAnimation(0.01f, 1.0f);
                    openAnim.setDuration(500);
                    openAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            photoView.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    photoView.clearAnimation();
                    photoView.startAnimation(openAnim);
                }
            });
        }

    }

    //////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////

    private final class MyPagerAdapter extends PagerAdapter {
        ArrayList<MediaUtils.ImageProperty> datas;
        public void setDatas(ArrayList<MediaUtils.ImageProperty> d){
            datas=d;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            if(datas==null) {
                return 0;
            }
            return datas.size();
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView photoView =  new ImageView(container.getContext());
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPageClicked(getCurImgPro());
                }
            });
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // Bind data
            final MediaUtils.ImageProperty imgPro = datas.get(position);
//            photoView.setTag(imgPro);
            ImageUtil.load(context,imgPro.fullPath,photoView, R.mipmap.ic_pig_rect);
//            if(mCallback!=null){
//                Bitmap bm = mCallback.onGetLargeBitmap(imgPro, getMeasuredWidth(), getMeasuredHeight());
//                if(bm!=null){
//                    setImageBitmap(photoView,imgPro, bm);
//                }else { // 无法立即取到图片的，先设置为不可见
//                    photoView.setVisibility(INVISIBLE);
//                }
//            }
            return photoView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView photoView = (ImageView)object;
//            photoView.setTag(null);
            container.removeView(photoView);
            photoView.setImageDrawable(null);
            photoView.setImageBitmap(null);

//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                photoView.getViewTreeObserver().removeGlobalOnLayoutListener(photoView.getAttacher());
//            } else {
//                photoView.getViewTreeObserver().removeOnGlobalLayoutListener(photoView.getAttacher());
//            }
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.getTag() == ((View)object).getTag();
        }
    } // end nest class
}

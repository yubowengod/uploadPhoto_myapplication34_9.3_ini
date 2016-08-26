package com.arlen.photo.photopickup.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.arlen.photo.photopickup.util.DensityUtils;

import java.util.LinkedList;

/**
 * 
 * @author Arlen
 *
 */
public final class SimpleGrid extends ViewGroup {

    public interface Callback{
        View onCreateView(ViewGroup viewGroup, int position);
        //void onBindView(int position,View v);
        void onRemoveView(int position, View v);
    }

    private int mMaxItemPerRow=1;
    private float mItemMarginHor;
    private float mItemMarginVer;
    private Callback mCallback;
    private final LinkedList<ViewHolder> mViewHolders = new LinkedList<ViewHolder>();

    private final class ViewHolder{
        View v;
        int left;
        int top;
    }

    public SimpleGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCallback(Callback cb){
        mCallback=cb;
    }

    public void setItemMarginHor(float margin){
        mItemMarginHor=margin;
    }

    public void setItemMarginVer(float margin){
        mItemMarginVer=margin;
    }

    public void setMaxItemPerRow(int count){
        if(count<1){
            throw new IllegalStateException("FUCK!");
        }
        mMaxItemPerRow=count;
    }

    public void removeView(int position){
        if(position<0||position>=mViewHolders.size()){
            throw new IndexOutOfBoundsException("Fuck");
        }
        ViewHolder holder = mViewHolders.get(position);
        removeView(holder.v);
        if(mCallback!=null){
            mCallback.onRemoveView(position,holder.v);
        }
    }

    @Override
    public void removeAllViews(){
        super.removeAllViews();
        mViewHolders.clear();
    }

    public void createViews(int total){
        if(mCallback==null){
            return;
        }
        removeAllViews();
        for(int idx=0;idx<total;++idx){
            final int pos = idx;
            final View v = mCallback.onCreateView(this,pos);
            if(v!=null) {
                //mCallback.onBindView(pos, v);
                ViewHolder holder = new ViewHolder();
                holder.v = v;
                mViewHolders.add(holder);
                addView(v);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Context context = getContext();
        final int givenWidth = MeasureSpec.getSize(widthMeasureSpec);
        if( mViewHolders.isEmpty()){
            setMeasuredDimension(givenWidth,getPaddingTop()+getPaddingBottom());
            return;
        }

        final int contentWidth = givenWidth - getPaddingLeft() - getPaddingRight();
        final int hMargin = DensityUtils.dp2px(context,mItemMarginHor);
        final int vMargin = DensityUtils.dp2px(context,mItemMarginVer);


        int itemW = (contentWidth-(mMaxItemPerRow*((hMargin)*2)))/mMaxItemPerRow;
        if(itemW<0)itemW=0;
        final int itemH = itemW;

        final int firstItemLeftPerRow = getPaddingLeft() + ((contentWidth-((itemW+2*hMargin)*mMaxItemPerRow))/2) + hMargin;
        int itemTop = getPaddingTop() + vMargin;
        int itemLeft = firstItemLeftPerRow;
        int itemCountPerRow=0;
        for(ViewHolder holder:mViewHolders){
            final View v = holder.v;
            final int wSpec = MeasureSpec.makeMeasureSpec(itemW, MeasureSpec.EXACTLY);
            final int hSpec = MeasureSpec.makeMeasureSpec(itemH, MeasureSpec.EXACTLY);
            v.measure(wSpec,hSpec);
            holder.left = itemLeft;
            holder.top = itemTop;
            itemCountPerRow++;
            if(itemCountPerRow==mMaxItemPerRow){
                itemLeft = firstItemLeftPerRow;
                itemTop += itemH + 2*vMargin;
                itemCountPerRow=0;
            }else{
                itemLeft += itemW + 2*hMargin;
            }
        }

        int rowCount = mViewHolders.size()/mMaxItemPerRow;
        if( (mViewHolders.size() % mMaxItemPerRow )>0)rowCount++;
        setMeasuredDimension(givenWidth,rowCount*(itemH+2*vMargin)+getPaddingTop()+getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(ViewHolder holder:mViewHolders) {
            final View v = holder.v;
            v.layout(holder.left,holder.top,holder.left+v.getMeasuredWidth(),holder.top+v.getMeasuredHeight());
        }
    }

}

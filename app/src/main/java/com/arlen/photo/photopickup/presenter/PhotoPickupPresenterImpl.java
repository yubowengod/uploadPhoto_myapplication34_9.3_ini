package com.arlen.photo.photopickup.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.arlen.photo.photopickup.util.BitmapUtil;
import com.arlen.photo.photopickup.util.DeferredHandler;
import com.arlen.photo.photopickup.util.FileSizeUtil;
import com.arlen.photo.photopickup.util.MediaUtils;
import com.arlen.photo.photopickup.view.IPhotoPickupView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public final class PhotoPickupPresenterImpl implements IPhotoPickupPresenter {
    private static final boolean DEBUG=false;
    private final Context mContext;
    private ArrayList<Pair<String,ArrayList<MediaUtils.ImageProperty>>> mImgProsGroup= new ArrayList<Pair<String,ArrayList<MediaUtils.ImageProperty>>>(256);
    private final Object mDataLock = new Object();
    private final DeferredHandler mHandler = new DeferredHandler();
    private final LruCache<String, Bitmap> mCache = BitmapUtil.createBitmapCache();
    private BitmapLoader mBmLoader;
    private IPhotoPickupView mViewCallback;
    private boolean mIsDestroyed;
    private final int mThreadPoolSize;
    private static final String DEF_BUNKET_NAME = "所有图片";

    public PhotoPickupPresenterImpl(Context context, int threadPoolSize){
        mContext=context;
        mThreadPoolSize=threadPoolSize;
    }

    public PhotoPickupPresenterImpl(Context contex){
        this(contex,1);
    }

    @Override
    public void onCreate() {
        mBmLoader = new BitmapLoader(mThreadPoolSize);
    }
    @Override
    public void onDestroy() {
        mIsDestroyed=true;
        synchronized (mDataLock){
            mImgProsGroup.clear();
            mCache.evictAll();
        }
        mBmLoader.setCanceled();
    }

    public void setCallback(IPhotoPickupView cb){
        mViewCallback=cb;
    }

    public void refresh(final HashSet<String> selImgIds){
        new Thread(){
            public final void run(){

                final HashMap<String,MediaUtils.ImageProperty> selResult = new HashMap<String,MediaUtils.ImageProperty>();
                synchronized (mDataLock) {
                    mImgProsGroup.clear();

                    final ArrayList<MediaUtils.ImageProperty> imgProperties = MediaUtils.listAllImage(mContext,selImgIds,selResult);
                    if(imgProperties!=null) {
                        ArrayList<MediaUtils.ImageProperty> allImgPros = new ArrayList<MediaUtils.ImageProperty>(imgProperties.size());
                        HashMap<String,ArrayList<MediaUtils.ImageProperty>> bunketMap = new HashMap<String,ArrayList<MediaUtils.ImageProperty>>();
                        for (MediaUtils.ImageProperty imgPro : imgProperties) {
                            final String bunketName = imgPro.bunketName;
                            if(TextUtils.isEmpty(bunketName)){
                                continue;//fuck!!
                            }
                            ArrayList<MediaUtils.ImageProperty> bunketImgPros = bunketMap.get(bunketName);
                            if(bunketImgPros==null){
                                bunketImgPros = new ArrayList<MediaUtils.ImageProperty>(256);
                                bunketMap.put(bunketName,bunketImgPros);
                            }

                            if(isFaceImg(imgPro)==false) {
                                allImgPros.add(imgPro);
                                bunketImgPros.add(imgPro);
                            }
                        }
                        mImgProsGroup.add(new Pair<>(DEF_BUNKET_NAME,allImgPros));
                        ArrayList<String> bunketNames = new ArrayList<String>(bunketMap.keySet());
                        Collections.sort(bunketNames);
                        for(final String bunketName:bunketNames){
                            mImgProsGroup.add(new Pair<>(bunketName,bunketMap.get(bunketName)));
                        }
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mIsDestroyed||mViewCallback==null){
                            return;
                        }
                        mViewCallback.onRefreshComplete(DEF_BUNKET_NAME,selResult);
                    }
                });
            }
        }.start();
    }

    /**
     * 过滤特殊机型空相册里面的特殊封面图
     * @param imgPro
     * @return
     */
    private static boolean isFaceImg(MediaUtils.ImageProperty imgPro){
        // This is for fucking coolpai
        return imgPro.displayName!=null && imgPro.displayName.indexOf("cover_for_new_album")>=0;
    }

    @Override
    public ArrayList<MediaUtils.ImageProperty> getImgProperties(String bunketName) {
        for(Pair<String,ArrayList<MediaUtils.ImageProperty>> p:mImgProsGroup){
            if(p.first.equals(bunketName)){
                return p.second;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Pair<String,ArrayList<MediaUtils.ImageProperty>>> getImgProsGroup(){
        return mImgProsGroup;
    }

    public boolean isDefBunketName(String bunketName){
        return bunketName.equals(DEF_BUNKET_NAME);
    }

    @Override
    public void cancelThumbnailBitmapGetting(Object reqKey, MediaUtils.ImageProperty imgPro) {
        mBmLoader.cancelTask(reqKey,imgPro);
    }

    @Override
    public void cancelLargeBitmapGetting(Object reqKey, MediaUtils.ImageProperty imgPro) {

    }

    @Override
    public Bitmap getThumbnailBitmap(Object taskKey, MediaUtils.ImageProperty imgPro) {
        //synchronized (mDataLock)
        {
            Bitmap bm = mCache.get(getThumbnailBitmapKey(imgPro));
            if(bm!=null){
                return bm;
            }
            LoadBitmapTask task = new LoadThumbnailBitmapTask(taskKey,imgPro);
            mBmLoader.addTask(task);
            return null;
        }
    }

    @Override
    public Bitmap getLargeBitmap(Object taskKey, MediaUtils.ImageProperty imgPro, int targetWidth, int targetHeight) {
        //synchronized (mDataLock)
        {
            Bitmap bm = mCache.get(getLargeBitmapKey(imgPro));
            if(bm!=null){
                return bm;
            }
            bm = mCache.get(getThumbnailBitmapKey(imgPro));
            LoadBitmapTask task = new LoadLargeBitmapTask(taskKey,imgPro,targetWidth,targetHeight);
            mBmLoader.addTask(task);
            return bm;
        }
    }

    private static String getThumbnailBitmapKey(MediaUtils.ImageProperty imgPro){
        if(imgPro==null || TextUtils.isEmpty(imgPro.fullPath)){
            return null;
        }
        return "T_"+imgPro.fullPath;
    }
    private static String getLargeBitmapKey(MediaUtils.ImageProperty imgPro){
        if(imgPro==null || TextUtils.isEmpty(imgPro.fullPath)){
            return null;
        }
        return "L_"+imgPro.fullPath;
    }


    private abstract class LoadBitmapTask{
        protected final Object taskKey;
        protected final MediaUtils.ImageProperty imgPro;
        boolean isCanceled;
        protected LoadBitmapTask(Object key, MediaUtils.ImageProperty imgPro){
            this.taskKey=key;
            this.imgPro=imgPro;
        }
        protected abstract String getBitmapKey();
        protected abstract Bitmap loadBitmap();
        protected abstract void onBitmapReady(Bitmap bm);
        final void doTask() {
            final Bitmap bitmap = this.loadBitmap();
            if (bitmap != null) {
                {
                    mCache.put(getBitmapKey(),bitmap);
                }
                imgPro.state=1;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if( mIsDestroyed || mViewCallback==null){
                            return;
                        }
                        onBitmapReady(bitmap);
                    }
                });

            }else{
                imgPro.state=-1;
            }
        }
    }

    private final class LoadThumbnailBitmapTask extends LoadBitmapTask {
        LoadThumbnailBitmapTask(Object key, MediaUtils.ImageProperty imgPro){
            super(key,imgPro);
        }
        @Override
        protected String getBitmapKey() {
            return getThumbnailBitmapKey(imgPro);
        }
        @Override
        protected Bitmap loadBitmap() {
            if (DEBUG){
                Log.w("", " -------------- fetch thumbnail begin ----------------");
                imgPro.dump();
            }
            return BitmapUtil.getThumbnailFinal(mContext,imgPro.id,imgPro.ori);
        }
        @Override
        protected void onBitmapReady(Bitmap bm) {
            mViewCallback.onThumbnailBitmapReady(taskKey, imgPro, bm);
        }
    }// end task

    private final class LoadLargeBitmapTask extends LoadBitmapTask {
        final int targetWidth;
        final int targetHeight;
        LoadLargeBitmapTask(Object key, MediaUtils.ImageProperty imgPro, int tw, int th) {
            super(key,imgPro);
            this.targetWidth=tw;
            this.targetHeight=th;
        }
        @Override
        protected String getBitmapKey() {
            return getLargeBitmapKey(imgPro);
        }
        @Override
        protected Bitmap loadBitmap() {
            if (DEBUG){
                Log.w("", " -------------- fetch large("+targetWidth+","+targetHeight+")begin ----------------");
                imgPro.dump();
            }
            return FileSizeUtil.compressBitmap(imgPro.fullPath, targetWidth, targetHeight);
        }
        @Override
        protected void onBitmapReady(Bitmap bm) {
            mViewCallback.onLargeBitmapReady(taskKey, imgPro, bm);
        }
    }// end class

    private final class BitmapLoader{
        private final Object queueLock = new Object();
        private final LinkedList<LoadBitmapTask> queue = new LinkedList<>();
        private ArrayList<Thread> threadPool = new ArrayList<>(3);
        private boolean isCanceled;
        BitmapLoader(int threadPoolSize){

            for(int i=0;i<threadPoolSize;++i){
                Thread workerThread = new WorkerThread();
                workerThread.start();
                threadPool.add(workerThread);
            }
        }
        private void notifyAllWorkerThread(){
            synchronized (BitmapLoader.this){
                BitmapLoader.this.notifyAll();
            }
        }
        void setCanceled(){
            synchronized (queueLock) {
                isCanceled = true;
                notifyAllWorkerThread();
            }
        }
        void addTask(LoadBitmapTask task){
            synchronized (queueLock){
                queue.addLast(task);
                notifyAllWorkerThread();
            }
        }
        void cancelTask(Object taskKey, MediaUtils.ImageProperty imgPro){
            synchronized (queueLock){
                for(LoadBitmapTask task:queue){
                    if(task.taskKey==taskKey && task.imgPro==imgPro ){
                        task.isCanceled=true;
                    }
                }
            }
        }
        final class WorkerThread extends Thread {
            @Override
            public final void run() {

                while (true) {
                    LoadBitmapTask task = null;
                    synchronized (queueLock) {
                        if (isCanceled) {
                            Log.w("BitmapLoader", "is canceled, worker thread quit now, threadid:" + getId());
                            return; // The only one exit point
                        }
                        if (queue.isEmpty()) {
                            task = null;
                        } else {
                            task = queue.removeFirst();
                        }
                    }

                    if (task != null) {
                        if (task.isCanceled == false) {
                            task.doTask();
                        }
                    } else {
                        if (DEBUG) {
                            Log.w("BitmapLoader", "queue is empty. worker thread wait.... threadid:"+getId());
                        }
                        synchronized (BitmapLoader.this) {
                            try {
                                BitmapLoader.this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (DEBUG) {
                            Log.w("BitmapLoader", "worker thread wake up, threadid:"+getId());
                        }
                    }

                    //continue;
                }//end while
            }//end run
        }//end workerThread

    }// end loader class
}

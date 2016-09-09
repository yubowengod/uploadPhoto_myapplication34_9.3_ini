package com.arlen.photo.ImageCachceUitl_package;

/**
 * Created by GOD on 2016/9/8.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;


/**
 * 图片的三级缓存工具类{日后项目需要}
 * @author double 江
 *
 */
public class ImageCachceUitl {
    public static final int SUCCSEE = 0;
    public static final int FAIL = 1;
    private Context context;
    private LruCache<String, Bitmap> cache;//lru算法集合，string是图片的url，bitmap为图片的值类型
    private File cacheDir;
    Handler handler;
    private ExecutorService executorService;//维护线性池
    public ImageCachceUitl(Context context ,Handler handler){
        this.context=context;
        this.handler=handler;
        cacheDir=context.getCacheDir();//获得cache文件夹
        //维护几个网络线程下载图片
        executorService=Executors.newFixedThreadPool(5);
        int maxSize=(int) (Runtime.getRuntime().maxMemory()/8);//获得运行环境的内存大小的1/8
        cache=new LruCache<String, Bitmap>(maxSize){
            // TODO 每存储一张图片的大小（作用于内存溢出丢图片）
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回当前一行所占的字节数*高度，就是图片的大小
                return value.getRowBytes()*value.getHeight();
            }
        };//当前图片缓存总数的大小
    }
    /**
     *
     * @param url 下载图片的连接
     * @param position 需要显示图片的imgeView的Tag
     * @return
     */
    public Bitmap getBitmapFromUrl(String url,int position){
        //1内存中获取图片LRU算法
        Bitmap bitmap=cache.get(url);
        //内存中有指定图片
        if (bitmap!=null) {
            Log.i("从内存中获得图片", "从内存中获得图片"+url);
            return bitmap;
        }
        //2文件中获取图片
        bitmap=getBitmapFromFile(url);
        if (bitmap!=null) {
            Log.i("从文件中获得图片", "从文件中获得图片"+url);
            return bitmap;
        }
        //3开启网络下载
        Log.i("从网络中获得图片", "从网络中获得图片"+url);
        getBitmapFromNet(url,position);
        return null;

    }

    /**
     * 网络获取图片
     * @param url 图片的；链接地址
     * @param position 需要显示的imageview的tag
     */
    private void getBitmapFromNet(String url, int position) {

        //开启任务
        executorService.execute(new RunnableTask(url,position));
    }
    //任务接口
    class RunnableTask implements Runnable{
        String imageUrl;
        int position;
        private HttpURLConnection httpURLConnection;
        public RunnableTask(String imageUrl, int position) {
            this.position=position;
            this.imageUrl=imageUrl;
        }

        @Override
        public void run() {
            //子线程的操作，开启网络下载图片
            try {
                URL url=new URL(imageUrl);
                //请求对象
                httpURLConnection=(HttpURLConnection) url.openConnection();
                //网络请求的方式
                httpURLConnection.setRequestMethod("GET");
                //超时的时间，
                httpURLConnection.setConnectTimeout(5000);
                //读取超时的时间
                httpURLConnection.setReadTimeout(5000);
                //httpURLConnection.getResponseCode()拿到最新数据
                if (httpURLConnection.getResponseCode()==200) {
                    //success get data from net;get tape
                    InputStream inputStream=httpURLConnection.getInputStream();
                    //将流转化成bitmap图片
                    Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
                    //利用handler机制放入主线程中显示
                    Message msg=new Message();
                    //需要在主线程中显示的图片msg.obj
                    msg.obj=bitmap;
                    msg.arg1=position;
                    //为msg设置标记
                    msg.what=SUCCSEE;
                    handler.sendMessage(msg);
                    //一，将下载完后的图片保存到内存中
                    cache.put(imageUrl, bitmap);
                    //二，将下载完后的图片保存到文件中
                    writeToLoce(imageUrl,bitmap);
                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            //关闭请求
            finally{
                //断开服务器
                if (httpURLConnection!=null) {
                    httpURLConnection.disconnect();
                }
            }
            //发送一个空消息
            handler.obtainMessage(FAIL).sendToTarget();
        }
    }
    /**
     * 图片写入cache文件夹下面的操作
     * @param imageUrl
     * @param bitmap
     */
    private void writeToLoce(String imageUrl, Bitmap bitmap) {
        try {
            String bitmapefilename=MD5Encoder.encode(imageUrl).substring(10);
            Log.i("bitmapefilename", bitmapefilename);
            File file=new File(cacheDir, bitmapefilename);
            FileOutputStream fileOutputStream =new FileOutputStream(file);
            //写入文件的操作(1图片类型2图片质量当为100时表示不压缩3文件流)
            bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 读取文件中图片的操作
     * @param url 图片的连接地址
     * @return
     */
    private Bitmap getBitmapFromFile(String url) {
        try {
            //使用Md5工具加密截取前10个字符串
            String bitmapefilename=MD5Encoder.encode(url).substring(10);
            /**
             * 在cache文件夹下面找到指定文件
             * dir cache文件存储路径
             * name 文件名称
             */
            File file=new File(cacheDir, bitmapefilename);
            //  file.mkdir();
            //FileInputStream fileInputStream=new FileInputStream(file);
            Bitmap bitmap=BitmapFactory.decodeFile(file.getPath());//完整文件路径
            //Log.i("文件路径", file.getPath().toString());
            //2读取之后放入内存,提高效率
            cache.put(url, bitmap);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
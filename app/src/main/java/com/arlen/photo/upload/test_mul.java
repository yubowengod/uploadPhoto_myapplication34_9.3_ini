package com.arlen.photo.upload;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.arlen.photo.photopickup.presenter.PhotoPresenter;
import com.arlen.photo.photopickup.util.MediaUtils;
import com.arlen.photo.ui.MainActivity;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by GOD on 2016/8/31.
 */


public class test_mul {

    private static SoapObject request;

    private Button upload;
    private Button upload1;

    private ImageView image;
    private static final String SERVICE_NAMESPACE ="http://tempuri.org/"; //http://tempuri.org/
//    public String SERVICE_NAMESPACE = "http://tempuri.org/";
    //    private static String SERVICE_URL = "http://192.168.1.102:8011/Service1.asmx";
    private String methodName = "FileUploadImage ";   //设置方法名  FileUploadImage
    private SoapObject result;
    private ListView listView;
    private MainActivity activity;
    List<String> List_result;
    String insetinfo_result;


    private Handler handler; //设置消息，通知主线程进行相关操作

    String Cname=null;  //webservice 需要的参数
    String Cnum=null;
    String FileUploadImage_str=null;


    public static PhotoPresenter mPhotoPresenter1;


    public  static void getImageromSdk( ArrayList<String> pic_path){



        Log.i("进入获取图片方法", "进入获取图片方法");

        String[] strArray=null;

        try{
            String srcUrl = "/storage/sdcard0/"; //路径

//            mPhotoPresenter1.mSelectedImgPros.get(0).fullPath.toString();
//
//            String fileName1 = "qwe.png";  //文件名
//            String fileName2 = "qwer.png";//文件名
//            String fileName3 = "qwert.png";
//            String fileName4 = "qwerty.png";
//            String fileName5 = "qwertyu.png";
//
//            List<String>imageList=new ArrayList<>();//定义一个list，里面装2个图片地址，模拟批量上传
//            imageList.add(fileName1);
//            imageList.add(fileName2);
//            imageList.add(fileName3);
//            imageList.add(fileName4);
//            imageList.add(fileName5);
//            imageList.add(fileName5);
//            imageList.add(fileName5);


            for (int i = 0; i < pic_path.size(); i++) {
                String ww=pic_path.get(i);
                FileInputStream fis = new FileInputStream(ww);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int count = 0;
                while((count = fis.read(buffer)) >= 0){
                    baos.write(buffer, 0, count);
                }

                String uploadBuffer = new String(Base64.encode(baos.toByteArray()));
                String methodName = "FileUploadImage";
                getImageFromAndroid(methodName,pic_path.get(i), uploadBuffer);   //调用webservice
                Log.i("connectWebService", "start");
                fis.close();
//                System.gc();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getImageFromAndroid(String methodName,String arg1, String uploadBuffer){
        Log.i("进入端口方法", "进入端口方法");
//        String methodName = "FileUploadImage";
        // 创建HttpTransportSE传输对象
        HttpTransportSE ht = new HttpTransportSE(Data_up.getSERVICE_URL());
        try {
            ht.debug = true;
            // 使用SOAP1.1协议创建Envelop对象
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // 实例化SoapObject对象
            SoapObject soapObject = new SoapObject(SERVICE_NAMESPACE,methodName);

            soapObject.addProperty("title","");
            soapObject.addProperty("contect","");
            soapObject.addProperty("bytestr",uploadBuffer);
            envelope.bodyOut = soapObject;
            // 设置与.NET提供的webservice保持较好的兼容性
            envelope.dotNet = true;

            // 调用webservice
            ht.call(SERVICE_NAMESPACE + methodName, envelope);

            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息
                SoapObject result = (SoapObject) envelope.bodyIn;
                String resuly_back ;
                resuly_back = result.getProperty(0).toString();//true
//                 insetinfo_result;= resuly_back;
                Log.i("进入端口方法", resuly_back);
            }
        } catch (SoapFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    };

//test
}

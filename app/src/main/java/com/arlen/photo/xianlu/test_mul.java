package com.arlen.photo.xianlu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.arlen.photo.upload.Data_up;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by GOD on 2016/9/7.
 */
public class test_mul {

    public ImageView imageView;
    public static String List_result ;


    public static void getImageromSdk(String pic_name){

        try{
            String methodName = "ImgToBase64String";
            getImageFromAndroid(methodName,pic_name);   //调用webservice
            Log.i("connectWebService", "start");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getImageFromAndroid(String methodName,String pic_name){
        Log.i("进入端口方法", "进入端口方法");
        // 创建HttpTransportSE传输对象
        HttpTransportSE ht = new HttpTransportSE(Data_up.getSERVICE_URL());
        try {
            ht.debug = true;
            // 使用SOAP1.1协议创建Envelop对象
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // 实例化SoapObject对象
            SoapObject soapObject = new SoapObject(Data_up.getSERVICE_NAMESPACE(),methodName);

            soapObject.addProperty("Imagefilename",pic_name);

            envelope.bodyOut = soapObject;
            // 设置与.NET提供的webservice保持较好的兼容性
            envelope.dotNet = true;

            // 调用webservice
            ht.call(Data_up.getSERVICE_NAMESPACE() + methodName, envelope);

            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息
                SoapObject result = (SoapObject) envelope.bodyIn;
                String resuly_back ;
                resuly_back = result.getProperty(0).toString();//true
                List_result = resuly_back;
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

    public static Bitmap onDecodeClicked(String string) {
        byte[] decode = android.util.Base64.decode(string, android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitmap;
    }

    public void setimageView(ImageView imageView) {
        this.imageView = imageView;
        imageView.setImageBitmap(onDecodeClicked(List_result));
    }
}

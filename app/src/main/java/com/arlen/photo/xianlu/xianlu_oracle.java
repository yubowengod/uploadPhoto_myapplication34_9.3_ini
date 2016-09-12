package com.arlen.photo.xianlu;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


import com.arlen.photo.upload.Data_up;

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


public class xianlu_oracle {

    public ImageView imageView;
    public static List<String> List_result ;


    public static void getImageromSdk(){

        try{
            String methodName = "selectAllZAIZHUANGXIANLU";
            getImageFromAndroid(methodName,"111");   //调用webservice
            Log.i("connectWebService", "start");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getImageFromAndroid(String methodName,String downloadBuffer){
        Log.i("进入端口方法", "进入端口方法");
        // 创建HttpTransportSE传输对象
        HttpTransportSE ht = new HttpTransportSE(Data_up.getSERVICE_URL());
        try {
            ht.debug = true;
            // 使用SOAP1.1协议创建Envelop对象
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // 实例化SoapObject对象
            SoapObject soapObject = new SoapObject(Data_up.getSERVICE_NAMESPACE(),methodName);

//            soapObject.addProperty("Imagefilename","111");

            envelope.bodyOut = soapObject;
            // 设置与.NET提供的webservice保持较好的兼容性
            envelope.dotNet = true;

            // 调用webservice
            ht.call(Data_up.getSERVICE_NAMESPACE() + methodName, envelope);

            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息
                SoapObject result = (SoapObject) envelope.bodyIn;
                SoapObject detail = (SoapObject) result.getProperty(methodName + "Result");
                // 解析服务器响应的SOAP消息
                List_result=parseProvinceOrCity(detail);
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

    public static List<String> parseProvinceOrCity(SoapObject detail) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 2; i < detail.getPropertyCount(); i=i+5) {
            // 解析出每个省份
            result.add(detail.getProperty(i).toString().split(",")[0]);
        }
        return result;
    }

    public static List<String> getList_result(){

        return List_result;
    }



}

package com.arlen.photo.upload;
/**
 * Created by GOD on 2016/8/30.
 */

public class Data_up {
//    private static String a ="feiyangxiaomi";

//    private static String SERVICE_URL_IP_PORT_webnnn = "http://192.168.1.110:8011/webnnn/";

    private static String SERVICE_URL_IP_PORT_webnnn = "http://192.168.155.1:8011/webnnn/";

    public static String getSERVICE_URL_IP_PORT_webnnn() {
        return SERVICE_URL_IP_PORT_webnnn;
    }

//    private static String SERVICE_URL = "http://192.168.1.110:8011/Service1.asmx";

    private static String SERVICE_URL = "http://192.168.155.1:8011/Service1.asmx";

    public static String getSERVICE_URL() {
        return SERVICE_URL;
    }

    public static void setA(String SERVICE_URL) {
        Data_up.SERVICE_URL = SERVICE_URL;
    }

    private static String SERVICE_NAMESPACE ="http://tempuri.org/"; //http://tempuri.org/

    public static String getSERVICE_NAMESPACE() {
        return SERVICE_NAMESPACE;
    }

}

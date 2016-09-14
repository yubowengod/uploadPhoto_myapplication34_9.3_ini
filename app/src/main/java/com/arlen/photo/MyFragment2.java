package com.arlen.photo;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arlen.photo.SpinnerAdapter.spinner_gongwei_oracle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by God on 2016/8/17.
 */
public class MyFragment2 extends Fragment {

    private class SpinnerAdapter extends ArrayAdapter<String> {
        Context context;
        String[] items = new String[]{};

        public SpinnerAdapter(final Context context,
                              final int textViewResourceId, final String[] objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.context = context;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(
                        android.R.layout.simple_spinner_item, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(items[position]);
            tv.setGravity(Gravity.CENTER);
//            tv.setTextColor(Color.BLUE);
            tv.setTextSize(20);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(
                        android.R.layout.simple_spinner_item, parent, false);
            }

            // android.R.id.text1 is default text view in resource of the android.
            // android.R.layout.simple_spinner_item is default layout in resources of android.

            TextView tv = (TextView) convertView
                    .findViewById(android.R.id.text1);
            tv.setText(items[position]);
            tv.setGravity(Gravity.CENTER);
//            tv.setTextColor(Color.BLUE);
            tv.setTextSize(20);
            return convertView;
        }
    }

    private String content;
    public MyFragment2(String content) {
        this.content = content;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_content2,container,false);
        TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        txt_content.setText(content);
        return view;
    }

    public static String[] my_upload_pic = {
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
    };

    public static String[] my_upload_pic_1 = {
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
    };
    public static String[] my_upload_pic_2 = {
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
    };
    public static String[] my_upload_pic_3 = {
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
            "http://192.168.1.110:8011/webnnn/2016951115_8_910.jpg",
    };

    private GridView gridview;

    private Spinner spinner_gongwei;
    private Spinner spinner_gongxu;
    private Spinner spinner_xiangdian;


    private String [] my_sp_gw = null;

    ArrayAdapter<String> gwAdapter_my = null;  //省级适配器

    private Handler mainHandler_sp = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 2201) {
                //只要在主线程就可以处理ui
                ((ImageView) getActivity().findViewById(msg.arg1)).setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    private ExecutorService executorService;
    private List<String> urlList = new ArrayList<String>();
    ArrayAdapter<String> gongwei_Adapter = null;  //省级适配器
//    private String[] gongwei = null;
    private String[] gongwei =  new String[] {"1工位",	"2工位",	"3工位",	"4工位"};

    private void ini_spinner(){

        spinner_gongwei = (Spinner) getActivity().findViewById(R.id.spinner_gongwei);
        spinner_gongxu = (Spinner) getActivity().findViewById(R.id.spinner_gongxu);
        spinner_xiangdian = (Spinner) getActivity().findViewById(R.id.spinner_xiangdian);




        executorService.submit(new Runnable() {
            @Override
            public void run() {
                spinner_gongwei_oracle.getImageromSdk();
                gongwei=new String[spinner_gongwei_oracle.getList_result().size()];
                for(int i=0;i<spinner_gongwei_oracle.getList_result().size();i++){
                    gongwei[i]=spinner_gongwei_oracle.getList_result().get(i);
                }
                int www = spinner_gongwei_oracle.getList_result().size();


                SpinnerAdapter provinceAdapter1=new SpinnerAdapter(getActivity(),android.R.layout.simple_spinner_item,gongwei);
                gongwei_Adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, gongwei);
//                spinner_gongwei.setAdapter(provinceAdapter1);
//                spinner_gongwei.setSelection(0,true);  //设置默认选中项，此处为默认选中第4个值
//

            }
        });
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        executorService = Executors.newFixedThreadPool(5);

        ini_spinner();







        gridview = (GridView) getActivity().findViewById(R.id.gridview_fg_my);

        gridview.setAdapter(new MyFragment2_gridview_fg_my_ImageListAdapter(getActivity(),my_upload_pic));

        Button btn1 = (Button) getActivity().findViewById(R.id.btn_1);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridview.setAdapter(new MyFragment2_gridview_fg_my_ImageListAdapter(getActivity(),my_upload_pic_1));
            }
        });

        Button btn2 = (Button) getActivity().findViewById(R.id.btn_2);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridview.setAdapter(new MyFragment2_gridview_fg_my_ImageListAdapter(getActivity(),my_upload_pic_2));
            }
        });

        Button btn3 = (Button) getActivity().findViewById(R.id.btn_3);

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridview.setAdapter(new MyFragment2_gridview_fg_my_ImageListAdapter(getActivity(),my_upload_pic_3));
            }
        });
    }


}

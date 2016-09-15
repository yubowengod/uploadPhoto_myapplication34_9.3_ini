package com.arlen.photo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arlen.photo.SpinnerAdapter.select_spinner_xiangdian;
import com.arlen.photo.SpinnerAdapter.spinner_gongwei_oracle;
import com.arlen.photo.SpinnerAdapter.spinner_gongxu_oracle;
import com.arlen.photo.ui.MainActivity;
import com.arlen.photo.upload.Data_up;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by God on 2016/8/19.
 */
public class crm_main_activity extends Activity {

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


    private TextView sp_text;

    private Spinner provinceSpinner = null;  //省级（省、直辖市）
    private Spinner citySpinner = null;     //地级市
    private Spinner countySpinner = null;    //县级（区、县、县级市）
    ArrayAdapter<String> provinceAdapter = null;  //省级适配器
    ArrayAdapter<String> gongweiAdapter = null;  //省级适配器
    ArrayAdapter<String> cityAdapter = null;    //地级适配器
    ArrayAdapter<String> countyAdapter = null;    //县级适配器
    static int provincePosition = 3;

    private TextView txt_crm_home;
    private TextView txt_crm_reset_xianlu;
    private TextView txt_crm_reset_yemian;
    private TextView txt_crm_next;

    private TextView text_chexing;
    private TextView text_chehao;
    private TextView text_zaizhuangxianlu;
//    private TextView text_zaizhuangxianlu_num;

    String str;
    String chetileibie;
    private int chetileibie_num;
    String chetileibie1;
    private int chetileibie1_num;

    private void setSelected(){
        txt_crm_home.setSelected(false);
        txt_crm_reset_xianlu.setSelected(false);
        txt_crm_reset_yemian.setSelected(false);
        txt_crm_next.setSelected(false);
    }

    MainActivity_login mainActivity_login;
//    private ExecutorService executorService;
//    private List<String> urlList = new ArrayList<String>();
//    private String [] gongwei = null;
//    private String [] [] gongxu = new String[50][50];
//    private String [] [] [] xiangdian = new String[50][50][50];
//
//    private Handler mainHandler_sp_gw = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.handleMessage(msg);
//            if (msg.what == 2221) {
//                //只要在主线程就可以处理ui
//                ((ImageView) crm_main_activity.this.findViewById(msg.arg1)).setImageBitmap((Bitmap) msg.obj);
//            }
//        }
//    };
//
//    private void ini_spinner(){
//
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//
//                spinner_gongwei_oracle.getImageromSdk();
//
//                gongwei = new String[spinner_gongwei_oracle.getList_result().size()];
//
//                for(int i=0;i<spinner_gongwei_oracle.getList_result().size();i++)
//                {
//                    gongwei[i]=spinner_gongwei_oracle.getList_result().get(i);
//
//                    spinner_gongxu_oracle.getImageromSdk(gongwei[i]);
//
//                    for (int j = 0;j<spinner_gongxu_oracle.getList_result().size();j++)
//                    {
//                        gongxu[i][j]=spinner_gongxu_oracle.getList_result().get(j);
//
//                        select_spinner_xiangdian.getImageromSdk(gongwei[i],gongxu[i][j]);
//
//                        for (int k = 0;k<select_spinner_xiangdian.getList_result().size();k++)
//                        {
//                            xiangdian[i][j][k] = select_spinner_xiangdian.getList_result().get(k);
//                        }
//                        select_spinner_xiangdian.getList_result().clear();
//
//                    }
//                    spinner_gongxu_oracle.getList_result().clear();
//
//                }
//
//                mainHandler_sp_gw.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        setSpinner();
//                    }
//                });
//            }
//        });
//    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crm_main);

        provinceSpinner = (Spinner)findViewById(R.id.spinner_gongwei);
        citySpinner = (Spinner)findViewById(R.id.spinner_gongxu);
        countySpinner = (Spinner)findViewById(R.id.spinner_xiangdian);

//        executorService = Executors.newFixedThreadPool(5);

        Bundle bundle = this.getIntent().getExtras();

        String xianlu_up_chehao = bundle.getString("chehao");
        String xianlu_up_chexing = bundle.getString("chexing");
        String xianlu_up_zaizhuangxianlu = bundle.getString("zaizhuangxianlu");
        String xianlu_up_zaizhuangxianlu_num = bundle.getString("zaizhuangxianlu_num");

        text_chehao = (TextView) findViewById(R.id.text_chehao);
        text_chexing = (TextView) findViewById(R.id.text_chexing);
        text_zaizhuangxianlu = (TextView) findViewById(R.id.crm_main_zaizhuangxianlu);
//        text_zaizhuangxianlu_num = (TextView) findViewById(R.id.crm_main_zaizhuangxianlu_num);
        text_chehao.setText(xianlu_up_chehao);
        text_chexing.setText(xianlu_up_chexing);
        text_zaizhuangxianlu.setText(xianlu_up_zaizhuangxianlu);
//        text_zaizhuangxianlu_num.setText(xianlu_up_zaizhuangxianlu_num);

//        ini_spinner();
        setSpinner();

        sp_text = (TextView) findViewById(R.id.sp_text);

        txt_crm_home = (TextView) findViewById(R.id.txt_crm_home);
        txt_crm_reset_xianlu = (TextView) findViewById(R.id.txt_crm_reset_xianlu);
        txt_crm_reset_yemian = (TextView) findViewById(R.id.txt_crm_reset_yemian);
        txt_crm_next = (TextView) findViewById(R.id.txt_crm_next);

        setSelected();

        txt_crm_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected();
                txt_crm_home.setSelected(false);
                txt_crm_home.setSelected(true);
                Intent intent = new Intent(crm_main_activity.this,MainActivity_login.class);
                startActivity(intent);
                finish();
            }
        });

        txt_crm_reset_xianlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected();
                txt_crm_reset_xianlu.setSelected(false);
                txt_crm_reset_xianlu.setSelected(true);
                Intent intent = new Intent(crm_main_activity.this,xianlu_main_activity.class);
                startActivity(intent);
                finish();
            }
        });

        txt_crm_reset_yemian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected();
                txt_crm_reset_yemian.setSelected(false);
                txt_crm_reset_yemian.setSelected(true);
                setSpinner();
            }
        });
        txt_crm_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected();
                txt_crm_next.setSelected(false);
                txt_crm_next.setSelected(true);
                Intent intent = new Intent(crm_main_activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    * 设置下拉框
    */
    private void setSpinner()
    {

        //绑定适配器和值
        SpinnerAdapter provinceAdapter=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,mainActivity_login.gongwei);
        provinceSpinner.setAdapter(provinceAdapter);
        provinceSpinner.setSelection(4,true);  //设置默认选中项，此处为默认选中第4个值

        SpinnerAdapter cityAdapter=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,mainActivity_login.gongxu[4]);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setSelection(0,true);  //默认选中第0个

        final SpinnerAdapter countyAdapter=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,mainActivity_login.xiangdian[4][1]);
        countySpinner.setAdapter(countyAdapter);
        countySpinner.setSelection(0, true);

//        bingo

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            // 表示选项被改变的时候触发此方法，主要实现办法：动态改变地级适配器的绑定值
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                //position为当前省级选中的值的序号

                //将地级适配器的值改变为city[position]中的值
                SpinnerAdapter cityAdapter=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,
                        mainActivity_login.gongxu[position]);
                // 设置二级下拉列表的选项内容适配器
                citySpinner.setAdapter(cityAdapter);
                provincePosition = position;    //记录当前省级序号，留给下面修改县级适配器时用
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });


        //地级下拉监听
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                SpinnerAdapter countyAdapter=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,
                        mainActivity_login.xiangdian[provincePosition][position]);
                countySpinner.setAdapter(countyAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_text.setText(countySpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }










    //    Integer.parseInt(str);
    private void showDialogtc(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(crm_main_activity.this);
        builder.setTitle("消息").setIcon(android.R.drawable.stat_notify_error);
        builder.setMessage("tc，请改正");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        dialog = builder.create();
        dialog.show();
    }
    private void showDialogmp(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(crm_main_activity.this);
        builder.setTitle("消息").setIcon(android.R.drawable.stat_notify_error);
        builder.setMessage("mp，请改正");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        dialog = builder.create();
        dialog.show();
    }
    private void showDialogm(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(crm_main_activity.this);
        builder.setTitle("消息").setIcon(android.R.drawable.stat_notify_error);
        builder.setMessage("m，请改正");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        dialog = builder.create();
        dialog.show();
    }

}

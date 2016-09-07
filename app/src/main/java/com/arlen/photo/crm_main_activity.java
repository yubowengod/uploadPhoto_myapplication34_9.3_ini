package com.arlen.photo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arlen.photo.ui.MainActivity;

import org.w3c.dom.Text;

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
    ArrayAdapter<String> cityAdapter = null;    //地级适配器
    ArrayAdapter<String> countyAdapter = null;    //县级适配器
        static int provincePosition = 3;


    //省级选项值
    private String[] province = new String[] {"1工位",	"2工位",	"3工位",	"4工位"};

//    private String[] province_m = new String[] {"1工位-m",	"2工位",	"3工位",	"4工位"};
//    private String[] province_mp = new String[] {"1工位-mp",	"2工位",	"3工位",	"4工位"};
//    private String[] province_tc = new String[] {"1工位-tc",	"2工位",	"3工位",	"4工位"};

    //地级选项值
    private String[][] city = new String[][]
            {
                    { "1工位1工序",	"1工位2工序",	"1工位3工序",	"1工位4工序",	"1工位5工序", 	"1工位6工序" },
                    { "2工位1工序",	"2工位2工序",	"2工位3工序"  },
                    { "3工位1工序",	"3工位2工序",	"3工位3工序",	"3工位4工序"  },
                    { "4工位1工序",	"4工位2工序",	"4工位3工序",	"4工位4工序",	"4工位5工序"    }// ,"珠海","汕头","佛山","湛江","肇庆","江门","茂名","惠州","梅州","汕尾","河源","阳江","清远","东莞","中山","潮州","揭阳","云浮"
            };
    //县级选项值
    private String[][][] county = new String[][][]
            {
                    {   //北京
                            {"1工位1工序1项点",
                                    "1工位1工序2项点",
                                    "1工位1工序3项点",
                                    "1工位1工序4项点",
                                    "1工位1工序5项点",
                                    "1工位1工序6项点",
                                    "1工位1工序7项点",
                                    "1工位1工序8项点",
                                    "1工位1工序9项点",
                                    "1工位1工序10项点",
                                    "1工位1工序11项点",
                                    "1工位1工序12项点",
                                    "1工位1工序13项点"},

                            {"1工位2工序1项点",
                                    "1工位2工序2项点",
                                    "1工位2工序3项点",
                                    "1工位2工序4项点",
                                    "1工位2工序5项点",
                                    "1工位2工序6项点",
                                    "1工位2工序7项点",
                                    "1工位2工序8项点",
                                    "1工位2工序9项点",
                                    "1工位2工序10项点",
                                    "1工位2工序11项点",
                                    "1工位2工序12项点",
                                    "1工位2工序13项点",
                                    "1工位2工序14项点"
                            },
                            {"1工位3工序1项点",
                                    "1工位3工序2项点",
                                    "1工位3工序3项点",
                                    "1工位3工序4项点",
                                    "1工位3工序5项点",
                                    "1工位3工序6项点",
                                    "1工位3工序7项点",
                                    "1工位3工序8项点",
                                    "1工位3工序9项点",
                                    "1工位3工序10项点",
                                    "1工位3工序11项点",
                                    "1工位3工序12项点",
                                    "1工位3工序13项点",
                                    "1工位3工序14项点",
                                    "1工位3工序15项点",
                                    "1工位3工序16项点"                        },
                            {"1工位4工序1项点",
                                    "1工位4工序2项点",
                                    "1工位4工序3项点",
                                    "1工位4工序4项点",
                                    "1工位4工序5项点",
                                    "1工位4工序6项点",
                                    "1工位4工序7项点",
                                    "1工位4工序8项点",
                                    "1工位4工序9项点"
                            },
                            {"1工位5工序1项点",
                                    "1工位5工序2项点",
                                    "1工位5工序3项点",
                                    "1工位5工序4项点",
                                    "1工位5工序5项点",
                                    "1工位5工序6项点",
                                    "1工位5工序7项点",
                                    "1工位5工序8项点",
                                    "1工位5工序9项点",
                                    "1工位5工序10项点",
                                    "1工位5工序11项点",
                                    "1工位5工序12项点"
                            },
                            {"1工位6工序1项点",
                                    "1工位6工序2项点",
                                    "1工位6工序3项点",
                                    "1工位6工序4项点",
                                    "1工位6工序5项点",
                                    "1工位6工序6项点",
                                    "1工位6工序7项点",
                                    "1工位6工序8项点",
                                    "1工位6工序9项点"
                            }
                    },
                    {    //上海
                            {"2工位1工序1项点",	"2工位1工序2项点",	"2工位1工序3项点",	"2工位1工序4项点",	"2工位1工序5项点",	"2工位1工序6项点"},
                            {"2工位2工序1项点",	"2工位2工序2项点",	"2工位2工序3项点",	"2工位2工序4项点",	"2工位2工序5项点",	"2工位2工序6项点",	"2工位2工序7项点"},
                            {"2工位3工序1项点",	"2工位3工序2项点",	"2工位3工序3项点",	"2工位3工序4项点",	"2工位3工序5项点",	"2工位3工序6项点",	"2工位3工序7项点",	"2工位3工序8项点",	"2工位3工序9项点",	"2工位3工序10项点"}


                    },
                    {    //天津
                            {"无"},{"无"},{"无"},{"无"}
                    },
                    {    //广东
                            {"4工位1工序1项点",	"4工位1工序2项点",	"4工位1工序3项点",	"4工位1工序4项点",	"4工位1工序5项点"},
                            {"4工位2工序1项点",	"4工位2工序2项点",	"4工位2工序3项点",	"4工位2工序4项点",	"4工位2工序5项点",	"4工位2工序6项点"},
                            {"4工位3工序1项点",	"4工位3工序2项点",	"4工位3工序3项点",	"4工位3工序4项点"},
                            {"4工位4工序1项点",	"4工位4工序2项点",	"4工位4工序3项点",	"4工位4工序4项点",	"4工位4工序5项点",	"4工位4工序6项点",	"4工位4工序7项点",	"4工位4工序8项点"},
                            {"4工位5工序1项点",	"4工位5工序2项点",	"4工位5工序3项点",	"4工位5工序4项点",	"4工位5工序5项点",	"4工位5工序6项点",	"4工位5工序7项点",	"4工位5工序8项点",	"4工位5工序9项点",	"4工位5工序10项点",	"4工位5工序11项点"}

                    }
            };





    private TextView txt_crm_home;
    private TextView txt_crm_reset_xianlu;
    private TextView txt_crm_reset_yemian;
    private TextView txt_crm_next;

    private TextView text_chexing;
    private TextView text_chehao;
    private TextView text_zaizhuangxianlu;
    private TextView text_zaizhuangxianlu_num;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crm_main);
//        Intent intent=getIntent();
//        String stringValue=intent.getStringExtra("extra");

        Bundle bundle = this.getIntent().getExtras();


        String xianlu_up_chehao = bundle.getString("chehao");
        String xianlu_up_chexing = bundle.getString("chexing");
        String xianlu_up_zaizhuangxianlu = bundle.getString("zaizhuangxianlu");
        String xianlu_up_zaizhuangxianlu_num = bundle.getString("zaizhuangxianlu_num");


        text_chehao = (TextView) findViewById(R.id.text_chehao);
        text_chexing = (TextView) findViewById(R.id.text_chexing);
        text_zaizhuangxianlu = (TextView) findViewById(R.id.crm_main_zaizhuangxianlu);
        text_zaizhuangxianlu_num = (TextView) findViewById(R.id.crm_main_zaizhuangxianlu_num);
        text_chehao.setText(xianlu_up_chehao);
        text_chexing.setText(xianlu_up_chexing);
        text_zaizhuangxianlu.setText("线路："+xianlu_up_zaizhuangxianlu);
        text_zaizhuangxianlu_num.setText(xianlu_up_zaizhuangxianlu_num);



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
        provinceSpinner = (Spinner)findViewById(R.id.spinner_gongwei);
        citySpinner = (Spinner)findViewById(R.id.spinner_gongxu);
        countySpinner = (Spinner)findViewById(R.id.spinner_xiangdian);

        //绑定适配器和值
        SpinnerAdapter provinceAdapter1=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,province);
        provinceAdapter = new ArrayAdapter<String>(crm_main_activity.this,android.R.layout.simple_spinner_item, province);
        provinceSpinner.setAdapter(provinceAdapter1);
        provinceSpinner.setSelection(0,true);  //设置默认选中项，此处为默认选中第4个值

        SpinnerAdapter cityAdapter1=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,city[0]);
//        cityAdapter = new ArrayAdapter<String>(sanji_activity.this,
//                android.R.layout.simple_spinner_item, city[3]);
        citySpinner.setAdapter(cityAdapter1);
        citySpinner.setSelection(0,true);  //默认选中第0个

        final SpinnerAdapter countyAdapter1=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,county[0][0]);
//        countyAdapter = new ArrayAdapter<String>(sanji_activity.this,
//                android.R.layout.simple_spinner_item, county[3][0]);
        countySpinner.setAdapter(countyAdapter1);
        countySpinner.setSelection(0, true);


        //省级下拉框监听
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            // 表示选项被改变的时候触发此方法，主要实现办法：动态改变地级适配器的绑定值
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                //position为当前省级选中的值的序号

                //将地级适配器的值改变为city[position]中的值
                SpinnerAdapter cityAdapter=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,city[position]);
//                cityAdapter = new ArrayAdapter<String>(
//                        sanji_activity.this, android.R.layout.simple_spinner_item, city[position]);
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
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3)
            {
                SpinnerAdapter countyAdapter=new SpinnerAdapter(crm_main_activity.this,android.R.layout.simple_spinner_item,county[provincePosition][position]);
//                countyAdapter = new ArrayAdapter<String>(sanji_activity.this,
//                        android.R.layout.simple_spinner_item, county[provincePosition][position]);
                countySpinner.setAdapter(countyAdapter);



            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

//        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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

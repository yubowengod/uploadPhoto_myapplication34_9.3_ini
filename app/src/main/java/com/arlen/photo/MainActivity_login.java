package com.arlen.photo;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.arlen.photo.SpinnerAdapter.select_spinner_xiangdian;
import com.arlen.photo.SpinnerAdapter.spinner_gongwei_oracle;
import com.arlen.photo.SpinnerAdapter.spinner_gongxu_oracle;
import com.arlen.photo.xianlu.xianlu_oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity_login extends Activity implements View.OnClickListener{


    //UI Object
    private TextView txt_topbar;
    private TextView txt_channel;
    private TextView txt_message;
    private TextView txt_better;
    private TextView txt_setting;
    private FrameLayout ly_content;

    //Fragment Object
    private MyFragment fg1;
    private MyFragment1 fg2;
    private MyFragment2 fg3;
    private MyFragment3 fg4;
    private NewContentFragment fg5;


    private FragmentManager fManager;


    private TextView txt_title;
    private FrameLayout fl_content;
    private Context mContext;
    private ArrayList<Data> datas = null;
    private FragmentManager fManager1 = null;
    private long exitTime = 0;

    private ExecutorService executorService;
    private List<String> urlList = new ArrayList<String>();
    public static String [] gongwei = null;
    public static String [] [] gongxu = new String[50][50];
    public static String [] [] [] xiangdian = new String[50][50][50];




    private void ini_spinner(){

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                xianlu_oracle.getImageromSdk();

                spinner_gongwei_oracle.getImageromSdk();

                gongwei = new String[spinner_gongwei_oracle.getList_result().size()];

                for(int i=0;i<spinner_gongwei_oracle.getList_result().size();i++)
                {
                    gongwei[i]=spinner_gongwei_oracle.getList_result().get(i);

                    spinner_gongxu_oracle.getImageromSdk(gongwei[i]);

                    for (int j = 0;j<spinner_gongxu_oracle.getList_result().size();j++)
                    {
                        gongxu[i][j]=spinner_gongxu_oracle.getList_result().get(j);

                        select_spinner_xiangdian.getImageromSdk(gongwei[i],gongxu[i][j]);

                        for (int k = 0;k<select_spinner_xiangdian.getList_result().size();k++)
                        {
                            xiangdian[i][j][k] = select_spinner_xiangdian.getList_result().get(k);
                        }
                        select_spinner_xiangdian.getList_result().clear();

                    }
                    spinner_gongxu_oracle.getList_result().clear();

                }
            }
        });
    }
    static int ww = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        executorService = Executors.newFixedThreadPool(5);
        ini_spinner();

        fManager = getFragmentManager();
        bindViews();
        txt_channel.performClick();   //模拟一次点击，既进去后选择第一项
    }

    //UI组件初始化与事件绑定
    private void bindViews() {
        txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_channel = (TextView) findViewById(R.id.txt_channel);
        txt_message = (TextView) findViewById(R.id.txt_message);
        txt_better = (TextView) findViewById(R.id.txt_better);
        txt_setting = (TextView) findViewById(R.id.txt_setting);
        ly_content = (FrameLayout) findViewById(R.id.ly_content);

        txt_channel.setOnClickListener(this);
        txt_message.setOnClickListener(this);
        txt_better.setOnClickListener(this);
        txt_setting.setOnClickListener(this);
    }

    //重置所有文本的选中状态
    private void setSelected(){
        txt_channel.setSelected(false);
        txt_message.setSelected(false);
        txt_better.setSelected(false);
        txt_setting.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fg1 != null)fragmentTransaction.hide(fg1);
        if(fg2 != null)fragmentTransaction.hide(fg2);
        if(fg3 != null)fragmentTransaction.hide(fg3);
        if(fg4 != null)fragmentTransaction.hide(fg4);
        if(fg5 != null)fragmentTransaction.hide(fg5);

    }


    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (v.getId()){
            case R.id.txt_channel:
                setSelected();
                txt_channel.setSelected(true);
                if(fg1 == null){
                    fg1 = new MyFragment("功能列表");
                    fTransaction.add(R.id.ly_content,fg1);
                }else{
                    fTransaction.show(fg1);
                }
                break;
            case R.id.txt_message:
                setSelected();
                txt_message.setSelected(true);
                if(fg2 == null){
                    fg2 = new MyFragment1("信息列表");
                    fTransaction.add(R.id.ly_content,fg2);
                }else{
                    fTransaction.show(fg2);
                }
                break;
            case R.id.txt_better:
                setSelected();
                txt_better.setSelected(true);
                if(fg3 == null){
                    fg3 = new MyFragment2("历史列表");
                    fTransaction.add(R.id.ly_content,fg3);
                }else{
                    fTransaction.show(fg3);
                }
                break;
            case R.id.txt_setting:
                setSelected();
                txt_setting.setSelected(true);
                if(fg4 == null){
                    fg4 = new MyFragment3("设置");
                    fTransaction.add(R.id.ly_content,fg4);
                }else{
                    fTransaction.show(fg4);
                }
                break;
        }
        fTransaction.commit();
    }
}

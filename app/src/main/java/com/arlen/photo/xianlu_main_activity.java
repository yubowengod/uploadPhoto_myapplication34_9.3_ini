package com.arlen.photo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.arlen.photo.xianlu.Fruit;
import com.arlen.photo.xianlu.FruitAdapter;
import com.arlen.photo.xianlu.listview_text;
import com.arlen.photo.xianlu.test_mul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by God on 2016/8/18.
 */
public class xianlu_main_activity extends Activity {



    private TextView txt_xianlu_home;
    private TextView txt_xianlu_back;
    private String xianluname_str;
    private String xianlunum_str;
//????????????????????????

    private Button button1;
    private String chehao;
    private String chexiang;
    private AlertDialog selfdialog;
//????????????????????

//    ???
private List<Fruit> fruitList = new ArrayList<Fruit>();
private ExecutorService executorService;
private Button btn;
    private ImageView imageView1,imageView2;
    Bitmap bm;
    ArrayList<Bitmap> bm_array = new ArrayList<Bitmap>();



    private TextView textView;
    public Handler handler;
    public String result;
    public String result_insetinfo;

    private test_mul test_mul_1;
    private listview_text listview_text_1;

    private List<String> main_List_result;

    private Handler mainHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == 2012){
                //只要在主线程就可以处理ui
                ((ImageView)xianlu_main_activity.this.findViewById(msg.arg1)).setImageBitmap((Bitmap) msg.obj);
            }
        }


    };
//    ???

    private List<xianlu_main_xianlu> xianlu_list = new ArrayList<xianlu_main_xianlu>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xianlu_main);
        handler = new Handler();
        executorService = Executors.newFixedThreadPool(5);
        listview_download();
        txt_xianlu_home = (TextView) findViewById(R.id.txt_xianlu_home);
        txt_xianlu_back = (TextView) findViewById(R.id.txt_xianlu_back);
        txt_xianlu_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_xianlu_home.setSelected(false);
                txt_xianlu_home.setSelected(true);
                finish();
            }
        });
        txt_xianlu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_xianlu_back.setSelected(false);
                txt_xianlu_back.setSelected(true);
                finish();
            }
        });



    }


    private void listview_download(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {

                listview_text_1.getImageromSdk();

                for(int i = 0;i<listview_text_1.getList_result().size();i++)
                {
                    test_mul_1.getImageromSdk(listview_text_1.getList_result().get(i).toString());

                    bm_array.add(test_mul_1.onDecodeClicked(test_mul_1.List_result));
                }

                try {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

//                            ((ImageView)MainActivity.this.findViewById(R.id.imageview)).setImageBitmap(bm);
//                            ((ImageView)MainActivity.this.findViewById(R.id.imageview1)).setImageBitmap(bm);

                            ArrayList<Fruit> fruit_array = new ArrayList<Fruit>();

                            for(int i = 0;i<listview_text_1.getList_result().size();i++){
                                fruit_array.add(new Fruit(listview_text_1.getList_result().get(i).toString(),bm_array.get(i)));
                                fruitList.add(fruit_array.get(i));
                            }
                            FruitAdapter adapter = new FruitAdapter(xianlu_main_activity.this,
                                    R.layout.xianlu_main_pic_tittle, fruitList);
                            ListView listView = (ListView) findViewById(R.id.xianlu_main_xianlu_listview);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                                    HashMap<String,String> map = (HashMap<String,String>) myListView.getItemAtPosition(position);
//                                    xianluname_str = map.get("xianlu_name");
//                                    xianlunum_str = map.get("xianlu_num");
//                                    Toast.makeText(getApplicationContext(),
//                                            "你选择了第"+position+"个Item，itemTitle的值是："+xianluname_str+"itemContent的值是:"+xianlunum_str,
//                                            Toast.LENGTH_SHORT).show();
                                    Fruit fruit = fruitList.get(position);
                                    Toast.makeText(xianlu_main_activity.this, fruit.getName(),Toast.LENGTH_SHORT).show();
                                    xianluname_str = fruit.getName().toString();

                                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                    view = inflater.inflate(R.layout.xianlupopup, null);

                                    final TextView pop_chehao = (EditText)view.findViewById(R.id.pop_chehao);
                                    final TextView pop_chexiang = (EditText)view.findViewById(R.id.pop_chexiang);

                                    AlertDialog.Builder ad =new AlertDialog.Builder(xianlu_main_activity.this);
                                    ad.setView(view);
                                    ad.setTitle("检查信息");
                                    selfdialog =ad.create();
                                    selfdialog.setButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //获取输入框的用户名密码

                                            chehao = pop_chehao.getText().toString();
                                            chexiang = pop_chexiang.getText().toString();

                                            if(chehao.equals("")||chexiang.equals("")){
                                                showDialog();
                                            }
                                            else {
                                                Intent intent = new Intent(xianlu_main_activity.this,crm_main_activity.class);

                                                Bundle bundle = new Bundle();
                                                bundle.putString("zaizhuangxianlu",xianluname_str);
                                                bundle.putString("zaizhuangxianlu_num",xianlunum_str);
                                                bundle.putString("chehao",chehao+"0"+chexiang);
                                                bundle.putString("chexiang",chexiang);

                                                if(chexiang.equals("1")||chexiang.equals("6"))
                                                {
                                                    bundle.putString("chexing", "tc");
                                                }
                                                else if(chexiang.equals("2")||chexiang.equals("5"))
                                                {
                                                    bundle.putString("chexing", "mp");
                                                }
                                                else if(chexiang.equals("3")||chexiang.equals("4"))
                                                {
                                                    bundle.putString("chexing", "m");
                                                }
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                dialog.cancel();
                                            }
                                        }
                                    });
                                    selfdialog.setButton2("取消", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            selfdialog.cancel();
                                        }
                                    });
                                    selfdialog.show();

                                }
                            });

                        }
                    });
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }

    private void showDialog(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(xianlu_main_activity.this);
        builder.setTitle("错误").setIcon(android.R.drawable.stat_notify_error);
        builder.setMessage("请输入车号及车厢！！！");
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

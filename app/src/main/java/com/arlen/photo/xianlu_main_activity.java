package com.arlen.photo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by God on 2016/8/18.
 */
public class xianlu_main_activity extends Activity {


//        private String[] data = { "1号线",
//                "2号线",
//                "3号线",
//                "4号线",
//                "5号线",
//                "6号线",
//                "7号线",
//                "8号线",
//                "9号线"
//
//        };
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
    private List<xianlu_main_xianlu> xianlu_list = new ArrayList<xianlu_main_xianlu>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xianlu_main);
//        inixianlu();
//        xianlu_main_xianluadapter adapter = new xianlu_main_xianluadapter(xianlu_main_activity.this,R.layout.xianlu_main_listview_item,xianlu_list);
//        ListView listView = (ListView) findViewById(R.id.xianlu_main_xianlu_listview);
//        listView.setAdapter(adapter);
        final ListView myListView = (ListView) findViewById(R.id.xianlu_main_xianlu_listview);
        ArrayList<HashMap<String,String>> myArrayList = new ArrayList<HashMap<String,String>>();

        for(int i=40;i>=1;i--){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("xianlu_name", "线路 "+i);
            map.put("xianlu_num", "车体数量 "+i);
            myArrayList.add(map);
        }

        //生成SimpleAdapter适配器对象
        SimpleAdapter mySimpleAdapter=new SimpleAdapter(this,
                myArrayList,//数据源
                R.layout.xianlu_main_listview_item,//ListView内部数据展示形式的布局文件listitem.xml
                new String[]{"xianlu_name","xianlu_num"},//HashMap中的两个key值 itemTitle和itemContent
                new int[]{R.id.xianlu_name,R.id.xianlu_num});/*布局文件listitem.xml中组件的id
                                                            布局文件的各组件分别映射到HashMap的各元素上，完成适配*/

        myListView.setAdapter(mySimpleAdapter);
        //添加点击事件
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> map = (HashMap<String,String>) myListView.getItemAtPosition(position);
                xianluname_str = map.get("xianlu_name");
                xianlunum_str = map.get("xianlu_num");
                Toast.makeText(getApplicationContext(),
                        "你选择了第"+position+"个Item，itemTitle的值是："+xianluname_str+"itemContent的值是:"+xianlunum_str,
                        Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(xianlu_main_activity.this,crm_main_activity.class);
//
//                intent.putExtra("extra", xianluname);
//
//                startActivity(intent);
//
//                finish();

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
//                            intent.putExtra("extra", "tc");
                            }
                            else if(chexiang.equals("2")||chexiang.equals("5"))
                            {
                                bundle.putString("chexing", "mp");
//                            intent.putExtra("extra", "mp");
                            }
                            else if(chexiang.equals("3")||chexiang.equals("4"))
                            {
                                bundle.putString("chexing", "m");
//                            intent.putExtra("extra", "m");
                            }
//                        Toast.makeText(xianlu_main_activity.this,xianluname_str+"+"+xianlunum_str+"+"+chehao,Toast.LENGTH_LONG).show();
                            intent.putExtras(bundle);
                            startActivity(intent);
                            dialog.cancel();
                            finish();
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

//        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
//                xianlu_main_activity.this,android.R.layout.simple_list_item_1,data
//        );
//        ListView listView = (ListView) findViewById(R.id.xianlu_main_xianlu_listview);
//        listView.setAdapter(adapter);

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
    private void inixianlu()
    {
        xianlu_main_xianlu no1 	= new xianlu_main_xianlu(" 上海13号线"  );	xianlu_list.add(no1);
        xianlu_main_xianlu no2	= new xianlu_main_xianlu(" 上海14号线"  );	xianlu_list.add(no2);
        xianlu_main_xianlu no3	= new xianlu_main_xianlu(" 上海15号线"  );	xianlu_list.add(no3);
        xianlu_main_xianlu no4	= new xianlu_main_xianlu(" 上海16号线"  );	xianlu_list.add(no4);
        xianlu_main_xianlu no5	= new xianlu_main_xianlu(" 上海17号线"  );	xianlu_list.add(no5);
        xianlu_main_xianlu no6	= new xianlu_main_xianlu(" 上海18号线"  );	xianlu_list.add(no6);
        xianlu_main_xianlu no7	= new xianlu_main_xianlu(" 上海19号线"  );	xianlu_list.add(no7);
        xianlu_main_xianlu no8	= new xianlu_main_xianlu(" 上海20号线"  );	xianlu_list.add(no8);
        xianlu_main_xianlu no9	= new xianlu_main_xianlu(" 上海21号线"  );	xianlu_list.add(no9);
        xianlu_main_xianlu no10	= new xianlu_main_xianlu(" 上海22号线"  );	xianlu_list.add(no10);
        xianlu_main_xianlu no11	= new xianlu_main_xianlu(" 上海23号线"  );	xianlu_list.add(no11);
        xianlu_main_xianlu no12	= new xianlu_main_xianlu(" 上海24号线"  );	xianlu_list.add(no12);
        xianlu_main_xianlu no13	= new xianlu_main_xianlu(" 上海25号线"  );	xianlu_list.add(no13);
        xianlu_main_xianlu no14	= new xianlu_main_xianlu(" 上海26号线"  );	xianlu_list.add(no14);
        xianlu_main_xianlu no15	= new xianlu_main_xianlu(" 上海27号线"  );	xianlu_list.add(no15);
        xianlu_main_xianlu no16	= new xianlu_main_xianlu(" 上海28号线"  );	xianlu_list.add(no16);
        xianlu_main_xianlu no17	= new xianlu_main_xianlu(" 上海29号线"  );	xianlu_list.add(no17);
    }
}

package com.arlen.photo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlen.photo.ImageCachceUitl_package.ImageCachceUitl;
import com.arlen.photo.upload.Data_up;
import com.arlen.photo.xianlu.xianlu_oracle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by God on 2016/8/18.
 */
public class xianlu_main_activity extends Activity {


    ////////////////////////////9-9
    private xianlu_oracle listview_xianlu_oracle;
    private ImageCachceUitl imageCachceUitl;
    private List<String> urlList = new ArrayList<String>();
    private ListView listView;
    private Handler handler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case ImageCachceUitl.SUCCSEE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    int psition = msg.arg1;
                    //通过TAg加载当前的limageview
                    ImageView imageView = (ImageView) listView.findViewWithTag(psition);
                    if (null != bitmap && null != imageView) {
                        imageView.setImageBitmap(bitmap);
                    }
                    break;
                case ImageCachceUitl.FAIL:
                    Toast.makeText(getApplicationContext(), "下载错误", Toast.LENGTH_LONG).show();
                default:
                    break;
            }
        }        ;
    };
    private ExecutorService executorService;
////////////////////////////9-9

    private TextView txt_xianlu_home;
    private TextView txt_xianlu_back;
    private String xianluname_str;
    private String xianlunum_str;
    private String chehao;
    private String chexiang;
    private AlertDialog selfdialog;
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 2012) {
                //只要在主线程就可以处理ui
                ((ImageView) xianlu_main_activity.this.findViewById(msg.arg1)).setImageBitmap((Bitmap) msg.obj);
            }
        }
    };
    private int MIN_MARK = 1;
    private int MAX_MARK = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xianlu_main);

        listView = (ListView) findViewById(R.id.xianlu_main_xianlu_listview);
        imageCachceUitl = new ImageCachceUitl(getApplicationContext(), handler1);

        urlList.add("http://ww4.sinaimg.cn/large/90bd89ffjw1eqvmd6o8r6j20go0p5ju2.jpg");
        urlList.add("http://ww4.sinaimg.cn/large/90bd89ffjw1eqvmd6o8r6j20go0p5ju2.jpg");

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

    private void listview_download() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {

                listview_xianlu_oracle.getImageromSdk();

                listview_xianlu_oracle.getList_result().size();

                for (int i = 0; i < listview_xianlu_oracle.getList_result().size(); i++) {
//                    urlList.add(listview_xianlu_oracle.getList_result().get(i).toString());
                    urlList.add(Data_up.getSERVICE_URL_IP_PORT_webnnn()+listview_xianlu_oracle.getList_result().get(i).toString()+".jpg");
                }

                try {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(new myListAdapt());
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    xianluname_str = urlList.get(position).toString();

                                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                    view = inflater.inflate(R.layout.xianlupopup, null);

                                    final TextView pop_chehao = (EditText) view.findViewById(R.id.pop_chehao);
                                    final TextView pop_chexiang = (EditText) view.findViewById(R.id.pop_chexiang);

                                    AlertDialog.Builder ad = new AlertDialog.Builder(xianlu_main_activity.this);
                                    ad.setView(view);

                                    pop_chexiang.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
                                            System.out.println("-1-beforeTextChanged-->"
                                                    + pop_chexiang.getText().toString() + "<--");
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
                                            System.out.println("-1-onTextChanged-->"
                                                    + pop_chexiang.getText().toString() + "<--");

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (s != null && !s.equals("")) {
                                                if (MIN_MARK != -1 && MAX_MARK != -1) {
                                                    int markVal = 0;
                                                    try {
                                                        markVal = Integer.parseInt(s.toString());
                                                    } catch (NumberFormatException e) {
                                                        markVal = 0;
                                                    }
                                                    if (markVal > MAX_MARK) {
                                                        Toast.makeText(getBaseContext(), "最大值为6", Toast.LENGTH_SHORT).show();
                                                        pop_chexiang.setText(String.valueOf(MAX_MARK));
                                                    }
                                                    if (s.length() > 0) {
                                                        if (MIN_MARK != -1 && MAX_MARK != -1) {
                                                            int num = Integer.parseInt(s.toString());
                                                            if (num > MAX_MARK) {

                                                                pop_chexiang.setText(String.valueOf(MAX_MARK));
                                                            } else if (num < MIN_MARK)
                                                                pop_chexiang.setText(String.valueOf(MIN_MARK));
                                                            return;
                                                        }
                                                    }
                                                    return;
                                                }
                                            }
                                        }
                                    });

                                    ad.setTitle("检查信息");
                                    selfdialog = ad.create();
                                    selfdialog.setButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            chehao = pop_chehao.getText().toString();
                                            chexiang = pop_chexiang.getText().toString();
                                            if (chehao.equals("") || chexiang.equals("")) {
                                                showDialog();
                                            } else {
                                                Intent intent = new Intent(xianlu_main_activity.this, crm_main_activity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("zaizhuangxianlu", xianluname_str);
                                                bundle.putString("zaizhuangxianlu_num", xianlunum_str);
                                                bundle.putString("chehao", chehao + "0" + chexiang);
                                                bundle.putString("chexiang", chexiang);
                                                if (chexiang.equals("1") || chexiang.equals("6")) {
                                                    bundle.putString("chexing", "tc");
                                                } else if (chexiang.equals("2") || chexiang.equals("5")) {
                                                    bundle.putString("chexing", "mp");
                                                } else if (chexiang.equals("3") || chexiang.equals("4")) {
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
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void showDialog() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(xianlu_main_activity.this);
        builder.setTitle("错误").setIcon(android.R.drawable.stat_notify_error);
        builder.setMessage("请输入车号及车厢！！！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    class myListAdapt extends BaseAdapter {
        private LayoutInflater layoutInflater;
        ImageView list_imag;
        Button list_but;
        TextView xianlu_my_image_item_textview;
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return urlList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return urlList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressLint({ "InflateParams", "ViewHolder" })
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            layoutInflater=LayoutInflater.from(getApplication());
            convertView = layoutInflater.inflate(R.layout.my_image_view, null);
            list_imag=(ImageView) convertView.findViewById(R.id.list_imag);
            xianlu_my_image_item_textview=(TextView) convertView.findViewById(R.id.xianlu_my_image_item_textview);
            list_imag.setTag(position);
            final Bitmap bitmap=imageCachceUitl.getBitmapFromUrl(urlList.get(position), position);
            if (null!=bitmap) {
                list_imag.setImageBitmap(bitmap);
            }
            list_imag.setVisibility(View.VISIBLE);
            xianlu_my_image_item_textview.setText(urlList.get(position).toString());
            return convertView;
        }
    }
}

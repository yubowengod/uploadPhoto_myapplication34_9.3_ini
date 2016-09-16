package com.arlen.photo.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arlen.photo.R;
import com.arlen.photo.photopickup.presenter.PhotoPresenter;
import com.arlen.photo.photopickup.view.ImageLookActivity;
import com.arlen.photo.photopickup.view.PhotoPickupActivity;
import com.arlen.photo.photopickup.widget.SimpleGrid;
import com.arlen.photo.upload.test_mul;
import com.arlen.photo.upload.upload_thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Arlen on 2016/8/12 10:30.
 */
public class MainActivity extends Activity implements View.OnClickListener {


    private Button btn_pic_info;

    private TextView pic_info;

    public static PhotoPresenter mPhotoPresenter;

    ArrayList<String> pic_path=new ArrayList<>();

    private SimpleGrid mSimpleGrid;

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private ExecutorService executorService;



    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 2012) {
                //只要在主线程就可以处理ui
                ((TextView) MainActivity.this.findViewById(msg.arg1)).setText((String) msg.obj);
            }
        }
    };

    private TextView txt_upload_4;
    private TextView txt_upload_3;
    private TextView txt_upload_reset_yemian;
    private TextView txt_upload_back;

    private void setSelected(){
        txt_upload_4.setSelected(false);
        txt_upload_3.setSelected(false);
        txt_upload_reset_yemian.setSelected(false);
        txt_upload_back.setSelected(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotoPresenter = new PhotoPresenter(this,"feedback");

        txt_upload_4 = (TextView) findViewById(R.id.txt_upload_4);
        txt_upload_3 = (TextView) findViewById(R.id.txt_upload_3);
        txt_upload_reset_yemian = (TextView) findViewById(R.id.txt_upload_reset_yemian);
        txt_upload_back = (TextView) findViewById(R.id.txt_upload_back);

        txt_upload_reset_yemian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt_upload_reset_yemian.setSelected(false);
                txt_upload_reset_yemian.setSelected(true);
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        txt_upload_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected();
                txt_upload_back.setSelected(false);
                txt_upload_back.setSelected(true);
                finish();
            }
        });

        initView();

        executorService = Executors.newFixedThreadPool(5);//开启5个线程，其实根据你的情况，一般不会超过8个

        btn_pic_info = (Button) findViewById(R.id.btn_pic_info);

        pic_info = (TextView) findViewById(R.id.pic_info);

        btn_pic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic_path.clear();

                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "图片上传中", "请稍候...", true);

                for (int i = 0 ; i<mPhotoPresenter.mSelectedImgPros.size(); i++)
                {
                    pic_path.add(mPhotoPresenter.mSelectedImgPros.get(i).fullPath.toString());
                }
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        test_mul.getImageromSdk(pic_path);
                        try
                        {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (test_mul.return_true_flag.size()==pic_path.size())
                                    {
                                        dialog.dismiss();
                                        pic_info.setText("上传图片完成，一共"+ String.valueOf( pic_path.size())+"张图片");
                                        test_mul.return_true_flag.clear();
                                    }
                                }
                            });
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }



    private void initView() {

        mSimpleGrid = (SimpleGrid) findViewById(R.id.img_grid);

        mPhotoPresenter.initView(mSimpleGrid);
        mSimpleGrid.setMaxItemPerRow(3);
        mSimpleGrid.setItemMarginHor(7f);
        mSimpleGrid.setItemMarginVer(7f);
        mPhotoPresenter.updateImgGrid();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPickupActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (PhotoPickupActivity.getSelectedImgPros(data) != null) {
                    mPhotoPresenter.pickPhotoResult(data);
                }
            }
        } else if (requestCode == ImageLookActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mPhotoPresenter.lookImageResult(data);
                }
            }
        }
    }
}

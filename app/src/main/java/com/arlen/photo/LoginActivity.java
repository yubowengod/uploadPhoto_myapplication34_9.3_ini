package com.arlen.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.arlen.photo.register.registerActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by God on 2016/8/21.
 */
public class LoginActivity extends Activity {
    private TextView login_input_name;
    private TextView login_input_password;
    private CheckBox login_switchBtn;


//    ????????????????????

    private TextView find_password_repassword_login;
    private TextView register_login;
//    ????????????????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //初始化
        login_input_name = (TextView) findViewById(R.id.login_input_name);
        login_input_password = (TextView) findViewById(R.id.login_input_password);
        login_switchBtn = (CheckBox) findViewById(R.id.login_switchBtn);

        register_login = (TextView) findViewById(R.id.register_login);
        register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this,registerActivity.class);
                startActivity(intent);

            }
        });


        onChangePassLook listener = new onChangePassLook();
        login_switchBtn.setOnCheckedChangeListener(listener);

    }


    public void checkLogin(View v) {

        checkLoginName(login_input_name.getText().toString());
        checkLoginPass(login_input_password.getText().toString());
//        if (checkLoginName(login_input_name.getText().toString())
//                && checkLoginPass(login_input_password.getText().toString()))
//        {


            Intent intent =new Intent(this, MainActivity_login.class);
            startActivity(intent);
            finish();
//        }
    }

    private boolean checkLoginPass(String s) {
        //正则
        String regularExpression = "[0-9]*";
        Pattern p = Pattern.compile(regularExpression);
        Matcher m =p.matcher(s);

        if (s.length() == 0) {
            //Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            login_input_password.setError(getResources().getString(R.string.please_password));
            login_input_password.requestFocus();
            return false;
        } else {
            if (m.matches()) {
                return true;
            } else {
                login_input_password.setError(getResources().getString(R.string.wrong_password));
                login_input_password.requestFocus();
                return false;
            }
        }

    }

    // 判断用户名是否正确
    private boolean checkLoginName(String s) {
        // 邮箱正则
        String regularExpression = "[0-9]*";
        Pattern p = Pattern.compile(regularExpression);
        Matcher m = p.matcher(s);

        if (s.length() == 0) {
            login_input_name.setError(getResources().getString(R.string.please_name));
            login_input_name.requestFocus();
            return false;
        } else {
            if (m.matches()) {
                return true;
            } else {
                login_input_name.setError(getResources().getString(R.string.wrong_name));
                login_input_name.requestFocus();
                return false;
            }
        }
    }


    //	密码切换监听器
    class onChangePassLook implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub

            if(isChecked){
                //如果选中，显示密码
                login_input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                //否则隐藏密码
                login_input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }

        }

    }



}

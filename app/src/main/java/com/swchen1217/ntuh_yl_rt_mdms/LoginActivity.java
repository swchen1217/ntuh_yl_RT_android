package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG ="debug";
    EditText et_acc,et_pw;
    Button btn_forget,btn_login;
    CheckBox cb_rememberme;
    int login_error_count=0;
    public static Boolean engineering_mode_nologin=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_acc=findViewById(R.id.et_acc);
        et_pw=findViewById(R.id.et_pw);
        btn_forget=findViewById(R.id.btn_forgetpw);
        btn_login=findViewById(R.id.btn_login);
        cb_rememberme=findViewById(R.id.cb_rememberme);
        btn_login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    if(engineering_mode_nologin==true && et_acc.getText().toString().equals("") && et_pw.getText().toString().equals("")){
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else{
                        if(et_acc.getText().toString().equals("") || et_pw.getText().toString().equals("")){
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("帳號或密碼為空白!!")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                        }else{
                            if(et_acc.getText().toString().equals("admin")){
                                if(et_pw.getText().toString().equals("admin")){
                                    login_error_count=0;
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();
                                }else{
                                    login_error_count++;
                                    if(login_error_count!=3){
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("密碼錯誤!!")
                                                .setMessage("還有 "+(3-login_error_count)+" 次機會")
                                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();
                                        et_pw.setText("");
                                    }else{
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("密碼錯誤!!")
                                                .setMessage("密碼錯誤 3 次")
                                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            }else{
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("員工編號錯誤!!")
                                        .setMessage("此員工編號尚未註冊")
                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();
                                et_acc.setText("");
                                et_pw.setText("");
                            }
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "無法連接至網際網路", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}

package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG ="debug";
    EditText et_acc,et_pw;
    Button btn_forget,btn_login,btn_change;
    CheckBox cb_rememberme;
    int login_error_count=0;
    public static Boolean engineering_mode_SkipLogin=false;
    final String server_url="http://swchen1217.ddns.net/ntuh_yl_RT_mdms_php/";
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences spf_rememberme=getSharedPreferences("remember",MODE_PRIVATE);
        et_acc=findViewById(R.id.et_acc);
        et_pw=findViewById(R.id.et_pw);
        btn_forget=findViewById(R.id.btn_forgetpw);
        btn_login=findViewById(R.id.btn_login);
        btn_change=findViewById(R.id.btn_changepw);
        cb_rememberme=findViewById(R.id.cb_rememberme);

        et_acc.setText(spf_rememberme.getString("acc",""));
        et_pw.setText(spf_rememberme.getString("pw",""));
        if(spf_rememberme.getString("acc","")!="")
            cb_rememberme.setChecked(true);

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(server_url+"change_pw.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(LoginActivity.this);
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("忘記密碼")
                        .setMessage("請輸入您註冊的Email")
                        .setView(input)
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 在此處理 input
                                Thread thread=new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String tmp=PostDataToSrever("user.php",
                                                    new FormBody.Builder()
                                                            .add("mode", "check_has_email")
                                                            .add("email", input.getText().toString())
                                                            .build());
                                            if(tmp.equals("no_email")){
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        new AlertDialog.Builder(LoginActivity.this)
                                                                .setTitle("無此Email,請聯繫管理員!!")
                                                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                });
                                            }else{
                                                URL url = new URL(server_url+"user.php?mode=forget_pw&email="+input.getText().toString());
                                                url.openStream();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        new AlertDialog.Builder(LoginActivity.this)
                                                                .setTitle("已將重設密碼資料傳送至Email")
                                                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                });
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                thread.start();
                            }
                        })
                        .show();
            }
        });
        btn_login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(engineering_mode_SkipLogin==true && et_acc.getText().toString().equals("") && et_pw.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "工程模式_SkipLogin", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MenuActivity.class));
                    finish();
                }
                if(isConnected()){
                    if((et_acc.getText().toString().equals("") || et_pw.getText().toString().equals("")) && engineering_mode_SkipLogin!=true){
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("帳號或密碼為空白!!")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }else{
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try  {
                                    //Your code goes here
                                    String check_re=PostDataToSrever("user.php",
                                            new FormBody.Builder()
                                                    .add("mode", "login_check")
                                                    .add("acc", et_acc.getText().toString())
                                                    .add("pw", et_pw.getText().toString())
                                                    .build());
                                    if(check_re.equals("tmppw_no_tmppw")){
                                        Log.d("OkHttp","login_check tmppw_no_tmppw");
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Code goes here
                                                new AlertDialog.Builder(LoginActivity.this)
                                                        .setTitle("此使用者無臨時密碼!!")
                                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .show();
                                                et_pw.setText("");
                                            }
                                        });
                                    }
                                    if(check_re.equals("tmppw_error")){
                                        Log.d("OkHttp","login_check tmppw_error");
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Code goes here
                                                new AlertDialog.Builder(LoginActivity.this)
                                                        .setTitle("臨時密碼錯誤!!")
                                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .show();
                                                et_pw.setText("");
                                            }
                                        });
                                    }
                                    if(check_re.equals("tmppw_timeout")){
                                        Log.d("OkHttp","login_check tmppw_timeout");
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Code goes here
                                                new AlertDialog.Builder(LoginActivity.this)
                                                        .setTitle("臨時密碼超過有效期限!!")
                                                        .setMessage("請重新申請")
                                                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        })
                                                        .show();
                                                et_pw.setText("");
                                            }
                                        });
                                    }
                                    if(check_re.equals("tmppw_ok")){
                                        Log.d("OkHttp","login_check tmppw_ok");
                                        if(cb_rememberme.isChecked())
                                        {
                                            spf_rememberme.edit()
                                                    .putString("acc",et_acc.getText().toString())
                                                    .putString("pw",et_pw.getText().toString())
                                                    .commit();
                                        }else{
                                            spf_rememberme.edit()
                                                    .putString("acc","")
                                                    .putString("pw","")
                                                    .commit();
                                        }
                                        String str=PostDataToSrever("user.php",
                                                new FormBody.Builder()
                                                        .add("mode", "get_user_name")
                                                        .add("acc", et_acc.getText().toString())
                                                        .build());
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, str+" ,歡迎回來(使用臨時密碼登入)", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        startActivity(new Intent(LoginActivity.this,MenuActivity.class));
                                        finish();
                                    }
                                    if(check_re.equals("ok")){
                                        Log.d("OkHttp","login_check OK");
                                        if(cb_rememberme.isChecked())
                                        {
                                            spf_rememberme.edit()
                                                    .putString("acc",et_acc.getText().toString())
                                                    .putString("pw",et_pw.getText().toString())
                                                    .commit();
                                        }else{
                                            spf_rememberme.edit()
                                                    .putString("acc","")
                                                    .putString("pw","")
                                                    .commit();
                                        }
                                        String str=PostDataToSrever("user.php",
                                                new FormBody.Builder()
                                                        .add("mode", "get_user_name")
                                                        .add("acc", et_acc.getText().toString())
                                                        .build());
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, str+" ,歡迎回來", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        login_error_count=0;
                                        startActivity(new Intent(LoginActivity.this,MenuActivity.class));
                                        finish();
                                    }
                                    if(check_re.equals("no_acc")){
                                        Log.d("OkHttp","login_check no_acc");
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Code goes here
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
                                        });
                                    }
                                    if(check_re.equals("pw_error")){
                                        Log.d("OkHttp","login_check pw_error");
                                        login_error_count++;
                                        if(login_error_count!=3){
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    //Code goes here
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

                                                }
                                            });
                                        }else{
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    //Code goes here
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
                                            });
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
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

    private boolean isServerCon() throws IOException {
        String con=PostDataToSrever("user.php",
                new FormBody.Builder()
                        .add("mode", "connection_test")
                        .build());
        if(con.equals("connection_ok"))
            return true;
        else
            return false;
    }

    public String PostDataToSrever(String file, FormBody formBody) throws IOException {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(logging).connectTimeout(5, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(server_url+file)
                .post(formBody) // 使用post連線
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            return response.body().string();
        }catch(Exception e){
            if (e instanceof SocketTimeoutException) {
                //判断超时异常
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Code goes here
                        Toast.makeText(LoginActivity.this, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
        /*call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Code goes here
                            Toast.makeText(LoginActivity.this, "無法連接至伺服器", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });*/
        /*Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            // 連線成功
            String result = response.body().string();
            Log.d("OkHttp result", result);
            return result;
        } else {
            // 連線失敗
            Log.d("OkHttp","Error");
            return null;
        }*/
    }
    public void login_ok(){
        Log.d("OkHttp", "Intent OK");
        startActivity(new Intent(LoginActivity.this,MenuActivity.class));
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (System.currentTimeMillis() - exitTime > 3000) {
                Toast.makeText(getApplicationContext(), "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}

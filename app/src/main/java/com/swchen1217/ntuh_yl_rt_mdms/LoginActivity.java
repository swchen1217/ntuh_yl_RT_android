package com.swchen1217.ntuh_yl_rt_mdms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText et_acc,et_pw;
    Button btn_forget,btn_login;
    CheckBox cb_rememberme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_acc=findViewById(R.id.et_acc);
        et_pw=findViewById(R.id.et_pw);
        btn_forget=findViewById(R.id.btn_forgetpw);
        btn_login=findViewById(R.id.btn_login);
        cb_rememberme=findViewById(R.id.cb_rememberme);
    }
}

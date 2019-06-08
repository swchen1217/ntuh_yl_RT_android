package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {
    ImageView logo;
    TextView tv_1,tv_2;
    public static Boolean engineering_mode_nowelcome=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();
        logo=findViewById(R.id.imageView);
        tv_1=findViewById(R.id.textView);
        tv_2=findViewById(R.id.textView2);

        if(engineering_mode_nowelcome==true){
            startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            finish();
        }

        Animation myanim = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.fadein);
        logo.startAnimation(myanim);
        tv_1.startAnimation(myanim);
        tv_2.startAnimation(myanim);
        Thread timer = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                    finish();
                }
            }
        };
        timer.start();
    }
}

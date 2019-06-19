package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class UpdateStatusActivity extends AppCompatActivity {

    Button btn_qr,btn_manual;
    TextView tv_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);
        btn_qr=findViewById(R.id.btn_qrcode);
        btn_manual=findViewById(R.id.btn_manual);
        tv_input-findViewById(R.id.tv_)
        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(UpdateStatusActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
                integrator.setPrompt("現況登錄 QR Code 掃描");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(true);
                integrator.initiateScan();
            }
        });
        btn_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result !=null){
            if(result.getContents() == null){
                Toast.makeText(this,"掃描錯誤!!,請再試一次或改為手動輸入",Toast.LENGTH_SHORT).show();;
            }else {
                InputOk(result.getContents());
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void InputOk(String input){
        tv_input.setText();
    }
}

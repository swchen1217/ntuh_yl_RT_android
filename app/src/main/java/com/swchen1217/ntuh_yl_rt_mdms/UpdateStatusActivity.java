package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class UpdateStatusActivity extends AppCompatActivity {

    Button btn_qr,btn_manual,btn_CheckInput;
    TextView tv_input;
    String input_data=null;
    RadioButton rb_use,rb_stock,rb_fix;
    ConstraintLayout cl_use,cl_stock,cl_fix;
    RadioGroup rg;
    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);
        btn_qr=findViewById(R.id.btn_qrcode);
        btn_manual=findViewById(R.id.btn_manual);
        tv_input=findViewById(R.id.tv_input);
        btn_CheckInput=findViewById(R.id.btn_CheckInput);
        btn_CheckInput.setVisibility(View.INVISIBLE);
        rb_use=findViewById(R.id.rb_use);
        rb_stock=findViewById(R.id.rb_stock);
        rb_fix=findViewById(R.id.rb_fix);
        cl_use=findViewById(R.id.cl_use);
        cl_stock=findViewById(R.id.cl_stock);
        cl_fix=findViewById(R.id.cl_fix);
        cl_use.setVisibility(View.INVISIBLE);
        cl_stock.setVisibility(View.INVISIBLE);
        cl_fix.setVisibility(View.INVISIBLE);
        rg=findViewById(R.id.radioGroup);
        btn_back=findViewById(R.id.btn_back);
        btn_back.setVisibility(View.INVISIBLE);

        setListener();
    }

    public void setListener(){
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
                EditText input = new EditText(UpdateStatusActivity.this);
                if(tv_input.getText().toString().length()>4)
                    input.setText(tv_input.getText().toString().substring(6));
                new AlertDialog.Builder(UpdateStatusActivity.this)
                        .setTitle("手動輸入")
                        .setMessage("請輸入設備ID或設備編號(設備ID位於QR Code 下方)")
                        .setView(input)
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(input.getText().toString().length()<6 || !input.getText().toString().substring(0,6).equals("MDMS.D"))
                                    InputDone(2,input.getText().toString());
                                else
                                    InputDone(1,input.getText().toString());
                            }
                        })
                        .show();
            }
        });
        btn_CheckInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_back.setVisibility(View.VISIBLE);
                btn_CheckInput.setEnabled(false);
                btn_qr.setEnabled(false);
                btn_manual.setEnabled(false);
                if(rb_use.isChecked()){
                    ChangeLayout("use");
                    if(input_data.length()<6 || !input_data.substring(0,6).equals("MDMS.D")){

                    }else{

                    }
                    Log.d("RB","1");
                }else if(rb_stock.isChecked()){
                    ChangeLayout("stock");
                    Log.d("RB","2");
                }else if(rb_fix.isChecked()){
                    ChangeLayout("fix");
                    Log.d("RB","3");
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_back.setVisibility(View.INVISIBLE);
                btn_CheckInput.setEnabled(true);
                btn_qr.setEnabled(true);
                btn_manual.setEnabled(true);
                rb_use.setEnabled(true);
                rb_stock.setEnabled(true);
                rb_fix.setEnabled(true);
                cl_use.setVisibility(View.INVISIBLE);
                cl_stock.setVisibility(View.INVISIBLE);
                cl_fix.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result !=null){
            if(result.getContents() == null){
                tv_input.setText("");
                Toast.makeText(this,"掃描錯誤!!,請再試一次或改為手動輸入",Toast.LENGTH_SHORT).show();;
            }else {
                InputDone(1,result.getContents());
                Log.d("QRCodeScanLog",result.getContents());
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void InputDone(int mode,String input){
        tv_input.setText(input);
        if(input.equals("")){
            tv_input.setText("");
            btn_CheckInput.setVisibility(View.INVISIBLE);
            btn_back.setVisibility(View.INVISIBLE);
        }else{
            btn_CheckInput.setVisibility(View.VISIBLE);
            btn_CheckInput.setEnabled(true);
            btn_back.setVisibility(View.INVISIBLE);
            if(mode==1){
                tv_input.setText(" 設備ID："+input);
            }else if(mode==2){
                tv_input.setText(" 設備編號："+input);
            }
            input_data=input;
        }
    }

    public void ChangeLayout(String checked){
        if(checked.equals("use")){
            rb_use.setEnabled(true);
            rb_stock.setEnabled(false);
            rb_fix.setEnabled(false);
            cl_use.setVisibility(View.VISIBLE);
            cl_stock.setVisibility(View.INVISIBLE);
            cl_fix.setVisibility(View.INVISIBLE);
        }
        if(checked.equals("stock")){
            rb_use.setEnabled(false);
            rb_stock.setEnabled(true);
            rb_fix.setEnabled(false);
            cl_use.setVisibility(View.INVISIBLE);
            cl_stock.setVisibility(View.VISIBLE);
            cl_fix.setVisibility(View.INVISIBLE);
        }
        if(checked.equals("fix")){
            rb_use.setEnabled(false);
            rb_stock.setEnabled(false);
            rb_fix.setEnabled(true);
            cl_use.setVisibility(View.INVISIBLE);
            cl_stock.setVisibility(View.INVISIBLE);
            cl_fix.setVisibility(View.VISIBLE);
        }
    }
}

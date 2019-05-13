package com.xdz.seekwork;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xdz.seekwork.util.SeekerSoftConstant;
import com.xdz.seekwork.view.SingleCountDownView;

// 管理
public class ManageActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_add_pro, btn_open_box, btn_exit_sys, btn_exit_manager;

    private String cardNo;

    private SingleCountDownView singleCountDownView;

    private TextView tv_take_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        cardNo = getIntent().getStringExtra("cardNo");

        btn_add_pro = findViewById(R.id.btn_add_pro);
        btn_add_pro.setOnClickListener(this);
        btn_open_box = findViewById(R.id.btn_open_box);
        btn_open_box.setOnClickListener(this);
        btn_exit_sys = findViewById(R.id.btn_exit_sys);
        btn_exit_sys.setOnClickListener(this);
        btn_exit_manager = findViewById(R.id.btn_exit_manager);
        btn_exit_manager.setOnClickListener(this);
        singleCountDownView = findViewById(R.id.singleCountDownView);
        tv_take_back = findViewById(R.id.tv_take_back);
        tv_take_back.setOnClickListener(this);
        singleCountDownView.setTextColor(Color.parseColor("#ff000000"));
        singleCountDownView.setTime(60);
        singleCountDownView.setTimeColorHex("#ff000000");
        singleCountDownView.setTimeSuffixText("s");
        singleCountDownView.setSingleCountDownEndListener(new SingleCountDownView.SingleCountDownEndListener() {
            @Override
            public void onSingleCountDownEnd() {
                ManageActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (singleCountDownView != null) {
            singleCountDownView.startCountDown();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_pro:
                // 去补货界面
                Intent intent = new Intent(ManageActivity.this, ManageListActivity.class);
                intent.putExtra("cardNo", cardNo);
                startActivity(intent);
                break;
            case R.id.btn_open_box:
                // 弹出框选择打开柜子
                Intent intent2 = new Intent(ManageActivity.this, OpenSerialActivity.class);
                intent2.putExtra("cardNo", cardNo);
                startActivity(intent2);
                break;
            case R.id.btn_exit_sys:
                Intent intentExit = new Intent(ManageActivity.this, MainActivity.class);
                intentExit.putExtra(SeekerSoftConstant.EXITAPP, 1);
                intentExit.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentExit);
                ManageActivity.this.finish();
                break;
            case R.id.btn_exit_manager:
                ManageActivity.this.finish();
                break;
            case R.id.tv_take_back:
                ManageActivity.this.finish();
                break;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (singleCountDownView != null) {
            singleCountDownView.pauseCountDown();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (singleCountDownView != null) {
            singleCountDownView.stopCountDown();
        }
    }
}

package com.xdz.seekwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

// 管理
public class ManageActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_add_pro, btn_open_box, btn_exit_sys, btn_exit_manager;

    private String cardNo;

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
                break;
            case R.id.btn_exit_manager:
                ManageActivity.this.finish();
                break;
        }

    }
}

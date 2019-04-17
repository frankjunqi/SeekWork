package com.xdz.seekwork;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xdz.seekwork.view.KeyBordView;

// 取货
public class TakeActivity extends AppCompatActivity {
    private MaterialDialog promissionDialog;

    private KeyBordView kbv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        kbv = findViewById(R.id.kbv);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.pop_take_layout, null);
        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        // promissionDialog.setCancelable(false);


        kbv.setSureClickListen(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 授权弹框
                promissionDialog.show();
            }
        });


    }
}

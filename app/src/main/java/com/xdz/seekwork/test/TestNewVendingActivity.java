package com.xdz.seekwork.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xdz.seekwork.R;
import com.xdz.seekwork.serialport.ShipmentCommad;
import com.xdz.seekwork.serialport.VendingSerialPort;


/**
 */

public class TestNewVendingActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = TestNewVendingActivity.class.getSimpleName();
    private EditText et_col, et_row;
    private Button btn_out, btn_gezi;
    private TextView tv_showdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_vending);
        et_col = (EditText) findViewById(R.id.et_col);
        et_row = (EditText) findViewById(R.id.et_row);

        tv_showdata = (TextView) findViewById(R.id.tv_showdata);

        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(this);

        btn_gezi = (Button) findViewById(R.id.btn_gezi);
        btn_gezi.setOnClickListener(this);


    }

    private int count = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_out:
                String col_str = et_col.getText().toString();
                String row_str = et_row.getText().toString();

                if (TextUtils.isEmpty(col_str) || TextUtils.isEmpty(row_str)) {
                    return;
                }

                int col = Integer.parseInt(col_str);
                int row = Integer.parseInt(row_str);
                tv_showdata.setText(col + " " + row);

                ShipmentCommad shipmentCommad = new ShipmentCommad("1,2");
                shipmentCommad.setContainerNum(0x01);

                VendingSerialPort.SingleInit().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                    @Override
                    public void onDataReceiveString(final String ResultStr) {
                        //Log.e("TAG","recevie = "+ResultStr);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] bytes = ResultStr.getBytes();
                                ShipmentCommad shipmentCommad = new ShipmentCommad("1,2");
                                shipmentCommad.setContainerNum(0x01);
                                VendingSerialPort.SingleInit().commadTakeOut(shipmentCommad);

                            }
                        });
                    }
                }).commadTakeOut(shipmentCommad);

                break;

            case R.id.btn_gezi:
                String col_str_ = et_col.getText().toString();
                String row_str_ = et_row.getText().toString();

                if (TextUtils.isEmpty(col_str_) || TextUtils.isEmpty(row_str_)) {
                    return;
                }

                int col1 = Integer.parseInt(col_str_);
                int row1 = Integer.parseInt(row_str_);
                tv_showdata.setText(col1 + " " + row1);

                ShipmentCommad shipmentCommad1 = new ShipmentCommad("1,2");
                shipmentCommad1.setContainerNum(0x01);

                VendingSerialPort.SingleInit().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                    @Override
                    public void onDataReceiveString(final String ResultStr) {

                        Log.e("TAG", "recevie = " + ResultStr);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] bytes = ResultStr.getBytes();
                            }
                        });
                    }
                }).commadTakeOut(shipmentCommad1);
                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VendingSerialPort.SingleInit().closeSerialPort();
    }
}

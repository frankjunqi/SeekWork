package com.xdz.seekwork.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xdz.seekwork.R;
import com.xdz.seekwork.serialport.ShipmentObject;
import com.xdz.seekwork.serialport.VendingSerialPort;


/**
 * Created by Frank on 16/11/20.
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
                ShipmentObject shipmentObject = new ShipmentObject();
                shipmentObject.containerNum = 1;
                shipmentObject.proNum = col * 10 + row;
                shipmentObject.objectId = count++;

                VendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject).setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                    @Override
                    public void onDataReceiveString(final String ResultStr) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] bytes = ResultStr.getBytes();
                            }
                        });
                    }
                }).commadTakeOut();

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
                ShipmentObject shipmentObjectG = new ShipmentObject();
                shipmentObjectG.containerNum = 2;
                shipmentObjectG.proNum = col1 * 10 + row1;
                shipmentObjectG.objectId = count++;

                VendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObjectG).setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                    @Override
                    public void onDataReceiveString(final String ResultStr) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] bytes = ResultStr.getBytes();
                            }
                        });
                    }
                }).commadTakeOut();
                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VendingSerialPort.SingleInit().closeSerialPort();
    }
}

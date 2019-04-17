package com.xdz.seekwork.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xdz.seekwork.R;
import com.xdz.seekwork.serialport.ShipmentObject;
import com.xdz.seekwork.serialport.VendingSerialPort;
import com.xdz.seekwork.util.LogCat;


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
                int col = Integer.parseInt(et_col.getText().toString());
                int row = Integer.parseInt(et_row.getText().toString());
                tv_showdata.setText(col + " " + row);
                ShipmentObject shipmentObject = new ShipmentObject();
                shipmentObject.containerNum = 1;
                shipmentObject.proNum = col * 10 + row;
                shipmentObject.objectId = count++;

                VendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject).setOnCmdCallBackListen(new VendingSerialPort.OnCmdCallBackListen() {
                    @Override
                    public void onCmdCallBack(boolean isSuccess) {
                        LogCat.e("<<< error" + tv_showdata.getText() + " -- " + isSuccess);
                    }
                });
                break;

            case R.id.btn_gezi:
                int col1 = Integer.parseInt(et_col.getText().toString());
                int row1 = Integer.parseInt(et_row.getText().toString());
                tv_showdata.setText(col1 + " " + row1);
                ShipmentObject shipmentObjectG = new ShipmentObject();
                shipmentObjectG.containerNum = 2;
                shipmentObjectG.proNum = col1 * 10 + row1;
                shipmentObjectG.objectId = count++;

                VendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObjectG).setOnCmdCallBackListen(new VendingSerialPort.OnCmdCallBackListen() {
                    @Override
                    public void onCmdCallBack(boolean isSuccess) {
                        LogCat.e("<<< gezi out result: " + tv_showdata.getText() + " -- " + isSuccess);
                    }
                });
                break;

        }

    }


}

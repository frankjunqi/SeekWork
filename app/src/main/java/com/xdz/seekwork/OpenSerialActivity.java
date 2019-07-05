package com.xdz.seekwork;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xdz.seekwork.network.api.Host;
import com.xdz.seekwork.network.api.SeekWorkService;
import com.xdz.seekwork.network.api.SrvResult;
import com.xdz.seekwork.network.entity.seekwork.MRoad;
import com.xdz.seekwork.network.gsonfactory.GsonConverterFactory;
import com.xdz.seekwork.serialport.ShipmentCommad;
import com.xdz.seekwork.serialport.ShipmentResult;
import com.xdz.seekwork.serialport.VendingSerialPort;
import com.xdz.seekwork.util.LogCat;
import com.xdz.seekwork.util.SeekerSoftConstant;
import com.xdz.seekwork.util.SerialResultUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 打开串口
public class OpenSerialActivity extends BaseActivity implements View.OnClickListener {


    private Button btn_zhu, btn_a, btn_b, btn_c;
    private TextView tv_take_back;

    private String cardNo;
    private MaterialDialog tipViewDialog;

    private TextView tv_tips;
    private ImageView iv_tip_error;
    private ImageView iv_close;
    private ScrollView sv;

    private ArrayList<MRoad> zhuList = new ArrayList<>();
    private ArrayList<MRoad> aList = new ArrayList<>();
    private ArrayList<MRoad> bList = new ArrayList<>();
    private ArrayList<MRoad> cList = new ArrayList<>();

    private boolean isClose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        cardNo = getIntent().getStringExtra("cardNo");

        tv_take_back = findViewById(R.id.tv_take_back);
        tv_take_back.setOnClickListener(this);
        btn_zhu = findViewById(R.id.btn_zhu);
        btn_zhu.setOnClickListener(this);
        btn_a = findViewById(R.id.btn_a);
        btn_a.setOnClickListener(this);
        btn_b = findViewById(R.id.btn_b);
        btn_b.setOnClickListener(this);
        btn_c = findViewById(R.id.btn_c);
        btn_c.setOnClickListener(this);

        getProList();

        initTipDialog();

    }

    private void initTipDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        View customViewTip = inflater.inflate(R.layout.pop_open_serial_layout, null);
        tv_tips = customViewTip.findViewById(R.id.tv_tips);

        sv = customViewTip.findViewById(R.id.sv);

        iv_tip_error = customViewTip.findViewById(R.id.iv_tip_error);
        iv_close = customViewTip.findViewById(R.id.iv_close);
        tipViewDialog = new MaterialDialog.Builder(this).customView(customViewTip, false).build();
        setWindowUIHide(tipViewDialog.getWindow());
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isClose = true;

                if (tipViewDialog != null && tipViewDialog.isShowing()) {
                    tipViewDialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams wl = tipViewDialog.getWindow().getAttributes();
        wl.width = widthPixels - 88;
        wl.height = heightPixels / 4 * 3;
        tipViewDialog.getWindow().setAttributes(wl);

        tipViewDialog.setCancelable(false);
    }

    private void showTipDialog(String tips) {
        tv_tips.setText(tips);
        sv.fullScroll(ScrollView.FOCUS_DOWN);
        if (tipViewDialog != null && !tipViewDialog.isShowing()) {
            tipViewDialog.show();
        }
    }

    private void getProList() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<List<MRoad>>> mRoadAction = service.queryRoad(SeekerSoftConstant.MachineNo);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<List<MRoad>>>() {
            @Override
            public void onResponse(Call<SrvResult<List<MRoad>>> call, Response<SrvResult<List<MRoad>>> response) {
                if (response != null && response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                    // 成功逻辑
                    List<MRoad> list = response.body().getData();
                    zhuList.clear();
                    aList.clear();
                    bList.clear();
                    cList.clear();
                    for (int i = 0; i < list.size(); i++) {
                        String cabNo = list.get(i).getCabNo();
                        if ("主柜".equals(cabNo)) {
                            zhuList.add(list.get(i));
                        } else if ("A".equals(cabNo)) {
                            aList.add(list.get(i));
                        } else if ("B".equals(cabNo)) {
                            bList.add(list.get(i));
                        } else if ("C".equals(cabNo)) {
                            cList.add(list.get(i));
                        }
                    }
                } else {
                    // 无数据
                    showTipDialog("此货柜没有配置货道信息，不可进行任何操作。");
                }
            }

            @Override
            public void onFailure(Call<SrvResult<List<MRoad>>> call, Throwable throwable) {
                // 异常
                showTipDialog("错误提示：网络异常。");
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zhu:
                isClose = false;
                open(0);
                break;
            case R.id.btn_a:
                isClose = false;
                open(1);
                break;
            case R.id.btn_b:
                isClose = false;
                open(2);
                break;
            case R.id.btn_c:
                isClose = false;
                open(3);
                break;
            case R.id.tv_take_back:
                OpenSerialActivity.this.finish();
                break;

        }

    }


    private int flag = 0;
    private StringBuffer sb = new StringBuffer();

    private void open(final int chooseFlag) {
        showTipDialog("");
        iv_close.setVisibility(View.VISIBLE);
        flag = 0;
        sb = new StringBuffer("");

        final ArrayList<ShipmentCommad> shipmentCommads = new ArrayList<>();

        if (chooseFlag == 0) {
            for (int i = 0; zhuList != null && i < zhuList.size(); i++) {
                ShipmentCommad shipmentCommad = new ShipmentCommad(zhuList.get(i).getRealCode());
                shipmentCommad.setGEZI(false);
                shipmentCommad.setContainerNum(zhuList.get(i).getContainer());
                shipmentCommads.add(shipmentCommad);
            }
        } else if (chooseFlag == 1) {
            for (int i = 0; aList != null && i < aList.size(); i++) {
                ShipmentCommad shipmentCommad = new ShipmentCommad(aList.get(i).getRealCode());
                shipmentCommad.setGEZI(true);
                shipmentCommad.setContainerNum(aList.get(i).getContainer());
                shipmentCommads.add(shipmentCommad);
            }
        } else if (chooseFlag == 2) {
            for (int i = 0; bList != null && i < bList.size(); i++) {
                ShipmentCommad shipmentCommad = new ShipmentCommad(bList.get(i).getRealCode());
                shipmentCommad.setGEZI(true);
                shipmentCommad.setContainerNum(bList.get(i).getContainer());
                shipmentCommads.add(shipmentCommad);
            }
        } else if (chooseFlag == 3) {
            for (int i = 0; cList != null && i < cList.size(); i++) {
                ShipmentCommad shipmentCommad = new ShipmentCommad(cList.get(i).getRealCode());
                shipmentCommad.setGEZI(true);
                shipmentCommad.setContainerNum(cList.get(i).getContainer());
                shipmentCommads.add(shipmentCommad);
            }
        }


        if (shipmentCommads != null && shipmentCommads.size() > 0) {
            VendingSerialPort.SingleInit().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                @Override
                public void onDataReceiveString(final String ResultStr) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            MRoad mRoad = null;
                            if (chooseFlag == 0 && zhuList != null && zhuList.size() > 0) {
                                mRoad = zhuList.get(flag);
                            } else if (chooseFlag == 1 && aList != null && aList.size() > 0) {
                                mRoad = aList.get(flag);
                            } else if (chooseFlag == 2 && bList != null && bList.size() > 0) {
                                mRoad = bList.get(flag);
                            } else if (chooseFlag == 3 && cList != null && cList.size() > 0) {
                                mRoad = cList.get(flag);
                            }

                            // 串口操作成功
                            ShipmentResult shipmentResult = SerialResultUtil.handleResult(ResultStr);
                            sb.append("货道号: " + mRoad.getCabNo() + mRoad.getRoadCode() + ",产品名称: " + mRoad.getProductName() + "。\n");
                            sb.append(shipmentResult.getResultMsg() + "\n\n");

                            // 标记+1
                            flag++;

                            // 判断是否继续下一个串口打开
                            if (isClose) {
                                // close tip dialog
                                if (tipViewDialog != null && tipViewDialog.isShowing()) {
                                    tipViewDialog.dismiss();
                                }
                            } else if (shipmentCommads.size() > flag) {
                                VendingSerialPort.SingleInit().commadTakeOut(shipmentCommads.get(flag));
                                showTipDialog(sb.toString());
                            } else {
                                // 所有的都打开了的操作
                                iv_close.setVisibility(View.VISIBLE);
                            }
                        }
                    });


                }
            }).commadTakeOut(shipmentCommads.get(flag));
        } else {
            showTipDialog("提示：货道暂无数据。");
        }


    }

}

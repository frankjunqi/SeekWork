package com.xdz.seekwork;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xdz.seekwork.network.api.Host;
import com.xdz.seekwork.network.api.SeekWorkService;
import com.xdz.seekwork.network.api.SrvResult;
import com.xdz.seekwork.network.entity.seekwork.MPickQueryByRFID;
import com.xdz.seekwork.network.entity.seekwork.MRoad;
import com.xdz.seekwork.network.gsonfactory.GsonConverterFactory;
import com.xdz.seekwork.serialport.CardReadSerialPort;
import com.xdz.seekwork.util.LogCat;
import com.xdz.seekwork.util.SeekerSoftConstant;
import com.xdz.seekwork.view.KeyBordView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 取货
public class TakeActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialDialog promissionDialog;
    private List<MRoad> list;
    private KeyBordView kbv;

    // 显示库存
    private TextView tv_qty;
    // 物品名称
    private TextView tv_productname;

    // 选择的数量
    private TextView tv_choose_num;
    private TextView tv_cut, tv_add;

    private ImageView iv_card;
    private ProgressBar pb_loadingdata;

    private MRoad mRoad = null;

    // 选择的数量
    private int choostNum = 1;

    private int successNum = 0;

    private CardReadSerialPort cardReadSerialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        kbv = findViewById(R.id.kbv);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.pop_take_layout, null);
        tv_qty = customView.findViewById(R.id.tv_qty);
        tv_productname = customView.findViewById(R.id.tv_productname);
        tv_choose_num = customView.findViewById(R.id.tv_choose_num);

        tv_cut = customView.findViewById(R.id.tv_cut);
        tv_cut.setOnClickListener(this);

        tv_add = customView.findViewById(R.id.tv_add);
        tv_add.setOnClickListener(this);

        iv_card = customView.findViewById(R.id.iv_card);
        pb_loadingdata = customView.findViewById(R.id.pb_loadingdata);

        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        promissionDialog.setCancelable(false);


        kbv.setSureClickListen(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String keyBoradRoad = kbv.getKeyBoradStr();
                // 是否存在货道
                boolean isExistRoad = false;
                // 是否存在库存
                boolean isExistNum = false;

                for (int i = 0; list != null && i < list.size(); i++) {
                    if (!TextUtils.isEmpty(keyBoradRoad) && keyBoradRoad.equals(list.get(i).getBorderRoad())) {
                        isExistRoad = true;
                        if (list.get(i).getQty() > 0) {
                            isExistNum = true;
                        }
                        mRoad = list.get(i);
                        break;
                    }
                }

                // 没有此货道 or 此货道没有库存
                if (!isExistRoad || !isExistNum) {
                    // TODO 做提示 没有此货道 or 此货道没有库存
                    Log.e("tag", (isExistRoad ? "有货道" : "无货道") + (isExistNum ? "有库存" : "无库存"));
                    mRoad = null;
                } else {
                    // TODO 正确逻辑
                    if (mRoad != null) {
                        tv_qty.setText(String.valueOf(mRoad.getQty()));
                        tv_productname.setText(mRoad.getProductName());
                    }
                    // 开去串口读卡器
                    cardReadSerialPort = new CardReadSerialPort();
                    cardReadSerialPort.setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
                        @Override
                        public void onDataReceiveString(final String IDNUM) {
                            Log.e("tag", "IDNUM = " + IDNUM);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cardReadSerialPort.closeReadSerial();
                                    iv_card.setVisibility(View.GONE);
                                    pb_loadingdata.setVisibility(View.VISIBLE);
                                    cardReadSerialPort = null;

                                    // TODO 根据卡号 机器号 请求是否可以取货接口

                                    pickQueryByRFID(IDNUM);
                                }
                            });

                        }
                    });
                    // 授权弹框
                    promissionDialog.show();
                }
            }
        });

        queryRoad();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cut) {
            if (choostNum == 1) {
                // TODO 提示最少取1件
                choostNum = 1;

            } else {
                choostNum = choostNum - 1;
            }
            tv_choose_num.setText(String.valueOf(choostNum));
        } else if (v.getId() == R.id.tv_add) {
            if (choostNum == mRoad.getQty()) {
                // TODO 提示最多只能库存数
                choostNum = mRoad.getQty();

            } else {
                choostNum = choostNum + 1;
            }
            tv_choose_num.setText(String.valueOf(choostNum));
        }
    }

    // 查询货道信息
    private void queryRoad() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<List<MRoad>>> mRoadAction = service.queryRoad(SeekerSoftConstant.MachineNo);
        LogCat.e("queryRoad = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<List<MRoad>>>() {
            @Override
            public void onResponse(Call<SrvResult<List<MRoad>>> call, Response<SrvResult<List<MRoad>>> response) {
                if (response != null && response.body() != null && response.body().getData() != null) {
                    // TODO 成功逻辑
                    list = response.body().getData();
                } else {
                    // TODO 无数据
                }
            }

            @Override
            public void onFailure(Call<SrvResult<List<MRoad>>> call, Throwable throwable) {
                // TODO 异常
            }
        });

    }


    // 刷卡取货
    private void pickQueryByRFID(final String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<MPickQueryByRFID>> mRoadAction = service.pickQueryByRFID(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), choostNum);
        LogCat.e("queryRoad = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<MPickQueryByRFID>>() {
            @Override
            public void onResponse(Call<SrvResult<MPickQueryByRFID>> call, Response<SrvResult<MPickQueryByRFID>> response) {
                if (response != null && response.body() != null && response.body().getData() != null) {
                    // TODO 成功逻辑
                    if (response.body().getData().isAuthorize()) {
                        // TODO 调用出货串口(统计多次发送出货命令)
                        successNum++;

                        // TODO 多次出货后，在出货串口成功返回的中出请求出货成功逻辑接口
                        pickSuccess(cardNo);

                        // TODO 提示出货成功，关闭取货页面，返回道首页
                        promissionDialog.dismiss();
                        TakeActivity.this.finish();
                    }
                } else {
                    // TODO 无数据
                }
            }

            @Override
            public void onFailure(Call<SrvResult<MPickQueryByRFID>> call, Throwable throwable) {
                // TODO 异常
            }
        });

    }

    // 取货成功调用接口
    private void pickSuccess(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.pickSuccess(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), successNum);
        LogCat.e("queryRoad = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                // TODO 根据出货数量 与 用户选择的进行比较：如果 出货成功次数==需求数量 则显示 "全部出货完成";
                //如果 出货成功次数==0 则显示 "全部出货失败";
                //如果 出货成功次数>0 && 出货成功次数!= 需求数量 则显示 "部分出货成功";
                String tips = "";
                if (choostNum == successNum) {
                    tips = "全部出货完成";
                } else if (successNum == 0) {
                    tips = "全部出货失败";
                } else {
                    tips = "部分出货成功";
                }

                if (response != null && response.body() != null && response.body().getData()) {
                    // TODO 接口逻辑处理成功
                } else {
                    // TODO 接口逻辑处理失败
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {

            }
        });
    }


    // 借货成功确认
    private void borrowComplete(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.borrowComplete(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), successNum);
        LogCat.e("queryRoad = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                // TODO 根据出货数量 与 用户选择的进行比较：如果 出货成功次数==需求数量 则显示 "全部出货完成";
                //如果 出货成功次数==0 则显示 "全部出货失败";
                //如果 出货成功次数>0 && 出货成功次数!= 需求数量 则显示 "部分出货成功";
                String tips = "";
                if (choostNum == successNum) {
                    tips = "全部出货完成";
                } else if (successNum == 0) {
                    tips = "全部出货失败";
                } else {
                    tips = "部分出货成功";
                }

                if (response != null && response.body() != null && response.body().getData()) {
                    // TODO 接口逻辑处理成功
                } else {
                    // TODO 接口逻辑处理失败
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {

            }
        });
    }

    // 还货成功确认
    private void backComplete(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.backComplete(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), successNum);
        LogCat.e("queryRoad = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                // TODO 根据出货数量 与 用户选择的进行比较：如果 出货成功次数==需求数量 则显示 "全部出货完成";
                //如果 出货成功次数==0 则显示 "全部出货失败";
                //如果 出货成功次数>0 && 出货成功次数!= 需求数量 则显示 "部分出货成功";
                String tips = "";
                if (choostNum == successNum) {
                    tips = "全部出货完成";
                } else if (successNum == 0) {
                    tips = "全部出货失败";
                } else {
                    tips = "部分出货成功";
                }

                if (response != null && response.body() != null && response.body().getData()) {
                    // TODO 接口逻辑处理成功
                } else {
                    // TODO 接口逻辑处理失败
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {

            }
        });
    }


    // 此货道被谁借走
    private void getLastBorrowName() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<String>> mRoadAction = service.getLastBorrowName(SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode());
        LogCat.e("queryRoad = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<String>>() {
            @Override
            public void onResponse(Call<SrvResult<String>> call, Response<SrvResult<String>> response) {

            }

            @Override
            public void onFailure(Call<SrvResult<String>> call, Throwable throwable) {

            }
        });
    }

    // 借货 还货 请求
    private void authBorrowAndBack(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.authBorrowAndBack(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), 1);
        LogCat.e("queryRoad = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {

            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardReadSerialPort != null) {
            cardReadSerialPort.closeReadSerial();
        }
    }

}

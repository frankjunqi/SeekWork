package com.xdz.seekwork;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.xdz.seekwork.serialport.ShipmentCommad;
import com.xdz.seekwork.serialport.VendingSerialPort;
import com.xdz.seekwork.util.LogCat;
import com.xdz.seekwork.util.SeekerSoftConstant;
import com.xdz.seekwork.view.KeyBordView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 取货
public class TakeActivity extends AppCompatActivity implements View.OnClickListener {

    public String ActionType = "";

    private TextView tv_take_back;

    private MaterialDialog promissionDialog;
    private List<MRoad> list;
    private KeyBordView kbv;

    // 显示库存
    private TextView tv_qty;
    // 物品名称
    private TextView tv_productname;

    // 选择的数量
    private TextView tv_choose_num;
    private TextView tv_cut, tv_add, tv_back;

    private ImageView iv_card;
    private ProgressBar pb_loadingdata;

    private MRoad mRoad = null;

    // 选择的数量
    private int choostNum = 1;

    private int successNum = 0;

    private MaterialDialog tipDialog;

    private void showTipDialog(String tips) {
        tipDialog = new MaterialDialog.Builder(this).content(tips).build();
        tipDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        ActionType = getIntent().getStringExtra(SeekerSoftConstant.ActionType);
        tv_take_back = findViewById(R.id.tv_take_back);
        tv_take_back.setOnClickListener(this);

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

        tv_back = customView.findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        iv_card = customView.findViewById(R.id.iv_card);
        pb_loadingdata = customView.findViewById(R.id.pb_loadingdata);

        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        promissionDialog.setCancelable(false);


        kbv.setSureClickListen(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoad = null;
                choostNum = 1;
                successNum = 0;

                String keyBoradRoad = kbv.getKeyBoradStr();

                for (int i = 0; list != null && i < list.size(); i++) {
                    if (!TextUtils.isEmpty(keyBoradRoad) && keyBoradRoad.equals(list.get(i).getBorderRoad())) {
                        mRoad = list.get(i);
                        break;
                    }
                }

                if (mRoad == null) {
                    // TODO 没有货道提示
                    showTipDialog("没有货道");
                } else {
                    if ("1".equals(mRoad.getCabType()) && mRoad.getQty() == 0 && SeekerSoftConstant.Borrow.equals(ActionType)) {
                        // 格子柜 & 库存 ＝＝ 0 & 借操作
                        // 调用查询被谁借走的接口
                        getLastBorrowName();
                    } else if ("1".equals(mRoad.getCabType()) && SeekerSoftConstant.Back.equals(ActionType) && mRoad.getQty() > 0) {
                        // TODO 格子柜 & 库存 > 0 & 还操作  ＝＝> 提示已经归还
                        showTipDialog("已经归还");
                    } else if (!"1".equals(mRoad.getCabType()) && mRoad.getQty() == 0) {
                        // TODO 螺纹柜 & 库存 == 0  ＝＝> 提示无库存
                        showTipDialog("无库存");
                    } else {
                        // 设置dialog中数据
                        iv_card.setVisibility(View.VISIBLE);
                        pb_loadingdata.setVisibility(View.INVISIBLE);

                        tv_qty.setText(String.valueOf(mRoad.getQty()));
                        tv_productname.setText(mRoad.getProductName());
                        tv_choose_num.setText(String.valueOf(choostNum));

                        if ("1".equals(mRoad.getCabType())) {
                            // TODO 格子柜不可点击处理，灰色处理

                        } else {
                            // TODO 螺纹柜 可点击 处理

                        }

                        // 开去串口读卡器
                        CardReadSerialPort.SingleInit().setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
                            @Override
                            public void onDataReceiveString(final String IDNUM) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv_card.setVisibility(View.GONE);
                                        pb_loadingdata.setVisibility(View.VISIBLE);

                                        // 有货道 ＋ 有库存 请求接口 判断是否有权限出货
                                        if (SeekerSoftConstant.Back.equals(ActionType) || SeekerSoftConstant.Borrow.equals(ActionType)) {
                                            // 格子柜 借 还
                                            if ("1".equals(mRoad.getCabType())) {
                                                // 操作 借 还 柜子
                                                authBorrowAndBack(IDNUM);
                                            } else {
                                                showTipDialog("此货道不可进行借还操作");
                                            }
                                        } else if (SeekerSoftConstant.Take.equals(ActionType)) {
                                            // 螺纹柜 取货
                                            // 根据卡号 机器号 请求是否可以取货接口
                                            pickQueryByRFID(IDNUM);
                                        }
                                    }
                                });

                            }
                        });

                        // 授权弹框
                        promissionDialog.show();
                    }

                }
            }
        });

        queryRoad();

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cut) {
            if (mRoad != null && !"1".equals(mRoad.getCabType())) {
                if (choostNum == 1) {
                    // TODO 提示最少取1件
                    choostNum = 1;
                    showTipDialog("最少取1件");
                } else {
                    choostNum = choostNum - 1;
                }
            } else {
                // 借还操作
            }
            tv_choose_num.setText(String.valueOf(choostNum));
        } else if (v.getId() == R.id.tv_add) {
            if (mRoad != null && !"1".equals(mRoad.getCabType())) {
                if (choostNum == mRoad.getQty()) {
                    // TODO 提示最多只能库存数
                    choostNum = mRoad.getQty();
                    showTipDialog("最多只能库存数");
                } else {
                    choostNum = choostNum + 1;
                }
            } else {
                // 借还操作
            }
            tv_choose_num.setText(String.valueOf(choostNum));
        } else if (v.getId() == R.id.tv_back) {
            // 弹出框操作中返回取消按钮
            promissionDialog.dismiss();
        } else if (v.getId() == R.id.tv_take_back) {
            // 取货页面关闭
            TakeActivity.this.finish();
        }
    }

    // 查询货道信息
    private void queryRoad() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<List<MRoad>>> mRoadAction = service.queryRoad(SeekerSoftConstant.MachineNo);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<List<MRoad>>>() {
            @Override
            public void onResponse(Call<SrvResult<List<MRoad>>> call, Response<SrvResult<List<MRoad>>> response) {
                if (response != null && response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                    // 成功逻辑
                    list = response.body().getData();
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

    private int flag = 0;

    // 刷卡取货
    private void pickQueryByRFID(final String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<MPickQueryByRFID>> mRoadAction = service.pickQueryByRFID(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), choostNum);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<MPickQueryByRFID>>() {
            @Override
            public void onResponse(Call<SrvResult<MPickQueryByRFID>> call, Response<SrvResult<MPickQueryByRFID>> response) {
                /*MPickQueryByRFID mPickQueryByRFID = new MPickQueryByRFID();
                mPickQueryByRFID.setAuthorize(true);
                response.body().setData(mPickQueryByRFID);*/
                if (response != null && response.body() != null && response.body().getData() != null) {
                    // 成功逻辑
                    if (response.body().getData().isAuthorize()) {
                        // 硬件编号，用户出货
                        int realRoad = mRoad.getRealCode();

                        // 记住出货第几个
                        flag = 0;

                        // 11 45
                        final List<ShipmentCommad> list = new ArrayList<>();
                        for (int i = 0; i < choostNum; i++) {
                            list.add(new ShipmentCommad(45));
                        }

                        VendingSerialPort.SingleInit().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                            @Override
                            public void onDataReceiveString(final String ResultStr) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        flag++;

                                        // TODO 判断出货是否成功
                                        if (!TextUtils.isEmpty(ResultStr)) {
                                            successNum++;
                                        }
                                        // 看下是否还有货要出
                                        // 调用出货串口(统计多次发送出货命令)
                                        if (list.size() > flag) {
                                            VendingSerialPort.SingleInit().commadTakeOut(list.get(flag));
                                        } else {
                                            // 根据出货数量 与 用户选择的进行比较：如果 出货成功次数==需求数量 则显示 "全部出货完成";
                                            // 如果 出货成功次数==0 则显示 "全部出货失败";
                                            // 如果 出货成功次数>0 && 出货成功次数!= 需求数量 则显示 "部分出货成功";
                                            String tips = "";
                                            if (choostNum == successNum) {
                                                tips = "全部出货完成";
                                            } else if (successNum == 0) {
                                                tips = "全部出货失败";
                                            } else {
                                                tips = successNum + "个物品出货成功，" + (choostNum - successNum) + "个物品出货失败。";
                                            }
                                            showTipDialog(tips);

                                            // 多次出货后，在出货串口成功返回的中出请求出货成功逻辑接口
                                            pickSuccess(cardNo);
                                        }
                                    }
                                });
                            }
                        }).commadTakeOut(list.get(flag));
                    } else {
                        showTipDialog("错误提示：此卡无权取货。");
                    }
                } else {
                    // 无数据
                    showTipDialog("错误提示：" + response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<SrvResult<MPickQueryByRFID>> call, Throwable throwable) {
                // 异常
                showTipDialog("错误提示：网络异常。");
            }
        });

    }

    // 取货成功调用接口
    private void pickSuccess(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.pickSuccess(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), successNum);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                // TODO 提示出货成功，关闭取货页面，返回道首页
                promissionDialog.dismiss();
                TakeActivity.this.finish();

            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                // TODO 提示出货成功，关闭取货页面，返回道首页
                promissionDialog.dismiss();
                TakeActivity.this.finish();
            }
        });
    }


    // 借货成功确认
    private void borrowComplete(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.borrowComplete(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), successNum);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getData()) {
                    // 接口逻辑处理成功
                    showTipDialog("借货成功。");
                } else {
                    // 接口逻辑处理失败
                    showTipDialog("借货失败。");
                }
                // 提示出货成功，关闭取货页面，返回道首页
                promissionDialog.dismiss();
                TakeActivity.this.finish();
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                showTipDialog("错误提示：网络异常。");
            }
        });
    }

    // 还货成功确认
    private void backComplete(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.backComplete(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), successNum);
        LogCat.e("url = " + mRoadAction.request().url().toString());
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

                // TODO 提示出货成功，关闭取货页面，返回道首页
                promissionDialog.dismiss();
                TakeActivity.this.finish();
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                showTipDialog("错误提示：网络异常。");
            }
        });
    }


    // 此货道被谁借走
    private void getLastBorrowName() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<String>> mRoadAction = service.getLastBorrowName(SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode());
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<String>>() {
            @Override
            public void onResponse(Call<SrvResult<String>> call, Response<SrvResult<String>> response) {
                // TODO 提示 是谁借走 此货柜的货物
            }

            @Override
            public void onFailure(Call<SrvResult<String>> call, Throwable throwable) {
                showTipDialog("错误提示：网络异常。");
            }
        });
    }

    // 借货 还货 请求
    private void authBorrowAndBack(final String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);

        int BorrowBackType = 0;
        if (SeekerSoftConstant.Back.equals(ActionType)) {
            // 还货
            BorrowBackType = 1;
        } else {
            // 借货
            BorrowBackType = 0;
        }

        final Call<SrvResult<Boolean>> mRoadAction = service.authBorrowAndBack(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), BorrowBackType);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, final Response<SrvResult<Boolean>> response) {
                // && response.body() != null && response.body().getData()
                if (response != null) {
                    // 操作串口格子柜

                    // 硬件编号，用户出货
                    int realRoad = mRoad.getRealCode();
                    ShipmentCommad shipmentCommad = new ShipmentCommad(11);
                    shipmentCommad.setGEZI(true);

                    if (mRoad != null && "1".equals(mRoad.getCabType())) {
                        // 格子柜
                        // TODO 数量不可操作
                    }
                    // TODO 判断串口是否出货成功

                    VendingSerialPort.SingleInit().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                        @Override
                        public void onDataReceiveString(final String ResultStr) {
                            // TODO 判断借还成功
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(ResultStr)) {
                                        showTipDialog("借还成功。");
                                        // 提交借还成功接口
                                        if (SeekerSoftConstant.Borrow.equals(ActionType)) {
                                            // 借成功
                                            borrowComplete(cardNo);
                                        } else {
                                            // 还成功
                                            backComplete(cardNo);
                                        }
                                    } else {
                                        showTipDialog("串口错误提示：");
                                    }
                                }
                            });

                        }
                    }).commadTakeOut(shipmentCommad);


                } else {

                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                showTipDialog("错误提示：网络异常。");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

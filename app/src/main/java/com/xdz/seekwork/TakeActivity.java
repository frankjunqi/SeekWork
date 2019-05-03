package com.xdz.seekwork;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private TextView tv_title;

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
    private LinearLayout ll_progress;

    private MRoad mRoad = null;

    // 选择的数量
    private int choostNum = 1;

    private int successNum = 0;

    private TextView tv_tips;
    private MaterialDialog tipDialog;

    private CardReadSerialPort.OnDataReceiveListener onDataReceiveListener;

    private void showTipDialog(String tips) {
        tv_tips.setText(tips);
        if (tipDialog != null && !tipDialog.isShowing()) {
            tipDialog.show();
        }
        tipDialog.show();
        new DownTimer().start();
    }

    private void showTipDialog(String tips, boolean closePage) {
        if (closePage) {
            // 关闭 取货dilog
            if (promissionDialog != null && promissionDialog.isShowing()) {
                promissionDialog.dismiss();
            }
        }
        tv_tips.setText(tips);
        if (tipDialog != null && !tipDialog.isShowing()) {
            tipDialog.show();
        }
        tipDialog.show();
        new DownTimer(closePage).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take);

        ActionType = getIntent().getStringExtra(SeekerSoftConstant.ActionType);
        tv_take_back = findViewById(R.id.tv_take_back);
        tv_take_back.setOnClickListener(this);

        tv_title = findViewById(R.id.tv_title);
        if (SeekerSoftConstant.Take.equals(ActionType)) {
            tv_title.setText("取货");
        } else if (SeekerSoftConstant.Borrow.equals(ActionType)) {
            tv_title.setText("借货");
        } else {
            tv_title.setText("还货");
        }

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
        ll_progress = customView.findViewById(R.id.ll_progress);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        // 刷卡后续操作
        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();

        WindowManager.LayoutParams wlp = promissionDialog.getWindow().getAttributes();
        wlp.width = widthPixels - 100;
        wlp.height = heightPixels / 5 * 4;
        promissionDialog.getWindow().setAttributes(wlp);

        promissionDialog.setCancelable(false);


        // dialog tip
        View customViewTip = inflater.inflate(R.layout.pop_tips_layout, null);
        tv_tips = customViewTip.findViewById(R.id.tv_tips);
        tipDialog = new MaterialDialog.Builder(this).customView(customViewTip, false).build();

        WindowManager.LayoutParams wl = tipDialog.getWindow().getAttributes();
        wl.width = widthPixels / 5 * 4;
        wl.height = heightPixels / 2;
        tipDialog.getWindow().setAttributes(wl);

        tipDialog.setCancelable(false);

        onDataReceiveListener = new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(final String IDNUM) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 读卡之后不接收读卡信息
                        CardReadSerialPort.SingleInit().setOnDataReceiveListener(null);

                        // 初始化界面
                        iv_card.setVisibility(View.GONE);
                        ll_progress.setVisibility(View.VISIBLE);

                        // 有货道 ＋ 有库存 请求接口 判断是否有权限出货
                        if (SeekerSoftConstant.Back.equals(ActionType) || SeekerSoftConstant.Borrow.equals(ActionType)) {
                            int borrowBackType = 0;
                            if (SeekerSoftConstant.Back.equals(ActionType)) {
                                // 还货
                                borrowBackType = 1;
                            } else {
                                // 借货
                                borrowBackType = 0;
                            }
                            // 格子柜 借 还
                            if ("1".equals(mRoad.getCabType())) {
                                // 操作 借 还 柜子
                                authBorrowAndBack(IDNUM, borrowBackType);
                            } else {
                                showTipDialog("提示：此货道不可进行借还操作。");
                            }
                        } else if (SeekerSoftConstant.Take.equals(ActionType)) {
                            // 格子柜 螺纹柜 取货
                            // 根据卡号 机器号 请求是否可以取货接口
                            pickQueryByRFID(IDNUM);
                        }
                    }
                });

            }
        };

        kbv.setSureClickListen(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 初始化记录信息
                mRoad = null;
                choostNum = 1;
                successNum = 0;

                String keyBoradRoad = kbv.getKeyBoradStr();

                if (TextUtils.isEmpty(keyBoradRoad)) {
                    // 检验
                    showTipDialog("提示：请输入货道号");
                    return;
                }

                for (int i = 0; list != null && i < list.size(); i++) {
                    if (!TextUtils.isEmpty(keyBoradRoad) && keyBoradRoad.equals(list.get(i).getBorderRoad())) {
                        mRoad = list.get(i);
                        break;
                    }
                }

                if (mRoad == null) {
                    // TODO 没有货道提示
                    showTipDialog("提示：当前货道暂未启用。");
                } else {
                    if ("1".equals(mRoad.getCabType()) && mRoad.getQty() == 0 && SeekerSoftConstant.Borrow.equals(ActionType)) {
                        // 格子柜 & 库存 ＝＝ 0 & 借操作
                        // 调用查询被谁借走的接口
                        getLastBorrowName();
                    } else if ("1".equals(mRoad.getCabType()) && SeekerSoftConstant.Back.equals(ActionType) && mRoad.getQty() > 0) {
                        // 格子柜 & 库存 > 0 & 还操作
                        showTipDialog("提示：此格子柜中物品已经归还。");
                    } else if (mRoad.getQty() == 0 && SeekerSoftConstant.Take.equals(ActionType)) {
                        // 格子柜 螺纹柜 取货功能
                        // 螺纹柜 & 库存 == 0  ＝＝> 提示无库存
                        showTipDialog("提示：当前货道暂无库存。");
                    } else {
                        // 设置dialog中数据
                        iv_card.setVisibility(View.VISIBLE);
                        ll_progress.setVisibility(View.INVISIBLE);

                        tv_qty.setText(String.valueOf(mRoad.getQty()));
                        tv_productname.setText(mRoad.getProductName());
                        tv_choose_num.setText(String.valueOf(choostNum));

                        if ("1".equals(mRoad.getCabType())) {
                            tv_add.setTextColor(getResources().getColor(R.color.un_title));
                            tv_cut.setTextColor(getResources().getColor(R.color.un_title));
                        } else {
                            tv_add.setTextColor(getResources().getColor(R.color.title));
                            tv_cut.setTextColor(getResources().getColor(R.color.title));
                        }


                        // 开去串口读卡器
                        CardReadSerialPort.SingleInit().setOnDataReceiveListener(onDataReceiveListener);

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
            if (mRoad != null && "2".equals(mRoad.getCabType())) {
                // 螺纹
                if (choostNum == 1) {
                    // 提示最少取1件
                    choostNum = 1;
                    tv_cut.setTextColor(getResources().getColor(R.color.un_title));
                    tv_add.setTextColor(getResources().getColor(R.color.title));
                    //showTipDialog("提示：最少要取1件。");
                } else {
                    choostNum = choostNum - 1;
                    tv_cut.setTextColor(getResources().getColor(R.color.title));
                    tv_add.setTextColor(getResources().getColor(R.color.title));
                }
            } else {
                // 格子 借还操作
                // showTipDialog("提示：借还柜，不可操作取货数量。");
            }
            tv_choose_num.setText(String.valueOf(choostNum));
        } else if (v.getId() == R.id.tv_add) {
            if (mRoad != null && "2".equals(mRoad.getCabType())) {
                // 螺纹
                if (choostNum == mRoad.getQty()) {
                    // 提示最多只能库存数
                    choostNum = mRoad.getQty();
                    tv_add.setTextColor(getResources().getColor(R.color.un_title));
                    tv_cut.setTextColor(getResources().getColor(R.color.title));
                    //showTipDialog("提示：最多只能取最大库存数。");
                } else {
                    tv_add.setTextColor(getResources().getColor(R.color.title));
                    tv_cut.setTextColor(getResources().getColor(R.color.title));
                    choostNum = choostNum + 1;
                }
            } else {
                // 格子 借还操作
                // showTipDialog("提示：借还柜，不可操作取货数量。");
            }
            tv_choose_num.setText(String.valueOf(choostNum));
        } else if (v.getId() == R.id.tv_back) {
            // 弹出框操作中返回取消按钮
            if (promissionDialog != null && promissionDialog.isShowing()) {
                promissionDialog.dismiss();
            }
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
                    // TODO 可以让输入货道可点击
                }
            }

            @Override
            public void onFailure(Call<SrvResult<List<MRoad>>> call, Throwable throwable) {
                // 异常
                showTipDialog("提示：网络异常。");
            }
        });

    }

    private int flag = 0;

    // 刷卡取货
    private void pickQueryByRFID(final String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        final Call<SrvResult<MPickQueryByRFID>> mRoadAction = service.pickQueryByRFID(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), choostNum);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<MPickQueryByRFID>>() {
            @Override
            public void onResponse(Call<SrvResult<MPickQueryByRFID>> call, Response<SrvResult<MPickQueryByRFID>> response) {
                if (response != null && response.body() != null && response.body().getData() != null) {
                    // 成功逻辑
                    if (response.body().getData().isAuthorize()) {
                        // 硬件编号，用户出货
                        int realRoad = mRoad.getRealCode();

                        // 记住出货第几个
                        flag = 0;

                        final List<ShipmentCommad> list = new ArrayList<>();
                        if ("1".equals(mRoad.getCabType())) {
                            // 格子(去除测试代码)
                            ShipmentCommad shipmentCommad = new ShipmentCommad(11);
                            shipmentCommad.setGEZI(true);
                            list.add(shipmentCommad);
                        } else {
                            // TODO 螺纹 (去除测试代码)
                            for (int i = 0; i < choostNum; i++) {
                                list.add(new ShipmentCommad(45));
                            }
                        }


                        VendingSerialPort.SingleInit().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                            @Override
                            public void onDataReceiveString(final String ResultStr) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 出货次数统计
                                        flag++;

                                        // 判断出货是否成功（格子 and 螺纹）
                                        if ("1".equals(mRoad.getCabType())) {
                                            // TODO 格子串口返回数据 （成功）（失败）

                                        } else {
                                            // TODO 螺纹串口返回数据 （成功）（失败）
                                        }

                                        if (!TextUtils.isEmpty(ResultStr)) {
                                            successNum++;
                                        }


                                        // 看下是否还有货要出
                                        // 调用出货串口(统计多次发送出货命令)
                                        if (list.size() > flag) {
                                            VendingSerialPort.SingleInit().commadTakeOut(list.get(flag));
                                        } else {
                                            // 螺纹柜 不接受串口返回逻辑
                                            VendingSerialPort.SingleInit().setOnDataReceiveListener(null);

                                            // 根据出货数量 与 用户选择的进行比较：如果 出货成功次数==需求数量 则显示 "全部出货完成";
                                            // 如果 出货成功次数==0 则显示 "全部出货失败";
                                            // 如果 出货成功次数>0 && 出货成功次数!= 需求数量 则显示 "部分出货成功";
                                            String tips = "";
                                            if ("1".equals(mRoad.getCabType())) {
                                                // 格子
                                                if (successNum == 1) {
                                                    tips = "提示：柜门打开成功。";
                                                    pickSuccess(cardNo);
                                                } else {
                                                    tips = "提示：柜门打开失败。";
                                                }
                                            } else {
                                                // 螺纹
                                                if (choostNum == successNum) {
                                                    tips = "提示：全部出货完成。";
                                                } else if (successNum == 0) {
                                                    tips = "提示：全部出货失败。";
                                                } else {
                                                    tips = "提示：" + successNum + "个物品出货成功，" + (choostNum - successNum) + "个物品出货失败。";
                                                }
                                                pickSuccess(cardNo);
                                            }
                                            showTipDialog(tips, true);
                                        }
                                    }
                                });
                            }
                        }).commadTakeOut(list.get(flag));
                    } else {
                        showTipDialog("提示：此卡无权取货。", true);
                    }
                } else {
                    // 无数据
                    showTipDialog("提示：" + response.body().getMsg() + "。", true);
                }
            }

            @Override
            public void onFailure(Call<SrvResult<MPickQueryByRFID>> call, Throwable throwable) {
                // 异常
                showTipDialog("提示：网络异常。", true);
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
                // 提示出货成功，关闭取货页面，返回道首页

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
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
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
        Call<SrvResult<Boolean>> mRoadAction = service.backComplete(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), 1);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {

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
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<String>>() {
            @Override
            public void onResponse(Call<SrvResult<String>> call, Response<SrvResult<String>> response) {
                // 提示 是谁借走 此货柜的货物
                if (response != null && response.body() != null) {
                    showTipDialog("提示：当前货道已借出，已被 " + response.body().getData() + "借出。");
                } else {
                    showTipDialog("提示：查询最后一次借出信息失败。");
                }
            }

            @Override
            public void onFailure(Call<SrvResult<String>> call, Throwable throwable) {
                showTipDialog("提示：网络异常。");
            }
        });
    }

    // 格子柜 借货 还货 请求
    private void authBorrowAndBack(final String cardNo, int borrowBackType) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        final Call<SrvResult<Boolean>> mRoadAction = service.authBorrowAndBack(cardNo, SeekerSoftConstant.MachineNo, mRoad.getCabNo(), mRoad.getRoadCode(), borrowBackType);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, final Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getData()) {
                    // 操作格子柜 ，硬件编号，用户出货
                    int realRoad = mRoad.getRealCode();
                    // TODO 测试数据删除
                    ShipmentCommad shipmentCommad = new ShipmentCommad(11);
                    shipmentCommad.setGEZI(true);

                    VendingSerialPort.SingleInit().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
                        @Override
                        public void onDataReceiveString(final String ResultStr) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 格子柜，不处理出货结果。
                                    VendingSerialPort.SingleInit().setOnDataReceiveListener(null);

                                    // TODO 判断串口是否出货成功 返回数据校验，提示串口失败原因。
                                    if (!TextUtils.isEmpty(ResultStr)) {
                                        // 提交借还成功接口
                                        if (SeekerSoftConstant.Borrow.equals(ActionType)) {
                                            showTipDialog("提示：借货成功。", true);
                                            // 借成功
                                            borrowComplete(cardNo);
                                        } else {
                                            showTipDialog("提示：还货成功。", true);
                                            // 还成功
                                            backComplete(cardNo);
                                        }
                                    } else {
                                        showTipDialog("提示：串口操作失败。", true);
                                    }
                                }
                            });

                        }
                    }).commadTakeOut(shipmentCommad);
                } else {
                    showTipDialog("提示：此卡无权取货。", true);
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                showTipDialog("提示：网络异常。");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class DownTimer extends CountDownTimer {

        private boolean closePage = false;

        public DownTimer() {
            super(3000, 1000);
        }

        public DownTimer(boolean closePage) {
            super(3000, 1000);
            this.closePage = closePage;
        }

        @Override
        public void onTick(long millusUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (tipDialog != null && tipDialog.isShowing()) {
                tipDialog.dismiss();
            }
            if (closePage) {
                // 关闭当前activity页面
                TakeActivity.this.finish();
            }
        }

    }

}

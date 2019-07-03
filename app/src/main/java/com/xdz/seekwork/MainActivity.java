package com.xdz.seekwork;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xdz.seekwork.network.api.Host;
import com.xdz.seekwork.network.api.SeekWorkService;
import com.xdz.seekwork.network.api.SrvResult;
import com.xdz.seekwork.network.entity.ResultObj;
import com.xdz.seekwork.network.entity.seekwork.MMachineInfo;
import com.xdz.seekwork.network.gsonfactory.GsonConverterFactory;
import com.xdz.seekwork.serialport.CardReadSerialPort;
import com.xdz.seekwork.serialport.VendingSerialPort;
import com.xdz.seekwork.util.LogCat;
import com.xdz.seekwork.util.SeekerSoftConstant;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 首页
public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {


    //private StatusBarManager mStatusBarManager;

    private RelativeLayout customView;

    private Button btn_take, btn_borrow, btn_back;

    private TextView tv_error;

    private MaterialDialog promissionDialog;

    private ProgressBar pb_loadingdata;
    private Button btn_try;

    private TextView tv_num, tv_name;

    public final static String[] PERMS_WRITE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS};

    private CardReadSerialPort cardReadSerialPort;

    private CountDownTimer timer = new CountDownTimer(10000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            if (btn_try != null) {
                btn_try.setText("不可操作，" + (millisUntilFinished / 1000) + "秒后自动重试");
            }
        }

        @Override
        public void onFinish() {
            btn_try.setEnabled(true);
            btn_try.setText("重试");
            loadRegisterMachine();
            ResultObj resultObj = null;
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // dialog tip
        customView = (RelativeLayout) inflater.inflate(R.layout.activity_main, null);
        setContentView(customView);

        //mStatusBarManager = (StatusBarManager) mContext.getSystemService(Context.STATUS_BAR_SERVICE);

        //hideNavigation();
        //prohibitDropDown();
        //showNavigation();
        btn_take = findViewById(R.id.btn_take);
        btn_take.setOnClickListener(this);
        btn_borrow = findViewById(R.id.btn_borrow);
        btn_borrow.setOnClickListener(this);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);


        Drawable drawable_take = getResources().getDrawable(R.drawable.icon_take); //获取图片
        drawable_take.setBounds(0, 0, 140, 140);  //设置图片参数
        btn_take.setCompoundDrawables(null, drawable_take, null, null);


        Drawable drawable_back = getResources().getDrawable(R.drawable.icon_back_); //获取图片
        drawable_back.setBounds(0, 0, 140, 140);  //设置图片参数
        btn_back.setCompoundDrawables(null, drawable_back, null, null);

        Drawable drawable_borrow = getResources().getDrawable(R.drawable.icon_borrow); //获取图片
        drawable_borrow.setBounds(0, 0, 140, 140);  //设置图片参数
        btn_borrow.setCompoundDrawables(null, drawable_borrow, null, null);

        tv_num = findViewById(R.id.tv_num);
        tv_name = findViewById(R.id.tv_name);

        TextView tv_num = findViewById(R.id.tv_num);
        tv_num.setOnClickListener(this);

        View customView = inflater.inflate(R.layout.pop_auth_layout, null);

        pb_loadingdata = customView.findViewById(R.id.pb_loadingdata);
        tv_error = customView.findViewById(R.id.tv_error);

        TextView tv_machine = customView.findViewById(R.id.tv_machine);
        tv_machine.setText("设备号：" + SeekerSoftConstant.DEVICEID);
        btn_try = customView.findViewById(R.id.btn_try);
        btn_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRegisterMachine();
            }
        });

        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        setWindowUIHide(promissionDialog.getWindow());
        promissionDialog.setCancelable(false);

        // 授权弹框
        registerMachine();

        //EasyPermissions.requestPermissions(this, "请求权限", 12, PERMS_WRITE);

        //startActivity(new Intent(this, ManageActivity.class));
    }

    private void loadRegisterMachine() {
        // 加载进度
        tv_error.setText("");
        pb_loadingdata.setVisibility(View.VISIBLE);
        btn_try.setVisibility(View.GONE);
        // 请求接口
        registerMachine();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_take) {
            Intent intent = new Intent(MainActivity.this, TakeActivity.class);
            intent.putExtra(SeekerSoftConstant.ActionType, SeekerSoftConstant.Take);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_borrow) {
            Intent intent = new Intent(MainActivity.this, TakeActivity.class);
            intent.putExtra(SeekerSoftConstant.ActionType, SeekerSoftConstant.Borrow);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_back) {
            Intent intent = new Intent(MainActivity.this, TakeActivity.class);
            intent.putExtra(SeekerSoftConstant.ActionType, SeekerSoftConstant.Back);
            startActivity(intent);
        }
    }

    private void registerMachine() {

        // 初始化串口设备
        CardReadSerialPort cardReadSerialPort = CardReadSerialPort.SingleInit();
        VendingSerialPort vendingSerialPort = VendingSerialPort.SingleInit();
        String error = "";
        if (cardReadSerialPort == null) {
            error = "读卡器串口打开失败。\n";
        }

        if (vendingSerialPort == null) {
            error = error + "柜子串口打开失败";
        }

        if (!TextUtils.isEmpty(error)) {
            if (!promissionDialog.isShowing()) {
                promissionDialog.show();
            }
            tv_error.setText("错误：\n" + error);
            pb_loadingdata.setVisibility(View.GONE);
            btn_try.setVisibility(View.VISIBLE);

            // 开启定时器 10秒再发送一次请求直到成功 才关闭这个模态框 关闭这个重试定时器
            btn_try.setEnabled(false);
            timer.start();
            return;
        }

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<MMachineInfo>> updateAction = service.getMachineInfo(SeekerSoftConstant.DEVICEID);
        LogCat.e("url = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SrvResult<MMachineInfo>>() {
            @Override
            public void onResponse(Call<SrvResult<MMachineInfo>> call, Response<SrvResult<MMachineInfo>> response) {
                if (response != null && response.body() != null && response.body().getStatus() == 1 && response.body().getData() != null && response.body().getData().isAuthorize()) {
                    LogCat.e("Status: " + response.body().getStatus());
                    SeekerSoftConstant.MachineNo = response.body().getData().getMachineNo();
                    tv_num.setText("设备编号：" + response.body().getData().getMachineNo());
                    tv_name.setText("紧急联系人：" + response.body().getData().getContacts() + "  " + response.body().getData().getNumbers());
                    // 成功授权显示逻辑
                    promissionDialog.dismiss();
                    // 成功授权取消加载进度
                    pb_loadingdata.setVisibility(View.GONE);
                    btn_try.setVisibility(View.VISIBLE);

                } else {
                    if (!promissionDialog.isShowing()) {
                        promissionDialog.show();
                    }
                    tv_error.setText("错误：" + response.body().getMsg());
                    pb_loadingdata.setVisibility(View.GONE);
                    btn_try.setVisibility(View.VISIBLE);

                    // 开启定时器 10秒再发送一次请求直到成功 才关闭这个模态框 关闭这个重试定时器
                    btn_try.setEnabled(false);
                    timer.start();

                }
            }

            @Override
            public void onFailure(Call<SrvResult<MMachineInfo>> call, Throwable throwable) {
                if (!promissionDialog.isShowing()) {
                    promissionDialog.show();
                }

                if (throwable != null) {
                    tv_error.setText("错误：" + throwable.getMessage());
                } else {
                    tv_error.setText("未知错误，请联系管理员。");
                }

                pb_loadingdata.setVisibility(View.GONE);
                btn_try.setVisibility(View.VISIBLE);

                // 开启定时器 10秒再发送一次请求直到成功 才关闭这个模态框 关闭这个重试定时器
                btn_try.setEnabled(false);
                timer.start();
            }
        });
    }

    private void loginValidate(final String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> updateAction = service.loginValidate(SeekerSoftConstant.MachineNo, cardNo);
        LogCat.e("url = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getStatus() == 1 && response.body().getData()) {
                    // 打开管理员页面
                    Intent intent = new Intent(MainActivity.this, ManageActivity.class);
                    intent.putExtra("cardNo", cardNo);
                    startActivity(intent);
                } else {
                    // 不是管理员卡，不需要做任何操作
                    LogCat.e("提示信息：" + response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CardReadSerialPort.SingleInit() != null) {
            CardReadSerialPort.SingleInit().setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
                @Override
                public void onDataReceiveString(String IDNUM) {
                    loginValidate(IDNUM);
                }
            });
        }


    }


    /**
     * 方法不能用，会导致屏幕不可触摸
     *
     * @return
     */
    public boolean hideNavigation() {
        boolean ishide;
        try {
            String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            ishide = true;
        } catch (Exception ex) {
            ishide = false;
        }
        return ishide;
    }

    /**
     * 方法不能用，会导致屏幕不可触摸
     *
     * @return
     */
    public boolean showNavigation() {
        boolean isshow;
        try {
            String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            isshow = true;
        } catch (Exception e) {
            isshow = false;
            e.printStackTrace();
        }
        return isshow;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CardReadSerialPort.SingleInit() != null) {
            CardReadSerialPort.SingleInit().setOnDataReceiveListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //allowDropDown();
        System.exit(0);//直接结束程序
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int exitFlag = intent.getIntExtra(SeekerSoftConstant.EXITAPP, 0);
        if (exitFlag == 1) {
            // 退出程序
            if (CardReadSerialPort.SingleInit() != null) {
                CardReadSerialPort.SingleInit().closeSerialPort();
            }

            if (VendingSerialPort.SingleInit() != null) {
                VendingSerialPort.SingleInit().closeSerialPort();
            }
            //showNavigation();
            this.finish();
        }
    }

    //禁止下拉
    private void prohibitDropDown() {
        manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        manager.addView(customView, localLayoutParams);
    }

    WindowManager manager;

    //允许下拉
    private void allowDropDown() {
        manager.removeView(customView);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}

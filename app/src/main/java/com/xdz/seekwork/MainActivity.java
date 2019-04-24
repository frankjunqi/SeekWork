package com.xdz.seekwork;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xdz.seekwork.network.api.Host;
import com.xdz.seekwork.network.api.SeekWorkService;
import com.xdz.seekwork.network.api.SrvResult;
import com.xdz.seekwork.network.entity.seekwork.MMachineInfo;
import com.xdz.seekwork.network.gsonfactory.GsonConverterFactory;
import com.xdz.seekwork.util.LogCat;
import com.xdz.seekwork.util.SeekerSoftConstant;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 首页
public class MainActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private Button btn_take, btn_borrow, btn_back;

    private TextView tv_error;

    private MaterialDialog promissionDialog;

    private ProgressBar pb_loadingdata;
    private Button btn_try;

    private TextView tv_num, tv_name;

    public final static String[] PERMS_WRITE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_take = findViewById(R.id.btn_take);
        btn_take.setOnClickListener(this);
        btn_borrow = findViewById(R.id.btn_borrow);
        btn_borrow.setOnClickListener(this);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        tv_num = findViewById(R.id.tv_num);
        tv_name = findViewById(R.id.tv_name);

        TextView tv_num = findViewById(R.id.tv_num);
        tv_num.setOnClickListener(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.pop_auth_layout, null);

        pb_loadingdata = customView.findViewById(R.id.pb_loadingdata);
        tv_error = customView.findViewById(R.id.tv_error);

        TextView tv_machine = customView.findViewById(R.id.tv_machine);
        tv_machine.setText("设备号：" + SeekerSoftConstant.DEVICEID);
        btn_try = customView.findViewById(R.id.btn_try);
        btn_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 加载进度
                tv_error.setText("");
                pb_loadingdata.setVisibility(View.VISIBLE);
                btn_try.setVisibility(View.GONE);
                // 请求接口
                registerMachine();
            }
        });

        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        promissionDialog.setCancelable(false);

        // 授权弹框
        registerMachine();

        EasyPermissions.requestPermissions(this, "请求权限", 12, PERMS_WRITE);
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
        } else if (v.getId() == R.id.tv_num) {
            Intent intent = new Intent(MainActivity.this, ManageActivity.class);
            startActivity(intent);

        }
    }

    private void registerMachine() {
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<MMachineInfo>> updateAction = service.getMachineInfo(SeekerSoftConstant.DEVICEID);
        LogCat.e("getSynchroBaseData = " + updateAction.request().url().toString());
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

                    // TODO 检查是否是管理员
                    loginValidate("1234r43234rdc");

                } else {
                    if (!promissionDialog.isShowing()) {
                        promissionDialog.show();
                    }
                    tv_error.setText("错误：" + response.body().getMsg());
                    pb_loadingdata.setVisibility(View.GONE);
                    btn_try.setVisibility(View.VISIBLE);
                    // TODO 开启定时器 10秒再发送一次请求直到成功 才关闭这个模态框 关闭这个重试定时器

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
            }
        });
    }

    private void loginValidate(final String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> updateAction = service.loginValidate(SeekerSoftConstant.MachineNo, cardNo);
        LogCat.e("loginValidate = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getStatus() == 1 && response.body().getData()) {
                    // TODO 打开管理员页面
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
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}

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
import com.xdz.seekwork.network.api.SeekerSoftService;
import com.xdz.seekwork.network.entity.takeout.TakeOutResBody;
import com.xdz.seekwork.network.gsonfactory.GsonConverterFactory;
import com.xdz.seekwork.test.TestNewVendingActivity;
import com.xdz.seekwork.test.TestReadActivity;
import com.xdz.seekwork.util.DeviceInfoTool;
import com.xdz.seekwork.util.LogCat;
import com.xdz.seekwork.util.SeekerSoftConstant;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 首页
public class MainActivity extends AppCompatActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks {

    private Button btn_take, btn_borrow, btn_back;

    private String deviceStr;

    private MaterialDialog promissionDialog;

    private ProgressBar pb_loadingdata;
    private Button btn_try;

    public final static String[] PERMS_WRITE ={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_SETTINGS};
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

        TextView tv_num = findViewById(R.id.tv_num);
        tv_num.setOnClickListener(this);

        deviceStr = DeviceInfoTool.getDeviceId();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.pop_auth_layout, null);

        pb_loadingdata = customView.findViewById(R.id.pb_loadingdata);

        TextView tv_machine = customView.findViewById(R.id.tv_machine);
        tv_machine.setText("设备号：" + deviceStr);
        btn_try = customView.findViewById(R.id.btn_try);
        btn_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 加载进度
                pb_loadingdata.setVisibility(View.VISIBLE);
                btn_try.setVisibility(View.GONE);
                // 请求接口
                registerMachine();
            }
        });

        promissionDialog = new MaterialDialog.Builder(this).customView(customView, false).build();
        promissionDialog.setCancelable(false);

        // 授权弹框
        promissionDialog.show();

        EasyPermissions.requestPermissions(this,"请求权限",12,PERMS_WRITE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_take) {
            Intent intent = new Intent(MainActivity.this, TakeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_borrow) {
            Intent intent = new Intent(MainActivity.this, TestReadActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_back) {
            Intent intent = new Intent(MainActivity.this, TestNewVendingActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tv_num) {
            Intent intent = new Intent(MainActivity.this, ManageActivity.class);
            startActivity(intent);

        }
    }

    private void registerMachine() {
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<TakeOutResBody> updateAction = service.takeOut(SeekerSoftConstant.DEVICEID, "", "", "");
        LogCat.e("getSynchroBaseData = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<TakeOutResBody>() {
            @Override
            public void onResponse(Call<TakeOutResBody> call, Response<TakeOutResBody> response) {
                if (response != null && response.body() != null && response.body().status != 201) {

                } else {

                }
                // 成功授权显示逻辑
                promissionDialog.dismiss();
                // 成功授权取消加载进度
                pb_loadingdata.setVisibility(View.GONE);
                btn_try.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<TakeOutResBody> call, Throwable throwable) {
                pb_loadingdata.setVisibility(View.GONE);
                btn_try.setVisibility(View.VISIBLE);
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

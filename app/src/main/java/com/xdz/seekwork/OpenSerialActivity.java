package com.xdz.seekwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xdz.seekwork.network.api.Host;
import com.xdz.seekwork.network.api.SeekWorkService;
import com.xdz.seekwork.network.api.SrvResult;
import com.xdz.seekwork.network.entity.seekwork.MRoad;
import com.xdz.seekwork.network.gsonfactory.GsonConverterFactory;
import com.xdz.seekwork.util.LogCat;
import com.xdz.seekwork.util.SeekerSoftConstant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 打开串口
public class OpenSerialActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_zhu, btn_a, btn_b, btn_c;
    private TextView tv_take_back;

    private String cardNo;
    private MaterialDialog tipDialog;

    private ArrayList<MRoad> zhuList = new ArrayList<>();

    private ArrayList<MRoad> aList = new ArrayList<>();

    private ArrayList<MRoad> bList = new ArrayList<>();

    private ArrayList<MRoad> cList = new ArrayList<>();

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

    }

    private void showTipDialog(String tips) {
        tipDialog = new MaterialDialog.Builder(this).content(tips).build();
        tipDialog.show();
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
                break;
            case R.id.btn_a:
                break;
            case R.id.btn_b:
                break;
            case R.id.btn_c:
                break;
            case R.id.tv_take_back:
                OpenSerialActivity.this.finish();
                break;

        }

    }
}

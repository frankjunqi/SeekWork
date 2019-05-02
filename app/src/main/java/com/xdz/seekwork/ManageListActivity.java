package com.xdz.seekwork;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xdz.seekwork.adpter.ItemListAdapter;
import com.xdz.seekwork.adpter.model.Item;
import com.xdz.seekwork.adpter.model.StuItem;
import com.xdz.seekwork.network.api.Host;
import com.xdz.seekwork.network.api.SeekWorkService;
import com.xdz.seekwork.network.api.SrvResult;
import com.xdz.seekwork.network.entity.seekwork.MNum;
import com.xdz.seekwork.network.entity.seekwork.MReplenish;
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

// 货道补货
public class ManageListActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView tv_take_back;

    private Button btn_zhugui, btn_a, btn_b, btn_c, btn_sure;

    private SmartRefreshLayout refreshLayout;
    private ListView listView;
    private ItemListAdapter itemListAdapter;

    private MaterialDialog tipDialog;
    private List<MRoad> list;

    private ArrayList<Item> itemZhuList = new ArrayList<>();
    private ArrayList<MRoad> zhuList = new ArrayList<>();

    private ArrayList<Item> itemAList = new ArrayList<>();
    private ArrayList<MRoad> aList = new ArrayList<>();

    private ArrayList<Item> itemBList = new ArrayList<>();
    private ArrayList<MRoad> bList = new ArrayList<>();

    private ArrayList<Item> itemCList = new ArrayList<>();
    private ArrayList<MRoad> cList = new ArrayList<>();

    // 0: 主柜 // 1：A // 2：B // 3: C
    private int currentFlag = 0;

    private String cardNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_list);

        cardNo = getIntent().getStringExtra("cardNo");

        tv_take_back = findViewById(R.id.tv_take_back);
        tv_take_back.setOnClickListener(this);

        btn_zhugui = findViewById(R.id.btn_zhugui);
        btn_zhugui.setOnClickListener(this);
        btn_a = findViewById(R.id.btn_a);
        btn_a.setOnClickListener(this);
        btn_b = findViewById(R.id.btn_b);
        btn_b.setOnClickListener(this);
        btn_c = findViewById(R.id.btn_c);
        btn_c.setOnClickListener(this);

        btn_sure = findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);

        listView = findViewById(R.id.listView);
        itemListAdapter = new ItemListAdapter(this, null);
        listView.setAdapter(itemListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        // 自动刷新
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.setNoMoreData(false);
                getProList();
            }
        });

        // 触发自动刷新
        refreshLayout.autoRefresh();

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
                    list = response.body().getData();

                    itemZhuList.clear();
                    zhuList.clear();

                    itemAList.clear();
                    aList.clear();

                    itemBList.clear();
                    bList.clear();

                    itemCList.clear();
                    cList.clear();

                    for (int i = 0; i < list.size(); i++) {
                        String cabNo = list.get(i).getCabNo();
                        if ("主柜".equals(cabNo)) {
                            zhuList.add(list.get(i));
                            itemZhuList.add(new StuItem(list.get(i)));
                        } else if ("A".equals(cabNo)) {
                            aList.add(list.get(i));
                            itemAList.add(new StuItem(list.get(i)));
                        } else if ("B".equals(cabNo)) {
                            bList.add(list.get(i));
                            itemBList.add(new StuItem(list.get(i)));
                        } else if ("C".equals(cabNo)) {
                            cList.add(list.get(i));
                            itemCList.add(new StuItem(list.get(i)));
                        }
                    }

                    itemListAdapter.notifyDataRefresh(itemZhuList);
                    // 成功刷新
                    refreshLayout.finishRefresh(0, true);

                    refreshLayout.finishLoadMoreWithNoMoreData();
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
            case R.id.btn_zhugui:
                currentFlag = 0;
                itemListAdapter.notifyDataRefresh(itemZhuList);
                break;
            case R.id.btn_a:
                currentFlag = 1;
                itemListAdapter.notifyDataRefresh(itemAList);
                break;
            case R.id.btn_b:
                currentFlag = 2;
                itemListAdapter.notifyDataRefresh(itemBList);
                break;
            case R.id.btn_c:
                currentFlag = 3;
                itemListAdapter.notifyDataRefresh(itemCList);
                break;
            case R.id.btn_sure:
                pushReplian();
                break;
            case R.id.tv_take_back:
                this.finish();
                break;
        }
    }

    private void pushReplian() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);

        MReplenish mReplenish = new MReplenish();
        mReplenish.setCardNo(cardNo);
        mReplenish.setMachineCode(SeekerSoftConstant.MachineNo);
        List<MNum> RodeList = new ArrayList<>();

        if (currentFlag == 0) {
            // 主柜
            for (int i = 0; i < zhuList.size(); i++) {
                MNum mNum = new MNum();
                mNum.setNo(zhuList.get(i).getNo());
                mNum.setQty(zhuList.get(i).getQty());
                mNum.setRoadCode(zhuList.get(i).getRoadCode());
                RodeList.add(mNum);
            }

        } else if (currentFlag == 1) {
            // A
            for (int i = 0; i < aList.size(); i++) {
                MNum mNum = new MNum();
                mNum.setNo(aList.get(i).getNo());
                mNum.setQty(aList.get(i).getQty());
                mNum.setRoadCode(aList.get(i).getRoadCode());
                RodeList.add(mNum);
            }
        } else if (currentFlag == 2) {
            // B
            for (int i = 0; i < bList.size(); i++) {
                MNum mNum = new MNum();
                mNum.setNo(bList.get(i).getNo());
                mNum.setQty(bList.get(i).getQty());
                mNum.setRoadCode(bList.get(i).getRoadCode());
                RodeList.add(mNum);
            }
        } else if (currentFlag == 3) {
            // C
            for (int i = 0; i < cList.size(); i++) {
                MNum mNum = new MNum();
                mNum.setNo(cList.get(i).getNo());
                mNum.setQty(cList.get(i).getQty());
                mNum.setRoadCode(cList.get(i).getRoadCode());
                RodeList.add(mNum);
            }
        }

        mReplenish.setRodeList(RodeList);

        Call<SrvResult<Boolean>> mRoadAction = service.replenish(mReplenish);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getData() != null && response.body().getData()) {
                    showTipDialog("补货成功");
                } else {
                    showTipDialog("补货失败");
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                showTipDialog("网络异常");
            }
        });
    }

}

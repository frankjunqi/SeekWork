package com.xdz.seekwork.adpter.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdz.seekwork.R;
import com.xdz.seekwork.adpter.model.Item;
import com.xdz.seekwork.adpter.model.StuItem;

/**
 */

public class StuView extends RelativeLayout implements ItemView {

    private Context mContext;

    private GradientDrawable gd;

    private TextView tv_name;
    private TextView tv_huodao;
    private TextView tv_bu_num;
    private TextView tv_diff_num;

    private TextView tv_add, tv_cut;

    public StuView(Context context) {
        this(context, null);
    }

    public StuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(40);

    }

    @Override
    public void prepareItemView() {
        tv_name = findViewById(R.id.tv_name);
        tv_huodao = findViewById(R.id.tv_huodao);
        tv_diff_num = findViewById(R.id.tv_diff_num);
        tv_bu_num = findViewById(R.id.tv_bu_num);
        tv_add = findViewById(R.id.tv_add);
        tv_cut = findViewById(R.id.tv_cut);

    }

    @Override
    public void setObject(Item item, int positon) {

        final StuItem stuItem = (StuItem) item;
        if (stuItem == null) {
            return;
        }

        if (stuItem.mRoad != null) {
            tv_huodao.setText(String.valueOf(stuItem.mRoad.getRoadCode()));
            tv_name.setText(stuItem.mRoad.getProductName());
            tv_bu_num.setText(String.valueOf(stuItem.mRoad.getLackNum()));
            tv_diff_num.setText(String.valueOf(stuItem.mRoad.getChaLackNum()));
        }

        tv_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stuItem.mRoad.getChaLackNum() >= stuItem.mRoad.getLackNum()) {
                    // 不可以在加

                } else {
                    stuItem.mRoad.setChaLackNum(stuItem.mRoad.getChaLackNum() + 1);
                }
                tv_diff_num.setText(String.valueOf(stuItem.mRoad.getChaLackNum()));
            }
        });

        tv_cut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stuItem.mRoad.getChaLackNum() == 0) {
                    // 不可以在减少
                } else {
                    stuItem.mRoad.setChaLackNum(stuItem.mRoad.getChaLackNum() - 1);
                }
                tv_diff_num.setText(String.valueOf(stuItem.mRoad.getChaLackNum()));
            }
        });


    }
}

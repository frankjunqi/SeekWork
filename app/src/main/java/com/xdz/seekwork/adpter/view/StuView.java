package com.xdz.seekwork.adpter.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
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
            //tv_diff_num.setText(String.valueOf(stuItem.mRoad.getLackNum()));
        }


    }
}

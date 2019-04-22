package com.xdz.seekwork.adpter.view;

import android.content.Context;
import android.content.Intent;
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
    private TextView tv_sex;
    private TextView tv_school;
    private TextView tv_class;

    private Button btn_handle;


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
        tv_sex = findViewById(R.id.tv_sex);
        tv_school = findViewById(R.id.tv_school);
        tv_class = findViewById(R.id.tv_class);
        btn_handle = findViewById(R.id.btn_handle);


    }

    @Override
    public void setObject(Item item, int positon) {

        final StuItem stuItem = (StuItem) item;
        if (stuItem == null) {
            return;
        }


    }
}

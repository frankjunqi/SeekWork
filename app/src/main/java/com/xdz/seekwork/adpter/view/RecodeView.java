package com.xdz.seekwork.adpter.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdz.seekwork.R;
import com.xdz.seekwork.adpter.model.Item;
import com.xdz.seekwork.adpter.model.RecodeItem;


/**
 * Created by kjh08490 on 2017/6/16.
 */

public class RecodeView extends RelativeLayout implements ItemView {

    private Context mContext;

    private GradientDrawable gd;

    private TextView tv_title;
    private TextView tv_time;
    private TextView tv_subtitle;


    public RecodeView(Context context) {
        this(context, null);
    }

    public RecodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(40);

    }

    @Override
    public void prepareItemView() {
        tv_title = findViewById(R.id.tv_title);
        tv_subtitle = findViewById(R.id.tv_subtitle);
        tv_time = findViewById(R.id.tv_time);
    }

    @Override
    public void setObject(Item item, int positon) {

        RecodeItem recodeItem = (RecodeItem) item;
        if (recodeItem == null) {
            return;
        }


    }
}

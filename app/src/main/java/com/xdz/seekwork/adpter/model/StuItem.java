package com.xdz.seekwork.adpter.model;

import android.content.Context;
import android.view.ViewGroup;

import com.xdz.seekwork.R;
import com.xdz.seekwork.adpter.view.ItemView;
import com.xdz.seekwork.network.entity.seekwork.MRoad;

public class StuItem extends Item {

    public MRoad mRoad;

    public StuItem(MRoad mRoad) {
        this.mRoad = mRoad;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.stu_view, parent);
    }
}

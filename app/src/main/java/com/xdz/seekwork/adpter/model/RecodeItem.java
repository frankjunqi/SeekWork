package com.xdz.seekwork.adpter.model;

import android.content.Context;
import android.view.ViewGroup;

import com.xdz.seekwork.R;
import com.xdz.seekwork.adpter.view.ItemView;


public class RecodeItem extends Item {

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.recode_view, parent);
    }
}

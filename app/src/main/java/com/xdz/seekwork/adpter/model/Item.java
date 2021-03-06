package com.xdz.seekwork.adpter.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xdz.seekwork.adpter.view.ItemView;


public abstract class Item {
    /**
     * Set to true when this item is enabled
     */
    public boolean enabled;

    /**
     * Create a new item.
     */
    public Item() {
        // By default, an item is enabled
        enabled = true;
    }

    public abstract ItemView newView(Context context, ViewGroup parent);

    protected static ItemView createCellFromXml(Context context, int layoutID, ViewGroup parent) {
        return (ItemView) LayoutInflater.from(context).inflate(layoutID, parent, false);
    }
}

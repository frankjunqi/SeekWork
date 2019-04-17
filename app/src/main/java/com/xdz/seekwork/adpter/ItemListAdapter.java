package com.xdz.seekwork.adpter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.xdz.seekwork.adpter.model.Item;
import com.xdz.seekwork.adpter.view.ItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemListAdapter extends BaseAdapter {

    public static final String TAG = "ItemListAdapter";

    // 最大的viewtype的数量
    private static final int DEFAULT_MAX_VIEW_TYPE_COUNT = 30;

    private static class TypeInfo {
        int count;
        int type;
    }

    private List<Item> mItems;
    private HashMap<Class<? extends Item>, TypeInfo> mTypes;
    private Context mContext;
    private int mMaxViewTypeCount;

    public ItemListAdapter(Context context, List<Item> items) {
        this(context, items, DEFAULT_MAX_VIEW_TYPE_COUNT);
    }

    public ItemListAdapter(Context context, List<Item> items, int maxViewTypeCount) {
        mContext = context;
        mItems = items;
        mTypes = new HashMap<>();
        mMaxViewTypeCount = Integer.MAX_VALUE;
        if (mItems != null) {
            for (Item item : mItems) {
                addItem(item);
            }
        }
        mMaxViewTypeCount = Math.max(1, Math.max(mTypes.size(), maxViewTypeCount));
    }

    private void addItem(Item item) {
        final Class<? extends Item> klass = item.getClass();
        TypeInfo info = mTypes.get(klass);
        if (info == null) {
            final int type = mTypes.size();
            if (type >= mMaxViewTypeCount) {
                mMaxViewTypeCount = type;
            }
            final TypeInfo newInfo = new TypeInfo();
            newInfo.count = 1;
            newInfo.type = type;
            mTypes.put(klass, newInfo);
        } else {
            info.count++;
        }
    }

    @Override
    public int getCount() {
        if (mItems != null) {
            return mItems.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mItems != null) {
            return mItems.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mTypes != null && mTypes.get(getItem(position).getClass()) != null) {
            return mTypes.get(getItem(position).getClass()).type;
        } else {
            return 0;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return ((Item) getItem(position)).enabled;
    }

    @Override
    public int getViewTypeCount() {
        return mMaxViewTypeCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Item item = (Item) getItem(position);
        ItemView cell = (ItemView) convertView;
        if (cell == null) {
            cell = item.newView(mContext, null);
            cell.prepareItemView();
        }
        cell.setObject(item, position);
        return (View) cell;
    }

    public void notifyDataLoadMore(List<Item> mItems) {
        if (mItems != null) {
            if (this.mItems == null) {
                this.mItems = new ArrayList<>();
            }
            this.mItems.addAll(mItems);
            if (mTypes == null) {
                mTypes = new HashMap<>();
            }
            mMaxViewTypeCount = Integer.MAX_VALUE;
            for (Item item : mItems) {
                addItem(item);
            }
            mMaxViewTypeCount = Math.max(1, Math.max(mTypes.size(), DEFAULT_MAX_VIEW_TYPE_COUNT));
            notifyDataSetChanged();
        }
    }

    public void notifyDataRefresh(List<Item> mItems) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        if (this.mItems == null) {
            this.mItems = new ArrayList<>();
        }
        this.mItems.clear();
        this.mItems.addAll(mItems);
        if (mTypes == null) {
            mTypes = new HashMap<>();
        }
        mMaxViewTypeCount = Integer.MAX_VALUE;
        for (Item item : mItems) {
            addItem(item);
        }
        mMaxViewTypeCount = Math.max(1, Math.max(mTypes.size(), DEFAULT_MAX_VIEW_TYPE_COUNT));
        notifyDataSetChanged();
    }

    public void notifyDataRemove(Item item) {
        if (item == null) {
            return;
        }
        this.mItems.remove(item);
        notifyDataSetChanged();
    }


}

package com.xdz.seekwork.adpter.view;


import com.xdz.seekwork.adpter.model.Item;

public interface ItemView {
    /**
     * 初始化view
     */
    void prepareItemView();

    /**
     * 初始化数据
     *
     * @param item
     */
    void setObject(Item item, int positon);
}

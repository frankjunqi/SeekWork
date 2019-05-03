package com.xdz.seekwork.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdz.seekwork.R;


/**
 */

public class KeyBordView extends RelativeLayout implements View.OnClickListener {

    private TextView tv_showstr;

    private Button btn_a, btn_b, btn_c, btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9;

    private Button btn_clear, btn_back, btn_sure;

    private StringBuilder enterBuild = new StringBuilder();

    private String[] keys = {"A", "B", "C", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public KeyBordView(Context context) {
        this(context, null, 0);
    }

    public KeyBordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyBordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public String getKeyBoradStr() {
        return enterBuild.toString();
    }

    public void setKeyWordHint(String hint) {
        if (tv_showstr != null && !TextUtils.isEmpty(hint)) {
            tv_showstr.setHint(hint);
        }
    }

    private void initView(Context context) {
        inflate(context, R.layout.keybord_view, this);
        tv_showstr = (TextView) findViewById(R.id.tv_showstr);

        btn_a = (Button) findViewById(R.id.btn_a);
        btn_a.setOnClickListener(this);
        btn_b = (Button) findViewById(R.id.btn_b);
        btn_b.setOnClickListener(this);
        btn_c = (Button) findViewById(R.id.btn_c);
        btn_c.setOnClickListener(this);

        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_0.setOnClickListener(this);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_1.setOnClickListener(this);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_2.setOnClickListener(this);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_3.setOnClickListener(this);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_4.setOnClickListener(this);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_5.setOnClickListener(this);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_6.setOnClickListener(this);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_7.setOnClickListener(this);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_8.setOnClickListener(this);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_9.setOnClickListener(this);

        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_sure = (Button) findViewById(R.id.btn_sure);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_a:
                enterBuild.append(keys[0]);
                setEnable();
                break;
            case R.id.btn_b:
                enterBuild.append(keys[1]);
                setEnable();
                break;
            case R.id.btn_c:
                enterBuild.append(keys[2]);
                setEnable();
                break;
            case R.id.btn_0:
                enterBuild.append(keys[3]);
                setEnable();
                break;
            case R.id.btn_1:
                enterBuild.append(keys[4]);
                setEnable();
                break;
            case R.id.btn_2:
                enterBuild.append(keys[5]);
                setEnable();
                break;
            case R.id.btn_3:
                enterBuild.append(keys[6]);
                setEnable();
                break;
            case R.id.btn_4:
                enterBuild.append(keys[7]);
                setEnable();
                break;
            case R.id.btn_5:
                enterBuild.append(keys[8]);
                setEnable();
                break;
            case R.id.btn_6:
                enterBuild.append(keys[9]);
                setEnable();
                break;
            case R.id.btn_7:
                enterBuild.append(keys[10]);
                setEnable();
                break;
            case R.id.btn_8:
                enterBuild.append(keys[11]);
                setEnable();
                break;
            case R.id.btn_9:
                enterBuild.append(keys[12]);
                setEnable();
                break;
            case R.id.btn_clear:
                if (enterBuild.toString().length() > 0) {
                    enterBuild.delete(0, enterBuild.toString().length());
                }
                setEnable();
                break;
            case R.id.btn_back:
                if (enterBuild.toString().length() > 0) {
                    enterBuild.delete(enterBuild.toString().length() - 1, enterBuild.toString().length());
                }
                setEnable();
                break;
        }
        tv_showstr.setText(enterBuild.toString());
    }


    private void setEnable() {
        String str = getKeyBoradStr();
        if (str.contains("A") || str.contains("B") || str.contains("C")) {
            btn_a.setEnabled(false);
            btn_b.setEnabled(false);
            btn_c.setEnabled(false);
        } else {
            btn_a.setEnabled(true);
            btn_b.setEnabled(true);
            btn_c.setEnabled(true);
        }
    }

    public void setSureClickListen(OnClickListener onClickListener) {
        if (btn_sure != null) {
            btn_sure.setOnClickListener(onClickListener);
        }
    }
}

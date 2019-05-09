package com.xdz.seekwork.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdz.seekwork.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownView extends LinearLayout {
    private static final int UPDATE_UI_CODE = 101;
    private Context context;
    private TextView hourTv;
    private TextView minuteTv;
    private TextView secondTv;
    private TextView hourColonTv;
    private TextView minuteColonTv;
    private long timeStamp;
    private boolean isContinue;
    private ExecutorService mExecutorService;
    private Handler myHandler;
    private CountDownView.CountDownEndListener countDownEndListener;

    public CountDownView(Context context) {
        this(context, (AttributeSet)null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isContinue = false;
        this.mExecutorService = Executors.newSingleThreadExecutor();
        this.myHandler = new CountDownView.MyHandler(this);
        this.context = context;
        this.init();
    }

    private void init() {
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(16);
        this.hourTv = new TextView(this.context);
        this.hourTv.setTextColor(Color.parseColor("#FFFFFF"));
        this.hourTv.setBackgroundColor(Color.parseColor("#FF7198"));
        this.hourTv.setTextSize(12.0F);
        this.hourTv.setGravity(17);
        this.addView(this.hourTv);
        this.hourColonTv = new TextView(this.context);
        this.hourColonTv.setTextColor(Color.parseColor("#FF7198"));
        this.hourColonTv.setTextSize(12.0F);
        this.hourColonTv.setText("");
        this.hourColonTv.setGravity(17);
        this.addView(this.hourColonTv);
        this.minuteTv = new TextView(this.context);
        this.minuteTv.setTextColor(Color.parseColor("#FFFFFF"));
        this.minuteTv.setBackgroundColor(Color.parseColor("#FF7198"));
        this.minuteTv.setTextSize(12.0F);
        this.minuteTv.setGravity(17);
        this.addView(this.minuteTv);
        this.minuteColonTv = new TextView(this.context);
        this.minuteColonTv.setTextColor(Color.parseColor("#FF7198"));
        this.minuteColonTv.setTextSize(12.0F);
        this.minuteColonTv.setText("");
        this.minuteColonTv.setGravity(17);
        this.addView(this.minuteColonTv);
        this.secondTv = new TextView(this.context);
        this.secondTv.setTextColor(Color.parseColor("#FFFFFF"));
        this.secondTv.setBackgroundColor(Color.parseColor("#FF7198"));
        this.secondTv.setTextSize(12.0F);
        this.secondTv.setGravity(17);
        this.addView(this.secondTv);
    }

    public CountDownView setTimeTvWH(int width, int height) {
        if (width > 0 && height > 0) {
            LayoutParams params = new android.widget.LinearLayout.LayoutParams(width, height);
            this.hourTv.setLayoutParams(params);
            this.minuteTv.setLayoutParams(params);
            this.secondTv.setLayoutParams(params);
        }

        return this;
    }

    public CountDownView setTimeTvSize(float size) {
        this.hourTv.setTextSize(size);
        this.minuteTv.setTextSize(size);
        this.secondTv.setTextSize(size);
        return this;
    }

    public CountDownView setTimeTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.hourTv.setTextColor(color);
        this.minuteTv.setTextColor(color);
        this.secondTv.setTextColor(color);
        return this;
    }

    public CountDownView setTimeTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.hourTv.setBackgroundColor(color);
        this.minuteTv.setBackgroundColor(color);
        this.secondTv.setBackgroundColor(color);
        return this;
    }

    public CountDownView setTimeTvBackgroundRes(int res) {
        this.hourTv.setBackgroundResource(res);
        this.minuteTv.setBackgroundResource(res);
        this.secondTv.setBackgroundResource(res);
        return this;
    }

    public CountDownView setTimeTvBackground(Drawable drawable) {
        if (drawable != null && VERSION.SDK_INT >= 16) {
            this.hourTv.setBackground(drawable);
            this.minuteTv.setBackground(drawable);
            this.secondTv.setBackground(drawable);
        }

        return this;
    }

    public CountDownView setTimeTvGravity(CountDownView.CountDownViewGravity countDownViewGravity) {
        int gravity = 17;
        if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = 80;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_CENTER) {
            gravity = 17;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_LEFT) {
            gravity = 8388611;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = 8388613;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_TOP) {
            gravity = 48;
        }

        this.hourTv.setGravity(gravity);
        this.minuteTv.setGravity(gravity);
        this.secondTv.setGravity(gravity);
        return this;
    }

    public CountDownView setColonTvWH(int width, int height) {
        LayoutParams params = new android.widget.LinearLayout.LayoutParams(width, height);
        this.hourColonTv.setLayoutParams(params);
        this.minuteColonTv.setLayoutParams(params);
        return this;
    }

    public CountDownView setColonTvSize(float size) {
        this.hourColonTv.setTextSize(size);
        this.minuteColonTv.setTextSize(size);
        return this;
    }

    public CountDownView setColonTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.hourColonTv.setTextColor(color);
        this.minuteColonTv.setTextColor(color);
        return this;
    }

    public CountDownView setColonTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.hourColonTv.setBackgroundColor(color);
        this.minuteColonTv.setBackgroundColor(color);
        return this;
    }

    public CountDownView setColonTvBackgroundRes(int res) {
        this.hourColonTv.setBackgroundResource(res);
        this.minuteColonTv.setBackgroundResource(res);
        return this;
    }

    public CountDownView setColonTvBackground(Drawable drawable) {
        if (drawable != null && VERSION.SDK_INT >= 16) {
            this.hourColonTv.setBackground(drawable);
            this.minuteColonTv.setBackground(drawable);
        }

        return this;
    }

    public CountDownView setColonTvGravity(CountDownView.CountDownViewGravity countDownViewGravity) {
        int gravity = 17;
        if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = 80;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_CENTER) {
            gravity = 17;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_LEFT) {
            gravity = 8388611;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = 8388613;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_TOP) {
            gravity = 48;
        }

        this.hourColonTv.setGravity(gravity);
        this.minuteColonTv.setGravity(gravity);
        return this;
    }

    public CountDownView setHourTvSize(int width, int height) {
        LayoutParams hourParams = (LayoutParams) this.hourTv.getLayoutParams();
        if (hourParams != null) {
            if (width > 0) {
                hourParams.width = width;
            }

            if (height > 0) {
                hourParams.height = height;
            }

            this.hourTv.setLayoutParams(hourParams);
        }

        return this;
    }

    public CountDownView setHourTvBackgroundRes(int res) {
        this.hourTv.setBackgroundResource(res);
        return this;
    }

    public CountDownView setHourTvBackground(Drawable drawable) {
        if (drawable != null && VERSION.SDK_INT >= 16) {
            this.hourTv.setBackground(drawable);
        }

        return this;
    }

    public CountDownView setHourTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.hourTv.setBackgroundColor(color);
        return this;
    }

    public CountDownView setHourTvTextSize(float size) {
        this.hourTv.setTextSize(size);
        return this;
    }

    public CountDownView setHourTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.hourTv.setTextColor(color);
        return this;
    }

    public CountDownView setHourTvGravity(CountDownView.CountDownViewGravity countDownViewGravity) {
        int gravity = 17;
        if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = 80;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_CENTER) {
            gravity = 17;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_LEFT) {
            gravity = 8388611;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = 8388613;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_TOP) {
            gravity = 48;
        }

        this.hourTv.setGravity(gravity);
        return this;
    }

    public CountDownView setHourTvPadding(int left, int top, int right, int bottom) {
        this.hourTv.setPadding(left, top, right, bottom);
        return this;
    }

    public CountDownView setHourTvMargins(int left, int top, int right, int bottom) {
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(-2, -2);
        params.setMargins(left, top, right, bottom);
        this.minuteTv.setLayoutParams(params);
        return this;
    }

    public CountDownView setHourTvBold(boolean bool) {
        this.hourTv.getPaint().setFakeBoldText(bool);
        return this;
    }

    public CountDownView setMinuteTvSize(int width, int height) {
        LayoutParams minuteParams = (LayoutParams) this.minuteTv.getLayoutParams();
        if (minuteParams != null) {
            if (width > 0) {
                minuteParams.width = width;
            }

            if (height > 0) {
                minuteParams.height = height;
            }

            this.minuteTv.setLayoutParams(minuteParams);
        }

        return this;
    }

    public CountDownView setMinuteTvBackgroundRes(int res) {
        this.minuteTv.setBackgroundResource(res);
        return this;
    }

    public CountDownView setMinuteTvBackground(Drawable drawable) {
        if (drawable != null && VERSION.SDK_INT >= 16) {
            this.minuteTv.setBackground(drawable);
        }

        return this;
    }

    public CountDownView setMinuteTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.minuteTv.setBackgroundColor(color);
        return this;
    }

    public CountDownView setMinuteTvTextSize(float size) {
        this.minuteTv.setTextSize(size);
        return this;
    }

    public CountDownView setMinuteTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.minuteTv.setTextColor(color);
        return this;
    }

    public CountDownView setMinuteTvGravity(CountDownView.CountDownViewGravity countDownViewGravity) {
        int gravity = 17;
        if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = 80;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_CENTER) {
            gravity = 17;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_LEFT) {
            gravity = 8388611;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = 8388613;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_TOP) {
            gravity = 48;
        }

        this.minuteTv.setGravity(gravity);
        return this;
    }

    public CountDownView setMinuteTvPadding(int left, int top, int right, int bottom) {
        this.minuteTv.setPadding(left, top, right, bottom);
        return this;
    }

    public CountDownView setMinuteTvMargins(int left, int top, int right, int bottom) {
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(-2, -2);
        params.setMargins(left, top, right, bottom);
        this.minuteTv.setLayoutParams(params);
        return this;
    }

    public CountDownView setMinuteTvBold(boolean bool) {
        this.minuteTv.getPaint().setFakeBoldText(bool);
        return this;
    }

    public CountDownView setSecondTvSize(int width, int height) {
        LayoutParams secondParams = (LayoutParams) this.secondTv.getLayoutParams();
        if (secondParams != null) {
            if (width > 0) {
                secondParams.width = width;
            }

            if (height > 0) {
                secondParams.height = height;
            }

            this.secondTv.setLayoutParams(secondParams);
        }

        return this;
    }

    public CountDownView setSecondTvBackgroundRes(int res) {
        this.secondTv.setBackgroundResource(res);
        return this;
    }

    public CountDownView setSecondTvBackground(Drawable drawable) {
        if (drawable != null && VERSION.SDK_INT >= 16) {
            this.secondTv.setBackground(drawable);
        }

        return this;
    }

    public CountDownView setSecondTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.secondTv.setBackgroundColor(color);
        return this;
    }

    public CountDownView setSecondTvTextSize(float size) {
        this.secondTv.setTextSize(size);
        return this;
    }

    public CountDownView setSecondTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.secondTv.setTextColor(color);
        return this;
    }

    public CountDownView setSecondTvGravity(CountDownView.CountDownViewGravity countDownViewGravity) {
        int gravity = 17;
        if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = 80;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_CENTER) {
            gravity = 17;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_LEFT) {
            gravity = 8388611;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = 8388613;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_TOP) {
            gravity = 48;
        }

        this.secondTv.setGravity(gravity);
        return this;
    }

    public CountDownView setSecondTvPadding(int left, int top, int right, int bottom) {
        this.secondTv.setPadding(left, top, right, bottom);
        return this;
    }

    public CountDownView setSecondTvMargins(int left, int top, int right, int bottom) {
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(-2, -2);
        params.setMargins(left, top, right, bottom);
        this.secondTv.setLayoutParams(params);
        return this;
    }

    public CountDownView setSecondTvBold(boolean bool) {
        this.secondTv.getPaint().setFakeBoldText(bool);
        return this;
    }

    public CountDownView setHourColonTvSize(int width, int height) {
        LayoutParams hourColonParams = (LayoutParams) this.hourColonTv.getLayoutParams();
        if (hourColonParams != null) {
            if (width > 0) {
                hourColonParams.width = width;
            }

            if (height > 0) {
                hourColonParams.height = height;
            }

            this.hourColonTv.setLayoutParams(hourColonParams);
        }

        return this;
    }

    public CountDownView setHourColonTvBackgroundRes(int res) {
        this.hourColonTv.setBackgroundResource(res);
        return this;
    }

    public CountDownView setHourColonTvBackground(Drawable drawable) {
        if (drawable != null && VERSION.SDK_INT >= 16) {
            this.hourColonTv.setBackground(drawable);
        }

        return this;
    }

    public CountDownView setHourColonTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.hourColonTv.setBackgroundColor(color);
        return this;
    }

    public CountDownView setHourColonTvTextSize(float size) {
        this.hourColonTv.setTextSize(size);
        return this;
    }

    public CountDownView setHourColonTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.hourColonTv.setTextColor(color);
        return this;
    }

    public CountDownView setHourColonTvGravity(CountDownView.CountDownViewGravity countDownViewGravity) {
        int gravity = 17;
        if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = 80;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_CENTER) {
            gravity = 17;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_LEFT) {
            gravity = 8388611;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = 8388613;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_TOP) {
            gravity = 48;
        }

        this.hourColonTv.setGravity(gravity);
        return this;
    }

    public CountDownView setHourColonTvPadding(int left, int top, int right, int bottom) {
        this.hourColonTv.setPadding(left, top, right, bottom);
        return this;
    }

    public CountDownView setHourColonTvMargins(int left, int top, int right, int bottom) {
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(-2, -2);
        params.setMargins(left, top, right, bottom);
        this.hourColonTv.setLayoutParams(params);
        return this;
    }

    public CountDownView setHourColonTvBold(boolean bool) {
        this.hourColonTv.getPaint().setFakeBoldText(bool);
        return this;
    }

    public CountDownView setMinuteColonTvSize(int width, int height) {
        LayoutParams minuteColonParams = (LayoutParams) this.minuteColonTv.getLayoutParams();
        if (minuteColonParams != null) {
            if (width > 0) {
                minuteColonParams.width = width;
            }

            if (height > 0) {
                minuteColonParams.height = height;
            }

            this.minuteColonTv.setLayoutParams(minuteColonParams);
        }

        return this;
    }

    public CountDownView setMinuteColonTvBackgroundRes(int res) {
        this.minuteColonTv.setBackgroundResource(res);
        return this;
    }

    public CountDownView setMinuteColonTvBackground(Drawable drawable) {
        if (drawable != null && VERSION.SDK_INT >= 16) {
            this.minuteColonTv.setBackground(drawable);
        }

        return this;
    }

    public CountDownView setMinuteColonTvBackgroundColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.minuteColonTv.setBackgroundColor(color);
        return this;
    }

    public CountDownView setMinuteColonTvTextSize(float size) {
        this.minuteColonTv.setTextSize(size);
        return this;
    }

    public CountDownView setMinuteColonTvTextColorHex(String colorHex) {
        int color = Color.parseColor(colorHex);
        this.minuteColonTv.setTextColor(color);
        return this;
    }

    public CountDownView setMinuteColonTvGravity(CountDownView.CountDownViewGravity countDownViewGravity) {
        int gravity = 17;
        if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_BOTTOM) {
            gravity = 80;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_CENTER) {
            gravity = 17;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_LEFT) {
            gravity = 8388611;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_RIGHT) {
            gravity = 8388613;
        } else if (countDownViewGravity == CountDownView.CountDownViewGravity.GRAVITY_TOP) {
            gravity = 48;
        }

        this.minuteColonTv.setGravity(gravity);
        return this;
    }

    public CountDownView setMinuteColonTvPadding(int left, int top, int right, int bottom) {
        this.minuteColonTv.setPadding(left, top, right, bottom);
        return this;
    }

    public CountDownView setMinuteColonTvMargins(int left, int top, int right, int bottom) {
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(-2, -2);
        params.setMargins(left, top, right, bottom);
        this.minuteColonTv.setLayoutParams(params);
        return this;
    }

    public CountDownView setMinuteColonTvBold(boolean bool) {
        this.minuteColonTv.getPaint().setFakeBoldText(bool);
        return this;
    }

    public CountDownView setCountTime(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public CountDownView startCountDown() {
        if (this.timeStamp <= 1L) {
            this.isContinue = false;
        } else {
            this.isContinue = true;
            this.countDown();
        }

        return this;
    }

    public CountDownView pauseCountDown() {
        this.isContinue = false;
        return this;
    }

    public CountDownView stopCountDown() {
        this.timeStamp = 0L;
        return this;
    }

    private void countDown() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        if (CountDownView.this.isContinue) {
                            CountDownView.this.isContinue = CountDownView.this.timeStamp-- > 1L;
                            String[] times = StringUtil.secToTimes(CountDownView.this.timeStamp);
                            Message message = new Message();
                            message.obj = times;
                            message.what = 101;
                            CountDownView.this.myHandler.sendMessage(message);
                            Thread.sleep(1000L);
                            continue;
                        }

                        CountDownView.this.isContinue = true;
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }

                    return;
                }
            }
        });
        if (this.mExecutorService == null || this.mExecutorService.isShutdown()) {
            this.mExecutorService = Executors.newCachedThreadPool();
        }

        this.mExecutorService.execute(thread);
    }

    private void updateTvText(String text, TextView textView) {
        textView.setText(text);
    }

    public void setCountDownEndListener(CountDownView.CountDownEndListener countDownEndListener) {
        this.countDownEndListener = countDownEndListener;
    }

    public interface CountDownEndListener {
        void onCountDownEnd();
    }

    static class MyHandler extends Handler {
        private final WeakReference<CountDownView> mCountDownView;

        MyHandler(CountDownView countDownView) {
            this.mCountDownView = new WeakReference(countDownView);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CountDownView currentCountDownView = (CountDownView)this.mCountDownView.get();
            switch(msg.what) {
                case 101:
                    if (msg.obj != null) {
                        String[] times = (String[])((String[])msg.obj);

                        for(int i = 0; i < times.length; ++i) {
                            switch(i) {
                                case 0:
                                    currentCountDownView.updateTvText(times[0], currentCountDownView.hourTv);
                                    break;
                                case 1:
                                    currentCountDownView.updateTvText(times[1], currentCountDownView.minuteTv);
                                    break;
                                case 2:
                                    currentCountDownView.updateTvText(times[2], currentCountDownView.secondTv);
                            }
                        }
                    }

                    if (!currentCountDownView.isContinue && currentCountDownView.countDownEndListener != null) {
                        currentCountDownView.countDownEndListener.onCountDownEnd();
                    }
                default:
            }
        }
    }

    public static enum CountDownViewGravity {
        GRAVITY_CENTER,
        GRAVITY_LEFT,
        GRAVITY_RIGHT,
        GRAVITY_TOP,
        GRAVITY_BOTTOM;

        private CountDownViewGravity() {
        }
    }
}


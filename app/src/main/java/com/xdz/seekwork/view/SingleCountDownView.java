package com.xdz.seekwork.view;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.AttributeSet;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleCountDownView extends AppCompatTextView {
    private static final int UPDATE_UI_CODE = 101;
    private int retryInterval;
    private int time;
    private boolean isContinue;
    private ExecutorService mExecutorService;
    private String prefixText;
    private String timeColorHex;
    private String suffixText;
    private Handler myHandler;
    private SingleCountDownView.SingleCountDownEndListener singleCountDownEndListener;

    public SingleCountDownView(Context context) {
        this(context, (AttributeSet)null);
    }

    public SingleCountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleCountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.retryInterval = 60;
        this.time = 60;
        this.isContinue = true;
        this.mExecutorService = Executors.newSingleThreadExecutor();
        this.prefixText = "";
        this.timeColorHex = "#FF7198";
        this.suffixText = "";
        this.myHandler = new SingleCountDownView.MyHandler(this);
        this.init();
    }

    private void init() {
        this.setGravity(17);
        this.setText("");
    }

    public SingleCountDownView setTimeColorHex(String colorHex) {
        this.timeColorHex = colorHex;
        return this;
    }

    public SingleCountDownView setTimePrefixText(String prefixText) {
        this.prefixText = prefixText;
        return this;
    }

    public SingleCountDownView setTimeSuffixText(String suffixText) {
        this.suffixText = suffixText;
        return this;
    }

    public SingleCountDownView setTime(int time) {
        this.time = time;
        this.retryInterval = this.time;
        return this;
    }

    public SingleCountDownView startCountDown() {
        if (this.time <= 1) {
            this.isContinue = false;
        } else {
            this.isContinue = true;
            this.countDown();
        }

        return this;
    }

    public SingleCountDownView pauseCountDown() {
        this.isContinue = false;
        return this;
    }

    public SingleCountDownView stopCountDown() {
        this.time = 0;
        return this;
    }

    private void countDown() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        if (SingleCountDownView.this.isContinue) {
                            SingleCountDownView.this.isContinue = SingleCountDownView.this.time-- > 1;
                            String text = SingleCountDownView.this.prefixText + "<font color=" + SingleCountDownView.this.timeColorHex + ">" + SingleCountDownView.this.time + "</font>" + SingleCountDownView.this.suffixText;
                            Message message = new Message();
                            message.obj = text;
                            message.what = 101;
                            SingleCountDownView.this.myHandler.sendMessage(message);
                            Thread.sleep(1000L);
                            continue;
                        }

                        SingleCountDownView.this.isContinue = true;
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

    public void setSingleCountDownEndListener(SingleCountDownView.SingleCountDownEndListener singleCountDownEndListener) {
        this.singleCountDownEndListener = singleCountDownEndListener;
    }

    public interface SingleCountDownEndListener {
        void onSingleCountDownEnd();
    }

    static class MyHandler extends Handler {
        private final WeakReference<SingleCountDownView> mSingleCountDownView;

        MyHandler(SingleCountDownView singleCountDownView) {
            this.mSingleCountDownView = new WeakReference(singleCountDownView);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SingleCountDownView currentSingleCountDownView = (SingleCountDownView)this.mSingleCountDownView.get();
            switch(msg.what) {
                case 101:
                    if (msg.obj != null) {
                        currentSingleCountDownView.setText(Html.fromHtml(msg.obj.toString()));
                        if (currentSingleCountDownView.time < currentSingleCountDownView.retryInterval && currentSingleCountDownView.time > 0) {
                            currentSingleCountDownView.setEnabled(false);
                        } else {
                            currentSingleCountDownView.setEnabled(true);
                        }
                    }

                    if (!currentSingleCountDownView.isContinue && currentSingleCountDownView.singleCountDownEndListener != null) {
                        currentSingleCountDownView.singleCountDownEndListener.onSingleCountDownEnd();
                    }
                default:
            }
        }
    }
}


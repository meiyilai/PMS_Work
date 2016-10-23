package com.gzmelife.app.device;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Created by HHD on 2016/10/21.
 *
 */
public abstract class CountDownTimerHHD {

    private final long mMillisInFuture;
    private final long mCountdownInterval;
    private long mStopTimeInFuture;

    public CountDownTimerHHD(long millisInFuture, long countDownInterval){
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    public final void cancel(){
        mHandler.removeMessages(MSG);
    }

    public synchronized final CountDownTimerHHD start(){
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();

    private static final int MSG = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized (CountDownTimerHHD.this) {
                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (millisLeft <= 0) {
                    onFinish();
                } else if (millisLeft < mCountdownInterval) {
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);
                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();
                    while (delay < 0) delay += mCountdownInterval;
                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };

}

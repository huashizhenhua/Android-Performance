package org.zenip.oomandsolution;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class TimerThreadLeakActivity extends Activity {
    private static final String TAG = "CursorAndFileActivity";
    static final int DURATION = 20000;

    // 分配10M内存，来演示内存泄漏
    private byte[] buffer = new byte[1024 * 1024 * 10];

    public String helloWorld = "helloworld";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_thread_leak);
    }

    public void timerLoopWithStrongRerference(View v) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            // strong reference to TimerThreadLeakActivity
            @Override
            public void run() {
                Log.d(TAG, "timerLoopWithStrongRerference print = " + helloWorld);
            }
        }, 0, DURATION);
        Toast.makeText(this, "timerLoopWithStrongRerference don't cause leak", Toast.LENGTH_LONG).show();
    }

    public void timerLoopWithWeakReference(View view) {
        Timer t = new Timer();
        t.schedule(new WeakTimerTask(this), 0, DURATION);
        Toast.makeText(this, "timerLoopWithWeekReference don't cause leak", Toast.LENGTH_LONG).show();
    }

    public static class WeakTimerTask extends TimerTask {
        private WeakReference<TimerThreadLeakActivity> mActivity;

        public WeakTimerTask(TimerThreadLeakActivity activity) {
            mActivity = new WeakReference<TimerThreadLeakActivity>(activity);
        }

        @Override
        public void run() {
            final TimerThreadLeakActivity finalAct = mActivity.get();
            if (finalAct != null) {
                Log.d(TAG, "timerLoopWithStrongRerference print = " + finalAct.helloWorld);
            }
        }
    }
}

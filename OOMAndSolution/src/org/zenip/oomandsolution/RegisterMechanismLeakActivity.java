package org.zenip.oomandsolution;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class RegisterMechanismLeakActivity extends Activity {
    private static final String TAG = "CursorAndFileActivity";

    static final int DURATION = 20000;

    // 分配5M内存，来演示内存泄漏
    private byte[] buffer = new byte[1024 * 1024 * 10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mechanism);
    }


    public void registerBrocast(View view) {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        }, filter);
        Toast.makeText(this, "registerBrocast cause leak", Toast.LENGTH_LONG).show();
    }

}

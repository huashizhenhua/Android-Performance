package org.zenip.oomandsolution.cursorfile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.zenip.oomandsolution.R;
import org.zenip.oomandsolution.image.ImageHelper;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class CursorAndFileActivity extends Activity {

    private static final String TAG = "CursorAndFileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursor_file);
    }


    /**
     * 在Android4.1.2上测试，发现无内存泄漏问题
     * 
     * @param view
     */
    public void fileOpenNoClose(View view) {
        InputStream is = null;
        int br = 0;
        byte[] buffer = new byte[1024];
        try {
            is = getApplicationContext().getAssets().open("img_file.jpg");
            while ((br = is.read(buffer)) != -1) {
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private Handler handler = new Handler();

    /**
     * 测试结果：在Android4.1.2上测试，发现无内存泄漏问题。
     * 额外反馈：部分网友在测试时发现会出现内存泄漏。
     * 
     * Message是具备
     * 
     * @param view
     */
    public void messageNewWithOutObtain(View view) {
        Log.d(TAG, "=====>messageNewWithOutObtain");
        int k = 50;
        while ((k--) > 0) {
            handler.sendMessage(new Message());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 1000 * 80;
                long start = System.currentTimeMillis();
                while ((i--) > 1) {
                    handler.sendMessage(new Message());
                }
                long end = System.currentTimeMillis();
                System.out.println("---------->cost(new) = " + (end - start));


                i = 1000 * 80;
                start = System.currentTimeMillis();
                while ((i--) > 1) {
                    handler.sendMessage(Message.obtain());
                }
                end = System.currentTimeMillis();
                System.out.println("---------->cost(obtain) = " + (end - start));

            }
        }).start();
    }


    /**
     * 
     * 经过测试，cursor创建的内存还是能被垃圾回收的
     * 
     * @param view
     */
    public void cusorOpenNOClose(View view) {
        int count = 1000;
        while ((count--) > 0) {
            Cursor cursor = new ImageHelper().getImage(this);
        }

    }

    /**
     * 测试结果：在Android4.1.2上测试，发现无内存泄漏问题。
     * 
     * @param view
     */
    public void urlOpenNoClose(View view) {
        //        cusorOpenNOClose(view);
        //        messageNewWithOutObtain(view);
        Log.d(TAG, "=====>urlOpenNoClose");
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    URL url = new URL("http://www.baidu.com");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    is = conn.getInputStream();
                    byte[] buffer = new byte[1024];
                    int br = 0;
                    while ((br = is.read(buffer)) != -1) {
                    }
                    is = null;
                    buffer = null;
                    conn = null;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

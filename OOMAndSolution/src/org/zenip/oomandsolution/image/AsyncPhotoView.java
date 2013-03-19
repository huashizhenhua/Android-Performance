package org.zenip.oomandsolution.image;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

public class AsyncPhotoView extends PhotoView {
    static ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(5, 6, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100), new RejectedExecutionHandler() {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    });

    private Uri mImageUri;

    public AsyncPhotoView(Context context) {
        super(context);
    }

    public AsyncPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private Future<?> mFuture;


    public void setImageURIToFitScreen(final Uri uri) {
        final Uri curImageUri = mImageUri;
        final AsyncPhotoView me = this;
        if (uri == null || (!uri.equals(curImageUri))) {

            if (curImageUri != null) {
                // cancel load
                final Future<?> future = mFuture;
                if (future != null) {
                    future.cancel(false);
                    mFuture = null;
                }
            }

            if (uri != null) {
                // load new
                mFuture = sExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        BitmapDrawable bd;
                        bd = ImageHelper.getBitmapDrawableWithOutOOM(me.getContext(), uri);

                        System.out.println("setImageURIToFitScreen to thread pool) = " + bd);
                        if (bd != null) {
                            BitmapFactory.Options option = ImageHelper.getBitmapOptions(getContext(), curImageUri);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = bd;
                            mHandler.sendMessage(msg);
                        }
                        mFuture = null;
                    }
                });
            }

        }
        mImageUri = uri;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            if (msg.what == 1) {
                AsyncPhotoView.this.setImageDrawable((BitmapDrawable) msg.obj);
            }
            return false;
        }
    });
}

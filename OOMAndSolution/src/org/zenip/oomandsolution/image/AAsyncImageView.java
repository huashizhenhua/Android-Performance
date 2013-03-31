package org.zenip.oomandsolution.image;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AAsyncImageView extends ImageView {
    static ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(5, 6, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        }
    });


    private Future<?> mFuture;

    private boolean mDisableUsingImageUri = false;;
    private Uri mImageUri;


    public void setAsyncEnable(boolean b) {
        mDisableUsingImageUri = !b;
    }

    public AAsyncImageView(Context context) {
        super(context);
    }

    public AAsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AAsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageURI(final Uri uri) {

        if (mDisableUsingImageUri) {
            super.setImageURI(uri);
            return;
        }

        final Uri curImageUri = mImageUri;
        final AAsyncImageView me = this;
        setImageResource(android.R.drawable.ic_menu_gallery);
        if (uri != null) {
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
                        System.out.println("mFuture to thread pool");
                        final BitmapDrawable bd = ImageHelper.getBitmapDrawable(me.getContext(), uri);
                        System.out.println("mFuture to thread pool) = " + bd);

                        if (bd != null) {
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


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                AAsyncImageView.this.setImageDrawable((BitmapDrawable) msg.obj);
                AAsyncImageView.this.startAnimation(AnimationUtils.loadAnimation(getContext(), org.zenip.oomandsolution.R.anim.alpha_in));
            }
            return false;
        }
    });
}

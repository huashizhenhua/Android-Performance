package org.zenip.oomandsolution.image;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

public class AsyncPhotoView extends PhotoView {

    private static final String TAG = "AsyncPhotoView";

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
                        BitmapDrawable bd = ImageHelper.getBitmapDrawableWithOutOOM(me.getContext(), uri);
                        Log.d(TAG, "getBitmapDrawableWithOutOOM bd = " + bd);
                        if (bd != null) {
                            mPriOptions = ImageHelper.getBitmapOptions(getContext(), uri);
                            mCompressRect = bd.getBounds();
                            me.postInvalidate();
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

    private BitmapFactory.Options mPriOptions;
    private Rect mCompressRect;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final BitmapFactory.Options finalOp = mPriOptions;
        final Rect rect = mCompressRect;

        StringBuilder sb = new StringBuilder("原始比例(px)：");
        if (finalOp != null) {
            sb.append("" + finalOp.outWidth + "*" + finalOp.outHeight + ",");
        }
        if (rect != null) {
            sb.append("转化比例（px）：" + rect.width() + "*" + rect.height() + "");
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        canvas.drawText(sb.toString(), 0, 100, paint);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d(TAG, "handleMessage");
                AsyncPhotoView.this.setImageDrawable((BitmapDrawable) msg.obj);
            }
            return false;
        }
    });
}

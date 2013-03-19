package org.zenip.oomandsolution.image;

import java.io.File;

import org.zenip.oomandsolution.R;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ImageViewActivity extends Activity {

    private HackyViewPager mViewPager;

    private int startIndex;

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setContentView(R.layout.activity_image_view);

        ImageHelper helper = new ImageHelper();
        mCursor = helper.getImage(this);

        startIndex = getIntent().getIntExtra("startIndex", 0);

        mViewPager = (HackyViewPager) findViewById(R.id.viewpager);

        mViewPager.setOffscreenPageLimit(2);
        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                System.out.println("instantiateItem ViewGroup = " + position);

                final Cursor cursor = mCursor;
                LayoutInflater inflater = LayoutInflater.from(container.getContext());
                View view = inflater.inflate(R.layout.page_item_image, null);
                container.addView(view);

                AsyncPhotoView img = (AsyncPhotoView) view.findViewById(R.id.imageview);
                cursor.moveToPosition(position);

                String str = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

                img.setImageURIToFitScreen(Uri.fromFile(new File(str)));

                return view;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {
                return mCursor.getCount();
            }
        };

        mViewPager.setAdapter(adapter);

        mViewPager.setCurrentItem(startIndex);

        super.onCreate(savedInstanceState);
    }

    

    @Override
    protected void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        super.onDestroy();
    }
}

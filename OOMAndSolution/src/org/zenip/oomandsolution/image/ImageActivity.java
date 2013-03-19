package org.zenip.oomandsolution.image;

import org.zenip.oomandsolution.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ImageActivity extends Activity {

    private GridView mGrid;
    private Cursor mCursor;

    private class ImageItem {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        mGrid = new GridView(this);
        mGrid.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mGrid.setNumColumns(4);
        setContentView(mGrid);

        ImageHelper helper = new ImageHelper();
        mCursor = helper.getImage(this);


        final ImageCursorAdapter adapter = new ImageCursorAdapter(this, R.layout.grid_item_image_gallery, mCursor);

        mGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    final Cursor cursor = mCursor;

                    if (cursor != null) {
                        cursor.moveToPosition(arg2);
                        String str = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        System.out.println("setOnItemClickListener " + str);

                        Intent intent = new Intent(arg1.getContext(), ImageViewActivity.class);
                        intent.putExtra("startIndex", arg2);
                        arg1.getContext().startActivity(intent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        mGrid.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
        super.onDestroy();
    }
}

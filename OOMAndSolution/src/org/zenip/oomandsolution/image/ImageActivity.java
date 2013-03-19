package org.zenip.oomandsolution.image;

import org.zenip.oomandsolution.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ImageActivity extends Activity {

    private GridView mGrid;
    private Cursor mCursor;
    private ImageCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGrid = new GridView(this);
        mGrid.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mGrid.setNumColumns(4);
        setContentView(mGrid);

        ImageHelper helper = new ImageHelper();
        mCursor = helper.getImage(this);
        mAdapter = new ImageCursorAdapter(this, R.layout.grid_item_image_gallery, mCursor);
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


        mGrid.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "同步加载");
        menu.add(0, 2, 1, "异步加载，图片大小压缩，线程队列缓冲机制（未执行任务可取消）");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ImageCursorAdapter adapter = mAdapter;
        if (adapter != null) {
            switch (item.getItemId()) {
            case 1:
                adapter.isAsyncEnable = false;
                break;
            case 2:
                adapter.isAsyncEnable = true;
                break;
            default:
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
        super.onDestroy();
    }
}

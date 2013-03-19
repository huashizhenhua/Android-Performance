package org.zenip.oomandsolution.image;

import java.io.File;

import org.zenip.oomandsolution.R;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ResourceCursorAdapter;

public class ImageCursorAdapter extends ResourceCursorAdapter {

    private ImageHelper imageHelper = new ImageHelper();

    public ImageCursorAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c);
    }

    public boolean isAsyncEnable = true;

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String str = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        final AAsyncImageView textView = (AAsyncImageView) view.findViewById(R.id.textview);
        final Uri uri = Uri.fromFile(new File(str));
        textView.setAsyncEnable(isAsyncEnable);
        if (uri == null) {
            System.out.println("empty uri");
        }
        textView.setImageURI(uri);
    }
}

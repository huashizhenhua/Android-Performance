package org.zenip.oomandsolution;

import org.zenip.oomandsolution.cursorfile.CursorAndFileActivity;
import org.zenip.oomandsolution.image.ImageActivity;
import org.zenip.oomandsolution.image.ImageViewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OOMListFragment extends ListFragment {
    static final String[] options = { "Bitmap: Compress, Async, Cancelable", "Cursor And File: Rember close", "TimerThreadLeak: WeakReference avoid leak",
            "RgisterMechanismLeak: Remember to unregister" };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent();
        switch (position) {
        case 0:
            intent.setClass(getActivity(), ImageActivity.class);
            startActivity(intent);
            break;
        case 1:
            intent.setClass(getActivity(), CursorAndFileActivity.class);
            startActivity(intent);
            break;
        case 2:
            intent.setClass(getActivity(), TimerThreadLeakActivity.class);
            startActivity(intent);
            break;
        case 3:
            intent.setClass(getActivity(), RegisterMechanismLeakActivity.class);
            startActivity(intent);
            break;
        default:
            break;
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options));
        super.onViewCreated(view, savedInstanceState);
    }
}

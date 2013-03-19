package org.zenip.oomandsolution;

import org.zenip.oomandsolution.image.ImageActivity;
import org.zenip.oomandsolution.image.ImageViewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class OOMListFragment extends ListFragment {
    static final String[] options = { "ImageGallery", "ViewPager Sample" };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
        case 0:
            Intent intent = new Intent();
            intent.setClass(getActivity(), ImageActivity.class);
            startActivity(intent);
            break;

        default:
            break;
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options));
        super.onViewCreated(view, savedInstanceState);
    }

}

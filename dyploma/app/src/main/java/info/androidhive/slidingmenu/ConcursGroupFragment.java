package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dyploma.garik.dyploma.R;

import java.util.List;

import info.androidhive.slidingmenu.Utils.ServiceUtils;

public class ConcursGroupFragment extends Fragment {

    private static final String TAG = "QueueFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public ConcursGroupFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_concurs_group, container, false);


        return rootView;
    }
}
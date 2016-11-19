package com.imajinno.tanya.ap9;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;


/**
 * Created by Tanya on 04.10.2016.
 */

public class HomeFragment extends Fragment {

    private TextView tv= null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fb_activity, container, false);
        tv = (TextView) view.findViewById(R.id.facebook_name);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}

package com.cs160.joleary.catnip;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by robinhu on 3/2/16.
 */
public class VoteFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.vote_fragment, container, false);
        Bundle b = getArguments();

        TextView county = (TextView) v.findViewById(R.id.county);
        county.setText(b.getString("County"));

        TextView state = (TextView) v.findViewById(R.id.state);
        state.setText(b.getString("State"));

        TextView dem_per = (TextView) v.findViewById(R.id.dem_per);
        dem_per.setText(b.getInt("Dem") + "%");

        TextView dem = (TextView) v.findViewById(R.id.dem_vote);
        ViewGroup.LayoutParams param = dem.getLayoutParams();
        param.width = (int) (2.8 * b.getInt("Dem"));
        dem.setLayoutParams(param);

        TextView rep_per = (TextView) v.findViewById(R.id.rep_per);
        rep_per.setText(b.getInt("Rep") + "%");

        TextView rep = (TextView) v.findViewById(R.id.rep_vote);
        ViewGroup.LayoutParams param2 = rep.getLayoutParams();
        param2.width = (int) (2.8 * b.getInt("Rep"));
        rep.setLayoutParams(param2);

        return v;
    }
}

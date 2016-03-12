package com.cs160.joleary.catnip;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robinhu on 3/2/16.
 */
public class PersonFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.person_fragment, container, false);
        Bundle b = getArguments();
        try {

            JSONObject json = new JSONObject(b.getString("Person"));
            final Person p = new Person(json);

            TextView name_field = (TextView) v.findViewById(R.id.text);
            TextView t2 = (TextView) v.findViewById(R.id.text2);

            ImageView image = (ImageView) v.findViewById(R.id.image);
            Bitmap bit = p.getImage();
            if (bit == null) {
                Drawable image_drawable = getResources().getDrawable(R.drawable.senator1, null);
                image.setImageDrawable(image_drawable);
            } else {
                image.setImageBitmap(bit);
            }

            if (p.getParty() == 0) {
                name_field.setText(p.getName() + "\nDemocrat");
            } else if (p.getParty() == 1) {
                name_field.setText(p.getName() + "\nRepublican");
                name_field.setBackgroundColor(Color.parseColor("#EE0B6E"));
                t2.setBackgroundColor(Color.parseColor("#EE0B6E"));
                image.setBackgroundColor(Color.parseColor("#EE0B6E"));
            } else if (p.getParty() == 2) {
                name_field.setText(p.getName() + "\nIndependent");
                name_field.setBackgroundColor(Color.parseColor("#5FC3B8"));
                t2.setBackgroundColor(Color.parseColor("#5FC3B8"));
                image.setBackgroundColor(Color.parseColor("#5FC3B8"));
            }

            final ViewGroup _container = container;
            v.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Context c = _container.getContext();
                    String pa = c.getPackageName();
                    Intent sendIntent = new Intent(c, WatchToPhoneService.class);
                    sendIntent.putExtra(pa + ".action", 0);
                    sendIntent.putExtra(pa + ".person", p.toJSON().toString());
                    c.startService(sendIntent);
                }
            });
        } catch (JSONException e ) {
            e.printStackTrace();
        }

        return v;
    }
}

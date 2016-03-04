package com.cs160.joleary.catnip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Main3Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Intent intent = getIntent();
        try {
            JSONObject json = new JSONObject(intent.getStringExtra(getPackageName() + ".json"));
            Person p = new Person(json);

            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.com_layout, p.getCommittees());

            ListView comView = (ListView) findViewById(R.id.committees);
            comView.setDivider(null);
            comView.setDividerHeight(0);
            comView.setAdapter(adapter);


            BillAdapter b_adapter = new BillAdapter(this, p.getBills());

            ListView billView = (ListView) findViewById(R.id.bills);
            billView.setDivider(null);
            billView.setDividerHeight(0);
            billView.setAdapter(b_adapter);

            TextView name_field = (TextView) findViewById(R.id.name_field);
            TextView party_field = (TextView) findViewById(R.id.party_field);
            name_field.setText(p.getName());
            if (p.getParty() == 1) {
                name_field.setBackgroundColor(Color.parseColor("#EE0B6E"));
                party_field.setText("Republican");
            } else if (p.getParty() == 2) {
                name_field.setBackgroundColor(Color.parseColor("#5FC3B8"));
                party_field.setText("Independent");
            }

            ImageView image = (ImageView) findViewById(R.id.image);
            int image_identifier = getResources().getIdentifier(p.getImage(), "drawable", getPackageName());
            Drawable image_drawable = getResources().getDrawable(image_identifier, null);
            image.setImageDrawable(image_drawable);

            TextView term_end = (TextView) findViewById(R.id.term_end);
            term_end.setText("Term End:\n" + p.getTermEnd());

            TextView email = (TextView) findViewById(R.id.email);
            email.setText("Email:\n" + p.getEmail());

            TextView website = (TextView) findViewById(R.id.website);
            website.setText("Website:\n" + p.getWebsite());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class BillAdapter extends ArrayAdapter<Bill> {
        private final Context context;
        private final Bill[] values;

        public BillAdapter(Context context, Bill[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.bill_layout, parent, false);
            TextView bill_name = (TextView) rowView.findViewById(R.id.bill_name);
            bill_name.setText(values[position].getName());

            TextView bill_date = (TextView) rowView.findViewById(R.id.bill_date);
            bill_date.setText(values[position].getDate());

            return rowView;
        }
    }
}

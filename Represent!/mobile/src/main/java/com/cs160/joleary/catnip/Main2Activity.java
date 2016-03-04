package com.cs160.joleary.catnip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        String pa = getPackageName();
        Person[] p;
        Vote v;

        Intent intent = getIntent();
        int action = intent.getIntExtra(pa + ".input", 0);
        if (action == 0) {
            p = gen_data();
            v = gen_vote();
        } else {
            p = gen_data2();
            v = gen_vote2(intent.getStringExtra(pa + ".zip"));
        }

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra(pa + ".json", Person.genMessage(p, v));
        startService(sendIntent);

        MyArrayAdapter adapter = new MyArrayAdapter(this, p);

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setAdapter(adapter);

    }

    private class MyArrayAdapter extends ArrayAdapter<Person> {
        private final Context context;
        private final Person[] values;

        public MyArrayAdapter(Context context, Person[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            final Person p = values[position];

            Button name_field = (Button) rowView.findViewById(R.id.name_field);
            TextView party_field = (TextView) rowView.findViewById(R.id.party_field);
            name_field.setText(p.getName());
            if (p.getParty() == 1) {
                name_field.setBackgroundResource(R.drawable.red_click);
                party_field.setText("Republican");
            } else if (p.getParty() == 2) {
                name_field.setBackgroundResource(R.drawable.go_button);
                party_field.setText("Independent");
            }
            name_field.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View v) {
                    String pa = getPackageName();
                    Intent intent = new Intent(context, Main3Activity.class);
                    intent.putExtra(pa + ".json", p.toJSON().toString());
                    startActivity(intent);
                }
            });


            ImageView image = (ImageView) rowView.findViewById(R.id.image);
            int image_identifier = getResources().getIdentifier(p.getImage(), "drawable", getPackageName());
            Drawable image_drawable = getResources().getDrawable(image_identifier, null);
            image.setImageDrawable(image_drawable);

            TextView tweet_name = (TextView) rowView.findViewById(R.id.tweet_name);
            tweet_name.setText(p.getTwitterName());
            TextView last_tweet = (TextView) rowView.findViewById(R.id.last_tweet);
            last_tweet.setText(p.getLastTweet());

            ImageButton email = (ImageButton) rowView.findViewById(R.id.email);
            email.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View v) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{p.getEmail()});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");
                    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            });

            ImageButton website = (ImageButton) rowView.findViewById(R.id.website);
            website.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getWebsite()));
                    startActivity(browserIntent);
                }
            });


            return rowView;
        }
    }

    private Vote gen_vote() {
        return new Vote("Waterloo", "MI", 95, 5);
    }

    private Vote gen_vote2(String zip) {
        return new Vote("ZIP:"+zip, "OH", 45, 55);
    }

    private Person[] gen_data2() {
        String[] c1 = gen_coms1();
        String[] c2 = gen_coms2();
        String[] c3 = gen_coms3();
        Bill[] b1 = gen_bills1();
        Bill[] b2 = gen_bills2();
        Bill[] b3 = gen_bills3();
        Person[] data = new Person[4];
        data[0] = new Person("Sen. Foo Bar", 1, "foobar@foobar.com", "foobar.com", "@foobar", "swag swag all day all night yo!", "senator1", c1, b1, "5/5/55");
        data[1] = new Person("Repr. Ryan Reynolds", 1, "star@hollywood.eu", "www.TheRealRyanReynolds.com", "@deadpool", "Anyone have any advice on how to remove warts from my back? Got a hot date tomorrow and can't look like no slouch!", "repr", c2, b2,"12/4/15");
        data[2] = new Person("Sen. Bashful", 0, "shyguy24@gmail.com", "google.com", "@shyguy247", "...", "sen3", c3, b3, "9/10/04");
        data[3] = new Person("Sen. Bazaar", 1, "foobar@foobar.com", "foobar.com", "@foobar", "swag swag all day all night yo!", "senator1", c1, b1, "5/5/55");
        return data;
    }

    private Person[] gen_data() {
        String[] c1 = gen_coms1();
        String[] c2 = gen_coms2();
        String[] c3 = gen_coms3();
        Bill[] b1 = gen_bills1();
        Bill[] b2 = gen_bills2();
        Bill[] b3 = gen_bills3();
        Person[] data = new Person[3];
        data[0] = new Person("Sen. Foo Bar", 0, "foobar@foobar.com", "foobar.com", "@foobar", "swag swag all day all night yo!", "senator1", c1, b1, "5/5/55");
        data[1] = new Person("Repr. Ryan Reynolds", 1, "star@hollywood.eu", "www.TheRealRyanReynolds.com", "@deadpool", "Anyone have any advice on how to remove warts from my back? Got a hot date tomorrow and can't look like no slouch!", "repr", c2, b2,"12/4/15");
        data[2] = new Person("Sen. Bashful", 2, "shyguy24@gmail.com", "google.com", "@shyguy247", "...", "sen3", c3, b3, "9/10/04");
        return data;
    }

    private String[] gen_coms1() {
        String[] coms = {"Swag committe", "Wag committee", "Bad committee", "Bow wow", "Show wow", "bow wow2", "bow wow 3"};
        return coms;
    }

    private String[] gen_coms2() {
        String[] coms = {"Swag committe Wag committee Bad committee Bow wow Show wow bow wow2 bow wow 3", "blastoise"};
        return coms;
    }

    private String[] gen_coms3() {
        String[] coms = {};
        return coms;
    }

    private Bill[] gen_bills1() {
        Bill[] bills = new Bill[6];
        bills[0] = new Bill("foo", "1/1/11");
        bills[1] = new Bill("foo2", "2/1/11");
        bills[2] = new Bill("foo3", "43/1/11");
        bills[3] = new Bill("foo4", "5/1/11");
        bills[4] = new Bill("foo5", "6/1/11");
        bills[5] = new Bill("foo6", "91/1/11");
        return bills;
    }

    private Bill[] gen_bills2() {
        Bill[] bills = new Bill[4];
        bills[0] = new Bill("baz", "1/1/11");
        bills[1] = new Bill("baz2", "2/1/11");
        bills[2] = new Bill("baz3", "43/1/11");
        bills[3] = new Bill("baz4", "5/1/11");
        return bills;
    }

    private Bill[] gen_bills3() {
        Bill[] bills = {};
        return bills;
    }

}

package com.cs160.joleary.catnip;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String data = intent.getStringExtra(getPackageName() + ".data");
        if (data == null) {
            setContentView(R.layout.emtpy);
        } else {
            Person[] p;
            Vote v;

            p = Person.parseMessage(data);
            v = Vote.parseMessage(data);

            MyGridPageAdapter adapter = new MyGridPageAdapter(this, getFragmentManager(), p, v);
            GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);

            DotsPageIndicator dotsIndicator = (DotsPageIndicator)findViewById(R.id.page_indicator);
            dotsIndicator.setDotFadeWhenIdle(false);
            dotsIndicator.setDotColorSelected(Color.parseColor("#BFBFBF"));
            dotsIndicator.setDotRadiusSelected((int) dotsIndicator.getDotRadius());
            dotsIndicator.setPager(pager);
        }

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                Intent i = new Intent(getBaseContext(), WatchToPhoneService.class);
                i.putExtra(getPackageName() + ".action", 1);
                startService(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    private class MyGridPageAdapter extends FragmentGridPagerAdapter {

        private final Context mContext;
        private final Person[] values;
        private final Vote vote;

        public MyGridPageAdapter(Context ctx, FragmentManager fm, Person[] p, Vote v) {
            super(fm);
            mContext = ctx;
            values = p;
            vote = v;
        }

        @Override
        public Fragment getFragment(int i, int i1) {
            if (i1 < values.length) {
                PersonFragment p = new PersonFragment();
                Bundle b = new Bundle();
                b.putString("Person", values[i1].toJSON().toString());
                b.putString("Package_Name", mContext.getPackageName());
                p.setArguments(b);
                return p;
            } else {
                VoteFragment v = new VoteFragment();
                Bundle b = new Bundle();
                b.putString("County", vote.getCounty());
                b.putString("State", vote.getState());
                b.putInt("Dem", vote.getDemVote());
                b.putInt("Rep", vote.getRepVote());
                v.setArguments(b);
                return v;
            }
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int i) {
            return values.length + 1;
        }
    }

    private Vote gen_vote() {
        return new Vote("Waterloo", "MI", 95, 5);
    }

    private Person[] gen_data() {
        String[] c1 = gen_coms1();
        String[] c2 = gen_coms2();
        String[] c3 = gen_coms3();
        Bill[] b1 = gen_bills1();
        Bill[] b2 = gen_bills2();
        Bill[] b3 = gen_bills3();
        Person[] data = new Person[3];
        data[0] = new Person("Foo Bar", 0, "foobar@foobar.com", "foobar.com", "@foobar", "swag swag all day all night yo!", "senator1", c1, b1, "5/5/55");
        data[1] = new Person("Ryan Reynolds", 1, "star@hollywood.eu", "www.TheRealRyanReynolds.com", "@deadpool", "Anyone have any advice on how to remove warts from my back? Got a hot date tomorrow and can't look like no slouch!", "repr", c2, b2,"12/4/15");
        data[2] = new Person("Bashful", 2, "shyguy24@gmail.com", "google.com", "@shyguy247", "...", "sen3", c3, b3, "9/10/04");
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

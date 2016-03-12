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

    public static Person[] p = null;
    public static Vote v = null;

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
            Log.v("T", (p == null) + "");
            Log.v("T", (v == null) + "");

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
}

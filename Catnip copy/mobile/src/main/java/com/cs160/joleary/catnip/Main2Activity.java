package com.cs160.joleary.catnip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class Main2Activity extends Activity {

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    private int mAction;
    private String mZip;
    private String mLat;
    private String mLong;
    private String mCounty = "UNKNOWN";
    private String mState = "UNKNOWN";
    private String pa;
    private Vote v;
    private Person[] p;
    private Main2Activity _that;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "wCcr67DepVT1oTL9y5lAPRp8B";
    private static final String TWITTER_SECRET = "IK9jMzbR73ob7RqdzdNRL2j5rBZYHpN4S9Vw0sT0Gvno3RafkW";



    private class LoadViewTask extends AsyncTask<Void, String, Boolean>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(Main2Activity.this,"Loading...",
                    "Loading vote data...", false, false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Boolean doInBackground(Void... params)
        {

            JSONObject voteData = loadVoteData();
            publishProgress("Getting County and State Info...");

            getCountyStateData();
            if (mCounty.equals("UNKNOWN") || mState.equals("UNKNOWN")) {
                v = new Vote(mCounty, mState, 0, 0);
            } else {
                try {
                    JSONObject json1 = voteData.getJSONObject(mState);
                    JSONArray json2 = json1.getJSONArray(mCounty);
                    int o = json2.getInt(0);
                    int r = json2.getInt(1);
                    Log.v("V", "VOTING DATA O : " + o + " R : " + r);
                    v = new Vote(mCounty, mState, o, r);
                } catch (JSONException e) {
                    Log.v("V", "FAILED TO FIND VOTING INFO FOR COUNTY : " + mCounty + " STATE : " + mState);
                    v = new Vote(mCounty, mState, 0, 0);
                }
            }

            publishProgress("Getting Sunlight Data...");
            getSunlightData();
            if (p.length  != 0) {
                publishProgress("Getting Sunlight Committee Data...");
                getSunlightCommitteeDate();
                publishProgress("Getting Sunlight Bill Data...");
                getSunlightBillData();
                publishProgress("Getting Sunlight Images...");
                getSunlightImages();
                publishProgress("Getting Twitter Data...");
                getLastTweets();
            } else {
                return false;
            }
            return true;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(String... values)
        {
            //set the current progress of the progress dialog
            progressDialog.setMessage(values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Boolean result)
        {
            if (!result) {
                progressDialog.dismiss();
                setContentView(R.layout.activity_main2_bad);
            }
        }

        protected JSONObject makeURLRequest(String url_string) {
            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;

            try
            {
                url = new URL(url_string);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = null;
                inStream = urlConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                String temp, response = "";
                while ((temp = bReader.readLine()) != null)
                    response += temp;
                bReader.close();
                inStream.close();
                urlConnection.disconnect();
                object = (JSONObject) new JSONTokener(response).nextValue();
            }
            catch (Exception e)
            {
                Log.v("V", "API REQUEST TO " + url_string + " FAILED");
                Log.v("V", e.toString());
            }

            return object;
        }

        protected JSONObject loadVoteData() {
            JSONObject json = null;
            try {
                InputStream stream = getAssets().open("new_results.json");
                int size = stream.available();
                byte[] buffer = new byte[size];
                stream.read(buffer);
                stream.close();
                String jsonString = new String(buffer, "UTF-8");
                json = new JSONObject(jsonString);

            } catch (Exception e) {
                Log.v("V", "LOAD VOTE DATA FAILED");
            }
            return json;
        }

        protected String getSunlightString() {
            StringBuilder urlString = new StringBuilder();
            urlString.append("https://congress.api.sunlightfoundation.com/legislators/locate?");
            urlString.append("apikey=").append("40129bbc6ba145419132633ceb2d7c45");

            if (mAction == 0) {
                urlString.append("&latitude=").append(mLat);
                urlString.append("&longitude=").append(mLong);
            } else {
                urlString.append("&zip=").append(mZip);
            }
            return urlString.toString();
        }

        protected String getSunlightCommitteeString(String bio) {
            StringBuilder urlString = new StringBuilder();
            urlString.append("https://congress.api.sunlightfoundation.com/committees?");
            urlString.append("apikey=").append("40129bbc6ba145419132633ceb2d7c45");
            urlString.append("&member_ids=").append(bio);
            return urlString.toString();
        }

        protected String getSunlightBillsString(String bio) {
            StringBuilder urlString = new StringBuilder();
            urlString.append("https://congress.api.sunlightfoundation.com/bills?");
            urlString.append("apikey=").append("40129bbc6ba145419132633ceb2d7c45");
            urlString.append("&sponsor_id=").append(bio);
            return urlString.toString();
        }

        protected String getSunlightImageString(String bio) {
            StringBuilder urlString = new StringBuilder();
            urlString.append("https://theunitedstates.io/images/congress/225x275/").append(bio).append(".jpg");
            return urlString.toString();
        }

        protected String getGeocodeString() {
            StringBuilder urlString = new StringBuilder();
            urlString.append("https://maps.googleapis.com/maps/api/geocode/json?");
            urlString.append("key=").append("AIzaSyBHbmRXyuK_PJIc8MDHEAnzO58-5HBV_x0");

            if (mAction == 0) {
                urlString.append("&latlng=").append(mLat).append(",").append(mLong);
                urlString.append("&result_type=administrative_area_level_2|administrative_area_level_1");
            } else {
                urlString.append("&address=").append(mZip);
            }
            return urlString.toString();
        }

        protected void getCountyStateData() {
            String geocodeURL = getGeocodeString();

            JSONObject object = makeURLRequest(geocodeURL);
            try {
                JSONObject result = object.getJSONArray("results").getJSONObject(0);
                JSONArray addr_comps = result.getJSONArray("address_components");
                for (int i = 0; i < addr_comps.length(); i++) {
                    JSONObject addr_comp = addr_comps.getJSONObject(i);
                    JSONArray types = addr_comp.getJSONArray("types");
                    for (int j = 0; j < types.length(); j++) {
                        if (types.getString(j).equals("administrative_area_level_2")) {
                            mCounty = addr_comp.getString("short_name");
                            mCounty = mCounty.substring(0, mCounty.length() - 7);
                        }
                        if (types.getString(j).equals("administrative_area_level_1")) {
                            mState = addr_comp.getString("short_name");
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Log.v("V", "LOAD COUNTY AND STATE DATA FAILED");
            }
        }

        protected void getSunlightData() {
            String sunlightURL = getSunlightString();

            JSONObject result = makeURLRequest(sunlightURL);
            try {
                JSONArray results = result.getJSONArray("results");
                p = new Person[results.length()];
                for (int i = 0; i < results.length(); i++) {
                    JSONObject r = results.getJSONObject(i);
                    String name = r.getString("title") + " " + r.getString("first_name") + " " + r.getString("last_name");
                    String party_string = r.getString("party");
                    int party = 0;
                    if (party_string.equals("R")) {
                        party = 1;
                    } else if (party_string.equals("I")) {
                        party = 2;
                    }
                    String email = r.getString("oc_email");
                    String website = r.getString("website");
                    String twitter_name = r.getString("twitter_id");
                    String term_end = r.getString("term_end");
                    String bio_id = r.getString("bioguide_id");
                    p[i] = new Person(name, party, email, website, twitter_name, term_end, bio_id);
                }
            } catch (JSONException e) {
                p = new Person[0];
            }
        }

        protected void getSunlightCommitteeDate() {
            for (int i = 0; i < p.length; i++) {
                String url = getSunlightCommitteeString(p[i].getBioguide_id());
                JSONObject result = makeURLRequest(url);
                String[] committees;
                try {
                    JSONArray results = result.getJSONArray("results");
                    committees = new String[results.length()];
                    for (int j = 0; j < results.length(); j++) {
                        committees[j] = results.getJSONObject(j).getString("name");
                    }
                } catch (JSONException e) {
                    committees = new String[0];
                }
                p[i].setCommittees(committees);
            }
        }

        protected void getSunlightBillData() {
            for (int i = 0; i < p.length; i++) {
                String url = getSunlightBillsString(p[i].getBioguide_id());
                JSONObject result = makeURLRequest(url);
                Bill[] bills;
                try {
                    JSONArray results = result.getJSONArray("results");
                    bills = new Bill[results.length()];
                    for (int j = 0; j < results.length(); j++) {
                        JSONObject r = results.getJSONObject(j);
                        String title = r.getString("short_title");
                        if (title.equals("null")) {
                            title = r.getString("official_title");
                            if (title.length() > 100) {
                                title = title.substring(0, 97);
                                title += "...";
                            }
                        }
                        bills[j] = new Bill(title, r.getString("introduced_on"));
                    }
                } catch (JSONException e) {
                    bills = new Bill[0];
                }
                p[i].setBills(bills);
            }
        }

        protected void getSunlightImages() {
            for (int i = 0; i < p.length; i++) {
                String url_string = getSunlightImageString(p[i].getBioguide_id());
                HttpURLConnection urlConnection = null;
                URL url = null;
                Bitmap b = null;
                try
                {
                    url = new URL(url_string);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    b = BitmapFactory.decodeStream(urlConnection.getInputStream());
                    urlConnection.disconnect();
                }
                catch (Exception e)
                {
                    Log.v("V", "API REQUEST TO " + url_string + " FAILED");
                    Log.v("V", e.toString());
                }
                p[i].setImage(b);
            }
        }

        private class TweetCallback extends Callback<List<Tweet>> {

            private int callbacks = 0;

            @Override
            public void success(Result<List<Tweet>> result) {
                for (Tweet tweet : result.data) {
                    for (int i = 0; i < p.length; i++) {
                        if (!p[i].getTwitterName().equals("null") && p[i].getTwitterName().equals(tweet.user.screenName)) {
                            p[i].setLastTweet(tweet.text);
                        }
                    }
                    Log.d("fabricstuff", "result: " + tweet.text + "  " + tweet.user.screenName);
                }
                callbacks += 1;
                if (callbacks == p.length) {
                    progressDialog.dismiss();

                    MyArrayAdapter adapter = new MyArrayAdapter(_that, p);

                    ListView listView = (ListView) _that.findViewById(R.id.listview);
                    listView.setDivider(null);
                    listView.setDividerHeight(0);
                    listView.setAdapter(adapter);

                    Log.v("V", "SENDING INTENT");
                    PhoneToWatchService.persons = p;
                    PhoneToWatchService.vote = v;
                    Intent sendIntent = new Intent(_that.getBaseContext(), PhoneToWatchService.class);
                    startService(sendIntent);
                }
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        }
        protected void getLastTweets() {
            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                @Override
                public void success(Result<AppSession> appSessionResult) {
                    AppSession session = appSessionResult.data;
                    TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                    TweetCallback callback = new TweetCallback();
                    for (int i = 0; i < p.length; i++) {
                        twitterApiClient.getStatusesService().userTimeline(null, p[i].getTwitterName(), 1, null, null, false, false, false, true, callback);
                    }
                }

                @Override
                public void failure(TwitterException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        pa = getPackageName();

        Intent intent = getIntent();
        mAction = intent.getIntExtra(pa + ".input", 0);
        if (mAction == 0) {
            mLat = intent.getStringExtra(pa + ".lat");
            mLong = intent.getStringExtra(pa + ".long");
        } else {
            mZip = intent.getStringExtra(pa + ".zip");
        }

        _that = this;

        new LoadViewTask().execute();

    }

    public void goHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
            Bitmap b = p.getImage();
            if (b == null) {
                Drawable image_drawable = getResources().getDrawable(R.drawable.senator1, null);
                image.setImageDrawable(image_drawable);
            } else {
                image.setImageBitmap(b);
            }

            TextView tweet_name = (TextView) rowView.findViewById(R.id.tweet_name);
            tweet_name.setText("@" + p.getTwitterName());
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

}

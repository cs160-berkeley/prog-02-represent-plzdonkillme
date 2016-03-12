package com.cs160.joleary.catnip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by robinhu on 3/2/16.
 */
public class Person {
    private String bioguide_id;
    private String name;
    private String email;
    private String website;
    private String twitter_name;
    private String last_tweet = "";
    private Bitmap image;
    private String term_end;
    private int party;
    private String[] committees;
    private Bill[] bills;

    public Person(String n, int p, String e, String w, String t, String te, String b) {
        name = n;
        party = p;
        email = e;
        if (!w.startsWith("http://") && !w.startsWith("https://")) {
            website = "http://" + w;
        } else {
            website = w;
        }
        twitter_name = t;
        term_end = te;
        bioguide_id = b;
    }


    public Person(JSONObject json) {
        try {
            name = json.getString("name");
            party = json.getInt("party");
            email = json.getString("email");
            website = json.getString("website");
            twitter_name = json.getString("twitter_name");
            last_tweet = json.getString("last_tweet");
            image = getBitmapFromString(json.getString("image"));
            term_end = json.getString("term_end");
            JSONArray coms = json.getJSONArray("committees");
            committees = new String[coms.length()];
            for (int j = 0; j < coms.length(); j++) {
                committees[j] = coms.getString(j);
            }
            JSONArray bs = json.getJSONArray("bill");
            bills = new Bill[bs.length()];
            for (int j = 0; j < bs.length(); j++) {
                bills[j] = new Bill(bs.getJSONObject(j).getString("name"), bs.getJSONObject(j).getString("date"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBioguide_id() {
        return bioguide_id;
    }

    public String getName() {
        return name;
    }

    public int getParty() {
        return party;
    }

    public String getTwitterName() {
        return twitter_name;
    }

    public String getLastTweet() {
        return last_tweet;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap b) {
        image = b;
    }

    public String[] getCommittees() {
        return committees;
    }

    public void setCommittees(String[] c) {
        committees = c;
    }

    public Bill[] getBills() {
        return bills;
    }

    public void setBills(Bill[] b) {
        bills = b;
    }

    public String getTermEnd() {
        return term_end;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", this.getName());
            json.put("party", this.getParty());
            json.put("email", this.getEmail());
            json.put("website", this.getWebsite());
            json.put("twitter_name", this.getTwitterName());
            json.put("last_tweet", this.getLastTweet());
            json.put("image", getStringFromBitmap(getImage()));
            json.put("term_end", this.getTermEnd());
            json.put("committees", new JSONArray());
            String[] c = this.getCommittees();
            for (int i = 0; i < c.length; i++) {
                json.accumulate("committees", c[i]);
            }
            json.put("bill", new JSONArray());
            Bill[] b = this.getBills();
            for (int i = 0; i < b.length; i++) {
                JSONObject json_bill = new JSONObject();
                json_bill.put("name", b[i].getName());
                json_bill.put("date", b[i].getDate());
                json.accumulate("bill", json_bill);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String genMessage(Person[] p, Vote v) {
        JSONObject json_final = new JSONObject();
        try {
            for (int j = 0; j < p.length; j++) {
                JSONObject json = p[j].toJSON();
                json_final.accumulate("Person", json);
            }
            json_final.put("Vote", v.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json_final.toString();
    }

    public static Person[] parseMessage(String m) {
        Person[] persons = null;
        try {
            JSONObject json = new JSONObject(m);
            JSONArray j_array = json.getJSONArray("Person");
            persons = new Person[j_array.length()];
            for (int i = 0; i < j_array.length(); i++) {
                JSONObject p_json = j_array.getJSONObject(i);
                persons[i] = new Person(p_json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return persons;
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
 /*
 * This functions converts Bitmap picture to a string which can be
 * JSONified.
 * */
        if (bitmapPicture == null) {
            return "null";
        }
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    private Bitmap getBitmapFromString(String jsonString) {
/*
* This Function converts the String back to Bitmap
* */    if (jsonString.equals("null")) {
            return null;
        }
        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}

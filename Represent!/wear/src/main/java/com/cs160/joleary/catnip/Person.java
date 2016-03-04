package com.cs160.joleary.catnip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robinhu on 3/2/16.
 */
public class Person {
    private String name;
    private String email;
    private String website;
    private String twitter_name;
    private String last_tweet;
    private String image;
    private String term_end;
    private int party;
    private String[] committees;
    private Bill[] bills;

    public Person(String n, int p, String e, String w, String t, String lt, String i, String[] c, Bill[] b, String te) {
        name = n;
        party = p;
        email = e;
        if (!w.startsWith("http://") && !w.startsWith("https://")) {
            website = "http://" + w;
        } else {
            website = w;
        }
        twitter_name = t;
        last_tweet = lt;
        image = i;
        committees = c;
        bills = b;
        term_end = te;
    }

    public Person(JSONObject json) {
        try {
            name = json.getString("name");
            party = json.getInt("party");
            email = json.getString("email");
            website = json.getString("website");
            twitter_name = json.getString("twitter_name");
            last_tweet = json.getString("last_tweet");
            image = json.getString("image");
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

    public String getImage() {
        return image;
    }

    public String[] getCommittees() {
        return committees;
    }

    public Bill[] getBills() {
        return bills;
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
            json.put("image", this.getImage());
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

}

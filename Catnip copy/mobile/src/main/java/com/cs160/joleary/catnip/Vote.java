package com.cs160.joleary.catnip;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robinhu on 3/2/16.
 */
public class Vote {
    private String county;
    private String state;
    private int dem_vote;
    private int rep_vote;

    public Vote(String c, String s, int d, int r) {
        county = c;
        state = s;
        dem_vote = d;
        rep_vote = r;
    }

    public Vote(JSONObject json) {
        try {
            county = json.getString("county");
            state = json.getString("state");
            dem_vote = json.getInt("dem_vote");
            rep_vote = json.getInt("rep_vote");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("county", county);
            json.put("state", state);
            json.put("dem_vote", dem_vote);
            json.put("rep_vote", rep_vote);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getCounty() {
        return county;
    }

    public String getState() {
        return state;
    }

    public int getDemVote() {
        return dem_vote;
    }

    public int getRepVote() {
        return rep_vote;
    }

    public static Vote parseMessage(String m) {
        Vote v = null;
        try {
            JSONObject json = new JSONObject(m);
            v = new Vote(json.getJSONObject("Vote"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;

    }
}

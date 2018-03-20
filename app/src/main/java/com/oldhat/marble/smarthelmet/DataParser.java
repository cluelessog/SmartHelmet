package com.oldhat.marble.smarthelmet;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sourabh on 11/3/18.
 */

class DataParser {

  //for getting distance and duration
  public HashMap<String, String> parseDirections(String jsonData) {

    JSONArray jsonArray = null;
    JSONObject jsonObject;

    try {
      jsonObject = new JSONObject(jsonData);
      jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0)
          .getJSONArray("legs");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return getDuration(jsonArray);

  }

  //for drawing route
  public String[] parseDirection(String jsonData) {

    JSONArray jsonArray = null;
    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject(jsonData);
      jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0)
          .getJSONArray("legs")
          .getJSONObject(0)
          .getJSONArray("steps");
      Log.d("location log", jsonArray.toString());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return getPaths(jsonArray);

  }

  //method for maneuver
  public HashMap<String, String> parseManeuver(String jsonData) {

    JSONArray jsonArray = null;
    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject(jsonData);
      jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0)
          .getJSONArray("legs")
          .getJSONObject(0)
          .getJSONArray("steps");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return getManeuvers(jsonArray);

  }

  private HashMap<String, String> getManeuvers(JSONArray jsonArray) {
    List<HashMap<String, LatLng>> maneu = new ArrayList<HashMap<String, LatLng>>();
    String manuever = "";
    LatLng dest;
    for (int i = 0; i < jsonArray.length(); i++) {

    }
    return null;
  }


  private HashMap<String, String> getDuration(JSONArray googleDirectionsJson) {
    HashMap<String, String> googleDirectionsMap = new HashMap<>();
    String duration = "";
    String distance = "";
    try {
      duration = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
      distance = googleDirectionsJson.getJSONObject(0).getJSONObject("distance").getString("text");
      googleDirectionsMap.put("duration", duration);
      googleDirectionsMap.put("distance", distance);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return googleDirectionsMap;
  }

  public String[] getPaths(JSONArray googleStepsJson) {
    int count = googleStepsJson.length();
    String[] polylines = new String[count];
    for (int i = 0; i < count; i++) {
      try {
        polylines[i] = getPath(googleStepsJson.getJSONObject(i));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return polylines;
  }

  public String getPath(JSONObject googlePathJson) {
    String polyline = "";
    try {
      polyline = googlePathJson.getJSONObject("polyline").getString("points");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return polyline;
  }

}

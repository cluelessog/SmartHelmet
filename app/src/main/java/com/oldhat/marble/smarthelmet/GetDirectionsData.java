package com.oldhat.marble.smarthelmet;

import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by sourabh on 11/3/18.
 */

public class GetDirectionsData extends AsyncTask<Object, String, String> {

  GoogleMap mMap;
  String url;
  String googleDirectionsData;
  String duration;
  String distance;
  LatLng latLng;

  @Override
  protected String doInBackground(Object... objects) {
    mMap = (GoogleMap) objects[0];
    url = (String) objects[1];
    latLng = (LatLng) objects[2];
    DownloadUrl downloadUrl = new DownloadUrl();
    try {
      googleDirectionsData = downloadUrl.readUrl(url);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return googleDirectionsData;
  }

  @Override
  protected void onPostExecute(String s) {
    HashMap<String, String> directionsList = null;
    DataParser parser = new DataParser();
    directionsList = parser.parseDirections(s);
    duration = directionsList.get("duration");
    distance = directionsList.get("distance");
    mMap.clear();
    MarkerOptions options = new MarkerOptions()
        .position(latLng)
        .title("Duration=" + duration)
        .snippet("Distance=" + distance);

    mMap.addMarker(options);

    //for drawing route
    String dirList[];
    DataParser dataParser = new DataParser();
    dirList = dataParser.parseDirection(s);
    displayDirection(dirList);


  }

  private void displayDirection(String[] dirList) {
    int count = dirList.length;
    for (int i = 0; i < count; i++) {
      PolylineOptions polylineOptions = new PolylineOptions();
      polylineOptions.color(Color.BLUE);
      polylineOptions.width(10);
      polylineOptions.addAll(PolyUtil.decode(dirList[i]));
      mMap.addPolyline(polylineOptions);
    }
  }
}

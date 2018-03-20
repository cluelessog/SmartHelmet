package com.oldhat.marble.smarthelmet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sourabh on 11/3/18.
 */

class DownloadUrl {

  public String readUrl(String myUrl) throws IOException {
    String data = null;
    InputStream inputStream = null;
    HttpURLConnection httpURLConnection = null;
    try {
      URL url = new URL(myUrl);
      httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.connect();
      inputStream = httpURLConnection.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
      StringBuffer sb = new StringBuffer();

      String line = "";
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      data = sb.toString();
      br.close();

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      inputStream.close();
      httpURLConnection.disconnect();
    }

    return data;
  }
}

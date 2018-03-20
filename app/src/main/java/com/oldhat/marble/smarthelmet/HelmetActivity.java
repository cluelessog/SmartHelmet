package com.oldhat.marble.smarthelmet;

/**
 * Created by sourabh on 21/2/18.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.widgets.Dialog;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;


public class HelmetActivity extends AppCompatActivity implements LocationListener,
    GpsStatus.Listener {


  //SPP UUID. Look for it
  static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  // Button btnOn, btnOff, btnDis;
  Button Discnt, Setting;
  String address = null;
  BluetoothAdapter myBluetooth = null;
  BluetoothSocket btSocket = null;
  private LocationManager mLocationManager;
  private Toolbar toolbar;
  private TextView status;
  private ProgressBarCircularIndeterminate progressBarCircularIndeterminate;
  private TextView currentSpeed;
  private ProgressDialog progress;
  private boolean isBtConnected = false;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    Intent newint = getIntent();
    address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

    //view of the ledControl
    setContentView(R.layout.activity_helmet_control);

    new ConnectBT().execute(); //Call the class to connect
    //speed widget initialisation

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    //setTitle("");
    status = findViewById(R.id.status);
    mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    currentSpeed = findViewById(R.id.currentSpeed);
    progressBarCircularIndeterminate = findViewById(R.id.progressBarCircularIndeterminate);



    //call the widgets
    Discnt = findViewById(R.id.discnt);
    Setting = findViewById(R.id.setting);
    Discnt.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Disconnect(); //close connection
      }
    });


  }


  @SuppressLint("MissingPermission")
  @Override
  protected void onResume() {
    super.onResume();

    if (mLocationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0) {
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
    } else {
      Log.w("HelmetActivity",
          "No GPS location provider found. GPS data display will not be available.");
    }

    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      showGpsDisabledDialog();
    }

    mLocationManager.addGpsStatusListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mLocationManager.removeUpdates(this);
    mLocationManager.removeGpsStatusListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private void speedAlert() {
    if (btSocket != null) {
      try {
        btSocket.getOutputStream().write("You are overspeeding".getBytes());
      } catch (IOException e) {
        msg("error");
      }
    }
  }

  @Override
  public void onLocationChanged(Location location) {

    if (location.hasSpeed()) {
      progressBarCircularIndeterminate.setVisibility(View.GONE);
      status.setText("");
      String speed = String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6) + "km/h";
      SpannableString s = new SpannableString(speed);
      s.setSpan(new RelativeSizeSpan(0.25f), s.length() - 4, s.length(), 0);
      currentSpeed.setText(s);
      int speedLimit = 70;
      int currSpeed = Integer
          .parseInt(String.format(Locale.ENGLISH, "%.0f", location.getSpeed() * 3.6));
      if (currSpeed > speedLimit) {
        speedAlert();
      }
    } else {
      status.setText(R.string.waiting_for_fix);
      currentSpeed.setText("");
      progressBarCircularIndeterminate.setVisibility(View.VISIBLE);
    }

  }

  public void onGpsStatusChanged(int event) {
    switch (event) {

      case GpsStatus.GPS_EVENT_STOPPED:
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
          showGpsDisabledDialog();
        }
        break;
      case GpsStatus.GPS_EVENT_FIRST_FIX:
        break;
    }
  }

  public void showGpsDisabledDialog() {
    Dialog dialog = new Dialog(this, getResources().getString(R.string.gps_disabled),
        getResources().getString(R.string.please_enable_gps));

    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
      }
    });
    dialog.show();
  }

  // public static Data getData() {
  //   return data;
  // }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {
  }

  @Override
  public void onProviderEnabled(String s) {
  }

  @Override
  public void onProviderDisabled(String s) {
  }


  private void Disconnect()
  {
    if (btSocket!=null) //If the btSocket is busy
    {
      try
      {
        btSocket.close(); //close connection
      }
      catch (IOException e)
      { msg("Error");}
    }
    finish(); //return to the first layout

  }



  /*private void turnOffLed()
  {
    if (btSocket!=null)
    {
      try
      {
        btSocket.getOutputStream().write("0".toString().getBytes());
      }
      catch (IOException e)
      {
        msg("Error");
      }
    }
  }
  public void sendText(View view) {
    if(btSocket != null){
      try {
        btSocket.getOutputStream().write("hello\n".getBytes());
      }catch (IOException e){
        msg("Error");
      }
    }
  }
  private void turnOnLed()
  {
    if (btSocket!=null)
    {
      try
      {
        btSocket.getOutputStream().write("1".toString().getBytes());
      }
      catch (IOException e)
      {
        msg("Error");
      }
    }
  }*/

  // fast way to call Toast
  private void msg(String s)
  {
    Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
  }

  public void settings(View view) {
    Intent i = new Intent(this, Settings.class);
    startActivity(i);
  }

  public void navActivity(View view) {

    Intent intent = new Intent(this, MapsActivity.class);
    startActivity(intent);


  }


  //Thread to connect to bluetooth
  private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
  {
    private boolean ConnectSuccess = true; //if it's here, it's almost connected

    @Override
    protected void onPreExecute()
    {
      progress = ProgressDialog.show(HelmetActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
    }

    @Override
    protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
    {
      try
      {
        if (btSocket == null || !isBtConnected)
        {
          myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
          BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
          btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
          BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
          btSocket.connect();//start connection
        }
      }
      catch (IOException e)
      {
        //ConnectSuccess = false;//if the try failed, you can check the exception here
      }
      return null;
    }
    @Override
    protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
    {
      super.onPostExecute(result);

      if (!ConnectSuccess)
      {
        msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
        finish();
      }
      else
      {
        msg("Connected.");
        isBtConnected = true;
      }
      progress.dismiss();
    }
  }


}



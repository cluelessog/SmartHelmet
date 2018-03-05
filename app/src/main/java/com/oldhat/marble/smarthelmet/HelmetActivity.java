package com.oldhat.marble.smarthelmet;

/**
 * Created by sourabh on 21/2/18.
 */

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;


public class HelmetActivity extends AppCompatActivity {

  //SPP UUID. Look for it
  static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  // Button btnOn, btnOff, btnDis;
  ImageButton Discnt, Abt;
  String address = null;
  BluetoothAdapter myBluetooth = null;
  BluetoothSocket btSocket = null;
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

    //call the widgets
    Discnt = findViewById(R.id.discnt);
    Abt = findViewById(R.id.abt);

    new ConnectBT().execute(); //Call the class to connect



    Discnt.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Disconnect(); //close connection
      }
    });


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
        ConnectSuccess = false;//if the try failed, you can check the exception here
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


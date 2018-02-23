package com.oldhat.marble.smarthelmet;

/**
 * Created by sourabh on 21/2/18.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash);

    Thread timerThread = new Thread(){
      public void run(){
        try{
          sleep(700);
        }catch(InterruptedException e){
          e.printStackTrace();
        }finally{
          Intent intent = new Intent(SplashScreen.this,DeviceList.class);
          startActivity(intent);
        }
      }
    };
    timerThread.start();
  }

  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    finish();
  }

}


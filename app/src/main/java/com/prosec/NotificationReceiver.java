package com.prosec;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;



/**
 * Created by gusta on 02/10/2015.
 */
public class NotificationReceiver extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Receiver", "NotificationReceiver");
        finish();
    }
}
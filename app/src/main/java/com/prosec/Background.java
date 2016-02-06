package com.prosec;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by gusta on 30/10/2015.
 */
public class Background extends Service {

    MainActivity principal = new MainActivity();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int StartId){

        principal.Executer("top -n 1");
        return START_STICKY;
    }
}

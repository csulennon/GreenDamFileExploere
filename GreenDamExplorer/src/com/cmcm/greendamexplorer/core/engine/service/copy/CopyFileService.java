package com.cmcm.greendamexplorer.core.engine.service.copy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CopyFileService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CopyFileEngine(this);
    }
}

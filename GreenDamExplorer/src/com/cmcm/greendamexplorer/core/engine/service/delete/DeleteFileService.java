package com.cmcm.greendamexplorer.core.engine.service.delete;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DeleteFileService extends Service {

    public DeleteFileService() {
        
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new DeleteEngine();
    }
    
    
}

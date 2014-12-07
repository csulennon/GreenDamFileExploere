package com.cmcm.greendamexplorer.core.engine.service.copy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.cmcm.greendamexplorer.activity.MainActivity;
import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class CopyNotyfication {

    private int mNotificationId = 0;
    private RemoteViews mRemoteViews = null;
    private Context mContext = null;
    private NotificationManager mManager = null;
    private Notification mNotification = null;
    private CopyFileEngine mEngine = null;
    private PendingIntent mPendingIntent = null;
    NotificationCompat.Builder mBuilder = null;

    public CopyNotyfication(Context context, CopyFileEngine engine) {
        super();
        this.mContext = context;
        this.mEngine = engine;
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void startNotyfy(int id) {

        Intent intent = new Intent();
        intent.setClass(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        mPendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_copy_file);
         
        mBuilder = new NotificationCompat.Builder(mContext);
        
        mBuilder.setContent(mRemoteViews);
        mBuilder.setAutoCancel(true).setOngoing(false);
        mBuilder.setContentIntent(mPendingIntent);
        mBuilder.setTicker("正在复制...").setWhen(System.currentTimeMillis()).setPriority(Notification.PRIORITY_DEFAULT);
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE).setSmallIcon(R.drawable.ic_launcher);
        mNotification = mBuilder.build();
        mNotification.defaults &= ~Notification.DEFAULT_VIBRATE;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mManager.notify(id, mNotification);
    }
    
    public void setNotyfiy(int id, String fileName, long allSize, long copySize, int progress) {
        mNotification.contentView.setProgressBar(R.id.progressBar1, 100, progress, false);
        mNotification.contentView.setTextViewText(R.id.mTvCopyNotifyFileName, FileUtils.getFileName(fileName));
        mNotification.contentView.setTextViewText(R.id.mTvCopyNotifySize, TextUtil.getSizeSting(copySize)+"/" + TextUtil.getSizeSting(allSize));
        
        mManager.notify(id, mNotification);
        
    }
    
    public void clear(int id) {
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mManager.notify(id, mNotification);
    }
    

}

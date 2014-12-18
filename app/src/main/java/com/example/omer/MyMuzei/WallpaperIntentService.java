package com.example.omer.MyMuzei;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WallpaperIntentService extends IntentService {

    public static final String BROADCAST_STOP_ACTION = "com.example.omer.MyMuzei.STOPSERVICE";
    public static final String BROADCAST_START_ACTION = "com.example.omer.MyMuzei.STARTSERVICE";
    private static boolean isFirstTime = true;
    private static boolean serviceIsStopped = false;
    private AlarmManager alarmManager;

    public WallpaperIntentService() {
        super("WallpaperIntentService");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    public static int i = 0;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SERVICEEEEEE", "onHandleIntent()");

         if (intent != null) {
            final String action = intent.getAction();
            if (BROADCAST_STOP_ACTION.equals(action)) {
                Log.d("Intent service", action);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                this.alarmManager.cancel(pendingIntent); // verifier si l'alarm ne vaut pas NULL quand on stop
                this.serviceIsStopped = true;
                stopService(intent);
                return;
            }
             else if (BROADCAST_START_ACTION.equals(action))
            {
                this.serviceIsStopped = false;
                intent.setAction("");
                Log.d("Intent service", "SET REPEATING");
            }
        }
        if (this.serviceIsStopped)
            return;
        setWallPaper();
        nextWPChange(intent);
        if (isFirstTime) {
            isFirstTime = false;
        }
    }

    private void nextWPChange(Intent theIntent) {

        try {
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, theIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            long currentTimeMillis = System.currentTimeMillis();
//        alarmManager.set(AlarmManager.RTC, currentTimeMillis + (5 * 1000), pendingIntent);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10 * 1000, pendingIntent);

            //this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, (20 * 1000), pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        private static boolean state = true;

    public void setWallPaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {

            Drawable currentWP = wallpaperManager.getDrawable();
            int height = currentWP.getIntrinsicHeight();
            int width = currentWP.getIntrinsicWidth();

            Bitmap rome;
            if (state) {
                rome = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.rome);
                state = false;
            }
            else {
                rome = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ggstyle);
                state = true;
            }

            Bitmap bitmap = Bitmap.createScaledBitmap(rome, width, height, true);
            wallpaperManager.setBitmap(bitmap);


            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Wallpaper change successfully", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

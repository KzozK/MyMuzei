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
    private static boolean isFirstTime = true;
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

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SERVICEEEEEE", "onHandleIntent()");

        if (intent != null) {
            final String action = intent.getAction();
            if (BROADCAST_STOP_ACTION.equals(action)) {
                Log.d("Intent service", action);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                return;
            }
        }
        setWallPaper();
        if (isFirstTime) {
            nextWPChange(intent);
            isFirstTime = false;
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void nextWPChange(Intent theIntent) {

        try {
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, theIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            long currentTimeMillis = System.currentTimeMillis();
//        alarmManager.set(AlarmManager.RTC, currentTimeMillis + (5 * 1000), pendingIntent);
            this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, (20 * 1000), pendingIntent);
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

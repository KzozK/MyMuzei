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
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.omer.wallpaperapp.action.FOO";
    private static final String ACTION_BAZ = "com.example.omer.wallpaperapp.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.omer.wallpaperapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.omer.wallpaperapp.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, WallpaperIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, WallpaperIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public WallpaperIntentService() {
        super("WallpaperIntentService");
    }

    private static boolean isFirstTime = true;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SERVICEEEEEE", "onHandleIntent()");

        //startTimer();

        //String dataString = workIntent.getDataString();

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
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
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC, currentTimeMillis + (5 * 1000), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, currentTimeMillis + (5 * 1000), pendingIntent);
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

            Toast toast = Toast.makeText(this, "Set wallpaper on BG successfully!", Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

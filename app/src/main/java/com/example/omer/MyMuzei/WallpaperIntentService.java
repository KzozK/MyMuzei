package com.example.omer.MyMuzei;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WallpaperIntentService extends IntentService {

    public static final String BROADCAST_STOP_ACTION = "com.example.omer.MyMuzei.STOPSERVICE";
    public static final String BROADCAST_START_ACTION = "com.example.omer.MyMuzei.STARTSERVICE";
    public static final String apiUrl = "http://mymuzei-api.herokuapp.com/api/get_wallpaper";
    private static boolean serviceIsStopped = false;
    private AlarmManager alarmManager;
    private ImageLoader imgLoader;

    public WallpaperIntentService() {
        super("WallpaperIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        this.imgLoader = ImageLoader.getInstance();

        // Create global configuration and initialize ImageLoader with this config
        if (!this.imgLoader.isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            this.imgLoader.getInstance().init(config);
        }
    }

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
            } else if (BROADCAST_START_ACTION.equals(action)) {
                this.serviceIsStopped = false;
                intent.setAction("");
                Log.d("Intent service", "SET REPEATING");
            }
        }
        if (this.serviceIsStopped)
            return;
        new HttpAsyncTask().execute(apiUrl);
        nextWPChange(intent);
    }

    private void nextWPChange(Intent theIntent) {

        try {
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, theIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            long currentTimeMillis = System.currentTimeMillis();
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10 * 1000, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWallPaper(Bitmap wp) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {

            Drawable currentWP = wallpaperManager.getDrawable();
            int height = currentWP.getIntrinsicHeight();
            int width = currentWP.getIntrinsicWidth();

            Bitmap bitmap = Bitmap.createScaledBitmap(wp, width, height, true);
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

    //region LOAD DAILY IMAGE


    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArr = new JSONArray(result);
                JSONObject WPObject = jsonArr.getJSONObject(0);
                WPObject.getJSONObject("image").getString("url");
                String imgUrl = WPObject.getJSONObject("image").getString("url");
                Log.d("JSON ", imgUrl);
                downloadImage(imgUrl);

            } catch (JSONException ex) {
                Log.d("JSON Error", "JSON Parse error in HttpAsyncTask class.");
            }
        }
    }

    public void downloadImage(String imgUrl) {
        String url = "https://mymuzei-api.herokuapp.com" + imgUrl;

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.ggstyle)
                .showImageOnFail(R.drawable.ggstyle)
                .showImageOnLoading(R.drawable.ggstyle).build();

//download and display image from url
        imageLoader.loadImage(url, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                setWallPaper(loadedImage);
            }
        });
    }

    //endregion
}

package com.example.omer.MyMuzei;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.WallpaperManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    private static MainActivity mInstance = null;
    private Bitmap wallpaper;
    public static final String BROADCAST_STOP_ACTION = "com.example.omer.MyMuzei.STOPSERVICE";
    public static final String BROADCAST_START_ACTION = "com.example.omer.MyMuzei.STARTSERVICE";
    public static final String apiUrl = "http://mymuzei-api.herokuapp.com/api/get_wallpaper";
    private ProgressDialog mProgress;
    private int mProgressStatus = 0;

    public static MainActivity getInstance() {
        if (mInstance == null) {
            mInstance = new MainActivity();
        }
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wallpaper = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ggstyle);
        setupProgressView();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        // check if you are connected or not
        if (isConnected()) {
            mProgress.show();
            new HttpAsyncTask().execute(apiUrl);
        } else {
            Toast.makeText(this, "You are not connected please try again later", Toast.LENGTH_LONG).show();
        }
    }

    private void setupProgressView()
    {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Downloading today's wallpaper :) ");
        mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgress.setIndeterminate(true);
        mProgress.setProgressNumberFormat(null);
        mProgress.setProgressPercentFormat(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void setWallPaper(View view) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {

            Drawable currentWP = wallpaperManager.getDrawable();
            int height = currentWP.getIntrinsicHeight();
            int width = currentWP.getIntrinsicWidth();

            Bitmap bitmap = Bitmap.createScaledBitmap(wallpaper, width, height, true);
            wallpaperManager.setBitmap(bitmap);

            Toast toast = Toast.makeText(this, "Set wallpaper successfully!", Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //region Start/Stop service

    public void stopService(View view) {
        Intent wpService = new Intent(this, WallpaperIntentService.class);
        wpService.setAction(BROADCAST_STOP_ACTION);
        startService(wpService);
        Toast.makeText(this, "service stoped successfully!", Toast.LENGTH_LONG).show();
        Log.d("MES LOOOGGG", "stop service");
    }

    public void startService(View view) {

        Log.d("MES LOOOGGG", "start service");
        Intent wpService = new Intent(this, WallpaperIntentService.class);
        wpService.setAction(BROADCAST_START_ACTION);
        startService(wpService);
        Toast.makeText(this, "service started successfully!", Toast.LENGTH_LONG).show();
        //finish();
    }

    //endregion

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
                downloadImage(imgUrl);

            } catch (JSONException ex) {
                Toast.makeText(getApplicationContext(), "Couldn't load today's WallPaper. Please try again later.", Toast.LENGTH_LONG).show();
                mProgress.cancel();
            }
        }
    }

    public void downloadImage(String imgUrl)
    {
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
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Toast.makeText(getApplicationContext(), "Couldn't load today's WallPaper. Please try again later.", Toast.LENGTH_LONG).show();
                mProgress.cancel();
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ImageView imageView = (ImageView) findViewById(R.id.wpImageView);
                mProgress.cancel();
                wallpaper = loadedImage;
                imageView.setImageBitmap(wallpaper);
            }
        });
    }

    //endregion
}

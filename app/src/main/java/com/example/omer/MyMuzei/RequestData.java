package com.example.omer.MyMuzei;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
 * Created by omer on 21/12/2014.
 */
public class RequestData {

    private String url;
    Context context;
    private MainActivity theActivity;

    public RequestData(Context activityContext, String url) {
        this.context = activityContext;
        theActivity = MainActivity.getInstance();

        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute(url);

    }

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


                //Handler mainHandler = new Handler(context.getMainLooper());

                theActivity.isConnected();

                //mainHandler.post(() -> {
                  //  theActivity.downloadImage(imgUrl);
                //});

                } catch (JSONException ex) {
                Log.d("JSON Error", "JSON Parse error in HttpAsyncTask class.");
            }
        }
    }





}

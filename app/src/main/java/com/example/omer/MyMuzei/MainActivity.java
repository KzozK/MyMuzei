package com.example.omer.MyMuzei;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.WallpaperManager;
import java.io.IOException;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.view.View;
import android.graphics.BitmapFactory;

public class MainActivity extends Activity {

    public void setWallPaper(View view) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {

            Drawable currentWP = wallpaperManager.getDrawable();
            int height = currentWP.getIntrinsicHeight();
            int width = currentWP.getIntrinsicWidth();

            Bitmap rome = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.rome);

            Bitmap bitmap = Bitmap.createScaledBitmap(rome, width, height, true);
            wallpaperManager.setBitmap(bitmap);

            Toast toast = Toast.makeText(this, "Set wallpaper successfully!", Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startService(View view)
    {
        Log.d("MES LOOOGGG", "start service");
        Intent wpService = new Intent(this, WallpaperIntentService.class);
        wpService.putExtra("url","www.toto.com");
        startService(wpService);
        Toast toast = Toast.makeText(this, "service started successfully!", Toast.LENGTH_LONG);
        toast.show();
        finish();
    }
/*
        Intent intent = new Intent(getApplicationContext(), AlaramReceiver.class);
        intent.putExtra(AlaramReceiver.ACTION_ALARM, AlaramReceiver.ACTION_ALARM);

        //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alert.idAlert, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        final PendingIntent pIntent = PendingIntent.getBroadcast(this, 1234567,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(pIntent);
    }*/

    private void downloadImage()
    {/*
        String name = c.getString(str_url);
        URL url_value = new URL(name);
        ImageView profile = (ImageView)v.findViewById(R.id.vdo_icon);
        if (profile != null) {
            Bitmap mIcon1 =
                    BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
            profile.setImageBitmap(mIcon1);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // startService(new View(getApplicationContext()));
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

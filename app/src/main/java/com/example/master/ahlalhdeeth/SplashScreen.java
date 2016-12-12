package com.example.master.ahlalhdeeth;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

//import com.daimajia.androidanimations.library.Techniques;
//import com.daimajia.androidanimations.library.YoYo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class SplashScreen extends Activity {

    public static int SPLASH_TIME_OUT = 3000;
    public static String stats = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //check for the internet connection.
        ConnectionDetector connection = new ConnectionDetector(SplashScreen.this);

        if(connection.IsConnectedToInternet()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        String url = "http://www.ahlalhdeeth.com/vb/index.php";
                        Document document = Jsoup.connect(url).get();
                        String title = document.title();

                        // sending a response of all big titles in the forum with their links/.
                        Elements Items = document.select("tbody#collapseobj_forumhome_stats");
                        stats = Items.text();
                        Log.i("thelog", stats);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    Bundle translateBundle = ActivityOptions.makeCustomAnimation(SplashScreen.this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                    startActivity(intent, translateBundle);
                    finish();
                }
            }.execute();
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }
}

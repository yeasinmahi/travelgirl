package com.auspicious.anik.travelgirl;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    private WebView tgwebView;
    AdView myAdView ;
    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckInternet();
        MobileAds.initialize(this,getString(R.string.app_id));
        BnrAdd ();
    }

    public void BnrAdd ()
    {

        myAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder().build();
        myAdView.loadAd(adRequest);
    }

    public void PrepAd()
    {
        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(MainActivity.this);
        // Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
        AdRequest adRequest= new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                displayInterstitial();
            }
        });
    }


    public void displayInterstitial() {
    // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
       /* ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (interstitial.isLoaded()) {
                         interstitial.show();
                        }
                       // PrepAd();
                    }
                });

            }
        }, 30, 30, TimeUnit.SECONDS);*/
    }

    @Override
    public void onBackPressed() {
        if(tgwebView.canGoBack()) {
            tgwebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }

        return false;

    }
    public void CheckInternet()
    {

        WebViewClient mWebClient = new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url){
                if (isConnected()) {
                    view.loadUrl(url);
                    if(url.contains("travelettesofbangladesh.com/blog") || url.contains("travelettesofbangladesh.com/videos") || url.contains("travelettesofbangladesh.com/event")){
                        PrepAd();
                    }
                }else{
                    buildDialog(MainActivity.this).show();
                }
                return true;
            }

            @Override
            public void onLoadResource(WebView  view, String  url){

            }
        };
        //start checking internet connection
        if(!isConnected(this)) {
            buildDialog(MainActivity.this).show();
        }
        else {
            Toast.makeText(MainActivity.this,"Welcome Travelettes!!", Toast.LENGTH_SHORT).show();
            // setContentView(R.layout.activity_main);
            tgwebView = (WebView)findViewById(R.id.webView);


            tgwebView.setWebViewClient(mWebClient);
            WebSettings webSettings = tgwebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            tgwebView.loadUrl("http://travelettesofbangladesh.com/");
        }
        //end checking internet connection
    }


    //start internet connection dialogue
    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting())
        {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }

    }
    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or WiFi to access this app.");

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                CheckInternet();
            }

        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                CheckInternet();
            }
        });
        return builder;
    }
    //end internet connection dialogue
}
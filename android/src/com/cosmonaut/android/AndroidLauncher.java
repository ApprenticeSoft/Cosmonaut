package com.cosmonaut.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.cosmonaut.ActionResolver;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AndroidLauncher extends AndroidApplication implements ActionResolver {

    private static final String TAG = "AndroidLauncher";
    private static final String INTERSTITIAL_ID = "ca-app-pub-7775582829834874/4753629945";

    private InterstitialAd interstitialAd;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.r = 8;
        config.g = 8;
        config.b = 8;
        config.a = 8;
        config.maxSimultaneousSounds = 32;
        config.useImmersiveMode = true;

        initialize(new MyGdxGame(this), config);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        MobileAds.initialize(this, initializationStatus -> loadInterstitialInternal());
    }

    @Override
    public void removeAds() {
        // Legacy in-app billing was removed during modernization.
        Data.setFullVersion(true);
        Log.i(TAG, "removeAds called: full version flag enabled locally");
    }

    @Override
    public void processPurchases() {
        // Billing migration is intentionally decoupled from this technical upgrade.
        Log.i(TAG, "processPurchases noop");
    }

    @Override
    public void showOrLoadInterstital() {
        runOnUiThread(() -> {
            if (!isConnected()) {
                return;
            }

            if (interstitialAd != null) {
                InterstitialAd ad = interstitialAd;
                interstitialAd = null;
                ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadInterstitialInternal();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.w(TAG, "Interstitial show failed: " + adError);
                        loadInterstitialInternal();
                    }
                });
                ad.show(this);
            } else {
                loadInterstitialInternal();
            }
        });
    }

    @Override
    public void LoadInterstital() {
        runOnUiThread(this::loadInterstitialInternal);
    }

    @Override
    public void showAdsBottom() {
        // Banner ads are intentionally disabled in this modernized branch.
    }

    @Override
    public void showAdsTop() {
        // Banner ads are intentionally disabled in this modernized branch.
    }

    @Override
    public void hideAds() {
        // Banner ads are intentionally disabled in this modernized branch.
    }

    @Override
    public int hauteurBanniere() {
        return 0;
    }

    @Override
    public boolean adsListener() {
        return Data.getFullVersion();
    }

    @Override
    public boolean isConnected() {
        if (connectivityManager == null) {
            return false;
        }

        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (capabilities == null) {
            return false;
        }

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
    }

    private void loadInterstitialInternal() {
        if (interstitialAd != null || !isConnected()) {
            return;
        }

        InterstitialAd.load(this, INTERSTITIAL_ID, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd ad) {
                        interstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        interstitialAd = null;
                        Log.w(TAG, "Interstitial load failed: " + loadAdError);
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }
}

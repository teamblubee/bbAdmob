package com.android.godot;

import android.util.Log;
import android.view.View;
import android.app.Activity;
import com.google.android.gms.ads.*;

import android.view.Gravity;
import android.widget.FrameLayout;
import android.view.ViewGroup.LayoutParams;

import android.provider.Settings;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GodotGooglePlayServices extends Godot.SingletonBase {

    private Activity            m_activity;
    private AdView              m_adview;
    private AdRequest           m_adrequest;
    private AdRequest           i_adrequest;
    private AdListener          m_adlistener;
    private InterstitialAd      m_interstitial;

    private boolean             isBannerOnTop = false;
    private boolean             isShowingBanner = false;
    private boolean             isBannerReady = false;
    private boolean             isinterstitialReady = false;
    private boolean             isInterstitialShowing = false;
    private boolean             isDebug = true;
    private boolean             isTest;

    private String              m_app_id;

    static public Godot.SingletonBase initialize(Activity p_activity) {

                return new GodotGooglePlayServices(p_activity);
    } 

    public GodotGooglePlayServices(Activity p_activity) {

          registerClass("bbAdmob", new String[]{"init_admob_test","init_admob_real","show_banner","hide_banner","show_interstitial"});
          m_activity = p_activity;

    }

    private String get_device_id(){
        String device_id_string = Settings.Secure.getString(m_activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        String admobDeviceId = md5(device_id_string).toUpperCase();

        return admobDeviceId;
    }

    private String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest   digest  = java.security.MessageDigest.getInstance("MD5");
            
            digest.update(s.getBytes());
            byte        messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer    hexString       = new StringBuffer();
            for(int i=0; i<messageDigest.length; i++) {
                String  h   = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch(NoSuchAlgorithmException e) {
            Log.d("--------- godot ----------", "failed to get android DEVICE ID from Java");
        }
        return "";
    }

    private void banner() {
        m_activity.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                FrameLayout layout = ((Godot)m_activity).layout;
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                
                if(isBannerOnTop){
                    layoutParams.gravity = Gravity.TOP;
                } else {
                    layoutParams.gravity = Gravity.BOTTOM;
                }

                if(m_adview != null) {
                    layout.removeView(m_adview);
                }
                
                layout.addView(m_adview, layoutParams);

                if(isShowingBanner){
                    m_adview.setVisibility(View.GONE);
                    m_adview.pause();
                    if(isDebug) {
                        Log.d("--------- godot ----------", "admob banner paused");   
                    }
                    isShowingBanner = !isShowingBanner;
                } else {
                    m_adview.setVisibility(View.VISIBLE);
                    m_adview.resume();
                    isShowingBanner = !isShowingBanner;
                    if(isDebug){
                        Log.d("--------- godot ----------", "admob banner resumed");
                    }
                }
            }
        });
    }

    private void prepare_banner_ad() {
        m_adlistener = new AdListener()
        {
            @Override public void onAdLoaded(){
                isBannerReady = true;
                if(isDebug){
                    Log.d("--------- godot ----------", "Banner ad listener loaded an ad: ");
                }
                
            }
            @Override public void onAdFailedToLoad(int errorCode) {

                String log = "Banner ad listener failed to load an ad: ";
                String err;

                switch(errorCode) {
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        err = "ERROR_CODE_INTERNAL_ERROR";
                        break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:
                        err = "ERROR_CODE_INVALID_REQUEST";
                        break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:
                        err = "ERROR_CODE_NETWORK_ERROR";
                        break;
                    case AdRequest.ERROR_CODE_NO_FILL:
                        err = "ERROR_CODE_NO_FILL";
                        break;
                    default:
                        err = "Banner failed To Load Ad Error because of unknown error code";
                        break;
                }
                Log.w("--------- godot ----------", log + err); 
            }
        };

        m_adview = new AdView(m_activity);
        m_adview.setAdUnitId(m_app_id);
        m_adview.setAdSize(AdSize.SMART_BANNER);
        m_adview.setAdListener(m_adlistener);
        m_adview.loadAd(m_adrequest);
    }

    public void prepare_interstitial_ads() {
        m_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isDebug) {
                    Log.d("--------- godot ----------", "m_interstitial preparing to show ads");
                }
                m_interstitial = new InterstitialAd(m_activity);
                m_interstitial.setAdUnitId(m_app_id);
                m_interstitial.setAdListener(new AdListener(){
                    @Override public void onAdLoaded(){
                        isinterstitialReady = true;
                        Log.d("--------- godot ----------", "m_interstitial ad listener loaded an ad: ");
                    }
                    @Override 
                    public void onAdFailedToLoad(int errorCode) {

                        String log = "m_interstitial ad listener failed to load an ad";
                        String err;

                        switch(errorCode) {
                            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                                err = "ERROR_CODE_INTERNAL_ERROR";
                                break;
                            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                                err = "ERROR_CODE_INVALID_REQUEST";
                                break;
                            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                                err = "ERROR_CODE_NETWORK_ERROR";
                                break;
                            case AdRequest.ERROR_CODE_NO_FILL:
                                err = "ERROR_CODE_NO_FILL";
                                break;
                            default:
                                err = "m_interstitial failed to load an ad because of unknown error code";
                                break;
                        }
                        Log.w("--------- godot ----------", log + err);
                    }
                    @Override
                    public void onAdClosed(){
                        i_adrequest = get_ads();
                        m_interstitial.loadAd(i_adrequest);
                    }
                });
                i_adrequest = get_ads();
                m_interstitial.loadAd(i_adrequest);
            }
        });
    }

    private AdRequest get_ads() {
        AdRequest ad;
        if(isTest) {
            if(isDebug) {
                Log.d("--------- godot ----------", "requesting to get real ads");
            }
            ad = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice(get_device_id())
            .build();
        } else {
            if(isDebug) {
                Log.d("--------- godot ----------", "requesting to get test ads");
            }
            ad = new AdRequest.Builder().build();
        }
        return ad;
    }

    public void init_admob_test(final String app_id, boolean isTop) {
        isTest = true;
        isDebug = true;
        isBannerOnTop = isTop;
        m_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isDebug = false;
                m_app_id = app_id;
                m_adrequest = get_ads();
                prepare_banner_ad();
                prepare_interstitial_ads();  
            }
        });   
    }

    public void init_admob_real(final String app_id, boolean isTop) {
        isTest = false;
        isBannerOnTop = isTop;
        m_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isDebug = false;
                m_app_id = app_id;
                m_adrequest = get_ads();
                prepare_banner_ad();
                prepare_interstitial_ads();
            }
        });   
    }

    public void show_banner() {
        if(isShowingBanner) {
            return;
        } else {
            banner();
        }
    }

    public void hide_banner() {
        if(!isShowingBanner) {
            return;
        } else {
            banner();
        }
    }

    public void show_interstitial() {
        m_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(m_interstitial.isLoaded()) {
                    m_interstitial.show();
                    if(isDebug) {
                        Log.d("--------- godot ----------", "m_interstitial loaded and your pressed to show");
                    }
                } else {
                    if(isDebug) {
                        Log.d("--------- godot ----------", "you tried to show m_interstitial but it isn't loaded yet");
                    }
                }
            }
        });
    }

// http://stackoverflow.com/questions/3934331/android-how-to-encrypt-a-string
}
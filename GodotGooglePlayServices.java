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
    private AdRequest           m_banner_adrequest;
    private AdRequest           m_interstitial_adrequest;

    private InterstitialAd      m_interstitial;

    private boolean             isBannerOnTop = false;
    private boolean             isBannerDisabled = false;
    private boolean             isShowingBanner = false;
    private boolean             isShowingInterstitial = false;
    private boolean             isInterstitialDisabled = false;
    private boolean             wasShowingBanner = false;
    private boolean             isBannerReady = false;
    private boolean             isinterstitialReady = false;
    private boolean             isDebug = true;
    private boolean             isFirstRun = true;
    private boolean             isTest;

    private String              m_banner_id;
    private String              m_interstitial_id;
    private int                 m_device_id;


    static public Godot.SingletonBase initialize(Activity p_activity) { return new GodotGooglePlayServices(p_activity); } 

    public GodotGooglePlayServices(Activity p_activity) {

          registerClass("bbAdmob", new String[]{"init_admob_test","init_admob_real",
                                                "init_admob_banner_test","init_admob_banner_real",
                                                "show_banner","hide_banner","show_interstitial",
                                                "init_admob_interstitial_test", "init_admob_interstitial_real","get_instance_id"});
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
            Log.w("--------- godot ----------", "failed to get android DEVICE ID from Java");
        }
        return "";
    }

    private void banner() {
        if(isBannerReady){
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
                        isShowingBanner = false;
                        if(isDebug) {
                            Log.d("--------- godot ----------", "admob banner paused");   
                        }
                    } else {
                        m_adview.setVisibility(View.VISIBLE);
                        m_adview.resume();
                        isShowingBanner = true;
                        if(isDebug){
                            Log.d("--------- godot ----------", "admob banner resumed");
                        }
                    }
                }
            });
        } else {
            Log.w("--------- godot ----------", "Trying to show banner ad before it's ready");
            return;
        }
    }

    private void prepare_banner_ads() {
        if(isDebug) {
            Log.d("--------- godot ----------", "banner preparing to show ads");
        }
        m_adview = new AdView(m_activity);
        m_adview.setAdUnitId(m_banner_id);
        m_adview.setAdSize(AdSize.SMART_BANNER);



        m_adview.setAdListener(new AdListener() {
            @Override public void onAdLoaded(){
                isBannerReady = true;
                if(isDebug){
                    Log.d("--------- godot ----------", "banner ad listener loaded an ad: ");
                }
                
            }
            @Override public void onAdFailedToLoad(int errorCode) {

                String log = "banner failed to load an ad: ";
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
                        err = "banner ad failed to load due to unknown error code";
                        break;
                }
                Log.w("--------- godot ----------", log + err); 
            }
        });
        m_banner_adrequest = get_ads();
        m_adview.loadAd(m_banner_adrequest);
    }

    private void prepare_interstitial_ads() {
        if(isDebug) {
            Log.d("--------- godot ----------", "m_interstitial preparing to show ads");
        }
        m_interstitial = new InterstitialAd(m_activity);
        m_interstitial.setAdUnitId(m_interstitial_id);
        m_interstitial.setAdListener(new AdListener(){
            @Override 
            public void onAdLoaded(){
                    isinterstitialReady = true;
                    Log.d("--------- godot ----------", "m_interstitial ad listener loaded an ad: ");
                }
                @Override 
                public void onAdFailedToLoad(int errorCode) {

                    String log = "m_interstitial ad listener failed to load an ad: ";
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
                    isShowingInterstitial = false;
                    m_interstitial_adrequest = get_ads();
                    m_interstitial.loadAd(m_interstitial_adrequest);
                }
        });
                    m_interstitial_adrequest = get_ads();
                    m_interstitial.loadAd(m_interstitial_adrequest);
    }

    private AdRequest get_ads() {
        AdRequest ad;
        if(isTest) {
            if(isDebug) {
                Log.d("--------- godot ----------", "requesting to get test ads");
            }

            ad = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDevice(get_device_id())
            .build();

        } else {
            if(isDebug) {
                Log.d("--------- godot ----------", "requesting to get real ads");
            }

            ad = new AdRequest.Builder().build();
        }
        return ad;
    }

    private void init(String banner_id, String interstitial_id, boolean isTop, boolean isTesting, boolean isDebuging) {
        isDebug = isDebuging;
        isBannerOnTop = isTop;
        isTest = isTesting;

        if(banner_id == null) {
            isBannerDisabled = true;
            Log.d("--------- godot ----------", "Failed to provide a banner id, cannot request banner ads");
            banner_id = "";
        } else if (banner_id.length() <= 0) {
            isBannerDisabled = true;
        } else {
            m_banner_id = banner_id;
        }

        if(interstitial_id == null ) {
            isInterstitialDisabled = true;
            interstitial_id = "";
            Log.d("--------- godot ----------", "Failed to provide a interstitial id, cannot request interstitial ads");
            return;
        } else if (interstitial_id.length() <= 0) {
            isInterstitialDisabled = true;
        } else {
            m_interstitial_id = interstitial_id;
        }

        isFirstRun = false;
    }

    private void init_both() {
        prepare_banner_ads();
        prepare_interstitial_ads();
    }


    public void init_admob_test(String banner_id, String interstitial_id, boolean isTop) {
        if(isFirstRun) {
            init(banner_id, interstitial_id, isTop, true, true);
            m_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    init_both();
                    Log.w("--------- godot ----------", "initing both in test mode");
                }
            });  
        } else {
            Log.w("--------- godot ----------", "trying to init admob twice, should only be called once");
            return;
        }
    }

    public void init_admob_real(String banner_id, String interstitial_id, boolean isTop) {
        if(isFirstRun) {
            init(banner_id, interstitial_id, isTop, false, false);
            m_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    init_both();
                    Log.w("--------- godot ----------", "initing both in real mode");
                }
            });  
        } else {
            
            Log.w("--------- godot ----------", "trying to init admob twice, should only be called once");
            return;
        }
    }

    public void init_admob_banner_test(String banner_id, boolean isTop) {
        if(isFirstRun) {
            init(banner_id, null , isTop, true, true);
            m_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isInterstitialDisabled = true;
                    prepare_banner_ads();
                    Log.w("--------- godot ----------", "initing banner in test mode");
                }
            });  
        } else {
            
            Log.w("--------- godot ----------", "trying to init admob twice, should only be called once");
            return;
        }
    }

    public void init_admob_banner_real(String banner_id, boolean isTop) {
        if(isFirstRun) {
            init(banner_id, null , isTop, false, false);
            m_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isInterstitialDisabled = true;
                    prepare_banner_ads();
                    Log.w("--------- godot ----------", "initing banner in real mode");
                }
            });  
        } else {
            
            Log.w("--------- godot ----------", "trying to init admob twice, should only be called once");
            return;
        }
    }

    public void init_admob_interstitial_test(String interstitial_id, boolean isTop) {
        if(isFirstRun) {
            init(null, interstitial_id , isTop, true, true);
            m_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isBannerDisabled = true;
                    prepare_interstitial_ads();
                    Log.w("--------- godot ----------", "initing interstitial in test mode");
                }
            });  
        } else {
            
            Log.w("--------- godot ----------", "trying to init admob twice, should only be called once");
            return;
        }
    }

    public void init_admob_interstitial_real(String interstitial_id, boolean isTop) {
        if(isFirstRun) {
            init(null, interstitial_id , isTop, false, false);
            m_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isBannerDisabled = true;
                    prepare_interstitial_ads();
                    Log.w("--------- godot ----------", "initing interstitial in real mode");
                }
            });  
        } else {
            
            Log.w("--------- godot ----------", "trying to init admob twice, should only be called once");
            return;
        }
    }

    public void show_banner() {
        if(!isBannerDisabled) {
            if(isShowingBanner) {
                return;
            } else {
                banner();
            }
        } else {
            Log.w("--------- godot ----------", "cannot show or hide banner ads after init_admob_banner_");
            return;
        }
    }

    public void hide_banner() {
        if(!isBannerDisabled) {
            if(!isShowingBanner) {
                return;
            } else {
                banner();
            }
        } else {
            Log.w("--------- godot ----------", "cannot show or hide ads after init_admob_banner_");
            return;
        }

    }

    public void show_interstitial() {
        if(!isInterstitialDisabled) {
            m_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(m_interstitial.isLoaded()) {
                        m_interstitial.show();
                        isShowingInterstitial = true;
                        if(isDebug) {
                            Log.d("--------- godot ----------", "m_interstitial_adrequest loaded and your request an ad to be shown");
                        }
                    } else {
                        if(isDebug) {
                            Log.d("--------- godot ----------", "you tried to show m_interstitial_adrequest but an ad isn't loaded yet");
                        }
                    }
                }
            });
        } else {
            Log.w("--------- godot ----------", "cannot use interstitial ads after init_admob_interstitial_");
            return;
        }
    }

    protected void onMainPause() {
        if(isShowingBanner && isShowingInterstitial) {
            wasShowingBanner = true;
            hide_banner();
        } else if (isShowingBanner && !isShowingInterstitial) {
            wasShowingBanner = true;
            hide_banner();
        }
    }
    
    protected void onMainResume() {
        if(wasShowingBanner) {
            show_banner();
            wasShowingBanner = false;
        }
    }

    public void get_instance_id(int id) {

        m_device_id = id;
    }
    
// http://stackoverflow.com/questions/3934331/android-how-to-encrypt-a-string
}
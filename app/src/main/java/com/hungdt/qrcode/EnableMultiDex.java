package com.hungdt.qrcode;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.hungdt.qrcode.utils.MySetting;

public class EnableMultiDex extends MultiDexApplication {
    private static final long CONFIG_EXPIRE_SECOND = 1 * 3600;
    public static Context context;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        try {
            MobileAds.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            AdSettings.addTestDevice("19f68107-a38f-4a30-a42d-57e74b799bed");
            AudienceNetworkAds.initialize(this);
            AppEventsLogger.activateApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
            QRCodeConfigs.getInstance().setConfig(config);
            FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
            config.setConfigSettingsAsync(settings);

            config.setDefaultsAsync(R.xml.remote_config_defaults);

            long expireTime = config.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0 : CONFIG_EXPIRE_SECOND;
            config.fetch(expireTime)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                QRCodeConfigs.getInstance().getConfig().fetchAndActivate();
                                getConfigGgFb();
                            }
                        }
                    });
        } catch (Exception ignored) {
        }

    }

    private void getConfigGgFb(){
        try{
            long config = QRCodeConfigs.getInstance().getConfig().getLong("config_gg_fb");
            MySetting.putConfigGgFb(this, (int) config);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
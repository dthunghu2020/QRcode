package com.hungdt.qrcode;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class QRCodeConfigs {
    private static QRCodeConfigs _instance;
    private FirebaseRemoteConfig config;

    private QRCodeConfigs() {

    }

    public FirebaseRemoteConfig getConfig() {
        return this.config;
    }

    public void setConfig(FirebaseRemoteConfig config) {
        this.config = config;
    }

    public static QRCodeConfigs getInstance() {
        if (_instance == null) {
            _instance = new QRCodeConfigs();
        }
        return _instance;
    }
}
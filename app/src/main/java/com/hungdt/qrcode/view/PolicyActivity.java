package com.hungdt.qrcode.view;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.hungdt.qrcode.R;
import com.hungdt.qrcode.utils.Helper;

public class PolicyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        Helper.setColorStatusBar(this, R.color.status_bar);

        //Ads.initBanner(((LinearLayout)findViewById(R.id.lnNative)), this, true);

        initView();
    }

    private void initView() {
        try {
            WebView wvHome = (WebView) findViewById(R.id.webView);
            wvHome.loadUrl("file:///android_asset/index.html");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

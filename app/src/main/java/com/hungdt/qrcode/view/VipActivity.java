package com.hungdt.qrcode.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.hungdt.qrcode.QRCodeConfigs;
import com.hungdt.qrcode.R;
import com.hungdt.qrcode.utils.Ads;
import com.hungdt.qrcode.utils.MySetting;
import com.unity3d.ads.UnityAds;

public class VipActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private BillingProcessor billingProcessor;
    private boolean readyToPurchase = false;
    private Button btnVip;
    private ImageView imgBack;
    private TextView txtMoney;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);

        Ads.initBanner(((LinearLayout) findViewById(R.id.llBanner)), this, true);

        billingProcessor = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
        billingProcessor.initialize();

        btnVip = findViewById(R.id.btnVip);
        txtMoney = findViewById(R.id.txtMoney);

        btnVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyToPurchase) {
                    billingProcessor.subscribe(VipActivity.this, getString(R.string.ID_SUBSCRIPTION));
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(MySetting.isSubscription(VipActivity.this)){
            btnVip.setVisibility(View.GONE);
            txtMoney.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        try {
            Toast.makeText(this, "Thanks for your Purchased!", Toast.LENGTH_SHORT).show();
            MySetting.setSubscription(this, true);
            MySetting.putRemoveAds(this, true);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Unable to process billing", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        try {
            btnVip.setText("Start Now (" + billingProcessor.getSubscriptionListingDetails(getString(R.string.ID_SUBSCRIPTION)).priceText + "/Month)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (QRCodeConfigs.getInstance().getConfig().getBoolean("config_on")) {
            if (MainActivity.ggInterstitialAd != null && MainActivity.ggInterstitialAd.isLoaded())
                MainActivity.ggInterstitialAd.show();
            else if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
                UnityAds.show(VipActivity.this, getString(R.string.INTER_UNI));
        }
    }
}

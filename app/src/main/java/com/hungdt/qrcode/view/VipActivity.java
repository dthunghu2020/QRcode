package com.hungdt.qrcode.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.hungdt.qrcode.R;
import com.hungdt.qrcode.utils.MySetting;

public class VipActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private BillingProcessor billingProcessor;
    private boolean readyToPurchase = false;
    private Button btnVip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);

        billingProcessor = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
        billingProcessor.initialize();

        btnVip = findViewById(R.id.btnVip);
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

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
}

package com.hungdt.qrcode.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.hungdt.qrcode.R;
import com.hungdt.qrcode.utils.Helper;
import com.hungdt.qrcode.utils.KEY;
import com.hungdt.qrcode.utils.MySetting;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    public static Bitmap BITMAP;
    private static final int GALLERY_REQUEST_CODE = 203;
    public static final int FILE_SHARE_PERMISSION = 102;
    public static final int REQUEST_CODE_DETAIL_CODE = 200;

    private boolean flashlight = false;
    private boolean readyToPurchase = false;

    private ImageView imgMenu, imgScanImage, imgFlashOn, imgFlashOff;
    private FrameLayout flFlash;
    private LinearLayout llGenerateCode, llSaved, llLike, llHistory;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private DecoratedBarcodeView scanner_view;


    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            bp = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
            bp.initialize(); // binds
        } catch (Exception e) {
            e.printStackTrace();
        }

        initView();
        imgFlashOn.setVisibility(View.INVISIBLE);

        CameraSettings cameraSettings = new CameraSettings();
        cameraSettings.setAutoFocusEnabled(false);
        cameraSettings.setFocusMode(CameraSettings.FocusMode.CONTINUOUS);
        cameraSettings.setRequestedCameraId(0);

        scanner_view.setStatusText("");
        scanner_view.getBarcodeView().setCameraSettings(cameraSettings);

        scanner_view.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                openCodeScannedView(result);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) drawerLayout.openDrawer(Gravity.LEFT);
                else drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    /*case R.id.nav_setting:
                     *//*Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_SETTING);
                        if (!showInterstitial()) {
                            if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))

                                UnityAds.show(MainActivity.this, getString(R.string.INTER_UNI));
                        }*//*
                        break;*/
                    case R.id.nav_upgradeToVIP:
                        try {
                            startActivity(new Intent(MainActivity.this, VipActivity.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.nav_remove_add:
                        try {
                            removeAds();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.nav_rate_us:
                        startActivity(new Intent(MainActivity.this, RateAppActivity.class));
                        break;
                    case R.id.nav_feedback_dev:
                        Helper.feedback(MainActivity.this);
                        break;
                    case R.id.nav_share:
                        Helper.shareApp(MainActivity.this);
                        break;
                    case R.id.nav_policy:
                        startActivity(new Intent(MainActivity.this, PolicyActivity.class));
                        /*if (!showInterstitial()) {
                            if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
                                UnityAds.show(MainActivity.this, getString(R.string.INTER_UNI));
                        }*/
                        break;
                    case R.id.nav_more_app:
                        startActivity(new Intent(MainActivity.this, MoreAppActivity.class));
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        imgScanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageScan();
            }
        });

        flFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flashlight) {
                    imgFlashOn.setVisibility(View.VISIBLE);
                    imgFlashOff.setVisibility(View.INVISIBLE);
                    scanner_view.setTorchOn();
                    flashlight = true;
                } else {
                    imgFlashOn.setVisibility(View.INVISIBLE);
                    imgFlashOff.setVisibility(View.VISIBLE);
                    scanner_view.setTorchOff();
                    flashlight = false;
                }

            }
        });

        llGenerateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GenerateCodeActivity.class);
                startActivity(intent);
            }
        });

        llSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListCodeActivity.class);
                intent.putExtra(KEY.TYPE_VIEW, KEY.SAVED);
                startActivity(intent);
            }
        });

        llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListCodeActivity.class);
                intent.putExtra(KEY.TYPE_VIEW, KEY.LIKE);
                startActivity(intent);
            }
        });

        llHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListCodeActivity.class);
                intent.putExtra(KEY.TYPE_VIEW, KEY.HISTORY);
                startActivity(intent);
            }
        });

        checkPermission();
    }

    private void checkPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)||(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            startActivity(new Intent(this,AskPermissionActivity.class));
        }
    }

    private void removeAds() {
        try {
            if (readyToPurchase) {
                bp.subscribe(this, getString(R.string.ID_REMOVE_ADS));
            } else {
                Toast.makeText(this, "Billing not initialized", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkRemoveAds() {
        try {
            if (bp.isSubscribed(getString(R.string.ID_REMOVE_ADS))) {
                MySetting.putRemoveAds(this, true);
            } else {
                MySetting.putRemoveAds(this, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.nav_view);
        scanner_view = findViewById(R.id.scanner_view);
        imgMenu = findViewById(R.id.imgMenu);
        imgScanImage = findViewById(R.id.imgScanImage);
        imgFlashOn = findViewById(R.id.imgFlashOn);
        imgFlashOff = findViewById(R.id.imgFlashOff);
        flFlash = findViewById(R.id.flFlash);
        llGenerateCode = findViewById(R.id.llGenerateCode);
        llSaved = findViewById(R.id.llSaved);
        llLike = findViewById(R.id.llLike);
        llHistory = findViewById(R.id.llHistory);
    }

    private void openCodeScannedView(BarcodeResult result) {
        Intent intent = new Intent(MainActivity.this, CodeScannedActivity.class);
        intent.putExtra(KEY.RESULT_TEXT, result.getText());
        intent.putExtra(KEY.RESULT_TYPE_CODE, result.getBarcodeFormat().toString());
        intent.putExtra(KEY.TYPE_CREATE, KEY.TYPE_SCAN_CAMERA);
        BITMAP = result.getBitmap();
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (!bp.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    // Get the URI of the selected file
                    final Uri uri = data.getData();
                    try {
                        BITMAP = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert BITMAP != null;
                    scanQRImage(BITMAP);
                }
            }
        }
    }

    private void scanQRImage(Bitmap bMap) {
        String data = null;
        String type = null;
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            data = result.getText();
            type = result.getBarcodeFormat().toString();
            Log.e("123123", "scanQRImage: " + data);
        } catch (Exception e) {
            Log.e("QrTest", "Error decoding barcode", e);
        }

        Intent intent = new Intent(MainActivity.this, CodeScannedActivity.class);
        intent.putExtra(KEY.RESULT_TEXT, data);
        intent.putExtra(KEY.RESULT_TYPE_CODE, type);
        intent.putExtra(KEY.TYPE_CREATE, KEY.TYPE_SCAN_GALLERY);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        scanner_view.resume();
        super.onResume();
    }

    private void chooseImageScan() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onPause() {
        scanner_view.pause();
        super.onPause();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Toast.makeText(this, "Thank you for your purchased!", Toast.LENGTH_SHORT).show();
        checkRemoveAds();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "You have declined payment", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bp != null) {
            bp.release();
        }
    }


}

package com.hungdt.qrcode.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.hungdt.qrcode.utils.KEY;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Bitmap BITMAP;
    private static final int GALLERY_REQUEST_CODE = 203;

    private ImageView imgMenu, imgScanImage, imgFlashOn, imgFlashOff;
    private FrameLayout flFlash;
    private LinearLayout llGenerateCode, llSaved, llHistory;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private DecoratedBarcodeView scanner_view;

    private CameraSettings cameraSettings = new CameraSettings();

    private boolean flashlight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        imgFlashOn.setVisibility(View.INVISIBLE);

        cameraSettings = new CameraSettings();
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
                    case R.id.nav_setting:
                        /*Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_SETTING);
                        if (!showInterstitial()) {
                            if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))

                                UnityAds.show(MainActivity.this, getString(R.string.INTER_UNI));
                        }*/
                        break;
                    case R.id.nav_upgradeToVIP:
                        try {
                            //Intent intentVip = new Intent(MainActivity.this, VipActivity.class);
                            //startActivity(intentVip);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.nav_remove_add:
                        /*try {
                            //removeAds();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        break;
                    case R.id.nav_rate_us:
                        //startActivity(new Intent(MainActivity.this, RateAppActivity.class));
                        break;
                    case R.id.nav_feedback_dev:
                        //Helper.feedback(this);
                        break;
                    case R.id.nav_share:
                        //Helper.shareApp(this);
                        break;
                    case R.id.nav_policy:
                        //startActivity(new Intent(MainActivity.this, PolicyActivity.class));
                        /*if (!showInterstitial()) {
                            if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
                                UnityAds.show(MainActivity.this, getString(R.string.INTER_UNI));
                        }*/
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        imgScanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
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
                Intent intent = new Intent(MainActivity.this,ListCodeActivity.class);
                intent.putExtra(KEY.TYPE_VIEW,KEY.SAVED);
                startActivity(intent);
            }
        });

        llHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListCodeActivity.class);
                intent.putExtra(KEY.TYPE_VIEW,KEY.HISTORY);
                startActivity(intent);
            }
        });
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
        llHistory = findViewById(R.id.llHistory);
    }

    private void openCodeScannedView(BarcodeResult result) {
        Intent intent = new Intent(MainActivity.this, CodeScannedActivity.class);
        intent.putExtra(KEY.RESULT_TEXT, result.getText());
        intent.putExtra(KEY.RESULT_TYPE_CODE, result.getBarcodeFormat().toString());
        intent.putExtra(KEY.TYPE_CREATE, KEY.TYPE_SCAN_CAMERA);
        BITMAP = result.getBitmap();
        /*ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
        intent.putExtra(KEY.RESULT_BITMAP, bs.toByteArray());*/
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!= null) {
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
        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            data = result.getText();
            type = result.getBarcodeFormat().toString();
            Log.e("123123", "scanQRImage: "+data);
        }
        catch (Exception e) {
            Log.e("QrTest", "Error decoding barcode", e);
        }

        Intent intent = new Intent(MainActivity.this, CodeScannedActivity.class);
        intent.putExtra(KEY.RESULT_TEXT,data);
        intent.putExtra(KEY.RESULT_TYPE_CODE,type);
        intent.putExtra(KEY.TYPE_CREATE, KEY.TYPE_SCAN_GALLERY);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        requestForCamera();
        super.onResume();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                scanner_view.resume();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(MainActivity.this, "camera deny", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    protected void onPause() {
        scanner_view.pause();
        super.onPause();
    }
}

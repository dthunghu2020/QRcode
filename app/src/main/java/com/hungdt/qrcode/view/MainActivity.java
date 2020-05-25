package com.hungdt.qrcode.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.hungdt.qrcode.QRCodeConfigs;
import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.utils.Ads;
import com.hungdt.qrcode.utils.Helper;
import com.hungdt.qrcode.utils.KEY;
import com.hungdt.qrcode.utils.MySetting;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    public static Bitmap BITMAP;
    private static final int GALLERY_REQUEST_CODE = 203;
    public static final int FILE_SHARE_PERMISSION = 102;
    public static final int REQUEST_CODE_DETAIL_CODE = 200;

    public static InterstitialAd ggInterstitialAd;
    public static com.facebook.ads.InterstitialAd fbInterstitialAd;
    private RewardedVideoAd videoAds;
    private BillingProcessor bp;

    private boolean flashlight = false;
    private boolean readyToPurchase = false;
    private boolean openVipActivity = false;
    private boolean rewardedVideoCompleted = false;
    private int gemReward = 0;

    private TextView txtFlash;
    private ImageView imgMenu, imgRemoveAds, imgGift, imgGiftGenerate;
    private CircleImageView imgFlashOn, imgFlashOff;
    private LinearLayout llGenerateCode, llSaved, llLike, llHistory, llScanImage, llFlash, llBanner;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private DecoratedBarcodeView scanner_view;


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
        initInterstitialAd();
        Ads.initNativeGgFb((LinearLayout) findViewById(R.id.lnNative), this, true);
        Ads.initBanner(((LinearLayout) findViewById(R.id.llBanner)), this, true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        if (QRCodeConfigs.getInstance().getConfig().getBoolean("config_on")) {
            Ads.initNativeGg((LinearLayout) hView.findViewById(R.id.lnNative), this, true, true);
        }

        final Animation animationRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        imgGift.startAnimation(animationRotate);
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
                        if (!showInterstitial()) {
                            if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
                                UnityAds.show(MainActivity.this, getString(R.string.INTER_UNI));
                        }
                        break;
                    case R.id.nav_more_app:
                        startActivity(new Intent(MainActivity.this, MoreAppActivity.class));
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        llScanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageScan();
            }
        });

        llFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flashlight) {
                    txtFlash.setText("Flash: On");
                    imgFlashOn.setVisibility(View.VISIBLE);
                    imgFlashOff.setVisibility(View.INVISIBLE);
                    scanner_view.setTorchOn();
                    flashlight = true;
                } else {
                    txtFlash.setText("Flash: Off");
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
                if (DBHelper.getInstance(MainActivity.this).getCodeSaved().size() == MySetting.getMaxLength(MainActivity.this)) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        openVideoAdsDialog();
                    } else {
                        openGenerateCodeActivity();
                    }
                } else {
                    openGenerateCodeActivity();
                }
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
                intent.putExtra(KEY.TYPE_VIEW, KEY.FAVORITE);
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

        imgRemoveAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    removeAds();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        imgGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Daily Reward", Toast.LENGTH_SHORT).show();
                //loadVideoAds();
            }
        });
        checkMaxLength();

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }

        if (!openVipActivity && Helper.isConnectedInternet(this)) {
            Intent intent = new Intent(this, VipActivity.class);
            startActivity(intent);
            openVipActivity = true;
        }

    }

    private void checkMaxLength() {
        if (DBHelper.getInstance(this).getCodeSaved().size() < MySetting.getMaxLength(this)) {
            imgGiftGenerate.setVisibility(View.INVISIBLE);
        } else if (DBHelper.getInstance(this).getCodeSaved().size() == MySetting.getMaxLength(this)) {
            imgGiftGenerate.setVisibility(View.VISIBLE);
        } else {
            MySetting.setMaxLength(this, DBHelper.getInstance(this).getCodeSaved().size());
            imgGiftGenerate.setVisibility(View.VISIBLE);
        }
    }

    private void openGenerateCodeActivity() {
        Intent intent = new Intent(MainActivity.this, GenerateCodeActivity.class);
        startActivity(intent);
    }

    ProgressDialog progressDialog;
    Dialog morePlaceDialog;

    @SuppressLint("SetTextI18n")
    private void openVideoAdsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.video_ads_dialog, null);

        Button btnYes = view.findViewById(R.id.btnYes);
        Button btnNoThanks = view.findViewById(R.id.btnBack);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (morePlaceDialog != null) morePlaceDialog.dismiss();
                drawerLayout.closeDrawers();
                loadVideoAds();
            }
        });
        btnNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (morePlaceDialog != null) morePlaceDialog.dismiss();
            }
        });
        builder.setView(view);
        builder.setCancelable(false);
        morePlaceDialog = builder.create();
        Objects.requireNonNull(morePlaceDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        morePlaceDialog.show();
    }

    public void loadVideoAds() {
        if (Helper.isConnectedInternet(this)) {
            videoAds = MobileAds.getRewardedVideoAdInstance(this);
            videoAds.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewarded(RewardItem reward) {
                    MySetting.setMaxLength(MainActivity.this, MySetting.getMaxLength(MainActivity.this) + 2);
                    rewardedVideoCompleted = true;
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    if (rewardedVideoCompleted) {
                        openGenerateCodeActivity();
                    }
                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int errorCode) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MainActivity.this, "Loading video failed, please try again later", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRewardedVideoAdLoaded() {
                    if (videoAds != null && videoAds.isLoaded()) videoAds.show();
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRewardedVideoAdOpened() {
                }

                @Override
                public void onRewardedVideoStarted() {
                }

                @Override
                public void onRewardedVideoCompleted() {

                }
            });

            AdRequest adRequest = null;
            if (ConsentInformation.getInstance(this).getConsentStatus().toString().equals(ConsentStatus.PERSONALIZED) ||
                    !ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown()) {
                adRequest = new AdRequest.Builder().build();
            } else {
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, Ads.getNonPersonalizedAdsBundle())
                        .build();
            }
            videoAds.loadAd(getString(R.string.VIDEO_G), adRequest);

            try {
                progressDialog = new ProgressDialog(this);
                //todo ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                progressDialog.setIcon(R.drawable.ic_code);
                progressDialog.setMessage("Please wait, the Ad is loaded...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (videoAds != null && !videoAds.isLoaded()) {
                            videoAds.destroy(MainActivity.this);
                        }
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Loading video failed, please try again later", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 15000);
        } else {
            Toast.makeText(MainActivity.this, "Please check your internet connection!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void openGemRewardedDialog() {
        final Dialog gemRewardedDialog = new Dialog(MainActivity.this);
        gemRewardedDialog.setContentView(R.layout.reward_success_dialog);

        TextView txtGemRewarded = gemRewardedDialog.findViewById(R.id.txtGemRewarded);
        Button btnOk = gemRewardedDialog.findViewById(R.id.btnOk);
        ImageView imgBack = gemRewardedDialog.findViewById(R.id.imgBack);

        txtGemRewarded.setText("x " + gemReward);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gemRewardedDialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gemRewardedDialog.dismiss();
            }
        });
        Objects.requireNonNull(gemRewardedDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gemRewardedDialog.show();
    }

    private void initInterstitialAd() {
        initUnityInterstitialAd();
        initFbInterstitialAd(false);
        initGgInterstitialAd();
    }

    private void initGgInterstitialAd() {
        try {
            ggInterstitialAd = new InterstitialAd(this);
            ggInterstitialAd.setAdUnitId(getString(R.string.INTER_G));
            ggInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    try {
                        if (fbInterstitialAd != null) fbInterstitialAd.destroy();
                        initFbInterstitialAd(false);
                        requestNewInterstitial();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    try {
                        if (fbInterstitialAd != null) fbInterstitialAd.destroy();
                        initFbInterstitialAd(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });
            requestNewInterstitial();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFbInterstitialAd(final boolean isLoad_ID_FB_2) {
        try {
            if (!isLoad_ID_FB_2)
                fbInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.INTER_FB));
            else
                fbInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.INTER_FB_2));
            fbInterstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    requestNewFBInterstitial();
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    if (!isLoad_ID_FB_2) {
                        if (ggInterstitialAd == null) initGgInterstitialAd();
                        else requestNewInterstitial();
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });
            requestNewFBInterstitial();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUnityInterstitialAd() {
        try {
            if (!MySetting.isRemoveAds(this)) {
                UnityAds.initialize(this, getString(R.string.GAME_ID), new IUnityAdsListener() {
                    @Override
                    public void onUnityAdsReady(String placementId) {
                    }

                    @Override
                    public void onUnityAdsStart(String placementId) {
                    }

                    @Override
                    public void onUnityAdsFinish(String placementId, UnityAds.FinishState result) {
                    }

                    @Override
                    public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
                    }
                }, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestNewFBInterstitial() {
        try {
            if (!MySetting.isRemoveAds(this)) {
                fbInterstitialAd.loadAd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestNewInterstitial() {
        try {
            if (!MySetting.isRemoveAds(this)) {
                AdRequest adRequest = null;
                if (ConsentInformation.getInstance(this).getConsentStatus().toString().equals(ConsentStatus.PERSONALIZED) ||
                        !ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown()) {
                    adRequest = new AdRequest.Builder().build();
                } else {
                    adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, Ads.getNonPersonalizedAdsBundle())
                            .build();
                }
                ggInterstitialAd.loadAd(adRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean showInterstitial() {
        if (ggInterstitialAd != null && ggInterstitialAd.isLoaded()) {
            ggInterstitialAd.show();
            return true;
        } else if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
            fbInterstitialAd.show();
            return true;
        } else return false;
    }

    private void checkPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            startActivity(new Intent(this, AskPermissionActivity.class));
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
        imgRemoveAds = findViewById(R.id.imgRemoveAds);
        imgGift = findViewById(R.id.imgGift);
        imgGiftGenerate = findViewById(R.id.imgGiftGenerate);
        llScanImage = findViewById(R.id.llScanImage);
        imgFlashOn = findViewById(R.id.imgFlashOn);
        imgFlashOff = findViewById(R.id.imgFlashOff);
        llFlash = findViewById(R.id.llFlash);
        txtFlash = findViewById(R.id.txtFlash);
        llGenerateCode = findViewById(R.id.llGenerateCode);
        llSaved = findViewById(R.id.llSaved);
        llLike = findViewById(R.id.llLike);
        llHistory = findViewById(R.id.llHistory);
        llBanner = findViewById(R.id.llBanner);
    }

    private void openCodeScannedView(BarcodeResult result) {
        Intent intent = new Intent(MainActivity.this, CodeScannedActivity.class);
        intent.putExtra(KEY.RESULT_TEXT, result.getText());
        intent.putExtra(KEY.RESULT_TYPE_CODE, result.getBarcodeFormat().toString());
        intent.putExtra(KEY.TYPE_CREATE, KEY.TYPE_SCAN_CAMERA);
        BITMAP = result.getBitmap();
        startActivity(intent);

        if (MainActivity.ggInterstitialAd != null && MainActivity.ggInterstitialAd.isLoaded())
            MainActivity.ggInterstitialAd.show();
        else if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
            UnityAds.show(this, getString(R.string.INTER_UNI));

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
        String typeCode = null;
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            data = result.getText();
            typeCode = result.getBarcodeFormat().toString();
            Log.e("123123", "scanQRImage: \n" + data);
        } catch (Exception e) {
            //Log.e("QrTest", "Error decoding barcode", e);
        }

        Intent intent = new Intent(MainActivity.this, CodeScannedActivity.class);
        intent.putExtra(KEY.RESULT_TEXT, data);
        intent.putExtra(KEY.RESULT_TYPE_CODE, typeCode);
        intent.putExtra(KEY.TYPE_CREATE, KEY.TYPE_SCAN_GALLERY);
        startActivity(intent);

        if (MainActivity.ggInterstitialAd != null && MainActivity.ggInterstitialAd.isLoaded())
            MainActivity.ggInterstitialAd.show();
        else if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
            UnityAds.show(this, getString(R.string.INTER_UNI));
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


    @Override
    public void onBackPressed() {
        if (QRCodeConfigs.getInstance().getConfig().getBoolean("config_on")) {
            if (!showInterstitial()) {
                if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
                    UnityAds.show(MainActivity.this, getString(R.string.INTER_UNI));
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openExitAppDialog();
            }
        }, 300);
    }

    private void openExitAppDialog() {
        final BottomSheetDialog exitDialog = new BottomSheetDialog(this);
        exitDialog.setContentView(R.layout.exit_app_dialog);

        if (QRCodeConfigs.getInstance().getConfig().getBoolean("config_on")) {
            Ads.initNativeGgFb((LinearLayout) exitDialog.findViewById(R.id.lnNative), this, false);
        }

        Button btnYes = exitDialog.findViewById(R.id.btnYes);
        Button btnCancel = exitDialog.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }

}

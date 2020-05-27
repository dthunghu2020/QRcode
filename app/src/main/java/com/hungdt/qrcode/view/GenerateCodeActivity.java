package com.hungdt.qrcode.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hungdt.qrcode.QRCodeConfigs;
import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.dataset.Constant;
import com.hungdt.qrcode.utils.Ads;
import com.hungdt.qrcode.utils.Helper;
import com.hungdt.qrcode.utils.KEY;
import com.hungdt.qrcode.utils.MySetting;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.unity3d.ads.UnityAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Pattern;

public class GenerateCodeActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private RewardedVideoAd videoAds;
    private BillingProcessor bp;

    private Spinner spinnerCodeType;
    private EditText edtTextGenerateCode;
    private LinearLayout llGenerateCode, llSaveCodeGenerate, llShareCodeGenerate;
    private ImageView imgBack, imgCodeGenerate;
    private TextView txtText;

    private MultiFormatWriter multiFormatWriter;
    private BarcodeFormat barcodeFormat;

    private String dataSave;
    private String typeCodeSave;
    private String typeText;
    private boolean codeGenerated = false;
    private boolean codeSaved = false;
    private boolean readyToPurchase = false;

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        initView();
        Ads.initBanner(((LinearLayout) findViewById(R.id.llBanner)), this, true);
        Ads.initNativeGgFb((LinearLayout) findViewById(R.id.lnNative), this, true);
        multiFormatWriter = new MultiFormatWriter();

        bp = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
        bp.initialize();

        final ArrayList<String> names = new ArrayList<String>();
        names.add("QR_CODE");
        names.add("CODE_128");
        names.add("CODE_93");
        names.add("CODE_39");
        names.add("EAN_13");
        names.add("EAN_8");
        names.add("UPC_A");
        names.add("UPC_E");
        names.add("ITF");
        names.add("PDF_417");
        names.add("CODABAR");
        names.add("DATA_MATRIX");
        names.add("AZTEC");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        spinnerCodeType.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCodeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        barcodeFormat = BarcodeFormat.QR_CODE;
                        txtText.setText("Enter Phone number, Website, Email, ... \nto generate code");
                        break;
                    case 1:
                        barcodeFormat = BarcodeFormat.CODE_128;
                        txtText.setText("Enter Phone number, Website, Email, ... \n(Max 80 characters)");
                        break;
                    case 2:
                        barcodeFormat = BarcodeFormat.CODE_93;
                        txtText.setText("Enter Phone number, Website, Email, ... \n(Maximum 80 characters)");
                        break;
                    case 3:
                        barcodeFormat = BarcodeFormat.CODE_39;
                        txtText.setText("Enter Phone number, Website, Email, ... \n(Maximum 80 characters)");
                        break;
                    case 4:
                        barcodeFormat = BarcodeFormat.EAN_13;
                        txtText.setText("Support only number! 12 characters\n(+1 character depends on the previous 12 characters!)");
                        break;
                    case 5:
                        barcodeFormat = BarcodeFormat.EAN_8;
                        txtText.setText("Support only number! 7 characters\n(+1 character depends on the previous 7 characters!)");
                        break;
                    case 6:
                        barcodeFormat = BarcodeFormat.UPC_A;
                        txtText.setText("Support only number! 11 characters\n(+1 character depends on the previous 11 characters!)");
                        break;
                    case 7:
                        barcodeFormat = BarcodeFormat.UPC_E;
                        txtText.setText("Support only number! 7 characters\n(+1 character depends on the previous 7 characters!)");
                        break;
                    case 8:
                        barcodeFormat = BarcodeFormat.ITF;
                        txtText.setText("Support only even number!\n(Maximum 80 characters)");
                        break;
                    case 9:
                        barcodeFormat = BarcodeFormat.PDF_417;
                        txtText.setText("Enter your data to generate code\n(Not support some language!)");
                        break;
                    case 10:
                        barcodeFormat = BarcodeFormat.CODABAR;
                        txtText.setText("Support only number! (Obsolete)");
                        break;
                    case 11:
                        barcodeFormat = BarcodeFormat.DATA_MATRIX;
                        txtText.setText("Enter Phone number, Website, Email, ...\n(Not support some language!)");
                        break;
                    case 12:
                        barcodeFormat = BarcodeFormat.AZTEC;
                        txtText.setText("Enter Phone number, Website, Email, ... \nto generate code!");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        invisibleView();

        llGenerateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeCodeSave = barcodeFormat.toString();
                try {
                    switch (barcodeFormat) {
                        case QR_CODE:
                            generateCode(BarcodeFormat.QR_CODE,true);
                            break;
                        case CODE_128:
                            generateCode(BarcodeFormat.CODE_128,false);
                            break;
                        case CODE_93:
                            generateCode(BarcodeFormat.CODE_93,false);
                            break;
                        case CODE_39:
                            generateCode(BarcodeFormat.CODE_39,false);
                            break;
                        case EAN_13:
                            generateCode(BarcodeFormat.EAN_13,false);
                            break;
                        case EAN_8:
                            generateCode(BarcodeFormat.EAN_8,false);
                            break;
                        case UPC_A:
                            generateCode(BarcodeFormat.UPC_A,false);
                            break;
                        case UPC_E:
                            generateCode(BarcodeFormat.UPC_E,false);
                            break;
                        case ITF:
                            generateCode(BarcodeFormat.ITF,false);
                            break;
                        case PDF_417:
                            generateCode(BarcodeFormat.PDF_417,false);
                            break;
                        case CODABAR:
                            generateCode(BarcodeFormat.CODABAR,false);
                            break;
                        case DATA_MATRIX:
                            generateCode(BarcodeFormat.DATA_MATRIX,true);
                            break;
                        case AZTEC:
                            generateCode(BarcodeFormat.AZTEC,true);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                llGenerateCode.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llGenerateCode.setEnabled(true);
                    }
                },500);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        llSaveCodeGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MySetting.isSubscription(GenerateCodeActivity.this)){
                    Toast.makeText(GenerateCodeActivity.this, "Save Success!", Toast.LENGTH_SHORT).show();
                    DBHelper.getInstance(GenerateCodeActivity.this).addData(dataSave, typeCodeSave, typeText, getInstantDateTime(), KEY.TYPE_GENERATE, "Yes", "Like", "");
                    codeSaved = true;
                }else if (MySetting.getCount(GenerateCodeActivity.this) != MySetting.getMaxLength(GenerateCodeActivity.this)) {
                    if (edtTextGenerateCode.getText().toString().equals(dataSave)) {
                        MySetting.setCount(GenerateCodeActivity.this, MySetting.getCount(GenerateCodeActivity.this) + 1);
                        Toast.makeText(GenerateCodeActivity.this, "Save Success!", Toast.LENGTH_SHORT).show();
                        DBHelper.getInstance(GenerateCodeActivity.this).addData(dataSave, typeCodeSave, typeText, getInstantDateTime(), KEY.TYPE_GENERATE, "Yes", "Like", "");
                        codeSaved = true;
                    } else {
                        Toast.makeText(GenerateCodeActivity.this, "You have change code. Please regenerate and try again!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 24) {
                        openVideoAdsDialog();
                    } else {
                        if (edtTextGenerateCode.getText().toString().equals(dataSave)) {
                            Toast.makeText(GenerateCodeActivity.this, "Save Success!", Toast.LENGTH_SHORT).show();
                            DBHelper.getInstance(GenerateCodeActivity.this).addData(dataSave, typeCodeSave, typeText, getInstantDateTime(), KEY.TYPE_GENERATE, "Yes", "Like", "");
                            codeSaved = true;
                        } else {
                            Toast.makeText(GenerateCodeActivity.this, "You have change code. Please regenerate and try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                llSaveCodeGenerate.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llSaveCodeGenerate.setEnabled(true);
                    }
                },1250);
            }
        });

        llShareCodeGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        shareDialog();
                    } else {
                        ActivityCompat.requestPermissions(GenerateCodeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.FILE_SHARE_PERMISSION);
                    }
                } else {
                    shareDialog();
                }
            }
        });
    }

    ProgressDialog progressDialog;
    Dialog morePlaceDialog;

    @SuppressLint("SetTextI18n")
    private void openVideoAdsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.video_ads_dialog, null);

        Button btnActiveVip = view.findViewById(R.id.btnActiveVip);
        Button btnWatchVideo = view.findViewById(R.id.btnWatchVideo);
        ImageView imgX = view.findViewById(R.id.imgX);

        btnWatchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (morePlaceDialog != null) morePlaceDialog.dismiss();
                loadVideoAds();
            }
        });
        imgX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (morePlaceDialog != null) morePlaceDialog.dismiss();
            }
        });
        btnActiveVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyToPurchase) {
                    bp.subscribe(GenerateCodeActivity.this, getString(R.string.ID_SUBSCRIPTION));
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
                }
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
                    MySetting.setMaxLength(GenerateCodeActivity.this, MySetting.getMaxLength(GenerateCodeActivity.this) + 2);
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {
                }

                @Override
                public void onRewardedVideoAdClosed() {

                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int errorCode) {
                    try {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(GenerateCodeActivity.this, "Loading video failed, please try again later", Toast.LENGTH_SHORT).show();
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
                progressDialog.setTitle("More Generate Code");
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
                            videoAds.destroy(GenerateCodeActivity.this);
                        }
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Toast.makeText(GenerateCodeActivity.this, "Loading video failed, please try again later", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 15000);
        } else {
            Toast.makeText(GenerateCodeActivity.this, "Please check your internet connection!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.choose_type_share_bottom_dialog);

        LinearLayout llShareText = bottomSheetDialog.findViewById(R.id.llShareText);
        LinearLayout llShareImage = bottomSheetDialog.findViewById(R.id.llShareImage);

        assert llShareText != null;
        llShareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText(dataSave);
                bottomSheetDialog.dismiss();
            }
        });

        assert llShareImage != null;
        llShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePicturePNG(GenerateCodeActivity.this, imgCodeGenerate);
                bottomSheetDialog.dismiss();
            }
        });
        //Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();
    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(GenerateCodeActivity.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void shareText(String dataSave) {

        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        String shareBody = "Here is the share content body";
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share code");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, dataSave);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "Share Code"));
    }

    public void sharePicturePNG(Context context, ImageView content) {
        try {
            //lưu vào cache
            content.setDrawingCacheEnabled(true);
            //get bitmap từ cache
            Bitmap bitmap = content.getDrawingCache();
            //tạo file
            File directory = new File(getDirPathDownloaded());
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File cachePath = new File(directory + "/" + System.currentTimeMillis() + ".png");

            try {
                cachePath.createNewFile();
                FileOutputStream optream = new FileOutputStream(cachePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, optream);
                optream.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("xxxx", e.getMessage());
            }

            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(
                        context,
                        getString(R.string.author),
                        new File(cachePath.getPath())
                );
            } else {
                uri = Uri.fromFile(new File(cachePath.getPath()));
            }
            //Action share
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(share, "Share Code"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDirPathDownloaded() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/qrcodeshare";
    }

    private void invisibleView() {
        llSaveCodeGenerate.setVisibility(View.GONE);
        llShareCodeGenerate.setVisibility(View.GONE);
        imgCodeGenerate.setVisibility(View.GONE);
    }

    private void visibleView() {
        llSaveCodeGenerate.setVisibility(View.VISIBLE);
        llShareCodeGenerate.setVisibility(View.VISIBLE);
        imgCodeGenerate.setVisibility(View.VISIBLE);
    }

    private void initView() {
        imgCodeGenerate = findViewById(R.id.imgCodeGenerate);
        spinnerCodeType = findViewById(R.id.spinnerCodeType);
        edtTextGenerateCode = findViewById(R.id.edtTextGenerateCode);
        llGenerateCode = findViewById(R.id.llGenerateCode);
        llSaveCodeGenerate = findViewById(R.id.llSaveCodeGenerate);
        llShareCodeGenerate = findViewById(R.id.llShareCodeGenerate);
        imgBack = findViewById(R.id.imgBack);
        txtText = findViewById(R.id.txtText);
    }

    private void generateCode(BarcodeFormat type,boolean is2D) throws WriterException {
        dataSave = edtTextGenerateCode.getText().toString();
        checkTypeText();
        BitMatrix bitMatrix;
        if(is2D){
            bitMatrix = multiFormatWriter.encode(dataSave, type, 600, 600);
        }else {
            bitMatrix = multiFormatWriter.encode(dataSave, type, 600, 200);
        }
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        Log.e("123123", "generateCode: " + bitmap);
        if (bitmap != null) {
            visibleView();
            codeGenerated = true;
            imgCodeGenerate.setImageBitmap(bitmap);
            Toast.makeText(this, "Generate Success!!!", Toast.LENGTH_SHORT).show();
        }else {
            invisibleView();
            Toast.makeText(this, "Your content is not support for this type code!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkTypeText() {
        if (typeCodeSave.equals("EAN_13") || typeCodeSave.equals("EAN_8")) {
            typeText = "Good";
        } else {
            if (Pattern.matches(KEY.LINK_PATTERN, dataSave)) {
                typeText = "Link";
            } else if (Pattern.matches(KEY.PHONE_PATTERN, dataSave)) {
                typeText = "Phone";
            } else if (Pattern.matches(KEY.EMAIL_PATTERN, dataSave)) {
                typeText = "Email";
            } else if (Pattern.matches(KEY.ADDRESS_PATTERN, dataSave)) {
                typeText = "Address";
            } else if (Pattern.matches(KEY.WIFI_PATTERN, dataSave)) {
                typeText = "Wifi";
            } else if (Pattern.matches(KEY.CALENDAR_PATTERN, dataSave)) {
                typeText = "Calender";
            } else if (Pattern.matches(KEY.SMS_PATTERN, dataSave)) {
                typeText = "SMS";
            } else {
                typeText = "Text";
            }
        }
    }

    private String getInstantDateTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat());
        return sdf.format(calendar.getTime());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void openExitGenerateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_qs_yes_no);

        TextView txtTitle = dialog.findViewById(R.id.txtTitleToolBar);
        TextView txtBody = dialog.findViewById(R.id.txtBody);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        btnNo.setText("Save");
        btnYes.setText("Yes");
        txtTitle.setText("Exit");
        txtBody.setText("Exit without save code generated?");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sendResult();
                interAds();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GenerateCodeActivity.this, "Save Success!", Toast.LENGTH_SHORT).show();
                DBHelper.getInstance(GenerateCodeActivity.this).addData(dataSave, typeCodeSave, typeText, getInstantDateTime(), KEY.TYPE_GENERATE, "Yes", "Like", "");
                codeSaved = true;
                dialog.dismiss();
                sendResult();
                interAds();
                finish();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (codeGenerated && !codeSaved) {
            if(MySetting.getCount(this)==MySetting.getMaxLength(this)){
                onBackPressed();
            }else {
                openExitGenerateDialog();
            }
        } else {
            super.onBackPressed();
            sendResult();
            interAds();
        }
    }

    private void interAds() {
        if (QRCodeConfigs.getInstance().getConfig().getBoolean("config_on")) {
            if (MainActivity.ggInterstitialAd != null && MainActivity.ggInterstitialAd.isLoaded())
                MainActivity.ggInterstitialAd.show();
            else if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
                UnityAds.show(GenerateCodeActivity.this, getString(R.string.INTER_UNI));
        }
    }

    private void sendResult() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        try {
            Toast.makeText(this, "Thanks for your Purchased!", Toast.LENGTH_SHORT).show();
            MySetting.setSubscription(this, true);
            MySetting.putRemoveAds(this, true);
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
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bp != null) {
            bp.release();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

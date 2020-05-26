package com.hungdt.qrcode.view;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.hungdt.qrcode.QRCodeConfigs;
import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.dataset.Constant;
import com.hungdt.qrcode.utils.Ads;
import com.hungdt.qrcode.utils.KEY;
import com.unity3d.ads.UnityAds;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class CodeScannedActivity extends AppCompatActivity {

    private ImageView imgBack, imgResultImage;
    private TextView txtResultCode, txtTitleToolBar, txtTitle, txtCreateAt, txtTime, txtCodeType, txtTextType,txtContentCode;
    private LinearLayout llCopyText, llShare, llSearch,llOption;
    private Button btnNewScan;

    private String codeText;

    final Calendar calendar = Calendar.getInstance();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);

        initView();
        Ads.initBanner(((LinearLayout) findViewById(R.id.llBanner)), this, true);
        Ads.initNativeGgFb((LinearLayout) findViewById(R.id.lnNative), this, false);
        Intent intent = getIntent();
        codeText = intent.getStringExtra(KEY.RESULT_TEXT);
        String typeCode = intent.getStringExtra(KEY.RESULT_TYPE_CODE);
        String typeCreate = intent.getStringExtra(KEY.TYPE);
        String typeText;
        if (typeCode == null) {
            typeText = null;
            txtContentCode.setVisibility(View.GONE);
            txtResultCode.setVisibility(View.GONE);
            llOption.setVisibility(View.GONE);
        } else {
            if (typeCode.equals("EAN_13") || typeCode.equals("EAN_8")||typeCode.equals("CODE_39")||typeCode.equals("CODE_93")||typeCode.equals("CODE_128")||typeCode.equals("UPC_A")||typeCode.equals("UPC_E")||typeCode.equals("UPC_EAN_EXTENSION")||typeCode.equals("ITF")) {
                typeText = "Good";
            } else {
                if (Pattern.matches(KEY.LINK_PATTERN, codeText)) {
                    typeText = "Link";
                } else if (Pattern.matches(KEY.PHONE_PATTERN, codeText)) {
                    typeText = "Phone";
                } else if (Pattern.matches(KEY.EMAIL_PATTERN, codeText)) {
                    typeText = "Email";
                } else if (Pattern.matches(KEY.ADDRESS_PATTERN, codeText)) {
                    typeText = "Address";
                } else if (Pattern.matches(KEY.WIFI_PATTERN, codeText)) {
                    typeText = "Wifi";
                } else if (Pattern.matches(KEY.CALENDAR_PATTERN, codeText)) {
                    typeText = "Calender";
                } else if (Pattern.matches(KEY.SMS_PATTERN, codeText)) {
                    typeText = "SMS";
                } else {
                    typeText = "Text";
                }
            }
        }


        DBHelper.getInstance(this).addData(codeText, typeCode, typeText, getInstantDateTime(), typeCreate, "No", "Like", "");

        assert typeCreate != null;
        switch (typeCreate) {
            case KEY.TYPE_SCAN_CAMERA:
                txtTitleToolBar.setText("Camera Scan");
                break;
            case KEY.TYPE_SCAN_GALLERY:
                txtTitleToolBar.setText("Scan Image");
                break;
        }
        if (codeText == null) {
            txtTitle.setText("Scan Fail!");
            txtTitle.setTextColor(getResources().getColor(R.color.red));
        } else {
            txtResultCode.setText(codeText);
            txtTitle.setText("Scan Success!");
            txtTitle.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        txtCreateAt.setText("From: " + typeCreate);
        txtTime.setText("Time: " + getInstantDateTime());
        txtCodeType.setText("Code: " + typeCode);
        txtTextType.setText("Type: " + typeText);

        imgResultImage.setImageBitmap(MainActivity.BITMAP);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llCopyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("your_text_to_be_copied", codeText);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(CodeScannedActivity.this, "Copied " + codeText, Toast.LENGTH_SHORT).show();
            }
        });

        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = null;
                try {
                    query = URLEncoder.encode(codeText, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = "http://www.google.com/search?q=" + query;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCode(txtResultCode.getText().toString());
            }
        });

        btnNewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void shareCode(String codeData) {
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        String shareBody = "Here is the share content body";
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share code");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, codeData);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "Share Code"));
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgResultImage = findViewById(R.id.imgResultImage);
        txtResultCode = findViewById(R.id.txtResultCode);
        txtCreateAt = findViewById(R.id.txtCreateAt);
        txtTime = findViewById(R.id.txtTime);
        txtTextType = findViewById(R.id.txtTextType);
        txtCodeType = findViewById(R.id.txtCodeType);
        txtContentCode = findViewById(R.id.txtContentCode);
        txtTitle = findViewById(R.id.txtTitle);
        txtTitleToolBar = findViewById(R.id.txtTitleToolBar);
        btnNewScan = findViewById(R.id.btnNewScan);
        llOption = findViewById(R.id.llOption);
        llCopyText = findViewById(R.id.llCopyText);
        llShare = findViewById(R.id.llShare);
        llSearch = findViewById(R.id.llSearch);
    }

    private String getInstantDateTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat());
        return sdf.format(calendar.getTime());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if (QRCodeConfigs.getInstance().getConfig().getBoolean("config_on")) {
            if (MainActivity.ggInterstitialAd != null && MainActivity.ggInterstitialAd.isLoaded())
                MainActivity.ggInterstitialAd.show();
            else if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI)))
                UnityAds.show(CodeScannedActivity.this, getString(R.string.INTER_UNI));
        }*/
    }
}

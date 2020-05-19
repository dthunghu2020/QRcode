package com.hungdt.qrcode.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.dataset.Constant;
import com.hungdt.qrcode.utils.KEY;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class CodeScannedActivity extends AppCompatActivity {

    private ImageView imgBack, imgResultImage;
    private TextView txtResultCode, txtTitle, txtNotification;
    private LinearLayout llCopyText, llShare, llSearch, llNewScan;

    private String codeText;

    final Calendar calendar = Calendar.getInstance();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);

        initView();

        Intent intent = getIntent();
        codeText = intent.getStringExtra(KEY.RESULT_TEXT);
        String typeCode = intent.getStringExtra(KEY.RESULT_TYPE_CODE);
        String typeCreate = intent.getStringExtra(KEY.TYPE_CREATE);

        DBHelper.getInstance(this).addData(codeText, typeCode, getInstantDateTime(), typeCreate, "No", "Like", "");

        /*if (intent.hasExtra(KEY.RESULT_BITMAP)) {
            bitmapResult = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra(KEY.RESULT_BITMAP),
                    0,
                    Objects.requireNonNull(intent.getByteArrayExtra(KEY.RESULT_BITMAP)).length);
            imgResultImage.setImageBitmap(bitmapResult);
        }*/

        assert typeCreate != null;
        switch (typeCreate) {
            case KEY.TYPE_SCAN_CAMERA:
                txtTitle.setText("Camera Scan");
                break;
            case KEY.TYPE_SCAN_GALLERY:
                txtTitle.setText("Image Scan");
                break;
        }
        if (codeText == null) {
            txtNotification.setText("Scan Fail!");
            txtNotification.setTextColor(getResources().getColor(R.color.red));
        } else {
            txtResultCode.setText(codeText);
            txtNotification.setTextColor(getResources().getColor(R.color.colorAccent));
        }

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

        llNewScan.setOnClickListener(new View.OnClickListener() {
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
        imgResultImage = findViewById(R.id.imgDetailImage);
        txtResultCode = findViewById(R.id.txtDetailCode);
        txtNotification = findViewById(R.id.txtNotification);
        txtTitle = findViewById(R.id.txtTitle);
        llNewScan = findViewById(R.id.llNewScan);
        llCopyText = findViewById(R.id.llCopyText);
        llShare = findViewById(R.id.llShare);
        llSearch = findViewById(R.id.llSearch);
    }

    private String getInstantDateTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat());
        return sdf.format(calendar.getTime());
    }
}

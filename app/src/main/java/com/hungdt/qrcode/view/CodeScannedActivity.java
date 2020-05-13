package com.hungdt.qrcode.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.dataset.Constant;
import com.hungdt.qrcode.utils.KEY;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CodeScannedActivity extends AppCompatActivity {

    private ImageView imgBack, imgResultImage;
    private TextView txtResultCode,txtTitle,txtNotification;
    private LinearLayout llCopyText, llShare, llSearch,llNewScan;

    private String codeData;
    private String typeCode;
    private String typeCreate;

    private static final int FILE_SHARE_PERMISSION = 102;

    final Calendar calendar = Calendar.getInstance();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);

        initView();

        Intent intent = getIntent();
        codeData = intent.getStringExtra(KEY.RESULT_TEXT);
        typeCode = intent.getStringExtra(KEY.RESULT_TYPE_CODE);
        typeCreate = intent.getStringExtra(KEY.TYPE_CREATE);

        DBHelper.getInstance(this).addData(codeData, typeCode,getInstantDateTime(), typeCreate,"No","Like","");

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
        if(codeData ==null){
            txtNotification.setText("Scan Fail!");
            txtNotification.setTextColor(getResources().getColor(R.color.red));
        }else {
            txtResultCode.setText(codeData);
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
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("your_text_to_be_copied",codeData);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(CodeScannedActivity.this, "Copied "+ codeData, Toast.LENGTH_SHORT).show();
            }
        });

        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CodeScannedActivity.this, "Search", Toast.LENGTH_SHORT).show();
            }
        });

        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        sharePicturePNG(CodeScannedActivity.this,imgResultImage);
                        //saveQrCode();
                    } else {
                        ActivityCompat.requestPermissions(CodeScannedActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_SHARE_PERMISSION);
                    }
                } else {
                    sharePicturePNG(CodeScannedActivity.this,imgResultImage);
                }
                sharePicturePNG(CodeScannedActivity.this,imgResultImage);
            }
        });
    }
    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(CodeScannedActivity.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
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

            //Action share

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("*/*");

            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri= FileProvider.getUriForFile(
                        context,
                        getString(R.string.author),
                        new File(cachePath.getPath())
                );
            } else {
                uri= Uri.fromFile(new File(cachePath.getPath()));
            }
            Log.d("xxxx", "uri: "+uri);



            Log.d("xxxx", "uri: "+codeData);
            Log.d("xxxx", "uri: "+uri);
            share.putExtra(Intent.EXTRA_TEXT,""+codeData);
            share.putExtra(Intent.EXTRA_STREAM,uri);
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "Share Code"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDirPathDownloaded() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/qrcode";
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgResultImage = findViewById(R.id.imgResultImage);
        txtResultCode = findViewById(R.id.txtResultCode);
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

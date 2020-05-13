package com.hungdt.qrcode.view;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.dataset.Constant;
import com.hungdt.qrcode.utils.KEY;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GenerateCodeActivity extends AppCompatActivity {

    private Spinner spinnerCodeType;
    private EditText edtTextGenerateCode;
    private LinearLayout llGenerateCode, llSaveCodeGenerate, llShareCodeGenerate;
    private ImageView imgBack, imgCodeGenerate;

    private MultiFormatWriter multiFormatWriter;
    private BarcodeFormat barcodeFormat;

    private static final int FILE_SHARE_PERMISSION = 102;

    private String dataSave;
    private String typeCodeSave;

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        initView();

        multiFormatWriter = new MultiFormatWriter();

        final ArrayList<String> names = new ArrayList<String>();
        names.add("QR_CODE");
        names.add("CODE_128");
        names.add("CODE_93");
        names.add("CODE_39");
        names.add("EAN_13");
        names.add("EAN_8");
        names.add("UPC_A");
        names.add("UPC_E");
        names.add("UPC_EAN_EXTENSION");
        names.add("ITF");
        names.add("PDF_417");
        names.add("CODABAR");
        names.add("DATA_MATRIX");
        names.add("AZTEC");
        names.add("RSS_14");
        names.add("MAXICODE");
        names.add("RSS_EXPANDED");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, names);
        spinnerCodeType.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCodeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        barcodeFormat = BarcodeFormat.QR_CODE;
                        break;
                    case 1:
                        barcodeFormat = BarcodeFormat.CODE_128;
                        break;
                    case 2:
                        barcodeFormat = BarcodeFormat.CODE_93;
                        break;
                    case 3:
                        barcodeFormat = BarcodeFormat.CODE_39;
                        break;
                    case 4:
                        barcodeFormat = BarcodeFormat.EAN_13;
                        break;
                    case 5:
                        barcodeFormat = BarcodeFormat.EAN_8;
                        break;
                    case 6:
                        barcodeFormat = BarcodeFormat.UPC_A;
                        break;
                    case 7:
                        barcodeFormat = BarcodeFormat.UPC_E;
                        break;
                    case 8:
                        barcodeFormat = BarcodeFormat.UPC_EAN_EXTENSION;
                        break;
                    case 9:
                        barcodeFormat = BarcodeFormat.ITF;
                        break;
                    case 10:
                        barcodeFormat = BarcodeFormat.PDF_417;
                        break;
                    case 11:
                        barcodeFormat = BarcodeFormat.CODABAR;
                        break;
                    case 12:
                        barcodeFormat = BarcodeFormat.DATA_MATRIX;
                        break;
                    case 13:
                        barcodeFormat = BarcodeFormat.AZTEC;
                        break;
                    case 14:
                        barcodeFormat = BarcodeFormat.RSS_14;
                        break;
                    case 15:
                        barcodeFormat = BarcodeFormat.MAXICODE;
                        break;
                    case 16:
                        barcodeFormat = BarcodeFormat.RSS_EXPANDED;
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
                            generateCode(BarcodeFormat.QR_CODE);
                            break;
                        case CODE_128:
                            generateCode(BarcodeFormat.CODE_128);
                            break;
                        case CODE_93:
                            generateCode(BarcodeFormat.CODE_93);
                            break;
                        case CODE_39:
                            generateCode(BarcodeFormat.CODE_39);
                            break;
                        case EAN_13:
                            generateCode(BarcodeFormat.EAN_13);
                            break;
                        case EAN_8:
                            generateCode(BarcodeFormat.EAN_8);
                            break;
                        case UPC_A:
                            generateCode(BarcodeFormat.UPC_A);
                            break;
                        case UPC_E:
                            generateCode(BarcodeFormat.UPC_E);
                            break;
                        case UPC_EAN_EXTENSION:
                            generateCode(BarcodeFormat.UPC_EAN_EXTENSION);
                            break;
                        case ITF:
                            generateCode(BarcodeFormat.ITF);
                            break;
                        case PDF_417:
                            generateCode(BarcodeFormat.PDF_417);
                            break;
                        case CODABAR:
                            generateCode(BarcodeFormat.CODABAR);
                            break;
                        case DATA_MATRIX:
                            generateCode(BarcodeFormat.DATA_MATRIX);
                            break;
                        case AZTEC:
                            generateCode(BarcodeFormat.AZTEC);
                            break;
                        case RSS_14:
                            generateCode(BarcodeFormat.RSS_14);
                            break;
                        case MAXICODE:
                            generateCode(BarcodeFormat.MAXICODE);
                            break;
                        case RSS_EXPANDED:
                            generateCode(BarcodeFormat.RSS_EXPANDED);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                if(edtTextGenerateCode.getText().toString().equals(dataSave)){
                    Toast.makeText(GenerateCodeActivity.this, "Save Success!", Toast.LENGTH_SHORT).show();
                    DBHelper.getInstance(GenerateCodeActivity.this).addData(dataSave, typeCodeSave,getInstantDateTime(), KEY.TYPE_GENERATE,"Yes","Like","");
                    onBackPressed();
                }else {
                    Toast.makeText(GenerateCodeActivity.this, "You have change code. Please regenerate and try again!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        llShareCodeGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        sharePicturePNG(GenerateCodeActivity.this,imgCodeGenerate);
                        //saveQrCode();
                    } else {
                        ActivityCompat.requestPermissions(GenerateCodeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_SHARE_PERMISSION);
                    }
                } else {
                    sharePicturePNG(GenerateCodeActivity.this,imgCodeGenerate);
                }

            }
        });
    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(GenerateCodeActivity.this, permission);
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



            Log.d("xxxx", "uri: "+edtTextGenerateCode.getText());
            Log.d("xxxx", "uri: "+uri);
            share.putExtra(Intent.EXTRA_TEXT,""+edtTextGenerateCode.getText());
            share.putExtra(Intent.EXTRA_STREAM,uri);
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "Share Code"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDirPathDownloaded() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/qrcodeshare";
    }

    private void invisibleView() {
        llSaveCodeGenerate.setVisibility(View.INVISIBLE);
        llShareCodeGenerate.setVisibility(View.INVISIBLE);
        imgCodeGenerate.setVisibility(View.INVISIBLE);
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
    }

    private void generateCode(BarcodeFormat type) throws WriterException {
        invisibleView();
        dataSave = edtTextGenerateCode.getText().toString();
        BitMatrix bitMatrix = multiFormatWriter.encode(dataSave, type, 400, 400);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        Log.e("123123", "generateCode: "+bitmap );
        if (bitmap != null) {
            visibleView();
            imgCodeGenerate.setImageBitmap(bitmap);
        }
    }

    private String getInstantDateTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Constant.getDateTimeFormat());
        return sdf.format(calendar.getTime());
    }

}

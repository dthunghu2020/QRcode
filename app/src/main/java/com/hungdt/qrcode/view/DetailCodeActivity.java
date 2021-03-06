package com.hungdt.qrcode.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.model.CodeData;
import com.hungdt.qrcode.utils.Ads;
import com.hungdt.qrcode.utils.KEY;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class DetailCodeActivity extends AppCompatActivity {

    private ImageView imgBack,imgLike,imgLove,imgDelete,imgDetailImage;
    private FrameLayout flLike;
    private TextView txtDetailCode,txtCreateAt,txtTime,txtCodeType,txtTextType, txtNotSupport;
    private LinearLayout llCopyText,llSearch,llShare;
    private CodeData codeData;
    private boolean like = true;

    private MultiFormatWriter multiFormatWriter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_code);

        initView();
        txtNotSupport.setVisibility(View.GONE);
        Ads.initBanner(((LinearLayout) findViewById(R.id.llBanner)), this, true);
        Ads.initNativeGgFb((LinearLayout) findViewById(R.id.lnNative), this, false);
        multiFormatWriter = new MultiFormatWriter();
        Intent intent = getIntent();
        codeData = DBHelper.getInstance(this).getOneCodeData(intent.getStringExtra(KEY.CODE_ID));

        txtCreateAt.setText("From: "+codeData.getCreateAt());
        txtTime.setText("Time: "+codeData.getCreateTime());
        txtTextType.setText("Type: "+codeData.getTextType());
        txtDetailCode.setText(codeData.getData());
        try {
            switch (codeData.getCodeType()) {
                case "QR_CODE":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.QR_CODE,true);
                    break;
                case "CODE_128":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.CODE_128,false);
                    break;
                case "CODE_93":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.CODE_93,false);
                    break;
                case "CODE_39":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.CODE_39,false);
                    break;
                case "EAN_13":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.EAN_13,false);
                    break;
                case "EAN_8":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.EAN_8,false);
                    break;
                case "UPC_A":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.UPC_A,false);
                    break;
                case "UPC_E":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.UPC_E,false);
                    break;
                case "ITF":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.ITF,false);
                    break;
                case "PDF_417":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.PDF_417,false);
                    break;
                case "CODABAR":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.CODABAR,false);
                    break;
                case "DATA_MATRIX":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.DATA_MATRIX,true);
                    break;
                case "AZTEC":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    generateCode(BarcodeFormat.AZTEC,true);
                    break;
                case "RSS_14":
                case "UPC_EAN_EXTENSION":
                case "MAXICODE":
                case "RSS_EXPANDED":
                    txtCodeType.setText("Code: "+codeData.getCodeType());
                    txtNotSupport.setVisibility(View.VISIBLE);
                    break;
            }
        }catch (WriterException e) {
            e.printStackTrace();
        }
        if(codeData.getLike().equals("Like")){
            imgLove.setVisibility(View.INVISIBLE);
        }else {
            imgLike.setVisibility(View.INVISIBLE);
            like =false;
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        flLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like){
                    imgLove.setVisibility(View.VISIBLE);
                    imgLike.setVisibility(View.INVISIBLE);
                    DBHelper.getInstance(DetailCodeActivity.this).setLike(codeData.getId(),"Love");
                    like = false;
                }else {
                    imgLove.setVisibility(View.INVISIBLE);
                    imgLike.setVisibility(View.VISIBLE);
                    DBHelper.getInstance(DetailCodeActivity.this).setLike(codeData.getId(),"Like");
                    like = true;
                }
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog();
            }
        });

        llCopyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("your_text_to_be_copied",codeData.getData());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(DetailCodeActivity.this, "Copied "+ codeData.getData(), Toast.LENGTH_SHORT).show();
            }
        });

        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = null;
                try {
                    query = URLEncoder.encode(codeData.getData(), "utf-8");
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
                shareDialog();
            }
        });

    }

    private void openDeleteDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_qs_yes_no);

        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.getInstance(DetailCodeActivity.this).deleteOneCodeData(codeData.getId());
                Intent intentDelete = new Intent();
                intentDelete.putExtra(KEY.TYPE_RESULT, KEY.DELETE);
                setResult(Activity.RESULT_OK, intentDelete);
                dialog.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void generateCode(BarcodeFormat type,boolean is2D) throws WriterException {
        BitMatrix bitMatrix;
        if(is2D){
            bitMatrix = multiFormatWriter.encode(codeData.getData(), type, 600, 600);
        }else {
            bitMatrix = multiFormatWriter.encode(codeData.getData(), type, 600, 200);
        }
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        Log.e("123123", "generateCode: "+bitmap );
        if (bitmap != null) {
            imgDetailImage.setImageBitmap(bitmap);
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

    private void shareDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.choose_type_share_bottom_dialog);

        LinearLayout llShareText = bottomSheetDialog.findViewById(R.id.llShareText);
        LinearLayout llShareImage = bottomSheetDialog.findViewById(R.id.llShareImage);

        assert llShareText != null;
        llShareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText(codeData.getData());
                bottomSheetDialog.dismiss();
            }
        });

        assert llShareImage != null;
        llShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePicturePNG(DetailCodeActivity.this,imgDetailImage);
                bottomSheetDialog.dismiss();
            }
        });
        //Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();
    }

    public static String getDirPathDownloaded() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/qrcode";
    }

    private void initView() {
        txtNotSupport = findViewById(R.id.txtNotSupport);
        imgBack = findViewById(R.id.imgBack);
        imgLike = findViewById(R.id.imgLike);
        imgLove = findViewById(R.id.imgLove);
        imgDelete = findViewById(R.id.imgDelete);
        imgDetailImage = findViewById(R.id.imgDetailImage);
        txtCreateAt = findViewById(R.id.txtCreateAt);
        txtTime = findViewById(R.id.txtTime);
        txtCodeType = findViewById(R.id.txtCodeType);
        txtTextType = findViewById(R.id.txtTextType);
        flLike = findViewById(R.id.flLike);
        txtDetailCode = findViewById(R.id.txtDetailCode);
        llCopyText = findViewById(R.id.llCopyText);
        llSearch = findViewById(R.id.llSearch);
        llShare = findViewById(R.id.llShare);
    }

    @Override
    public void onBackPressed() {
        Log.e("123123", "onBackPressed: ");
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY.TYPE_RESULT, KEY.UPDATE);
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}

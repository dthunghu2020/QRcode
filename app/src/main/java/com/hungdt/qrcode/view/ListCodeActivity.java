package com.hungdt.qrcode.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.model.CodeData;
import com.hungdt.qrcode.utils.KEY;
import com.hungdt.qrcode.view.adapter.CodeDataAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListCodeActivity extends AppCompatActivity {

    private List<CodeData> dataList = new ArrayList<>();
    private CodeDataAdapter codeDataAdapter;
    private RecyclerView rcvCodeData;
    private ImageView imgBack,imgDelete;
    private TextView txtTitle;

    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_code);

        initView();

        dataList.clear();
        Intent intent = getIntent();
        title = intent.getStringExtra(KEY.TYPE_VIEW);
        txtTitle.setText(title);
        if(title.equals(KEY.HISTORY)){
            dataList.addAll(DBHelper.getInstance(this).getAllData());
        }else if(title.equals(KEY.SAVED)){
            dataList.addAll(DBHelper.getInstance(this).getCodeSaved());
        }

        Collections.reverse(dataList);
        codeDataAdapter= new CodeDataAdapter(this,dataList);
        rcvCodeData.setLayoutManager(new LinearLayoutManager(this));
        rcvCodeData.setAdapter(codeDataAdapter);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListCodeActivity.this, "Delete", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        txtTitle = findViewById(R.id.txtTitle);
        rcvCodeData = findViewById(R.id.rcvCodeData);
        imgBack = findViewById(R.id.imgBack);
        imgDelete = findViewById(R.id.imgDelete);
    }
}

package com.hungdt.qrcode.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private ImageView imgBack, imgDelete, imgCheckAll;
    private TextView txtTitle, txtBack;
    private boolean onDelete = false;

    private int positionSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_code);
        initView();

        //todo
        //String[] Ids

        dataList.clear();
        Intent intent = getIntent();
        final String title = intent.getStringExtra(KEY.TYPE_VIEW);
        txtTitle.setText(title);
        assert title != null;
        switch (title) {
            case KEY.HISTORY:
                dataList.addAll(DBHelper.getInstance(this).getAllData());
                break;
            case KEY.SAVED:
                dataList.addAll(DBHelper.getInstance(this).getCodeSaved());
                break;
            case KEY.LIKE:
                dataList.addAll(DBHelper.getInstance(this).getAllCodeLike());
                break;
        }

        Collections.reverse(dataList);
        codeDataAdapter = new CodeDataAdapter(this, dataList);
        rcvCodeData.setLayoutManager(new LinearLayoutManager(this));
        rcvCodeData.setAdapter(codeDataAdapter);
        viewOffCheckBox();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ok
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onDelete) {
                    viewOnCheckBox();
                    codeDataAdapter.enableCheckBox();
                    codeDataAdapter.notifyDataSetChanged();
                    onDelete = true;
                } else {
                    //sau khi checkbox xong và thực hiện xóa
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).isTicked()) {
                            DBHelper.getInstance(ListCodeActivity.this).deleteOneCodeData(dataList.get(i).getId());
                        }
                    }
                    dataList.clear();
                    switch (title) {
                        case KEY.HISTORY:
                            dataList.addAll(DBHelper.getInstance(ListCodeActivity.this).getAllData());
                            break;
                        case KEY.SAVED:
                            dataList.addAll(DBHelper.getInstance(ListCodeActivity.this).getCodeSaved());
                            break;
                        case KEY.LIKE:
                            dataList.addAll(DBHelper.getInstance(ListCodeActivity.this).getAllCodeLike());
                            break;
                    }
                    Collections.reverse(dataList);
                    disableViewCheckBox();
                    onDelete=false;
                }
            }
        });
        //OK
        imgCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i<dataList.size();i++){
                    dataList.get(i).setTicked(true);
                }
                codeDataAdapter.notifyDataSetChanged();
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableViewCheckBox();
            }
        });

        codeDataAdapter.setOnDetailCodeItemClickListener(new CodeDataAdapter.OnDetailCodeItemClickListener() {

            @Override
            public void OnItemClicked(int position, boolean checkBox) {
                positionSave = position;
                if (!checkBox) {
                    Intent intentDetails = new Intent(ListCodeActivity.this, DetailCodeActivity.class);
                    intentDetails.putExtra(KEY.CODE_ID, dataList.get(position).getId());
                    startActivityForResult(intentDetails, MainActivity.REQUEST_CODE_DETAIL_CODE);
                }else {
                    if(!dataList.get(position).isTicked()){
                        dataList.get(position).setTicked(true);
                    }else {
                        dataList.get(position).setTicked(false);
                    }
                    codeDataAdapter.notifyItemChanged(position);
                }
            }
        });

    }

    private void disableViewCheckBox() {
        setDefaultTick();
        txtBack.setVisibility(View.GONE);
        imgCheckAll.setVisibility(View.GONE);
        codeDataAdapter.disableCheckBox();
        codeDataAdapter.notifyDataSetChanged();
        onDelete = false;
    }

    private void setDefaultTick() {
        for (int i = 0; i < dataList.size(); i++) {
                dataList.get(i).setTicked(false);

        }
        for (int i = 0; i < dataList.size(); i++) {
            Log.e("123123", "setDefaultTick: "+dataList.get(i).isTicked() );
        }
    }

    private void viewOnCheckBox() {
        txtBack.setVisibility(View.VISIBLE);
        imgCheckAll.setVisibility(View.VISIBLE);
    }

    private void viewOffCheckBox() {
        txtBack.setVisibility(View.GONE);
        imgCheckAll.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_CODE_DETAIL_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String typeResult = data.getStringExtra(KEY.TYPE_RESULT);
                Log.e("123123", "onActivityResult: " + typeResult);
                assert typeResult != null;
                if (typeResult.equals(KEY.DELETE)) {
                    DBHelper.getInstance(this).deleteOneCodeData(dataList.get(positionSave).getId());
                    dataList.remove(positionSave);
                    codeDataAdapter.notifyDataSetChanged();
                }
                if (typeResult.equals(KEY.UPDATE)) {
                    CodeData codeData = DBHelper.getInstance(this).getOneCodeData(dataList.get(positionSave).getId());
                    Log.e("123123", "onActivityResult: " + codeData);
                    dataList.set(positionSave, codeData);
                    codeDataAdapter.notifyItemChanged(positionSave);
                }

            }
        }
    }

    private void initView() {
        txtTitle = findViewById(R.id.txtTitle);
        txtBack = findViewById(R.id.txtBack);
        rcvCodeData = findViewById(R.id.rcvCodeData);
        imgBack = findViewById(R.id.imgBack);
        imgCheckAll = findViewById(R.id.imgCheckAll);
        imgDelete = findViewById(R.id.imgDelete);
    }

    @Override
    public void onBackPressed() {
        if (onDelete) {
            disableViewCheckBox();
        } else {
            super.onBackPressed();
        }
    }
}

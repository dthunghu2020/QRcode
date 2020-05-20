package com.hungdt.qrcode.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    private ImageView imgBack, imgDelete,imgTitle;
    private TextView txtToolBar,txtTitle,txtBody,txtTitleItem;
    private CardView cvTitle,cvSelectAll;
    private Button btnDelete;
    private ConstraintLayout clTitleItem;

    private boolean onDelete = false;
    private int positionSave;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_code);
        initView();

        dataList.clear();
        Intent intent = getIntent();
         title = intent.getStringExtra(KEY.TYPE_VIEW);

        assert title != null;
        switch (title) {
            case KEY.HISTORY:
                imgTitle.setBackgroundResource(R.drawable.image_history);
                txtTitle.setText("Check");
                txtTitle.append("Your History");
                txtBody.setText("Show your History");
                txtTitleItem.setText("All History");
                dataList.addAll(DBHelper.getInstance(this).getAllData());
                break;
            case KEY.SAVED:
                txtTitle.setText("Check");
                txtTitle.append("Your Saved");
                txtBody.setText("Show your Saved");
                txtTitleItem.setText("All Saved");
                imgTitle.setBackgroundResource(R.drawable.image_generate);
                dataList.addAll(DBHelper.getInstance(this).getCodeSaved());
                break;
            case KEY.LIKE:
                txtTitle.setText("Check");
                txtTitle.append("Your Favorite");
                txtBody.setText("Show your Favorite");
                txtTitleItem.setText("All Favorite");
                imgTitle.setBackgroundResource(R.drawable.image_favorite);
                dataList.addAll(DBHelper.getInstance(this).getAllCodeLike());
                break;
        }

        Collections.reverse(dataList);
        codeDataAdapter = new CodeDataAdapter(this, dataList);
        rcvCodeData.setLayoutManager(new LinearLayoutManager(this));
        rcvCodeData.setAdapter(codeDataAdapter);
        viewOffDelete();

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    viewOnDelete();
                    codeDataAdapter.enableCheckBox();
                    codeDataAdapter.notifyDataSetChanged();
                    onDelete = true;
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        cvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i<dataList.size();i++){
                    dataList.get(i).setTicked(true);
                }
                codeDataAdapter.notifyDataSetChanged();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        imgBack.setVisibility(View.GONE);
        cvSelectAll.setVisibility(View.GONE);
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

    private void viewOnDelete() {
        txtToolBar.setText("Delete "+title);
        cvTitle.setVisibility(View.GONE);
        clTitleItem.setVisibility(View.GONE);
        cvSelectAll.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
    }

    private void viewOffDelete() {
        txtToolBar.setText(title);
        cvTitle.setVisibility(View.VISIBLE);
        clTitleItem.setVisibility(View.VISIBLE);
        cvSelectAll.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
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
        txtToolBar = findViewById(R.id.txtToolBar);
        imgTitle = findViewById(R.id.imgTitle);
        imgDelete = findViewById(R.id.imgDelete);
        txtTitle = findViewById(R.id.txtTitle);
        txtBody = findViewById(R.id.txtBody);
        txtTitleItem = findViewById(R.id.txtTitleItem);
        cvTitle = findViewById(R.id.cvTitle);
        clTitleItem = findViewById(R.id.clTitleItem);
        cvSelectAll = findViewById(R.id.cvSelectAll);
        btnDelete = findViewById(R.id.btnDelete);
        imgBack = findViewById(R.id.imgBack);
        rcvCodeData = findViewById(R.id.rcvCodeData);
        imgBack = findViewById(R.id.imgBack);
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

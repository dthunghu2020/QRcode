package com.hungdt.qrcode.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.dataset.Constant;
import com.hungdt.qrcode.model.CodeData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CodeDataAdapter extends RecyclerView.Adapter<CodeDataAdapter.CodeDataHolder> {

    private List<CodeData> listData;
    private LayoutInflater layoutInflater;
    private String dateCheck = "";

    public CodeDataAdapter(Context context, List<CodeData> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CodeDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.code_data_adapter, parent, false);
        return new CodeDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CodeDataHolder holder, final int position) {
        setVisible(holder);
        final CodeData codeData = listData.get(position);


        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDate = new SimpleDateFormat(Constant.getDateFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDateTime = new SimpleDateFormat(Constant.getDateTimeFormat());
        Date date;
        try {
            date = sdfDateTime.parse(codeData.getCreateTime());
            if (date != null) {
                if (!dateCheck.equals(sdfDate.format(date))) {
                    dateCheck = sdfDate.format(date);
                    holder.txtDay.setVisibility(View.VISIBLE);
                    holder.txtDay.setText(dateCheck);
                }else {
                    holder.txtDay.setVisibility(View.GONE);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txtTitle.setText(codeData.getCreateAt());
        holder.txtTime.setText(codeData.getCreateTime());
        holder.txtCodeType.setText(codeData.getType());
        holder.txtData.setText(codeData.getData());

        if(codeData.getLike().equals("Like")){
            setViewLike(holder);
        }else {
            setViewLove(holder);
        }

        holder.flLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeData.getLike().equals("Like")){
                    setViewLove(holder);
                    DBHelper.getInstance(layoutInflater.getContext()).setLike(codeData.getId(),"Love");
                }else {
                    setViewLike(holder);
                    DBHelper.getInstance(layoutInflater.getContext()).setLike(codeData.getId(),"Like");
                }
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(layoutInflater.getContext(), "Delete", Toast.LENGTH_SHORT).show();
            }
        });

        holder.clItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(layoutInflater.getContext(), "Item", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setViewLike(CodeDataHolder holder) {
        holder.imgLove.setVisibility(View.INVISIBLE);
        holder.imgLike.setVisibility(View.VISIBLE);
    }
    private void setViewLove(CodeDataHolder holder) {
        holder.imgLove.setVisibility(View.VISIBLE);
        holder.imgLike.setVisibility(View.INVISIBLE);
    }

    private void setVisible(CodeDataHolder holder) {
        holder.checkbox.setVisibility(View.GONE);
        holder.imgLove.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class CodeDataHolder extends RecyclerView.ViewHolder {
        private TextView txtDay, txtTitle, txtTime, txtCodeType, txtData;
        private CheckBox checkbox;
        private ImageView imgTypeOfText, imgLike, imgLove, imgDelete;
        private FrameLayout flLike;
        private ConstraintLayout clItem;

        public CodeDataHolder(@NonNull View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtCodeType = itemView.findViewById(R.id.txtCodeType);
            txtData = itemView.findViewById(R.id.txtData);
            checkbox = itemView.findViewById(R.id.checkbox);
            imgTypeOfText = itemView.findViewById(R.id.imgTypeOfText);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgLove = itemView.findViewById(R.id.imgLove);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            flLike = itemView.findViewById(R.id.flLike);
            clItem = itemView.findViewById(R.id.clItem);
        }
    }
}

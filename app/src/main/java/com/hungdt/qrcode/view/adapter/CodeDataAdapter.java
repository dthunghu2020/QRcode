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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hungdt.qrcode.R;
import com.hungdt.qrcode.database.DBHelper;
import com.hungdt.qrcode.dataset.Constant;
import com.hungdt.qrcode.model.CodeData;
import com.hungdt.qrcode.utils.KEY;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CodeDataAdapter extends RecyclerView.Adapter<CodeDataAdapter.CodeDataHolder> {

    private List<CodeData> listData;
    private LayoutInflater layoutInflater;
    private Boolean onCheckBox = false;
    public int countItemClicked = 0;

    private OnDetailCodeItemClickListener onDetailCodeItemClickListener;
    private SetCheckBoxInterface setCheckBoxInterface;

    public CodeDataAdapter(Context context, List<CodeData> listData, SetCheckBoxInterface setCheckBoxInterface) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        this.setCheckBoxInterface = setCheckBoxInterface;
    }

    @NonNull
    @Override
    public CodeDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.code_data_adapter, parent, false);
        return new CodeDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CodeDataHolder holder, final int position) {

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDate = new SimpleDateFormat(Constant.getDateFormat());
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDateTime = new SimpleDateFormat(Constant.getDateTimeFormat());
        Date date;
        try {
            date = sdfDateTime.parse(listData.get(position).getCreateTime());
            if (date != null) {
                if (position == 0) {
                    holder.txtHistoryDay.setVisibility(View.VISIBLE);
                    holder.txtHistoryDay.setText(sdfDate.format(date));
                } else {
                    String dayCheck = sdfDate.format(date);
                    date = sdfDateTime.parse(listData.get(position - 1).getCreateTime());
                    assert date != null;
                    if (!dayCheck.equals(sdfDate.format(date))) {
                        holder.txtHistoryDay.setVisibility(View.VISIBLE);
                        holder.txtHistoryDay.setText(dayCheck);
                    } else {
                        holder.txtHistoryDay.setVisibility(View.GONE);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.txtTitle.setText(listData.get(position).getCreateAt());
        holder.txtTime.setText(listData.get(position).getCreateTime());
        holder.txtCodeType.setText(listData.get(position).getCodeType());
        holder.txtData.setText(listData.get(position).getData());
        switch (listData.get(position).getTextType()) {
            case KEY.GOOD:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_goods);
                break;
            case KEY.LINK:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_link);
                break;
            case KEY.PHONE:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_phone_number);
                break;
            case KEY.EMAIL:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_mail);
                break;
            case KEY.ADDRESS:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_location);
                break;
            case KEY.WIFI:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_wifi);
                break;
            case KEY.CALENDER:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_calendar);
                break;
            case KEY.SMS:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_messenger);
                break;
            case KEY.TEXT:
                holder.imgTypeOfText.setImageResource(R.drawable.ic_text);
                break;
        }


        if (!onCheckBox) {
            holder.checkbox.setVisibility(View.INVISIBLE);
            holder.imgTypeOfText.setVisibility(View.VISIBLE);
        } else {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.imgTypeOfText.setVisibility(View.INVISIBLE);
        }

        if (listData.get(position).getLike().equals("Like")) {
            setViewLike(holder);
        } else {
            setViewLove(holder);
        }

        if (listData.get(position).isTicked()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        holder.flLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onCheckBox) {
                    if (listData.get(position).getLike().equals("Like")) {
                        setViewLove(holder);
                        listData.get(position).setLike("Love");
                        DBHelper.getInstance(layoutInflater.getContext()).setLike(listData.get(position).getId(), "Love");
                    } else {
                        setViewLike(holder);
                        listData.get(position).setLike("Like");
                        DBHelper.getInstance(layoutInflater.getContext()).setLike(listData.get(position).getId(), "Like");
                    }
                } else {
                    setClickITem(listData.get(position), holder.checkbox);
                }
            }
        });

        holder.clItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("1111", "onClickListener: item");
                onDetailCodeItemClickListener.OnItemClicked(holder.getAdapterPosition(), onCheckBox);
                if (onCheckBox) {
                    setClickITem(listData.get(position), holder.checkbox);
                }
            }
        });
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("1111", "onClick: checkbox");
                setClickITem(listData.get(position), holder.checkbox);
            }
        });
    }

    private void setClickITem(CodeData codeData, CheckBox checkBox) {
        if (codeData.isTicked()) {
            if (countItemClicked == listData.size()) {
                setCheckBoxInterface.setUnCheckBox();
            }
            countItemClicked--;
            codeData.setTicked(false);
            checkBox.setChecked(false);
        } else {
            countItemClicked++;
            if (countItemClicked == listData.size()) {
                setCheckBoxInterface.setCheckedBox();
            }
            codeData.setTicked(true);
            checkBox.setChecked(true);
        }
        Log.e("1111", "setClickITem: " + countItemClicked);
    }

    private void setViewLike(CodeDataHolder holder) {
        holder.imgLove.setVisibility(View.INVISIBLE);
        holder.imgLike.setVisibility(View.VISIBLE);
    }

    private void setViewLove(CodeDataHolder holder) {
        holder.imgLove.setVisibility(View.VISIBLE);
        holder.imgLike.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    static class CodeDataHolder extends RecyclerView.ViewHolder {
        private TextView txtHistoryDay, txtTitle, txtTime, txtCodeType, txtData;
        private CheckBox checkbox;
        private ImageView imgTypeOfText, imgLike, imgLove;
        private FrameLayout flLike;
        private ConstraintLayout clItem;

        CodeDataHolder(@NonNull View itemView) {
            super(itemView);
            txtHistoryDay = itemView.findViewById(R.id.txtHistoryDay);
            txtTitle = itemView.findViewById(R.id.txtCreateAt);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtCodeType = itemView.findViewById(R.id.txtCodeType);
            txtData = itemView.findViewById(R.id.txtData);
            checkbox = itemView.findViewById(R.id.checkbox);
            imgTypeOfText = itemView.findViewById(R.id.imgTypeOfText);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgLove = itemView.findViewById(R.id.imgLove);
            flLike = itemView.findViewById(R.id.flLike);
            clItem = itemView.findViewById(R.id.clItem);
        }
    }

    public void setOnDetailCodeItemClickListener(OnDetailCodeItemClickListener onDetailCodeItemClickListener) {
        this.onDetailCodeItemClickListener = onDetailCodeItemClickListener;
    }

    public interface OnDetailCodeItemClickListener {
        void OnItemClicked(int position, boolean checkBox);
    }

    public interface SetCheckBoxInterface {
        void setCheckedBox();
        void setUnCheckBox();
    }

    public void enableCheckBox() {
        onCheckBox = true;
    }

    public void disableCheckBox() {
        onCheckBox = false;
    }
}

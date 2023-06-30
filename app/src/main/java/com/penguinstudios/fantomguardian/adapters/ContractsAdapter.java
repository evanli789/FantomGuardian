package com.penguinstudios.fantomguardian.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.data.model.FormattedContract;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractsAdapter extends RecyclerView.Adapter<ContractsAdapter.ViewHolder> {

    public interface ContractClickCallback {
        void onContractClick(int adapterPosition);

        void onBtnResetSwitchClick(int adapterPosition);
    }

    private static final float VIEW_HOLDER_WIDTH = 0.66f;
    private final List<FormattedContract> list;
    private final ContractClickCallback callback;
    private int redTextColor, whiteTextColor, greenProgressBarColor;

    public ContractsAdapter(List<FormattedContract> list, ContractClickCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Context context = recyclerView.getContext();
        redTextColor = ContextCompat.getColor(context, R.color.red_500);
        whiteTextColor = ContextCompat.getColor(context, R.color.white);
        greenProgressBarColor = ContextCompat.getColor(context, R.color.green_500);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.viewholder_contract, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() * VIEW_HOLDER_WIDTH);
        //Do not set layoutParams.height. It will prevent centering
        itemView.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.tvAmountFtm.setText(list.get(i).getFormattedAmount());
        holder.tvDateToReset.setText(list.get(i).getDateToReset());
        holder.tvNumRecipients.setText(list.get(i).getNumRecipients());
        holder.tvContractAddress.setText(list.get(i).getContractAddress());
        holder.linearProgressIndicator.setProgress(list.get(i).getProgress());

        boolean isExpired = list.get(i).isContractExpired();

        if (isExpired) {
            holder.tvDateToReset.setTextColor(redTextColor);
        } else {
            holder.tvDateToReset.setTextColor(whiteTextColor);
        }

        if (list.get(i).isProgressLessThan33Percent()) {
            holder.linearProgressIndicator.setIndicatorColor(redTextColor);
        } else {
            holder.linearProgressIndicator.setIndicatorColor(greenProgressBarColor);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_reset)
        LinearProgressIndicator linearProgressIndicator;

        @BindView(R.id.tv_contract_address)
        TextView tvContractAddress;

        @BindView(R.id.tv_date_to_reset)
        TextView tvDateToReset;

        @BindView(R.id.tv_num_recipients)
        TextView tvNumRecipients;

        @BindView(R.id.tv_amount_ftm)
        TextView tvAmountFtm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.parent_layout)
        void onParentLayout() {
            callback.onContractClick(getAdapterPosition());
        }

        @OnClick(R.id.btn_reset_switch)
        void onResetSwitch() {
            callback.onBtnResetSwitchClick(getAdapterPosition());
        }
    }
}

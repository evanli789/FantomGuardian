package com.penguinstudios.fantomguardian.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.data.model.FormattedWithdraw;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawnFundsAdapter extends RecyclerView.Adapter<WithdrawnFundsAdapter.ViewHolder> {

    public interface WithdrawnFundsClickCallback{
        void onWithdrawnFundsClick(int adapterPosition);
    }

    private static final float VIEW_HOLDER_WIDTH = 0.66f;
    private final List<FormattedWithdraw> list;
    private final WithdrawnFundsClickCallback callback;

    public WithdrawnFundsAdapter(List<FormattedWithdraw> list, WithdrawnFundsClickCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.viewholder_withdrawn_funds, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() * VIEW_HOLDER_WIDTH);
        //Do not set layoutParams.height. It will prevent centering
        itemView.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.tvDateWithdrawn.setText(list.get(i).getFormattedDateWithdrawn());
        holder.tvContractAddress.setText(list.get(i).getContractAddress());
        holder.tvAmount.setText(list.get(i).getFormattedAmount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_date_withdrawn)
        TextView tvDateWithdrawn;

        @BindView(R.id.tv_contract_address)
        TextView tvContractAddress;

        @BindView(R.id.tv_amount)
        TextView tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            callback.onWithdrawnFundsClick(getAdapterPosition());
        }
    }
}

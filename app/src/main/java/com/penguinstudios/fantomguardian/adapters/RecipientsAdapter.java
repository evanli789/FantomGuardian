package com.penguinstudios.fantomguardian.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.data.model.ContractInfoRecipient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipientsAdapter extends RecyclerView.Adapter<RecipientsAdapter.ViewHolder> {

    public interface SelectedRecipientCallback {
        void onRecipientClick(int adapterPosition);
    }

    private static final float VIEW_HOLDER_WIDTH = 0.66f;
    private final List<ContractInfoRecipient> list;
    private final SelectedRecipientCallback callback;

    public RecipientsAdapter(List<ContractInfoRecipient> list, SelectedRecipientCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.viewholder_recipient, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() * VIEW_HOLDER_WIDTH);
        //Do not set layoutParams.height. It will prevent centering
        itemView.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.tvWalletAddress.setText(list.get(i).getWalletAddress());
        holder.tvComments.setText(list.get(i).getComments());
        holder.tvAmount.setText(list.get(i).getFormattedAmount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_wallet_address)
        TextView tvWalletAddress;

        @BindView(R.id.tv_comments)
        TextView tvComments;

        @BindView(R.id.tv_amount)
        TextView tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            callback.onRecipientClick(getAdapterPosition());
        }
    }
}

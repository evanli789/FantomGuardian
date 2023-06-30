package com.penguinstudios.fantomguardian.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.data.model.SendToRecipient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendToAdapter extends RecyclerView.Adapter<SendToAdapter.ViewHolder> {

    public interface AdapterCallback {
        void onRecipientAddressEditTextChange(int adapterPosition, CharSequence s);

        void onAmountEditTextChange(int adapterPosition, CharSequence s);

        void onCommentEditTextChange(int adapterPosition, CharSequence s);

        void onScanBtnClick(int adapterPosition);
    }

    private final List<SendToRecipient> list;
    private final AdapterCallback callback;

    public SendToAdapter(List<SendToRecipient> list, AdapterCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_send_to, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        String recipientNumber = i + 1 + ".";
        holder.tvRecipientNumber.setText(recipientNumber);
        holder.etSendToAddress.setText(list.get(i).getRecipientAddress());

        if (list.get(i).getAmountFTM() == null) {
            holder.etAmountFtm.setText("");
        } else {
            holder.etAmountFtm.setText(list.get(i).getAmountFTM());
        }

        holder.etComments.setText(list.get(i).getComments());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements TextWatcher {

        @BindView(R.id.til_send_to_address)
        TextInputLayout tilSendToAddress;

        @BindView(R.id.et_send_to_address)
        TextInputEditText etSendToAddress;

        @BindView(R.id.et_amount_ftm)
        TextInputEditText etAmountFtm;

        @BindView(R.id.et_comments)
        TextInputEditText etComments;

        @BindView(R.id.tv_recipient_number)
        TextView tvRecipientNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            etSendToAddress.addTextChangedListener(this);
            etAmountFtm.addTextChangedListener(this);
            etComments.addTextChangedListener(this);
            tilSendToAddress.setEndIconOnClickListener(icon -> {
                callback.onScanBtnClick(getAdapterPosition());
            });
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int adapterPosition = getAdapterPosition();
            if (etSendToAddress.hasFocus()) {
                callback.onRecipientAddressEditTextChange(adapterPosition, s);
            } else if (etAmountFtm.hasFocus()) {
                callback.onAmountEditTextChange(adapterPosition, s);
            } else if (etComments.hasFocus()) {
                callback.onCommentEditTextChange(adapterPosition, s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}

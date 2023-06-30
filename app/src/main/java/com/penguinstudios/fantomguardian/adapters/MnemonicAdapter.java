package com.penguinstudios.fantomguardian.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MnemonicAdapter extends RecyclerView.Adapter<MnemonicAdapter.ViewHolder> {

    public interface EditTextCallback {
        void onEditTextChange(int adapterPosition, CharSequence s);
    }

    private final Map<Integer, CharSequence> mnemonicMap;
    private final EditTextCallback callback;
    private final int numWords;

    public MnemonicAdapter(Map<Integer, CharSequence> mnemonicMap, int numWords, EditTextCallback callback) {
        this.mnemonicMap = mnemonicMap;
        this.numWords = numWords;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_mnemonic_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        String number = i + 1 + ".";
        holder.tvNumber.setText(number);
        holder.etWord.setText(mnemonicMap.get(i));
    }

    @Override
    public int getItemCount() {
        return numWords;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements TextWatcher{

        @BindView(R.id.tv_number)
        TextView tvNumber;

        @BindView(R.id.et_word)
        EditText etWord;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            etWord.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            int adapterPosition = getAdapterPosition();
            if (etWord.hasFocus()) {
                callback.onEditTextChange(adapterPosition, charSequence);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}

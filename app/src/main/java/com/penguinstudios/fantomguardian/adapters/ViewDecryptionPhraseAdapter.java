package com.penguinstudios.fantomguardian.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewDecryptionPhraseAdapter extends RecyclerView.Adapter<ViewDecryptionPhraseAdapter.ViewHolder> {

    private final List<String> wordList;

    public ViewDecryptionPhraseAdapter(List<String> wordList) {
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_view_decryption_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        String number = i + 1 + ".";
        holder.tvNumber.setText(number);
        holder.tvWord.setText(wordList.get(i));
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_word)
        TextView tvWord;

        @BindView(R.id.tv_word_number)
        TextView tvNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

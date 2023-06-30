package com.penguinstudios.fantomguardian.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.util.SpacingUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletPopupWindow extends PopupWindow {

    public interface ClearWalletCallback {
        void onClearWallet();

        void onWalletQr();
    }

    private final Context context;
    private final ClearWalletCallback callback;

    public WalletPopupWindow(Context context, ClearWalletCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;

        setupView();
    }

    @SuppressLint("InflateParams")
    private void setupView() {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.popup_window_wallet_options, null);
        ButterKnife.bind(this, view);

        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bgr_menu_clear_wallet));
        setElevation(SpacingUtils.convertIntToDP(context, 4));
        setContentView(view);
    }

    @OnClick(R.id.tv_clear_wallet)
    void onClearWalletClick() {
        this.dismiss();
        callback.onClearWallet();
    }

    @OnClick(R.id.tv_wallet_qr)
    void onWalletQrClick() {
        this.dismiss();
        callback.onWalletQr();
    }
}

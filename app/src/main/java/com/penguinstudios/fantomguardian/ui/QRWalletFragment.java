package com.penguinstudios.fantomguardian.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.util.ClipboardUtil;
import com.penguinstudios.fantomguardian.util.SpacingUtils;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class QRWalletFragment extends DialogFragment {

    @BindView(R.id.iv_wallet_qr)
    ImageView ivWalletQr;

    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;

    private final String walletAddress;
    private static final int QR_SIZE = 150;

    public QRWalletFragment(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.qr_wallet_fragment, container, false);
        ButterKnife.bind(this, view);

        tvWalletAddress.setText(walletAddress);

        try {
            Bitmap bitmap = createQrCode(walletAddress);
            Glide.with(this)
                    .load(bitmap)
                    .apply(RequestOptions.centerInsideTransform())
                    .into(ivWalletQr);
        } catch (Exception e) {
            Timber.e(e);
        }

        return view;
    }

    private Bitmap createQrCode(String walletPublicKey) throws Exception {
        int QR_WIDTH_HEIGHT = SpacingUtils.convertIntToDP(requireContext(), QR_SIZE);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.encodeBitmap(walletPublicKey,
                BarcodeFormat.QR_CODE, QR_WIDTH_HEIGHT, QR_WIDTH_HEIGHT);
    }

    @OnClick(R.id.btn_close)
    void onCloseBtnClick() {
        this.dismiss();
    }

    @OnClick(R.id.layout_wallet_address)
    void onCopyAddressBtnClick() {
        ClipboardUtil.copyText(requireContext(), walletAddress);
        Toast.makeText(requireContext(), "Address copied", Toast.LENGTH_SHORT).show();
    }
}

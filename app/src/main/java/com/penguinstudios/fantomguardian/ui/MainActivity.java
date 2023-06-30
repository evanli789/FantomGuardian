package com.penguinstudios.fantomguardian.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.penguinstudios.fantomguardian.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private CreateFragment createFragment;
    private WalletFragment walletFragment;
    private WithdrawFragment withdrawFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottomNavigationView.setOnItemSelectedListener(bottomNavListener);

        createFragment = new CreateFragment();
        walletFragment = new WalletFragment();
        withdrawFragment = new WithdrawFragment();

        currentFragment = walletFragment;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, currentFragment)
                .commit();
    }

    private final NavigationBarView.OnItemSelectedListener bottomNavListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_wallet:
                showFragment(walletFragment);
                return true;

            case R.id.navigation_create:
                showFragment(createFragment);
                return true;

            case R.id.navigation_withdraw:
                showFragment(withdrawFragment);
                return true;

            default:
                return false;
        }
    };

    private void showFragment(Fragment fragment) {
        if (fragment == currentFragment) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragment.isAdded()) {
            transaction.hide(currentFragment).show(fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment).hide(currentFragment);
        }

        transaction.commit();
        currentFragment = fragment;
    }
}


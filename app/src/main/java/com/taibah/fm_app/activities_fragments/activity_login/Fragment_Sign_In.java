package com.taibah.fm_app.activities_fragments.activity_login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_home.HomeActivity;
import com.taibah.fm_app.activities_fragments.activity_sign_up.SignUpActivity;
import com.taibah.fm_app.databinding.FragmentSignInBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.models.LoginModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Sign_In extends Fragment implements Listeners.LoginListener, Listeners.ShowCountryDialogListener, Listeners.SkipListener, OnCountryPickerListener {
    private FragmentSignInBinding binding;
    private LoginActivity activity;
    private String lang;
    private CountryPicker countryPicker;
    private LoginModel loginModel;
    private Preferences preferences;

    public static Fragment_Sign_In newInstance() {
        return new Fragment_Sign_In();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        preferences = Preferences.newInstance();
        loginModel = new LoginModel();
        activity = (LoginActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        binding.setLoginModel(loginModel);
        binding.setLoginListener(this);
        binding.setSkipListener(this);
        binding.setShowCountryListener(this);
        binding.setLoginListener(this);
        createCountryDialog();
        binding.tvNewAccount.setOnClickListener(view -> {
            Intent intent = new Intent(activity, SignUpActivity.class);
            startActivity(intent);
            activity.finish();
        });
        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0")) {
                    binding.edtPhone.setText("");
                }
            }
        });

    }

    private void createCountryDialog() {
        countryPicker = new CountryPicker.Builder()
                .canSearch(true)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (countryPicker.getCountryFromSIM() != null) {
                updatePhoneCode(countryPicker.getCountryFromSIM());
            } else if (telephonyManager != null && countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()) != null) {
                updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
            } else if (countryPicker.getCountryByLocale(Locale.getDefault()) != null) {
                updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
            } else {
                String code = "+20";
                binding.tvCode.setText(code);
                loginModel.setPhone_code(code.replace("+", "00"));

            }
        } catch (Exception e) {
            String code = "+20";
            binding.tvCode.setText(code);
            loginModel.setPhone_code(code.replace("+", "00"));
        }


    }

    @Override
    public void checkDataLogin() {

        if (loginModel.isDataValid(activity)) {
            Common.CloseKeyBoard(activity, binding.edtPhone);
            login(loginModel);
        }
    }

    private void login(LoginModel loginModel) {


    }

    @Override
    public void showDialog() {
        countryPicker.showDialog(activity);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }

    private void updatePhoneCode(Country country) {
        binding.tvCode.setText(country.getDialCode());
        loginModel.setPhone_code(country.getDialCode().replace("+", "00"));

    }


    @Override
    public void skip() {
        Intent intent = new Intent(activity, HomeActivity.class);
        startActivity(intent);
        activity.finish();
    }
}

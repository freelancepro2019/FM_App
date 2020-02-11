package com.taibah.fm_app.activities_fragments.activity_sign_up;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_login.LoginActivity;
import com.taibah.fm_app.databinding.ActivitySignUpBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.SignUpModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;

import java.util.Locale;

import io.paperdb.Paper;

public class SignUpActivity extends AppCompatActivity implements Listeners.ShowCountryDialogListener, OnCountryPickerListener, Listeners.BackListener, Listeners.SignUpListener {
    private ActivitySignUpBinding binding;
    private String lang;
    private CountryPicker countryPicker;
    private SignUpModel signUpModel;
    private Preferences preferences;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        initView();
    }

    private void initView() {
        signUpModel = new SignUpModel();
        preferences = Preferences.newInstance();
        lang = Paper.book().read("lang","ar");
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setShowCountryListener(this);
        binding.setSignUpListener(this);
        binding.setSignUpModel(signUpModel);
        createCountryDialog();

        binding.checkbox.setOnClickListener(view -> {
            if (binding.checkbox.isChecked())
            {
                signUpModel.setAccept(true);
                /*Intent intent = new Intent(activity, TermsActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);*/
            }else
            {
                signUpModel.setAccept(false);

            }
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


    @Override
    public void checkDataSignUp() {

        if (signUpModel.isDataValid(this)) {
            Common.CloseKeyBoard(this,binding.edtName);


        }
    }

    private void createCountryDialog() {
        countryPicker = new CountryPicker.Builder()
                .canSearch(true)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(this)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (countryPicker.getCountryFromSIM() != null) {
                updatePhoneCode(countryPicker.getCountryFromSIM());
            } else if (telephonyManager != null && countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()) != null) {
                updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
            } else if (countryPicker.getCountryByLocale(Locale.getDefault()) != null) {
                updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
            } else {
                String code = "+966";
                binding.tvCode.setText(code);
                signUpModel.setPhone_code(code.replace("+", "00"));

            }
        } catch (Exception e) {
            String code = "+966";
            binding.tvCode.setText(code);
            signUpModel.setPhone_code(code.replace("+", "00"));
        }


    }

    @Override
    public void showDialog() {
        countryPicker.showDialog(this);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }

    private void updatePhoneCode(Country country) {
        binding.tvCode.setText(country.getDialCode());
        signUpModel.setPhone_code(country.getDialCode().replace("+", "00"));

    }


    @Override
    public void back()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

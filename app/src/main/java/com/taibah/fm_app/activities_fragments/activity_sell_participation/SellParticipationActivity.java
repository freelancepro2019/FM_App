package com.taibah.fm_app.activities_fragments.activity_sell_participation;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.databinding.ActivitySellParticipationBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.SellParticipationModel;

import java.util.Locale;

import io.paperdb.Paper;

public class SellParticipationActivity extends AppCompatActivity implements Listeners.BackListener ,Listeners.ShowCountryDialogListener, OnCountryPickerListener , Listeners.SellListener {
    private ActivitySellParticipationBinding binding;
    private String lang;
    private SellParticipationModel model;
    private CountryPicker countryPicker;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sell_participation);
        initView();

    }


    private void initView() {
        model = new SellParticipationModel();
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        binding.setModel(model);
        binding.setBackListener(this);
        binding.setSellListener(this);
        binding.setShowCountryListener(this);
        createCountryDialog();


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
                model.setPhone_code(code);

            }
        } catch (Exception e) {
            String code = "+966";
            binding.tvCode.setText(code);
            model.setPhone_code(code);
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
        model.setPhone_code(country.getDialCode());

    }

    @Override
    public void back() {
        finish();
    }


    @Override
    public void checkSellData(SellParticipationModel sellParticipationModel) {

        if (sellParticipationModel.isDataValid(this))
        {
            sell(sellParticipationModel);
        }
    }

    private void sell(SellParticipationModel sellParticipationModel) {


    }
}

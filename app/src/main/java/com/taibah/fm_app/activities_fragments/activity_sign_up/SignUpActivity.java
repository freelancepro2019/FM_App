package com.taibah.fm_app.activities_fragments.activity_sign_up;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_confirm_code.ConfirmCodeActivity;
import com.taibah.fm_app.activities_fragments.activity_home.HomeActivity;
import com.taibah.fm_app.activities_fragments.activity_login.LoginActivity;
import com.taibah.fm_app.activities_fragments.activity_terms.TermsActivity;
import com.taibah.fm_app.databinding.ActivitySignUpBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.SignUpModel;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;
import com.taibah.fm_app.tags.Tags;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class SignUpActivity extends AppCompatActivity implements Listeners.ShowCountryDialogListener, OnCountryPickerListener, Listeners.BackListener, Listeners.SignUpListener {
    private ActivitySignUpBinding binding;
    private String lang;
    private CountryPicker countryPicker;
    private SignUpModel signUpModel;
    private Preferences preferences;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private ProgressDialog dialog;

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
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        mAuth = FirebaseAuth.getInstance();
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
                Intent intent = new Intent(this, TermsActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
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
            sendSmsToPhone();

        }
    }

    private void sendSmsToPhone() {

        dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                mAuth.setLanguageCode(lang);
                mAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = task.getResult().getUser().getUid();

                                UserModel userModel = new UserModel(userId, signUpModel.getName(), signUpModel.getEmail(), signUpModel.getPhone_code() + signUpModel.getPhone());
                                registerUser(userModel);

                            }
                        }).addOnFailureListener(e -> {
                    if (e.getMessage() != null) {
                        Common.CreateDialogAlert(SignUpActivity.this,e.getMessage());

                    }
                });

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                if (e.getMessage()!=null)
                {
                    Common.CreateDialogAlert(SignUpActivity.this,e.getMessage());

                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                dialog.dismiss();
                Intent intent = new Intent(SignUpActivity.this, ConfirmCodeActivity.class);
                intent.putExtra("verification_id",verificationId);
                intent.putExtra("data",signUpModel);
                startActivity(intent);
                finish();

            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                signUpModel.getPhone_code()+signUpModel.getPhone(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
                );
    }

    private void registerUser(UserModel user)
    {

        dRef.child(user.getId())
                .setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        preferences.create_update_userData(this, user);
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(e -> {
            if (e.getMessage() != null) {
                dialog.dismiss();
                Common.CreateDialogAlert(SignUpActivity.this,e.getMessage());
            }
        });
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
                signUpModel.setPhone_code(code);

            }
        } catch (Exception e) {
            String code = "+966";
            binding.tvCode.setText(code);
            signUpModel.setPhone_code(code);
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
        signUpModel.setPhone_code(country.getDialCode());

    }

    @Override
    public void onBackPressed() {
      back();
    }

    @Override
    public void back()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

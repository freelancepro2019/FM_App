package com.creative.share.apps.homecare.activities_fragments.activity_sign_up;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.databinding.FragmentDoctorSignUpBinding;
import com.creative.share.apps.homecare.interfaces.Listeners;
import com.creative.share.apps.homecare.models.SignUpDoctorModel;
import com.creative.share.apps.homecare.preferences.Preferences;
import com.creative.share.apps.homecare.share.Common;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Doctor_SignUp extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener,Listeners.BackListener,Listeners.SignUpListener{
    private FragmentDoctorSignUpBinding binding;
    private SignUpActivity activity;
    private String lang;
    private CountryPicker countryPicker;
    private SignUpDoctorModel signUpDoctorModel;
    private Preferences preferences;
    private Uri uri = null;
    private final String camera_perm = Manifest.permission.CAMERA;
    private final String write_perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int camera_req = 1;

    public static Fragment_Doctor_SignUp newInstance() {
        return new Fragment_Doctor_SignUp();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_doctor_sign_up, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        signUpDoctorModel = new SignUpDoctorModel();
        preferences = Preferences.newInstance();
        activity = (SignUpActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        binding.setLang(lang);
        binding.setShowCountryListener(this);
        binding.setBackListener(this);
        binding.setSignUpListener(this);
        binding.setSignUpModel(signUpDoctorModel);
        createCountryDialog();

        binding.imageSwitchUser.setOnClickListener((v)->activity.back());
        binding.checkbox.setOnClickListener(view -> {
            if (binding.checkbox.isChecked())
            {
                signUpDoctorModel.setAcceptTerms(true);
            }else
                {
                    signUpDoctorModel.setAcceptTerms(false);

                }
        });
        binding.flImage.setOnClickListener((v)->checkCameraPermission());



    }

    @Override
    public void checkDataSignUp() {

        if (signUpDoctorModel.isDataValid(activity))
        {
            Common.CloseKeyBoard(activity,binding.edtName);

        }
    }

    private void createCountryDialog()
    {
        countryPicker = new CountryPicker.Builder()
                .canSearch(true)
                .listener(this)
                .theme(CountryPicker.THEME_NEW)
                .with(activity)
                .build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (countryPicker.getCountryFromSIM()!=null)
            {
                updatePhoneCode(countryPicker.getCountryFromSIM());
            }else if (telephonyManager!=null&&countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso())!=null)
            {
                updatePhoneCode(countryPicker.getCountryByISO(telephonyManager.getNetworkCountryIso()));
            }else if (countryPicker.getCountryByLocale(Locale.getDefault())!=null)
            {
                updatePhoneCode(countryPicker.getCountryByLocale(Locale.getDefault()));
            }else
            {
                String code = "+20";
                binding.tvCode.setText(code);
                signUpDoctorModel.setPhone_code(code.replace("+","00"));

            }
        }catch (Exception e)
        {
            String code = "+20";
            binding.tvCode.setText(code);
            signUpDoctorModel.setPhone_code(code.replace("+","00"));
        }


    }


    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, camera_perm) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, write_perm) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{camera_perm, write_perm}, camera_req);
        }
    }

    private void selectImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, camera_req);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == camera_req && grantResults.length > 0) {
            boolean isGranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    isGranted = true;
                } else {
                    isGranted = false;
                }
            }
            if (isGranted) {
                selectImage();

            }
            else{
                Toast.makeText(activity, "access images denied", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == camera_req && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            binding.iconUpload.setVisibility(View.GONE);
            binding.image.setImageBitmap(bitmap);
            uri = getUriFromBitmap(bitmap);
        }
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "", ""));
    }


    @Override
    public void showDialog() {
        countryPicker.showDialog(activity);
    }

    @Override
    public void onSelectCountry(Country country) {
        updatePhoneCode(country);
    }

    private void updatePhoneCode(Country country)
    {
        binding.tvCode.setText(country.getDialCode());
        signUpDoctorModel.setPhone_code(country.getDialCode().replace("+","00"));

    }

    @Override
    public void back() {
        activity.FinishActivity();
    }



}

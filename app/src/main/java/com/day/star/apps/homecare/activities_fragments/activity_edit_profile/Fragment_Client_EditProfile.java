package com.day.star.apps.homecare.activities_fragments.activity_edit_profile;

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
import android.text.Editable;
import android.text.TextWatcher;
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

import com.day.star.apps.homecare.R;
import com.day.star.apps.homecare.databinding.FragmentClientEditProfileBinding;
import com.day.star.apps.homecare.interfaces.Listeners;
import com.day.star.apps.homecare.models.EditProfileClientModel;
import com.day.star.apps.homecare.models.UserModel;
import com.day.star.apps.homecare.preferences.Preferences;
import com.day.star.apps.homecare.tags.Tags;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Client_EditProfile extends Fragment implements Listeners.ShowCountryDialogListener, OnCountryPickerListener {
    private FragmentClientEditProfileBinding binding;
    private EditProfileActivity activity;
    private String lang;
    private CountryPicker countryPicker;
    private EditProfileClientModel  editProfileClientModel;
    private Preferences preferences;
    private UserModel userModel;
    private Uri uri = null;
    private final String camera_perm = Manifest.permission.CAMERA;
    private final String write_perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int camera_req = 1;


    public static Fragment_Client_EditProfile newInstance() {
        return new Fragment_Client_EditProfile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_edit_profile, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        editProfileClientModel = new EditProfileClientModel();
        activity = (EditProfileActivity) getActivity();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setShowCountryListener(this);
        binding.setEditModel(editProfileClientModel);
        createCountryDialog();
        updateUI();

        binding.rbMale.setOnClickListener(view ->

            editProfileClientModel.setGender(1)
        );

        binding.rbFemale.setOnClickListener(view ->

                editProfileClientModel.setGender(2)
        );
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

        binding.flImage.setOnClickListener(view -> checkCameraPermission());

    }

    private void updateUI()
    {
        binding.edtName.setText(userModel.getName());
        binding.edtEmail.setText(userModel.getEmail());
        binding.tvCode.setText(userModel.getPhone_code().replaceFirst("00","+"));
        binding.edtPhone.setText(userModel.getPhone());

        editProfileClientModel.setName(userModel.getName());
        editProfileClientModel.setEmail(userModel.getEmail());
        editProfileClientModel.setPhone_code(userModel.getPhone_code());
        editProfileClientModel.setPhone(userModel.getPhone());

        if (userModel.getLogo()!=null&&!userModel.getLogo().isEmpty()&&!userModel.getLogo().equals("0"))
        {
            binding.iconUpload.setVisibility(View.GONE);
            Picasso.with(activity).load(Uri.parse(Tags.IMAGE_AVATAR+userModel.getLogo())).fit().into(binding.image);

        }

        if (userModel.getGender().equals("1"))
        {
            binding.rbMale.setChecked(true);

        }else if (userModel.getGender().equals("2"))
            {
                binding.rbFemale.setChecked(true);

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
            uploadImage(uri);
        }
    }



    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "", ""));
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
                String code = "+966";
                binding.tvCode.setText(code);
                editProfileClientModel.setPhone_code(code.replace("+", "00"));

            }
        } catch (Exception e) {
            String code = "+20";
            binding.tvCode.setText(code);
            editProfileClientModel.setPhone_code(code.replace("+", "00"));
        }


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
        editProfileClientModel.setPhone_code(country.getDialCode().replace("+", "00"));

    }


    private void uploadImage(Uri uri) {

    }




}

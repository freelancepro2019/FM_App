package com.taibah.fm_app.activities_fragments.activity_sign_up;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_home.HomeActivity;
import com.taibah.fm_app.activities_fragments.activity_terms.TermsActivity;
import com.taibah.fm_app.databinding.ActivitySignUpBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.SignUpModel;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;
import com.taibah.fm_app.tags.Tags;

import io.paperdb.Paper;

public class SignUpActivity extends AppCompatActivity implements  Listeners.BackListener, Listeners.SignUpListener {
    private ActivitySignUpBinding binding;
    private String lang;
    private SignUpModel signUpModel;
    private Preferences preferences;
    private String phone_code,phone_number, user_id;
    private DatabaseReference dRef;


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
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent != null&&intent.hasExtra("user_id")) {

            phone_code = intent.getStringExtra("phone_code");
            phone_number = intent.getStringExtra("phone_number");
            user_id = intent.getStringExtra("user_id");

        }
    }

    private void initView() {

        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        signUpModel = new SignUpModel();
        preferences = Preferences.newInstance();
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        binding.setSignUpListener(this);
        binding.setSignUpModel(signUpModel);
        binding.tvCode.setText(phone_code);
        binding.tvPhone.setText(phone_number);
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



    }


    @Override
    public void checkDataSignUp() {

        if (signUpModel.isDataValid(this)) {
            Common.CloseKeyBoard(this,binding.edtName);
            UserModel userModel = new UserModel(user_id,signUpModel.getName(),signUpModel.getEmail(),phone_code+phone_number);
            registerUser(userModel);

        }
    }

    private void registerUser(UserModel user)
    {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
    @Override
    public void onBackPressed() {
      back();
    }

    @Override
    public void back()
    {
        finish();
    }
}

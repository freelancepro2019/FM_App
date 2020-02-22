package com.taibah.fm_app.activities_fragments.activity_update_profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taibah.fm_app.R;
import com.taibah.fm_app.databinding.ActivityUpdateProfileBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;
import com.taibah.fm_app.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;

public class UpdateProfileActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityUpdateProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private DatabaseReference dRef;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        initView();
    }


    private void initView() {
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);

        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setUserModel(userModel);

        binding.btnUpdate.setOnClickListener(view -> {
            checkData();
        });


    }

    private void checkData() {
        String name = binding.edtName.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();

        if (!name.isEmpty() &&
                !email.isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            binding.edtName.setError(null);
            binding.edtEmail.setError(null);
            Common.CloseKeyBoard(this, binding.edtName);

            userModel.setName(name);
            userModel.setEmail(email);
            update(userModel);
        } else {

            if (name.isEmpty()) {
                binding.edtName.setError(getString(R.string.field_req));
            } else {
                binding.edtName.setError(null);

            }


            if (email.isEmpty()) {
                binding.edtEmail.setError(getString(R.string.field_req));
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.setError(getString(R.string.inv_email));

            } else {
                binding.edtEmail.setError(null);

            }
        }
    }

    private void update(UserModel userModel) {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();

        dRef.child(userModel.getId())
                .setValue(userModel)
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful())
                    {
                        Toast.makeText(this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                        preferences.create_update_userData(this,userModel);
                        binding.setUserModel(userModel);

                    }
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void back() {
        finish();
    }

}

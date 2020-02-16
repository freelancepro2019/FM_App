package com.taibah.fm_app.activities_fragments.activity_confirm_code;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_home.HomeActivity;
import com.taibah.fm_app.activities_fragments.activity_login.LoginActivity;
import com.taibah.fm_app.activities_fragments.activity_sign_up.SignUpActivity;
import com.taibah.fm_app.databinding.ActivityConfirmCodeBinding;
import com.taibah.fm_app.databinding.DialogAlertBinding;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;
import com.taibah.fm_app.tags.Tags;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class ConfirmCodeActivity extends AppCompatActivity {
    private ActivityConfirmCodeBinding binding;

    private boolean canResend = true;
    private CountDownTimer countDownTimer;
    private String lang;
    private Preferences preferences;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private ProgressDialog dialog;
    private String phone_code, phone_number;
    private String verificationId;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_code);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("phone_number")) {

            phone_code = intent.getStringExtra("phone_code");
            phone_number = intent.getStringExtra("phone_number");


        }
    }

    private void initView() {

        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        preferences = Preferences.newInstance();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.btnConfirm.setOnClickListener(v -> checkData());

        binding.btnResend.setOnClickListener(v -> {

            if (canResend) {
                reSendSMSCode();
            }
        });
        reSendSMSCode();
        startCounter();
    }

    private void checkData() {
        String code = binding.edtCode.getText().toString().trim();
        if (!TextUtils.isEmpty(code)) {
            Common.CloseKeyBoard(this, binding.edtCode);
            validateCode(code);
        } else {
            binding.edtCode.setError(getString(R.string.field_req));
        }
    }

    private void reSendSMSCode() {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                mAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                String userId = task.getResult().getUser().getUid();
                                getUserData(userId);


                            }
                        }).addOnFailureListener(e -> {
                    if (e.getMessage() != null) {
                        Common.CreateDialogAlert(ConfirmCodeActivity.this, e.getMessage());
                    }
                });
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                if (e.getMessage() != null) {
                    createDialogAlert(e.getMessage());
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                ConfirmCodeActivity.this.verificationId = verificationId;

            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_code+phone_number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );
    }

    private void validateCode(String smsCode) {
        dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, smsCode);
        mAuth.setLanguageCode(lang);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();
                        getUserData(userId);


                    }
                }).addOnFailureListener(e -> {

            if (e.getMessage() != null) {
                dialog.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startCounter() {
        countDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                canResend = false;

                int AllSeconds = (int) (millisUntilFinished / 1000);
                int seconds = AllSeconds % 60;
                binding.btnResend.setText("00:" + seconds);
            }

            @Override
            public void onFinish() {
                canResend = true;
                binding.btnResend.setText(getString(R.string.resend));
            }
        }.start();
    }

    private void getUserData(String userId) {

        Log.e("id",userId+"__");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);

        // check is user table already created or not
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue()!=null)
                {
                    checkIsUserRegisterBefore(userId);
                }else
                    {
                        Intent intent = new Intent(ConfirmCodeActivity.this, SignUpActivity.class);
                        intent.putExtra("phone_code", phone_code);
                        intent.putExtra("phone_number", phone_number);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                        finish();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkIsUserRegisterBefore(String userId){

        dRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        preferences.create_update_userData(ConfirmCodeActivity.this, userModel);
                        Intent intent = new Intent(ConfirmCodeActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(ConfirmCodeActivity.this, SignUpActivity.class);
                        intent.putExtra("phone_code", phone_code);
                        intent.putExtra("phone_number", phone_number);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("ccccccc","__");
            }


        });

    }


    private void createDialogAlert(String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogAlertBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_alert, null, false);

        binding.tvMsg.setText(msg);
        binding.btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();

                    Intent intent = new Intent(ConfirmCodeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}


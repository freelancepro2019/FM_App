package com.taibah.fm_app.activities_fragments.activity_sell_participation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.adapters.UsersAdapter;
import com.taibah.fm_app.databinding.ActivitySellParticipationBinding;
import com.taibah.fm_app.databinding.DialogAlertSellBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.MyJoinModel;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;
import com.taibah.fm_app.tags.Tags;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SellParticipationActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivitySellParticipationBinding binding;
    private String lang;
    private List<UserModel> userModelList;
    private UsersAdapter adapter;
    private DatabaseReference dRef, dRefJoin;
    private Preferences preferences;
    private UserModel userModel;
    private MyJoinModel myJoinModel;
    private String currentDate;
    private int sellingDays;
    private LocalDate startDate, endDate;
    private String cost = "";


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sell_participation);
        getDataFromIntent();
        initView();
        getCurrentDate();
        getSellingDays();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("data")) {
            myJoinModel = (MyJoinModel) intent.getSerializableExtra("data");
            Log.e( "data",myJoinModel.getDate()+"_");
        }
    }


    private void initView() {
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_USERS);
        dRefJoin = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_JOINS);

        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        userModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(userModelList, this);
        binding.recView.setAdapter(adapter);

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String query = editable.toString().trim();
                if (!query.isEmpty()) {
                    search(query);
                } else {
                    userModelList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void search(String query) {
        binding.progBar.setVisibility(View.VISIBLE);
        binding.tvNoSearchResults.setVisibility(View.GONE);
        userModelList.clear();
        adapter.notifyDataSetChanged();

        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);

                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserModel model = ds.getValue(UserModel.class);

                        if (!model.getId().equals(userModel.getId()) && (model.getName().toLowerCase().contains(query.toLowerCase()) || model.getPhone().contains(query))) {
                            userModelList.add(model);
                        }
                    }

                    if (userModelList.size() > 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        binding.tvNoSearchResults.setVisibility(View.VISIBLE);

                    }
                } else {
                    binding.progBar.setVisibility(View.GONE);
                    binding.tvNoSearchResults.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void back() {
        finish();
    }


    public void setItemData(UserModel model) {

        createDialogSelling(model);


    }

    private void getCurrentDate() {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        currentDate = df.format(c);


        Log.e("nnnnnnnnnnnnn", currentDate);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getSellingDays() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MMM/yyyy").withLocale(Locale.ENGLISH);

//        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy",Locale.ENGLISH);


        startDate = LocalDate.parse(myJoinModel.getJoin_date(), f);
        endDate = LocalDate.parse(currentDate, f);

        Period period = Period.between(startDate, endDate);
        sellingDays = period.getDays();
        Log.e("nnnnnnnnnnnnn", period.getDays() + "");
    }

    private void createDialogAlert(UserModel model) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogAlertSellBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_alert_sell, null, false);

        binding.tvMsg.setText(R.string.sell_join);
        binding.btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();
            addJoin(model);
        });
        binding.btnCancel.setOnClickListener(v -> dialog.dismiss()

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void createDialogSelling(UserModel model) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogAlertSellBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_alert_sell, null, false);
        Log.e("mmmm", sellingDays + "");

        Log.e("kkkkkk", startDate + " " + endDate + "");
        if (startDate.toString().equals(endDate.toString())) {
            cost = myJoinModel.getCost();

        } else {
            cost = (Integer.parseInt(myJoinModel.getCost()) - sellingDays * 10) + "";
        }
//       cost = sellingDays * 10;
        binding.tvMsg.setText(getString(R.string.selling_cost) + " " + cost + " " + getString(R.string.sar));
        binding.btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();
            createDialogAlert(model);
        });
        binding.btnCancel.setOnClickListener(v -> dialog.dismiss()

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void addJoin(UserModel model) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();

        dRefJoin.child(model.getId()).child(myJoinModel.getId())
                .setValue(myJoinModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        removeOldJoin(dialog);
                    }
                }).addOnFailureListener(e -> {
            dialog.dismiss();
            Common.CreateDialogAlert(this, getString(R.string.failed));
        });

    }

    private void removeOldJoin(ProgressDialog dialog) {

        dRefJoin.child(userModel.getId()).child(myJoinModel.getId())
                .removeValue()
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, getString(R.string.suc), Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        finish();
                    }

                }).addOnFailureListener(e -> dialog.dismiss());


    }


}

package com.taibah.fm_app.activities_fragments.activity_health_food;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.adapters.ProductAdapter;
import com.taibah.fm_app.databinding.ActivityHealthFoodBinding;
import com.taibah.fm_app.databinding.DialogAlertSellBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.ProductModel;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;
import com.taibah.fm_app.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class HealthFoodActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityHealthFoodBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private ProductAdapter adapter;
    private List<ProductModel> productModelList;
    private DatabaseReference dRef;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_health_food);
        initView();
    }


    private void initView() {
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME);
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        productModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new ProductAdapter(productModelList, this);
        binding.recView.setAdapter(adapter);

        getData();

    }

    private void getData() {
        dRef.child(Tags.TABLE_PRODUCTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        ProductModel productModel = ds.getValue(ProductModel.class);
                        if (productModel!=null)
                        {
                            productModelList.add(productModel);
                        }
                    }

                    if (productModelList.size()>0)
                    {
                        binding.tvNoProduct.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                    }else
                        {
                            binding.tvNoProduct.setVisibility(View.VISIBLE);

                        }

                } else {
                    binding.tvNoProduct.setVisibility(View.VISIBLE);
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

    public void setItemData(ProductModel model)
    {

        if (userModel!=null)
        {
            createDialogAlert(model);

        }else
            {
                Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
            }
    }

    private void createDialogAlert(ProductModel model)
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogAlertSellBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_alert_sell, null, false);

        binding.tvMsg.setText(getString(R.string.buy)+" "+model.getName());
        binding.btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();
            buy(model);
        });
        binding.btnCancel.setOnClickListener(v -> dialog.dismiss()

        );
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

    private void buy(ProductModel model)
    {
        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        dRef.child(Tags.TABLE_PURCHASES).child(userModel.getId()).child(model.getId())
                .setValue(model)
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful())
                    {
                        Toast.makeText(this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                });

    }


}

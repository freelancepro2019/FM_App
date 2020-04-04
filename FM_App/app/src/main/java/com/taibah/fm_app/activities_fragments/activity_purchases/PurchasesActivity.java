package com.taibah.fm_app.activities_fragments.activity_purchases;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

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
import com.taibah.fm_app.databinding.ActivityPurchasesBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.ProductModel;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class PurchasesActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityPurchasesBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchases);
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
        dRef.child(Tags.TABLE_PURCHASES).child(userModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
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



}

package com.taibah.fm_app.activities_fragments.activity_my_join;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_sell_participation.SellParticipationActivity;
import com.taibah.fm_app.adapters.MyJoinsAdapter;
import com.taibah.fm_app.databinding.ActivityMyJoinsBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.MyJoinModel;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class MyJoinsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityMyJoinsBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private DatabaseReference dRef;
    private List<MyJoinModel> myJoinModelList;
    private MyJoinsAdapter adapter;
    private int selectedPos = -1;






    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_joins);
        initView();
    }


    private void initView()
    {
        myJoinModelList = new ArrayList<>();
        preferences  = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_JOINS);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyJoinsAdapter(myJoinModelList,this);
        binding.recView.setAdapter(adapter);

        getJoins();

    }

    private void getJoins() {
        dRef.child(userModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binding.progBar.setVisibility(View.GONE);
                if (dataSnapshot.getValue()!=null)
                {
                    for (DataSnapshot ds :dataSnapshot.getChildren())
                    {
                        MyJoinModel myJoinModel = ds.getValue(MyJoinModel.class);
                        myJoinModelList.add(myJoinModel);
                    }
                    if (myJoinModelList.size()>0)
                    {
                        adapter.notifyDataSetChanged();
                        binding.tvNoData.setVisibility(View.GONE);
                    }else
                        {
                            binding.tvNoData.setVisibility(View.VISIBLE);
                        }
                }else
                    {
                        binding.tvNoData.setVisibility(View.VISIBLE);
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

    public void setItemData(MyJoinModel myJoinModel, int adapterPosition) {
        selectedPos = adapterPosition;
        Intent intent = new Intent(this, SellParticipationActivity.class);
        intent.putExtra("data",myJoinModel);
        startActivityForResult(intent,100);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==100&&resultCode==RESULT_OK)
        {
            if (selectedPos!=-1&&myJoinModelList.size()>0)
            {
                myJoinModelList.remove(selectedPos);
                adapter.notifyItemRemoved(selectedPos);

                if (myJoinModelList.size()>0)
                {
                    binding.tvNoData.setVisibility(View.GONE);
                }else
                    {
                        binding.tvNoData.setVisibility(View.VISIBLE);

                    }

                selectedPos = -1;
            }
        }
    }
}

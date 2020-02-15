package com.taibah.fm_app.activities_fragments.activity_health_food;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.taibah.fm_app.R;
import com.taibah.fm_app.databinding.ActivityHealthFoodBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;

import java.util.Locale;

import io.paperdb.Paper;

public class HealthFoodActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityHealthFoodBinding binding;
    private String lang;






    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_health_food);
        initView();
    }




    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);



    }



    @Override
    public void back() {
        finish();
    }

}

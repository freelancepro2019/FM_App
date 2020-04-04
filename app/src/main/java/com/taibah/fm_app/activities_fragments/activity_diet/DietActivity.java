package com.taibah.fm_app.activities_fragments.activity_diet;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.taibah.fm_app.R;
import com.taibah.fm_app.adapters.DurationAdapter;
import com.taibah.fm_app.databinding.ActivityDietBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.DietModel;
import com.taibah.fm_app.tags.Tags;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import io.paperdb.Paper;

public class DietActivity extends AppCompatActivity implements Listeners.BackListener , Listeners.DietListener {
    private ActivityDietBinding binding;
    private String lang;
    private DietModel model;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diet);
        initView();

    }


    private void initView() {
        Paper.init(this);
        model = new DietModel();

        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.setModel(model);
        binding.setDietListener(this);

        binding.scrollView.getParent().requestChildFocus(binding.scrollView,binding.scrollView);



        binding.rbMale.setOnClickListener(view ->{
            model.setGender(Tags.male);
            binding.setModel(model);
        });

        binding.rbFemale.setOnClickListener(view ->{
            model.setGender(Tags.female);
            binding.setModel(model);
        });





    }





    @Override
    public void back() {
        finish();
    }




    @Override
    public void checkDietData(DietModel dietModel) {
        if (dietModel.isDataValid(this ))
        {
            send(dietModel);
        }
    }
    private void send(DietModel dietModel) {

    }
}

package com.taibah.fm_app.activities_fragments.activity_join_now;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.taibah.fm_app.R;
import com.taibah.fm_app.adapters.DurationAdapter;
import com.taibah.fm_app.databinding.ActivityJoinNowBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.JoinNowModel;
import com.taibah.fm_app.tags.Tags;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class JoinNowActivity extends AppCompatActivity implements Listeners.BackListener , Listeners.JoinListener, DatePickerDialog.OnDateSetListener {
    private ActivityJoinNowBinding binding;
    private String lang;
    private JoinNowModel model;
    private List<JoinNowModel.Duration> durationList;
    private DatePickerDialog datePickerDialog;
    private DurationAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_now);
        initView();

    }


    private void initView() {
        durationList = new ArrayList<>();
        model = new JoinNowModel();
        model.setType(Tags.student);
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.setModel(model);
        binding.setJoinListener(this);

        binding.scrollView.getParent().requestChildFocus(binding.scrollView,binding.scrollView);
        adapter = new DurationAdapter(this,durationList);
        binding.spinner.setAdapter(adapter);
        addDuration();
        CreateDatePickerDialog();
        binding.rbMale.setOnClickListener(view ->{
            model.setGender(Tags.male);
            binding.setModel(model);
        });

        binding.rbFemale.setOnClickListener(view ->{
            model.setGender(Tags.female);
            binding.setModel(model);
        });


        binding.rbStudent.setOnClickListener(view ->{
            model.setType(Tags.student);
            binding.setModel(model);
            binding.llId.setVisibility(View.VISIBLE);
            addDuration();
        });

        binding.rbTrainer.setOnClickListener(view ->{
            model.setType(Tags.trainer);
            binding.setModel(model);
            binding.llId.setVisibility(View.GONE);
            addDuration();
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i==0)
                {
                    model.setDuration(null);
                    binding.setModel(model);
                }else
                    {
                        model.setDuration(durationList.get(i));
                        binding.setModel(model);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.tvChange.setOnClickListener(view -> {
            try {
                datePickerDialog.show(getFragmentManager(), "");
            } catch (Exception e) {
            }
        });


    }

    private void addDuration()
    {
        durationList.clear();
        durationList.add(new JoinNowModel.Duration(getString(R.string.ch),""));
        if (model.getType()==Tags.student)
        {
            durationList.add(new JoinNowModel.Duration(getString(R.string.day),"10"));
            durationList.add(new JoinNowModel.Duration(getString(R.string.month),"300"));

        }
        durationList.add(new JoinNowModel.Duration(getString(R.string.month3),"900"));
        durationList.add(new JoinNowModel.Duration(getString(R.string.year),"10800"));

        adapter.notifyDataSetChanged();
        binding.spinner.setSelection(0);

    }

    private void CreateDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,Calendar.JANUARY);
        calendar.set(Calendar.YEAR,1995);


        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setAccentColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.setCancelColor(ActivityCompat.getColor(this, R.color.gray4));
        datePickerDialog.setOkColor(ActivityCompat.getColor(this, R.color.colorPrimary));
        datePickerDialog.setOkText(getString(R.string.ch));
        datePickerDialog.setCancelText(getString(R.string.cancel));
        datePickerDialog.setLocale(new Locale(lang));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setMinDate(calendar);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        String d = dateFormat.format(new Date(calendar.getTimeInMillis()));

        binding.tvDate.setText(d);

        model.setBirthDate(d);
        binding.setModel(model);

    }


    @Override
    public void back() {
        finish();
    }


    @Override
    public void checkJoinData(JoinNowModel joinNowModel) {

        if (joinNowModel.isDataValid(this))
        {
            send(joinNowModel);
        }
    }

    private void send(JoinNowModel joinNowModel) {

    }
}

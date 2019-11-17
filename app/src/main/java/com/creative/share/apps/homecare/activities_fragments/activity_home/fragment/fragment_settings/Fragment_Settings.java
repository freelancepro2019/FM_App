package com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.fragment_settings;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.homecare.databinding.FragmentSettingsBinding;
import com.creative.share.apps.homecare.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Settings extends Fragment {
    private FragmentSettingsBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;
    private LinearLayout llShare,llRate,ll_lang;
    private String [] language_array;

    public static Fragment_Settings newInstance() {
        return new Fragment_Settings();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        View view = binding.getRoot();
        initView(view);
        return view;
    }

    private void initView(View view) {
        preferences = Preferences.newInstance();
        activity = (HomeActivity) getActivity();
        llShare = view.findViewById(R.id.llShare);
        llRate= view.findViewById(R.id.llRate);
        ll_lang= view.findViewById(R.id.ll_lang);

        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        language_array = new String[]{"English","العربية"};

        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String app_url = "https://play.google.com/store/apps/details?id=" + activity.getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, "Home Care App ");
                intent.putExtra(Intent.EXTRA_TEXT, app_url);
                startActivity(intent);
            }
        });
        llRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));

                }
            }
        });

        ll_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateLanguageDialog();
            }
        });


    }
    private void CreateLanguageDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view  = LayoutInflater.from(activity).inflate(R.layout.dialog_language,null);
        Button btn_select = view.findViewById(R.id.btn_select);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        final NumberPicker numberPicker = view.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(language_array.length-1);
        numberPicker.setDisplayedValues(language_array);
        numberPicker.setWrapSelectorWheel(false);
        if (lang.equals("ar"))
        {
            numberPicker.setValue(2);

        }else if (lang.equals("en"))
        {
            numberPicker.setValue(0);

        }else
        {
            numberPicker.setValue(1);
        }
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                int pos = numberPicker.getValue();
                if (pos == 0)
                {
                    activity.RefreshActivity("en");
                }else if (pos ==1)
                {
                    activity.RefreshActivity("ar");

                }


            }
        });




        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //  dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setView(view);
        dialog.show();
    }

}

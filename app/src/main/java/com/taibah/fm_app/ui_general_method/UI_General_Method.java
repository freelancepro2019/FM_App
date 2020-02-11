package com.taibah.fm_app.ui_general_method;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UI_General_Method {

    @BindingAdapter("error")
    public static void setErrorUi(View view, String error)
    {
        if (view instanceof EditText)
        {
            EditText editText = (EditText) view;
            editText.setError(error);

        }else if (view instanceof TextView)
        {
            TextView textView = (TextView) view;
            textView.setError(error);

        }
    }



    @BindingAdapter("date")
    public static void displayDate(TextView textView,long date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        String sTime = dateFormat.format(new Date(date*1000));
        textView.setText(sTime);
    }

    @BindingAdapter("time")
    public static void displayTime(TextView textView,long time)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String sTime = dateFormat.format(new Date(time*1000));
        textView.setText(sTime);
    }





}

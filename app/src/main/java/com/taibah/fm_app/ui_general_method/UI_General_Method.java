package com.taibah.fm_app.ui_general_method;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;


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

    @BindingAdapter("image")
    public static void displayImage(View view ,String url)
    {
        if (url!=null&&!url.isEmpty())
        {
            if (view instanceof RoundedImageView)
            {
                RoundedImageView imageView = (RoundedImageView) view;

                Picasso.with(view.getContext()).load(Uri.parse(url)).fit().into(imageView);
            }
        }
    }





}

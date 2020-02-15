package com.taibah.fm_app.models;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.taibah.fm_app.R;

import java.io.Serializable;

public class DietModel extends BaseObservable implements Serializable {

    private String id;
    private int gender;
    private int age;
    private int weight;
    private int height;
    private String details;
    public ObservableField<String> error_id = new ObservableField<>();
    public ObservableField<String> error_age = new ObservableField<>();
    public ObservableField<String> error_height = new ObservableField<>();
    public ObservableField<String> error_weight = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();


    public DietModel() {
        this.id = "";
        this.details = "";
        this.age = 0;
        this.height=0;
        this.weight=0;
    }

    public DietModel(int age, String id, int gender, int height,int weight ,String details) {
        this.id = id;
        this.gender = gender;
        this.gender = 0;
        this.details = details;
        this.weight=weight;
        this.height=height;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);

    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }


    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        notifyPropertyChanged(BR.details);

    }

    public boolean isDataValid(Context context) {
        if (age != 0 &&
                gender != 0 &&
                height != 0 &&
                weight != 0 &&

                !TextUtils.isEmpty(details)

        ) {
            error_age.set(null);
            error_height.set(null);
            error_weight.set(null);
            error_details.set(null);




            if (gender == 0) {
                Toast.makeText(context,context.getString(R.string.ch_gender), Toast.LENGTH_SHORT).show();

            }

            if (age== 0) {
                error_age.set(context.getString(R.string.field_req));
            } else {
                error_age.set(null);
            }
            if (height== 0) {
                error_height.set(context.getString(R.string.field_req));
            } else {
                error_height.set(null);
            }
            if (weight== 0) {
                error_weight.set(context.getString(R.string.field_req));
            } else {
                error_weight.set(null);
            }

            if (details.isEmpty()) {
                error_details.set(context.getString(R.string.field_req));
            } else {
                error_details.set(null);
            }


        }
        return false;

    }


}

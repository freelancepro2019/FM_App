package com.taibah.fm_app.models;

import android.content.Context;
import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.taibah.fm_app.R;

import java.io.Serializable;

public class SellParticipationModel extends BaseObservable implements Serializable {

    private String name;
    private String phone_code;
    private String phone;
    private String details;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();


    public SellParticipationModel() {
        this.name = "";
        this.phone = "";
        this.phone_code = "";
        this.details = "";
    }

    public SellParticipationModel(String name, String phone_code,String phone, String details) {
        this.name = name;
        notifyPropertyChanged(BR.name);
        this.phone_code = phone_code;
        notifyPropertyChanged(BR.phone_code);

        this.phone = phone;
        notifyPropertyChanged(BR.phone);

        this.details = details;

    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);


    }


    @Bindable
    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
        notifyPropertyChanged(BR.phone_code);

    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);

    }

    @Bindable
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        notifyPropertyChanged(BR.details);

    }

    public boolean isDataValid(Context context) {
        if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(phone_code) &&
                !TextUtils.isEmpty(phone) &&
                !TextUtils.isEmpty(details)

        ) {
            error_name.set(null);
            error_phone.set(null);
            error_details.set(null);

            return true;
        } else {
            if (name.isEmpty()) {
                error_name.set(context.getString(R.string.field_req));
            } else {
                error_name.set(null);
            }


            if (phone.isEmpty()) {
                error_phone.set(context.getString(R.string.field_req));
            } else {
                error_phone.set(null);
            }


            if (details.isEmpty()) {
                error_details.set(context.getString(R.string.field_req));
            } else {
                error_details.set(null);
            }





            return false;
        }
    }
}

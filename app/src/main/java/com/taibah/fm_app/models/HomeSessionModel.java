package com.taibah.fm_app.models;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.taibah.fm_app.R;

import java.io.Serializable;

public class HomeSessionModel extends BaseObservable implements Serializable {

    private String phone_code;
    private String phone;
    private String time;
    private String date;
    private String age;
    private String service;
    private String address;
    private String details;

    public ObservableField<String> error_date = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_age = new ObservableField<>();
    public ObservableField<String> error_time = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();


    public HomeSessionModel() {
        this.service = "";
        this.phone_code = "";
        this.details = "";
        this.date = "";
        this.time = "";
        this.age = "";
        this.address = "";
        this.phone = "";

        setPhone_code(phone_code);
        setPhone(phone);
        setAge(age);
        setDate(date);
        setTime(time);
        setService(service);
        setAddress(address);
        setDetails(details);
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
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        notifyPropertyChanged(BR.time);

    }

    @Bindable
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
        notifyPropertyChanged(BR.age);

    }

    @Bindable
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        notifyPropertyChanged(BR.date);

    }

    @Bindable
    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);

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
        if (!TextUtils.isEmpty(phone) &&
                        !TextUtils.isEmpty(date) &&
                        !TextUtils.isEmpty(time) &&
                        !service.isEmpty()&&
                        !TextUtils.isEmpty(age) &&
                        !TextUtils.isEmpty(address) &&
                        !TextUtils.isEmpty(details)

        ) {
            error_phone.set(null);
            error_date.set(null);
            error_age.set(null);
            error_time.set(null);
            error_address.set(null);
            error_details.set(null);


        } else {


            if (service.isEmpty()) {
                Toast.makeText(context, R.string.ch_ser, Toast.LENGTH_SHORT).show();
            }


            if (phone.isEmpty()) {
                error_phone.set(context.getString(R.string.field_req));
            } else {
                error_phone.set(null);
            }

            if (date.isEmpty()) {
                error_date.set(context.getString(R.string.field_req));
            } else {
                error_date.set(null);
            }
            if (age.isEmpty()) {
                error_age.set(context.getString(R.string.field_req));
            } else {
                error_age.set(null);
            }
            if (time.isEmpty()) {
                error_time.set(context.getString(R.string.field_req));
            } else {
                error_time.set(null);
            }
            if (address.isEmpty()) {
                error_address.set(context.getString(R.string.field_req));
            } else {
                error_details.set(null);
            }
            if (details.isEmpty()) {
                error_details.set(context.getString(R.string.field_req));
            } else {
                error_details.set(null);
            }


        }
        return false;

    }

    public static class Service implements Serializable {
        private String service;
        private String cost;

        public Service() {
        }

        public Service(String service, String cost) {
            this.service = service;
            this.cost = cost;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }
}

package com.taibah.fm_app.models;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.taibah.fm_app.R;
import com.taibah.fm_app.tags.Tags;

import java.io.Serializable;

public class HomeSessionModel extends BaseObservable implements Serializable {

    private String id;
    private String phone;
    private String time;
    private String age;
    private String date;
    private String service;
    private String address;
    private String details;
    public ObservableField<String> error_id = new ObservableField<>();
    public ObservableField<String> error_date = new ObservableField<>();
    public ObservableField<String> error_phone = new ObservableField<>();
    public ObservableField<String> error_age = new ObservableField<>();
    public ObservableField<String> error_time = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();


    public HomeSessionModel() {
        this.id = "";
        this.details = "";
        this.date = "";
        this.time = "";
        this.age = "";
        this.address = "";
        this.phone = "";
    }

    public HomeSessionModel( String id, String phone, String age, String date,String time,String service,String address, String details) {
        this.id = id;
        this.phone = phone;
        this.age = age;
        this.date = date;
        this.time = time;
        this.service = service;
        this.address=address;
        this.details = details;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        if (service != null &&
                !TextUtils.isEmpty(phone) &&
                !TextUtils.isEmpty(date) &&
                !TextUtils.isEmpty(age) &&
                !TextUtils.isEmpty(time) &&
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



            if (service == null) {
                Toast.makeText(context, R.string.ch_ser, Toast.LENGTH_SHORT).show();
            }




            if (date.isEmpty()) {
                error_date.set(context.getString(R.string.field_req));
            } else {
                error_date.set(null);
            }
            if (age.isEmpty()){
                error_age.set(context.getString(R.string.field_req));
            }else {
                error_age.set(null);
            }
            if (time.isEmpty()){
                error_time.set(context.getString(R.string.field_req));
            }else {
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

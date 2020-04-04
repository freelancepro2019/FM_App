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

    private String time;
    private String date;
    private String age;
    private String service;
    private String service_name;
    private String address;
    private String details;
    private String cost;
    private double lat;
    private double lng;

    public ObservableField<String> error_date = new ObservableField<>();
    public ObservableField<String> error_age = new ObservableField<>();
    public ObservableField<String> error_time = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();


    public HomeSessionModel() {
        this.service = "";
        this.details = "";
        this.date = "";
        this.time = "";
        this.age = "";
        this.address = "";

        setAge(age);
        setDate(date);
        setTime(time);
        setService(service);
        setAddress(address);
        setDetails(details);
    }

    public boolean isDataValid(Context context) {

        if (!TextUtils.isEmpty(date) &&
                !TextUtils.isEmpty(time) &&
                !service.isEmpty() &&
                !TextUtils.isEmpty(age) &&
                !TextUtils.isEmpty(address) &&
                !TextUtils.isEmpty(details)

        ) {
            error_date.set(null);
            error_age.set(null);
            error_time.set(null);
            error_address.set(null);
            error_details.set(null);

            return true;

        } else {


            if (service.isEmpty()) {
                Toast.makeText(context, R.string.ch_ser, Toast.LENGTH_SHORT).show();
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public static class Service implements Serializable {
        private int id;
        private String serviceName;
        private String cost;

        public Service() {
        }

        public Service(int id, String serviceName, String cost) {
            this.id = id;
            this.serviceName = serviceName;
            this.cost = cost;
        }

        public int getId() {
            return id;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getCost() {
            return cost;
        }
    }
}

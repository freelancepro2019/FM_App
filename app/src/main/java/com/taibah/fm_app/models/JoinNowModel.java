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

public class JoinNowModel extends BaseObservable implements Serializable {

    private int type;
    private String id;
    private Duration duration;
    private int gender;
    private String birthDate;
    private String details;
    public ObservableField<String> error_id = new ObservableField<>();
    public ObservableField<String> error_birth_date = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();


    public JoinNowModel() {
        this.id = "";
        this.details = "";
        this.birthDate = "";
    }

    public JoinNowModel(int type, String id, Duration duration, int gender, String birthDate, String details) {
        this.type = type;
        this.id = id;
        this.duration = duration;
        this.gender = gender;
        this.birthDate = birthDate;
        this.gender = 0;
        this.details = details;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);

    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Bindable
    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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
        if (duration != null &&
                gender != 0 &&
                !TextUtils.isEmpty(birthDate) &&
                !TextUtils.isEmpty(details)

        ) {
            error_birth_date.set(null);
            error_details.set(null);


            if (type == Tags.student) {

                if (id.isEmpty()) {
                    error_id.set(context.getString(R.string.field_req));
                    return false;
                }else {
                    error_id.set(null);

                    return true;
                }
            } else
                {
                    return true;
                }

        } else {


            if (type == Tags.student) {

                if (id.isEmpty()) {
                    error_id.set(context.getString(R.string.field_req));
                }else {
                    error_id.set(null);

                }

            }

            if (duration == null) {
                Toast.makeText(context, R.string.ch_dur, Toast.LENGTH_SHORT).show();
            }


            if (gender == 0) {
                Toast.makeText(context,context.getString(R.string.ch_gender), Toast.LENGTH_SHORT).show();

            }

            if (birthDate.isEmpty()) {
                error_birth_date.set(context.getString(R.string.field_req));
            } else {
                error_birth_date.set(null);
            }


            if (details.isEmpty()) {
                error_details.set(context.getString(R.string.field_req));
            } else {
                error_details.set(null);
            }


            return false;
        }
    }

    public static class Duration implements Serializable {
        private String duration;
        private String cost;

        public Duration() {
        }

        public Duration(String duration, String cost) {
            this.duration = duration;
            this.cost = cost;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }
}

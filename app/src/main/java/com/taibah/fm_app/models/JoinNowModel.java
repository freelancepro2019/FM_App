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
    private String user_id;
    private Duration duration;
//    private int gender;
    private String joinDate;
    private String details;
    public ObservableField<String> error_user_id = new ObservableField<>();
    public ObservableField<String> error_join_date = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();


    public JoinNowModel() {
        this.user_id = "";
        this.details = "";
        this.joinDate = "";
    }

    public JoinNowModel(int type, String user_id, Duration duration, String joinDate, String details) {
        this.type = type;
        this.user_id = user_id;
        this.duration = duration;
      //  this.gender = gender;
        this.joinDate = joinDate;
     //   this.gender = 0;
        this.details = details;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Bindable
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String id) {
        this.user_id = id;
        notifyPropertyChanged(BR.user_id);

    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

/*
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
*/

    @Bindable
    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
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
              //  gender != 0 &&
                !TextUtils.isEmpty(joinDate) &&
                !TextUtils.isEmpty(details)

        ) {
            error_join_date.set(null);
            error_details.set(null);


            if (type == Tags.student) {

                if (user_id.isEmpty()) {
                    error_user_id.set(context.getString(R.string.field_req));
                    return false;
                }else {
                    error_user_id.set(null);

                    return true;
                }
            } else
                {
                    return true;
                }

        } else {


            if (type == Tags.student) {

                if (user_id.isEmpty()) {
                    error_user_id.set(context.getString(R.string.field_req));
                }else {
                    error_user_id.set(null);

                }

            }

            if (duration == null) {
                Toast.makeText(context, R.string.ch_dur, Toast.LENGTH_SHORT).show();
            }


/*
            if (gender == 0) {
                Toast.makeText(context,context.getString(R.string.ch_gender), Toast.LENGTH_SHORT).show();

            }
*/

            if (joinDate.isEmpty()) {
                error_join_date.set(context.getString(R.string.field_req));
            } else {
                error_join_date.set(null);
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

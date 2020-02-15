package com.taibah.fm_app.interfaces;


import com.taibah.fm_app.models.DietModel;
import com.taibah.fm_app.models.HomeSessionModel;
import com.taibah.fm_app.models.JoinNowModel;
import com.taibah.fm_app.models.SellParticipationModel;

public interface Listeners {


    interface LoginListener {
        void checkDataLogin();
    }
    interface SkipListener
    {
        void skip();
    }
    interface BackListener
    {
        void back();
    }
    interface ShowCountryDialogListener
    {
        void showDialog();
    }


    interface SignUpListener {
        void checkDataSignUp();
    }


    interface SellListener {
        void checkSellData(SellParticipationModel sellParticipationModel);
    }


    interface JoinListener {
        void checkJoinData(JoinNowModel joinNowModel);
    }
    interface DietListener {
        void checkDietData(DietModel dietModel);
    }
    interface HomeSessionListener {
        void checkHomeSessionData(HomeSessionModel homeSessionModel);
    }
    interface UpdateProfileListener
    {
        void updateProfile();
    }

    /*interface ContactListener
    {
        void sendContact(ContactUsModel contactUsModel);
    }*/
}

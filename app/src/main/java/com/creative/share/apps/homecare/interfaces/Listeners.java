package com.creative.share.apps.homecare.interfaces;

import com.creative.share.apps.homecare.models.ContactUsModel;

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

    interface MoreActions
    {
        void aboutApp();
        void changeLanguage();
        void contactUs();
        void rateApp();
        void terms();
        void share();
        void logout();
    }

    interface ContactListener
    {
        void sendContact(ContactUsModel contactUsModel);
    }
}

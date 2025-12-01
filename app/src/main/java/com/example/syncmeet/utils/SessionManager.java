package com.example.syncmeet.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "SyncMeetSession";
    private static final String KEY_LOGIN = "loginSaved";
    private static final String KEY_PASSWORD = "passwordSaved";
    private static final String KEY_REMEMBER = "remember";

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveLogin(String login, String password) {
        editor.putString(KEY_LOGIN, login);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER, true);
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isRemembered() {
        return pref.getBoolean(KEY_REMEMBER, false);
    }

    public String getSavedLogin() {
        return pref.getString(KEY_LOGIN, "");
    }

    public String getSavedPassword() {
        return pref.getString(KEY_PASSWORD, "");
    }
}

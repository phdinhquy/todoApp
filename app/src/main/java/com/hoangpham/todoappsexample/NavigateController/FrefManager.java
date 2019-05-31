package com.hoangpham.todoappsexample.NavigateController;

import android.content.Context;
import android.content.SharedPreferences;

public class FrefManager {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    Context mContext;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "android-hive-welcome";
    private static final String IS_LOGIN_IN = "IsLogin";
    private static final String USER_NAME = "username";
    private static final String EMAIL = "email";
    private static FrefManager mInstance;

    // singleton pattern
    public static FrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FrefManager(context);
        }
        return mInstance;
    }

    public FrefManager(Context context) {
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public void setUserName(String userName) {
        mEditor.putString(USER_NAME, userName);
        mEditor.apply();
    }
    public String getUsername() {
        return mPref.getString(USER_NAME,"");
    }
    public void setEmail(String email) {
        mEditor.putString(EMAIL, email);
        mEditor.apply();
    }
    public String getEmail() {
        return mPref.getString(EMAIL,"");
    }

    public void setLogin(boolean isFirstTime) {
        mEditor.putBoolean(IS_LOGIN_IN, isFirstTime);
        mEditor.apply();
    }
    public boolean isLoginIn() {
        return mPref.getBoolean(IS_LOGIN_IN, true);
    }
}

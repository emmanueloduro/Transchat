package com.example.alawatrik.transchat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ALAWATRIK on 4/15/2018.
 */

public class FirebaseOffline extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

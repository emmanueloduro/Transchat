package com.example.alawatrik.transchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by ALAWATRIK on 4/14/2018.
 */

public class ToChat extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("prefs", 0);
        boolean firstR = sp.getBoolean("firstR", false);
        if (!firstR) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("firstR", true);
            editor.apply();
            Intent main_page_intent = new Intent(ToChat.this,MainActivity.class);
            startActivity(main_page_intent);
        }
        else{
            Intent chat_page_intent = new Intent(ToChat.this,ChatActivity.class);
            startActivity(chat_page_intent);
        }

    }

}

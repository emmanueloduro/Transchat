package com.example.alawatrik.transchat;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTablayout;
    private DatabaseReference mRef;
    private String name;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("SignUp").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                mToolbar = (Toolbar)findViewById(R.id.mainpage_toolbar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setTitle(name);
                mToolbar.setTitleTextColor(0xFFFFFFFF);
                mToolbar.setSubtitleTextColor(0xFFFFFFFF);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mUserRef = FirebaseDatabase.getInstance().getReference().child("SignUp").child(mAuth.getCurrentUser().getUid());



        //Tabs
        mViewPager = (ViewPager)findViewById(R.id.main_tabPager);

        //PagerAdapter for the viewpager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //passing the adapter to the ViewPager
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTablayout = (TabLayout)findViewById(R.id.main_tabs);
        mTablayout.setupWithViewPager(mViewPager);
        mTablayout.setTabTextColors(0xFFFFFFFF,0xFFFFFFFF);
    }

    //menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }


    //MENU
    //perform actions when menu item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        //when Account settings menu item is clicked
        if(item.getItemId()==(R.id.accounts_settings)){

            Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
            startActivity(intent);

        }

        return true;
    }

    //onStart method of the the Chat Activity
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current_user = mAuth.getCurrentUser();
        if(current_user !=null) {

            mUserRef.child("online").setValue(true);

        }
    }//end of onStart method

    //onStop method


    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser current_user = mAuth.getCurrentUser();
        if(current_user !=null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }//end of onStop method



}//end of main ChatActivyt.java class

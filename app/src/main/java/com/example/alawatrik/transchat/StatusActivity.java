package com.example.alawatrik.transchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ProgressDialog mProgress;
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;
    private TextInputLayout mStatus;
    private Button mSavebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        mStatus = (TextInputLayout)findViewById(R.id.status_input);
        mSavebtn = (Button)findViewById(R.id.status_save_btn);

        mToolbar = (Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value");

        mStatus.getEditText().setText(status_value);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Progress
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Updating status");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();

                //get current user uid to ensure update corresponds
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                String current_uid = mCurrentUser.getUid();


                //referencing signup database
                mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("SignUp").child(current_uid);


                String status = mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            mProgress.dismiss();
                            Intent statusIntent = new Intent(StatusActivity.this, SettingsActivity.class);
                            startActivity(statusIntent);
                            Toast.makeText(getApplicationContext(),"Status successfully updated", Toast.LENGTH_SHORT);


                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving Changes.", Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }
        });








    }
}


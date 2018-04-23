package com.example.alawatrik.transchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.database.Cursor;
import android.widget.Button;
import android.provider.MediaStore;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private Button mStatusBtn;
    private Button mImageBtn;
    private CircleImageView mDisplayImage;
    private TextView mName;
    private CircleImageView settings_image;
    private TextView mStatus;
    private DatabaseReference mUserDatabase;
    private StorageReference mImageStorage;
    private FirebaseUser mCurrentUser;
    private static final int GALLERY_PICK = 1;
    private static final String TAG = "MainActivity";

    private DatabaseReference databaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mStatusBtn = (Button) findViewById(R.id.settings_status_btn);
        mStatus = (TextView) findViewById(R.id.settings_status);
        settings_image = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView)findViewById(R.id.settings_name);
        //firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        mImageStorage = FirebaseStorage.getInstance().getReference();


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("SignUp").child(current_uid);
        mUserDatabase.keepSynced(true);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieving the data in Signup database of current users
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //DisplayUserInfo();


        settings_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        //status button onclickListerner
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mStatus.getText().toString();

                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);

            }
        });
    }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == GALLERY_PICK) {
                    // Get the url from data
                    Uri selectedImageUri = data.getData();
                    if (null != selectedImageUri) {
                        // Get the path from the Uri
                        String path = getPathFromURI(selectedImageUri);
                        Log.i(TAG, "Image Path : " + path);
                        // Set the image in ImageView
                       ((ImageView) findViewById(R.id.settings_image)).setImageURI(selectedImageUri);

                       //saving image in database
                        StorageReference filepath = mImageStorage.child("profile_images").child("profile_image.jpg");
                        filepath.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(getApplicationContext(),"Profile image successfully uploaded", Toast.LENGTH_SHORT);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Error uploading profile image", Toast.LENGTH_SHORT);
                                }

                            }
                        });



                    }
                }
            }
         }

   // Get the real path from the URI
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

   /* private void DisplayUserInfo(){

        //getting current user id.
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("SignUp").child("status");

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String status = dataSnapshot.getValue(String.class);
                mStatus.setText(status);


            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    } */




}

package com.example.alawatrik.transchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.graphics.Color;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private Button submit;
    private EditText status;
    private EditText username;
    private CircleImageView mDisplayImage;
    private ProgressDialog progressDialog;
    private static final int GALLERY_PICK = 1;
    private FirebaseAuth mAuth;
    private CircleImageView profile_image;
    private static final String TAG = "PhoneAuthActivity";

    //ProgressDialog
    private ProgressDialog mRegProgress;

    public static Spinner language_spinner;

    //firebase database instances
    DatabaseReference databaseUsers;
    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    //databaseUsers reference

        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);

        mAuth = FirebaseAuth.getInstance();
        //referencing controls
        profile_image = (CircleImageView)findViewById(R.id.profile_image);
        mRegProgress = new ProgressDialog(this);
        language_spinner = (Spinner)findViewById(R.id.spinner1);
        submit = (Button)findViewById(R.id.submit);
        username = (EditText)findViewById(R.id.username);
        status = (EditText)findViewById(R.id.status);
        progressDialog = new ProgressDialog(this);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        //submit button onclick listerner
        submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               mRegProgress.setTitle("Registering User");
               mRegProgress.setMessage("Please wait while we create your account !");
               mRegProgress.setCanceledOnTouchOutside(false);
               mRegProgress.show();


               AddUser();
           }
       });
    }



    public void AddUser() {


        String name = username.getText().toString().trim();
        String language = language_spinner.getSelectedItem().toString();
        String userstatus = status.getText().toString().trim();



        if(!TextUtils.isEmpty(name)) {

            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = current_user.getUid();

                databaseUsers = FirebaseDatabase.getInstance().getReference().child("SignUp").child(uid);
                databaseUsers.keepSynced(true);
                HashMap<String, String> SignUpMap = new HashMap<>();
                SignUpMap.put("name", name);
                SignUpMap.put("language", language);
                SignUpMap.put("status", userstatus);
                SignUpMap.put("image", "default");
                SignUpMap.put("thumb_image", "default");

                databaseUsers.setValue(SignUpMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Intent mainIntent = new Intent(SignUpActivity.this, ChatActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }
                    }
                });


            }

        else{
            Toast.makeText(getApplication(), "Please enter your username", Toast.LENGTH_LONG);
        }

    }//end of oncreate method





    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


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


}

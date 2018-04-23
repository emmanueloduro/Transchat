package com.example.alawatrik.transchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;



public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mPhoneNumberField, mVerificationField;
    Button mStartButton, mVerifyButton, mResendButton;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    Context context = this;
    String PhoneNumber;
    ProgressBar progressBar;
    //ProgressDialog
    private ProgressDialog mRegProgress;
    private CountryCodePicker countryCodePicker;



    private static final String TAG = "PhoneAuthActivity";

    @Override    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        mRegProgress = new ProgressDialog(this);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);
        progressBar = (ProgressBar)findViewById(R.id.pro_Bar);


        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        mResendButton = (Button) findViewById(R.id.button_resend);
        countryCodePicker = (CountryCodePicker)findViewById(R.id.Country_Code);

        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        progressBar = new ProgressBar(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {



            @Override            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
                //Toast.makeText(getApplication(), "Verification Completed", Toast.LENGTH_LONG).show();


                mRegProgress.setTitle("Authenticating your phone number.");
                mRegProgress.setMessage("Please wait while we create your account !");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();



            }

            @Override            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
            }




            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token)
            {

                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            Toast.makeText(getApplication(), "Verification Completed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(TestActivity.this, SignUpActivity.class));
                            getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                                Toast.makeText(getApplication(),"TransChat couldn't send the code, Check your internet connection and try again",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }


    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);


        mRegProgress.setTitle("Sending verification code");
        mRegProgress.setMessage("Please wait while we verify your phone number !");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.show();

        Toast.makeText(getApplication(), "TransChat is sending a verification code to +"+ phoneNumber + " \nMake sure you are connected to the internet.", Toast.LENGTH_LONG).show();

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        Toast.makeText(getApplication(), " Verifying...", Toast.LENGTH_LONG).show();

    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);

       // progressBar.setVisibility(View.VISIBLE);


        mRegProgress.setTitle("Resending verification code");
        mRegProgress.setMessage("Please wait while we verify your account !");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.show();

    }

    private boolean validatePhoneNumber() {

        mPhoneNumberField.setError(null);
        countryCodePicker.registerCarrierNumberEditText(mPhoneNumberField);
        PhoneNumber = countryCodePicker.getFullNumber();


       String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
       //  mRegProgress.dismiss();
         mRegProgress.cancel();
            return false;
        }else{


        }
        return true;
    }
    @Override    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            startActivity(new Intent(TestActivity.this, SignUpActivity.class));
        }
    }

    @Override    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {

                    return;
                }
                startPhoneNumberVerification(PhoneNumber.toString());
                break;
            case R.id.button_verify_phone:
                String code = PhoneNumber.toString();

                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(PhoneNumber.toString(), mResendToken);
                break;
        }

    }


}
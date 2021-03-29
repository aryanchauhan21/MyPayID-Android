package com.leotarius.mypayid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Phone_OTP2 extends AppCompatActivity {
    int time;
    private static final String TAG = "RegisterOTP2";
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean codeSent=false;
    private FirebaseAuth auth;
    private boolean verified = false;
    TextView error;
    EditText otp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_otp2);

        Button resend = findViewById(R.id.resend);
        TextView timer = findViewById(R.id.timer);
        Button verifyBtn = findViewById(R.id.verify);
        otp = findViewById(R.id.otp);
        error = findViewById(R.id.error_text);

        Log.d(TAG, "onCreate: ");
        // initially the button will be inactive because the OTP is just sent.

        // setting firebase
//        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("en");

        // get phone from previous activity
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");

        makeSendOtpCall(phone);
        toggleBtnTimer(resend, timer);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // toogle button and timer
                toggleBtnTimer(resend, timer);

                // make the otp send call again.
                makeSendOtpCall(phone);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codeSent){
                    String code = otp.getText().toString();
                    Log.d(TAG, "onClick: entererd OTP : " + code);
                    Log.d(TAG, "onClick: Verifying via user entered code......");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            error.setVisibility(View.GONE);

                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(), "Verification Successful", Toast.LENGTH_LONG).show();
//                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {

                            // Sign in failed, display a message and update the UI
                            error.setVisibility(View.VISIBLE);

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Log.d(TAG, "onComplete: " + task.getException().getMessage());
                            }
                        }

                    }
                });
    }

    private void makeSendOtpCall(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 30, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

//                otp.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Automatically verifying...", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

                signInWithPhoneAuthCredential(phoneAuthCredential);
                phoneVerified();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.d(TAG, "onVerificationFailed: number entered is wrong");
                    Toast.makeText(getApplicationContext(), "Wrong number", Toast.LENGTH_SHORT).show();

                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(getApplicationContext(), "SMS quota exceeded", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onVerificationFailed: quota exceeded");
                }

                Log.d(TAG, "onVerificationFailed: verification failed");
                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // code is sent, let the user know.
                Toast.makeText(getApplicationContext(), "Code sent!", Toast.LENGTH_SHORT).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                codeSent = true;
                // Now go to click listener on verify button
            }

        });



    }

    private void phoneVerified(){
        Log.d(TAG, "phoneVerified: phone verified successfully");
    }
    
    public void toggleBtnTimer(Button resend, TextView timer){
        resend.setEnabled(false);
        resend.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.disabled_btn_bg));
        time = 30;
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("0:" + checkDigit(time));
                time--;
            }

            public void onFinish() {
                timer.setText("0:00");
                resend.setEnabled(true);
                resend.setBackgroundColor(Color.TRANSPARENT);
            }
        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}

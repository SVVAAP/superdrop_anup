package com.example.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.android.gms.tasks.TaskExecutors;

public class OtpVerifyActivity extends AppCompatActivity {

    private EditText[] etCodeArray = new EditText[6];
    private Button btnVerify;
    private ProgressBar progressBarVerify;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        etCodeArray[0] = findViewById(R.id.etC1);
        etCodeArray[1] = findViewById(R.id.etC2);
        etCodeArray[2] = findViewById(R.id.etC3);
        etCodeArray[3] = findViewById(R.id.etC4);
        etCodeArray[4] = findViewById(R.id.etC5);
        etCodeArray[5] = findViewById(R.id.etC6);

        btnVerify = findViewById(R.id.btnVerify);
        progressBarVerify = findViewById(R.id.progressBarVerify);

        // Retrieve verification ID from the intent
        verificationId = getIntent().getStringExtra("verificationId");

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder otpBuilder = new StringBuilder();
                for (EditText editText : etCodeArray) {
                    otpBuilder.append(editText.getText().toString());
                }
                String enteredOTP = otpBuilder.toString();

                verifyOTP(enteredOTP);
            }
        });
    }

    private void verifyOTP(String enteredOTP) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOTP);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // OTP verification successful, proceed to the next activity
                            // For example, you can redirect to the user's profile or dashboard
                        } else {
                            // OTP verification failed, show an error message
                        }
                    }
                });
    }
}

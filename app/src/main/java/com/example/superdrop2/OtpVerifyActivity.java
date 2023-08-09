package com.example.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
        // Inside your onCreate method
        for (int i = 0; i < etCodeArray.length; i++) {
            final int currentIndex = i;
            final EditText currentEditText = etCodeArray[currentIndex];

            currentEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 1 && currentIndex < etCodeArray.length - 1) {
                        etCodeArray[currentIndex + 1].requestFocus();
                    } else if (charSequence.length() == 0 && currentIndex > 0) {
                        etCodeArray[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }

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
        for (EditText editText : etCodeArray) {
            editText.setEnabled(false);
        }
        btnVerify.setEnabled(false);

        // Show progress bar
        progressBarVerify.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOTP);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // OTP verification successful, proceed to the next activity
                            // For example, you can redirect to the user's profile or dashboard
                            startActivity(new Intent(OtpVerifyActivity.this, Detail_Activity.class));
                            finish();
                        } else {
                            for (EditText editText : etCodeArray) {
                                editText.setEnabled(true);
                            }
                            btnVerify.setEnabled(true);

                            // Hide progress bar
                            progressBarVerify.setVisibility(View.GONE);
                            // OTP verification failed, show an error message
                            Toast.makeText(OtpVerifyActivity.this, "Error!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

package com.svvaap.superdrop2;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpVerifyActivity extends AppCompatActivity {

    private EditText[] etCodeArray = new EditText[6];
    private Button btnVerify;
    private ProgressBar progressBarVerify;
    private String verificationId,phoneNumber1;



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
        etCodeArray[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 6) {
                    for (int j = 0; j < etCodeArray.length; j++) {
                        etCodeArray[j].setText(String.valueOf(charSequence.charAt(j)));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Retrieve verification ID from the intent
        verificationId = getIntent().getStringExtra("verificationId");
        phoneNumber1 = getIntent().getStringExtra("phoneNumber");

        TextView tvPhoneNumber = findViewById(R.id.tvMobile); // Use the appropriate TextView ID from your XML
        tvPhoneNumber.setText(phoneNumber1);
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
                    } else if (charSequence.length() == 0) {
                        if (i1 > 0) { // Handling backspace
                            if (currentIndex > 0) {
                                etCodeArray[currentIndex - 1].requestFocus();
                            }
                        }
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
                            Intent intent = new Intent(OtpVerifyActivity.this,Detail_Activity.class);
                            intent.putExtra("phoneNumber", phoneNumber1);
                            startActivity(intent);
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

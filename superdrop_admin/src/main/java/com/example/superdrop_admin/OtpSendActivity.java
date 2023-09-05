package com.example.superdrop_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class OtpSendActivity extends AppCompatActivity {

    private EditText etPhone;
    private Button btnSend,btSkip;
    private ProgressBar progressBar;
    private CountryCodePicker countryCodePicker;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> phoneNumberLauncher;
    private WebView webView;
    private WebViewClient webViewClient;
//    private WebViewCompat webViewCompat;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentuser= mAuth.getCurrentUser();
        if(currentuser!=null) {
            startActivity(new Intent(OtpSendActivity.this, OwnersActivity.class));
           finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_send);

        etPhone = findViewById(R.id.etPhone);
        btnSend = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.progressBar);
        countryCodePicker = findViewById(R.id.ccp);
        btSkip = findViewById(R.id.skip_bt);

        mAuth = FirebaseAuth.getInstance();

//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                // Intercept the URL and load it in the WebView
//                view.loadUrl(request.getUrl().toString());
//                return true;
//            }
//        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = countryCodePicker.getSelectedCountryCode();
                String phoneNumber = etPhone.getText().toString();
                String fullPhoneNumber = "+" + countryCode + phoneNumber;

                sendOTP(fullPhoneNumber);
            }
        });
        btSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(OtpSendActivity.this, OwnersActivity.class);
                startActivity(intent);
                finish(); // Close the current activity after logout
            }
        });
    }

    private void sendOTP(String phoneNumber) {
        // Disable UI elements
        etPhone.setEnabled(false);
        btnSend.setEnabled(false);
        countryCodePicker.setEnabled(false);
       // FirebaseUser firebase.auth().settings.appVerificationDisabledForTesting = true;
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // Auto-retrieval or instant verification is successful.
                        // Proceed with verifying the credential directly
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        etPhone.setEnabled(true);
                        btnSend.setEnabled(true);
                        countryCodePicker.setEnabled(true);

                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OtpSendActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        // Save the verification ID and token for later use
                        // Call the OTP verification activity
                        Intent intent = new Intent(OtpSendActivity.this, OtpVerifyActivity.class);
                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                    }
                };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}

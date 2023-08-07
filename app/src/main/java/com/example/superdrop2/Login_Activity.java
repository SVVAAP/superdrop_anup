package com.example.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.superdrop2.navigation.NavActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.DOMConfiguration;

import java.util.concurrent.TimeUnit;

public class Login_Activity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    EditText lgusername;
    EditText lgpassword;
    ProgressBar progressBar;
    FirebaseAuth auth;
    Button btlogin;
    int count = 0;
    Button btn_signin,btn_signup;
    // variable for FirebaseAuth class
    private FirebaseAuth mAuth;

    // variable for our text input
// field for phone and OTP.
    private EditText edtPhone, edtOTP;

    // buttons for generating OTP and verifying OTP
    private Button verifyOTPBtn, generateOTPBtn;

    // string for storing our verification ID
    private String verificationId;

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentuser= auth.getCurrentUser();
//        if(currentuser!=null) {
//            startActivity(new Intent(Login_Activity.this, NavActivity.class));
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        imageView = findViewById(R.id.imageView);
//        textView = findViewById(R.id.textView);
//       btn_signin= findViewById(R.id.sign_in_bt);
  //      btn_signup=findViewById(R.id.sign_up_bt);

        imageView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
//                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
//                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeLeft() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
//                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
//                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeBottom() {
            }

        });
        // below line is for getting instance of our FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();

// initializing variables for button and Edittext.
        edtPhone = findViewById(R.id.idEdtPhoneNumber);
        edtOTP = findViewById(R.id.idEdtOtp);
        verifyOTPBtn = findViewById(R.id.idBtnVerify);
        generateOTPBtn = findViewById(R.id.idBtnGetOtp);

// setting onclick listener for generate OTP button.
        generateOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// below line is for checking whether the user has entered his mobile number or not.
                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
// when mobile number text field is empty displaying a toast message.
                    Toast.makeText(Login_Activity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
// if the text field is not empty we are calling our send OTP method for getting OTP from Firebase.
                    String phone = "+91" + edtPhone.getText().toString();
                    sendVerificationCode(phone);
                }
            }
        });

// initializing on click listener for verify otp button
        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// validating if the OTP text field is empty or not.
                if (TextUtils.isEmpty(edtOTP.getText().toString())) {
// if the OTP text field is empty display a message to user to enter OTP
                    Toast.makeText(Login_Activity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
// if OTP field is not empty calling method to verify the OTP.
                    verifyCode(edtOTP.getText().toString());
                }
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
// inside this method we are checking if the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
// if the code is correct and the task is successful we are sending our user to new activity.
                            Intent i = new Intent(Login_Activity.this, Detail_Activity.class);
                            startActivity(i);
                            finish();
                        } else {
// if the code is not correct then we are displaying an error message to the user.
                            Toast.makeText(Login_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void sendVerificationCode(String number) {
// this method is used for getting OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity (for callback binding)
                        .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

// initializing our callbacks for on verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
// when we receive the OTP it contains a unique id which we are storing in our string which we have already created.
            verificationId = s;
        }

        // this method is called when user receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
// below line is used for getting OTP code which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

// checking if the code is null or not.
            if (code != null) {
// if the code is not null then we are setting that code to our OTP edittext field.
                edtOTP.setText(code);

// after setting this code to OTP edittext field we are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
// displaying error message with firebase exception.
            Toast.makeText(Login_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
// below line is used for getting credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

// after getting credential we are calling sign in method.
        signInWithCredential(credential);


//        progressBar = findViewById(R.id.loading);
//        lgusername = findViewById(R.id.idEdtPhoneNumber);
//        lgpassword = findViewById(R.id.idEdtOtp);
//        auth = FirebaseAuth.getInstance();
//        btlogin= findViewById(R.id.idBtnGetOtp);
//        Button skip =findViewById(R.id.idBtnVerify);
//
//        btlogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String email= lgusername.getText().toString();
//                String password= lgpassword.getText().toString();
//
//                if(TextUtils.isEmpty(email)){
//                    lgusername.setError("Email is blank :(");
//                    lgusername.requestFocus();
//                }
//                else if(TextUtils.isEmpty(password)){
//                    lgpassword.setError("Email is blank :(");
//                    lgpassword.requestFocus();
//                }
//
//                else{
//                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(task.isSuccessful()){
//                                Toast.makeText(Login_Activity.this,"you are loged in :)",Toast.LENGTH_LONG).show();
//                                startActivity(new Intent(Login_Activity.this, Detail_Activity.class));
//                                finish();
//
//                            }else {
//                                Toast.makeText(Login_Activity.this, "Error... :(", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                }
//
//
//            }
//        });
//        skip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Login_Activity.this, NavActivity.class));
//                finish();
//            }
//        });
    }
}

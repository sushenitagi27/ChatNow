package com.example.chatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    EditText mgetphonenumber;
    android.widget.Button msendotp;
    CountryCodePicker mcoutrycodepicker;
    String countrycode;
    String phonenumber;

    FirebaseAuth firebaseAuth;
    ProgressBar mprogressbarofmain;


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;
    String codesent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcoutrycodepicker = findViewById(R.id.countrycodepicker);
        msendotp = findViewById(R.id.sendotpbutton);
        mgetphonenumber = findViewById(R.id.getphno);
        mprogressbarofmain = findViewById(R.id.progressbarinfo);

        firebaseAuth = FirebaseAuth.getInstance();

        countrycode = mcoutrycodepicker.getSelectedCountryCodeWithPlus();

        mcoutrycodepicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode = mcoutrycodepicker.getSelectedCountryCodeWithPlus();
            }
        });
        msendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                number = mgetphonenumber.getText().toString();
                if(number.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter your phone number ", Toast.LENGTH_SHORT).show();
                }
                else if(number.length()<10)
                {
                    Toast.makeText(MainActivity.this, "Please Enter correct number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mprogressbarofmain.setVisibility(View.VISIBLE);
                    phonenumber = countrycode+number;
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phonenumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(mcallbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                }
            }
        });

        mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //how to get otp
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(MainActivity.this, "Otp is sent", Toast.LENGTH_SHORT).show();

                mprogressbarofmain.setVisibility(View.INVISIBLE);
                codesent = s;

                Intent intent = new Intent(MainActivity.this,Authentication.class);
                intent.putExtra("otp",codesent);
                intent.putExtra("phno",phonenumber);
                startActivity(intent);
            }
        };

    }
    @Override
    protected void onStart()
    {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent = new Intent(MainActivity.this,chat.class);
            intent.putExtra("phno",phonenumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
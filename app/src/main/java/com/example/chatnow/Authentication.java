package com.example.chatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class Authentication extends AppCompatActivity {

    TextView mchangenumber;
    EditText mgetotp;
    android.widget.Button mverify;
    String entereredotp;
    String PhoneNumber;

    FirebaseAuth firebaseAuth;
    ProgressBar mprogressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        mchangenumber = findViewById(R.id.changeno);
        mverify = findViewById(R.id.verifyotp);
        mgetotp = findViewById(R.id.getotp);
        mprogressbar = findViewById(R.id.progressbarinfo_auth);

        firebaseAuth = FirebaseAuth.getInstance();

        mchangenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Authentication.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entereredotp = mgetotp.getText().toString();
                if(entereredotp.isEmpty())
                {
                    Toast.makeText(Authentication.this, "Enter otp", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mprogressbar.setVisibility(View.VISIBLE);
                    String codereceived = getIntent().getStringExtra("otp");
                    PhoneNumber=getIntent().getStringExtra("phno");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codereceived,entereredotp);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });

    }

    private  void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    mprogressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(Authentication.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Authentication.this,SetProfile.class);
                    intent.putExtra("phno",PhoneNumber);

                    startActivity(intent);
                    finish();
                }
                else
                {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        mprogressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(Authentication.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
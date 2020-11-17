package com.example.bankingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LogIn extends AppCompatActivity {
    CountryCodePicker c;
     FirebaseAuth fa;
     FirebaseFirestore fstore;
     EditText e1,e2;
     TextView t1;
     ProgressBar p;
     Button b;
     String vid;
     boolean checkVerify=false;
    PhoneAuthProvider.ForceResendingToken Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        fa=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        e1=findViewById(R.id.text1);
        e2=findViewById(R.id.text2);
        t1=findViewById(R.id.state);
        p=findViewById(R.id.pb);
        b=findViewById(R.id.button);
        c= findViewById(R.id.ccp);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkVerify){
                    if(!e1.getText().toString().isEmpty() && e1.getText().toString().length()==10){
                        String s="+" + c.getSelectedCountryCode() + e1.getText().toString();
                        p.setVisibility(View.VISIBLE);
                        t1.setVisibility(View.VISIBLE);
                        requestFotOP(s);
                    }
                    else
                        e1.setError("Invalid Number");
                }
                else{
                  String s1 = e2.getText().toString();
                  if(!s1.isEmpty() && s1.length()==6){
                     PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vid,s1);
                     verifyUser(credential);
                  }
                  else
                      e2.setError("Invalid OTP Entered");
                }
            }
        });
    }
    @Override
    protected  void onStart(){
        super.onStart();
        if(fa.getCurrentUser()!= null){
            p.setVisibility(View.VISIBLE);
            t1.setText("Checking...");
            t1.setVisibility(View.VISIBLE);
            checkingUser();
        }
    }
    private void verifyUser(PhoneAuthCredential credential) {
        fa.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkingUser();
                }
                else{
                    Toast.makeText(LogIn.this ,"Authentication Failed " ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkingUser() {
        DocumentReference drs=fstore.collection("users").document(fa.getCurrentUser().getUid());
        drs.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(getApplicationContext(),EnterInformation.class));
                    finish();
                }
            }
        });
    }

    private void requestFotOP(String s) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(s, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                p.setVisibility(View.INVISIBLE);
                t1.setVisibility(View.GONE);
                e2.setVisibility(View.VISIBLE);
                b.setText("Verify OTP");
                vid =s;
                Token= forceResendingToken;
                checkVerify=true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(LogIn.this ,"Time expired " ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                 verifyUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LogIn.this ,"Can not create OTP " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.bankingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import  java.util.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EnterInformation extends AppCompatActivity {
    EditText e1,e2,e3;
    Button b1;
    FirebaseAuth fa;
    FirebaseFirestore fstore;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_information);
        e1=findViewById(R.id.firstName);
        e2=findViewById(R.id.lastName);
        e3=findViewById(R.id.emailAddress);
        b1=findViewById(R.id.button1);
        fa=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        userId=fa.getCurrentUser().getUid();

        final DocumentReference drs= fstore.collection("users").document(userId);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!e1.getText().toString().isEmpty() && !e2.getText().toString().isEmpty() && !e3.getText().toString().isEmpty() ){
                      String first=e1.getText().toString();
                    String last=e2.getText().toString();
                    String email=e3.getText().toString();

                    Map<String,Object> user=new HashMap<>();

                   user.put("fname",first);
                   user.put("lname",last);
                   user.put("emailId",email);

                   drs.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               startActivity(new Intent(getApplicationContext(),MainActivity.class));
                               finish();
                           }
                           else{
                               Toast.makeText(EnterInformation.this ,"Data is not Inserted " ,Toast.LENGTH_SHORT).show();
                               return;
                           }
                       }
                   });

                }
                else{
                    Toast.makeText(EnterInformation.this ,"All fields are required " ,Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
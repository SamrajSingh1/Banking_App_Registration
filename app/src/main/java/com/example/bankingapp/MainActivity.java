package com.example.bankingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fa;
    FirebaseFirestore fstore;
    TextView t1,t2,t3;
    Toolbar toolbr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1=findViewById(R.id.profileFullName);
        t2=findViewById(R.id.profileEmail);
        t3=findViewById(R.id.profilePhone);
        toolbr=findViewById(R.id.toolbar);
        setSupportActionBar(toolbr);
        getSupportActionBar().setTitle("Sangam Pride");
        fa=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        DocumentReference drs=fstore.collection("users").document(fa.getCurrentUser().getUid());

        drs.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String ss1=documentSnapshot.getString("fname") + " " + documentSnapshot.getString("lname");
                    t1.setText(ss1);
                    t2.setText(documentSnapshot.getString("emailId"));
                    t3.setText(fa.getCurrentUser().getPhoneNumber());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf= getMenuInflater();
        inf.inflate(R.menu.logout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),LogIn.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
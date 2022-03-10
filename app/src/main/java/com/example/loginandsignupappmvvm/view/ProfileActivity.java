package com.example.loginandsignupappmvvm.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginandsignupappmvvm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    TextView Name, Email, Number, Created_at;
    DatabaseReference ref;
    String name, email, number, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        Number = findViewById(R.id.number);
        Created_at = findViewById(R.id.created_at);
        readSharedPreferences();

        try {

            if (mAuth.getUid() != null)
                ref.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        name = dataSnapshot.child("name").getValue(String.class);
                        email = dataSnapshot.child("email").getValue(String.class);
                        number = dataSnapshot.child("number").getValue(String.class);
                        date = dataSnapshot.child("date").getValue(String.class);

                        Name.setText("Name : " + name);
                        Email.setText("Email : " + email);
                        Number.setText("Number : " + number);
                        Created_at.setText("Created at : " + date);
                        saveSharedPreferences(name, email, number, date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(ProfileActivity.this, "Error " + databaseError, Toast.LENGTH_SHORT).show();
                    }

                });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveSharedPreferences(String name, String email, String number, String date) {
        SharedPreferences sharedPref = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("number", number);
        editor.putString("date", date);
        editor.apply();
    }

    public void readSharedPreferences() {

        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        String name = prefs.getString("name", null);//"No name defined" is the default value.
        String email = prefs.getString("email", null);
        String number = prefs.getString("number", null);
        String date = prefs.getString("date", null);
        Name.setText("Name : " + name);
        Email.setText("Email : " + email);
        Number.setText("Number : " + number);
        Created_at.setText("Created at : " + date);
    }


}
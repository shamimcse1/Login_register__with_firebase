package com.example.loginandsignupappmvvm.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginandsignupappmvvm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private EditText name, number, email, password;
    private Button register;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String UserName, UserEmail, UserNumber, UserPassword;
    private TextView login_text;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading.......");
        dialog.setCancelable(false);

    }

    public void init() {

        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login_text= findViewById(R.id.login_text);

        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserName = name.getText().toString();
                UserEmail = email.getText().toString();
                Log.d("Tag",UserEmail);
                UserNumber = number.getText().toString();
                UserPassword = password.getText().toString();

                if (UserName.isEmpty()) {
                    name.setError("Please Enter Name");
                    name.requestFocus();
                    return;
                } else if (UserEmail.isEmpty()) {
                    email.setError("Please Enter Email");
                    email.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
                    email.setError("Please Enter Valid Email");
                    email.requestFocus();
                    return;
                } else if (UserNumber.isEmpty()) {
                    number.setError("Please Enter Number");
                    number.requestFocus();
                    return;
                } else if (UserPassword.isEmpty()) {
                    password.setError("Please Enter Password");
                    password.requestFocus();
                    return;
                } else if (UserPassword.length() < 8) {
                    password.setError("Password Length must be greater then 8 Character");
                    password.requestFocus();
                    return;
                } else {
                    CreateUser();
                }
            }
        });


    }

    private void CreateUser() {
        dialog.show();
        auth.createUserWithEmailAndPassword(UserEmail, UserPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UpdateUI();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Sign Up Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(RegisterActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void UpdateUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate_andTime = sdf.format(new Date());

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", UserName);
        map.put("email", UserEmail);
        map.put("number", UserNumber);
        map.put("date", currentDate_andTime);

        reference.child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    dialog.dismiss();
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Database Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Database Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
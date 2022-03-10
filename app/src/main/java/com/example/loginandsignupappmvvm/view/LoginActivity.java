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
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private TextView register_text;
    private String UserEmail, UserPassword;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        init();

        if (user != null) {
            String userId = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }


    }

    private void init() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading.......");
        dialog.setCancelable(false);

        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        login = findViewById(R.id.Login);
        register_text = findViewById(R.id.register_text);

        register_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserEmail = email.getText().toString();
                UserPassword = password.getText().toString();

                if (UserEmail.isEmpty()) {
                    email.setError("Please Enter Email");
                    email.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
                    email.setError("Please Enter Valid Email");
                    email.requestFocus();
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
                    Login();
                }

            }
        });
    }

    private void Login() {
        dialog.show();
        auth.signInWithEmailAndPassword(UserEmail, UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Sing In Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "Sing In Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.photoportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private TextView textView_signup;
    private Button btn_login;
    private EditText txt_email, txt_password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView_signup = (TextView) findViewById(R.id.login_tv_signup);
        btn_login = (Button) findViewById(R.id.login_btn_login);
        txt_email = (EditText) findViewById(R.id.login_txt_email);
        txt_password = (EditText) findViewById(R.id.login_txt_password);
        mAuth = FirebaseAuth.getInstance();

        textView_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate to signup screen
                startActivity(new Intent(Login.this, Logup.class));
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txt_email.getText().toString(),
                        password = txt_password.getText().toString();

                if(email.isEmpty()  || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fill all your data first", Toast.LENGTH_SHORT).show();

                }else{
                    mAuth.signInWithEmailAndPassword(email, password)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                        startActivity(new Intent(Login.this, MainActivity.class));

                                    } else {
                                        Toast.makeText(Login.this, task.getException().toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                        });

                }
            }
        });





    }

}
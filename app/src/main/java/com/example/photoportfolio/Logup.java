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

public class Logup extends AppCompatActivity {

    TextView tv_login;
    private Button btn_logup;
    private EditText txt_email, txt_password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logup);
        tv_login = (TextView) findViewById(R.id.signup_tv_login);
        btn_logup = (Button) findViewById(R.id.logup_btn_logup);
        txt_email = (EditText) findViewById(R.id.logup_txt_email);
        txt_password = (EditText) findViewById(R.id.logup_txt_password);
        mAuth = FirebaseAuth.getInstance();

        btn_logup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txt_email.getText().toString(),
                        password = txt_password.getText().toString();

                if(email.isEmpty()  || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fill all your data first", Toast.LENGTH_SHORT).show();

                }else if(!email.contains("@")){
                    Toast.makeText(getApplicationContext(), "Email is incorrect", Toast.LENGTH_SHORT).show();
                }else if(password.length() < 8){
                    Toast.makeText(getApplicationContext(), "Password should 8 and more characters", Toast.LENGTH_SHORT).show();
                }else if(!isValidPassword(password)){
                    Toast.makeText(getApplicationContext(), "Password is not valid. Please ensure it contains at least one uppercase letter, one lowercase letter, and one special character.", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                        startActivity(new Intent(Logup.this, Login.class));
                                    } else {
                                        Toast.makeText(Logup.this, task.getException().toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Logup.this, Login.class));
            }
        });
    }

    private boolean isValidPassword(String password) {
        // Check for at least one uppercase letter
        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        Matcher upperCaseMatcher = upperCasePattern.matcher(password);

        // Check for at least one lowercase letter
        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        Matcher lowerCaseMatcher = lowerCasePattern.matcher(password);

        // Check for at least one special character
        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);

        // Check if all three conditions are met
        return upperCaseMatcher.find() && lowerCaseMatcher.find() && specialCharMatcher.find();
    }
}
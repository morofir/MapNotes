package com.example.moveonotes.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.moveonotes.R;
import com.example.moveonotes.databinding.ActivityRegisterBinding;
import com.example.moveonotes.viewmodel.RegisterViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RegisterViewModel registerViewModel;
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        auth = FirebaseAuth.getInstance();

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        //observers
        registerViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
                    sendToMain();
                }
            }
        });

        registerViewModel.getRegToastObserver().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            binding.progressbarRegister.setVisibility(View.GONE);
        });



        binding.registerCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.registerPassEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    binding.confirmPassEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    binding.registerPassEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.confirmPassEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });

        //if Enter pressed will login (in confirm password only)
        binding.confirmPassEt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    binding.registerBtn.performClick();
                    return true;
                }
                return false;
            }
        });
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                String email = binding.registerEmailEt.getText().toString();
                String pass = binding.registerPassEt.getText().toString();
                String confirm = binding.confirmPassEt.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm)) {
                    if (pass.equals(confirm)) {
                        registerViewModel.register(email, pass, confirm); //will create user node if adding new note
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Please Fill all fields", Toast.LENGTH_SHORT).show();
            }


        });

        binding.signupToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            sendToMain();
        }
    }}
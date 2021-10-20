package com.example.moveonotes.views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.moveonotes.R;
import com.example.moveonotes.databinding.ActivityLoginBinding;
import com.example.moveonotes.viewmodel.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);


        //observers
        loginViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    sendToMain();
                }

            }
        });

        loginViewModel.getToastObserver().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            binding.progressbarLogin.setVisibility(View.GONE);
        });



        binding.showpassCb.setChecked(false);
        binding.loginPassEt.setTransformationMethod(PasswordTransformationMethod.getInstance()); //first will show password as *** (UI)

        binding.showpassCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.loginPassEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

            } else {
                binding.loginPassEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        binding.forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //if Enter pressed will login
        binding.loginPassEt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    binding.loginBtn.performClick();
                    return true;
                }
                return false;
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                binding.progressbarLogin.setVisibility(View.VISIBLE);
                String email = binding.loginEmailEt.getText().toString();
                String pass = binding.loginPassEt.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                    loginViewModel.loginUser(email, pass);
                }else {
                    Toast.makeText(getApplicationContext(), "Please set all Fields!", Toast.LENGTH_SHORT).show();
                    binding.progressbarLogin.setVisibility(View.GONE);
                }

            }
        });
    }



    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
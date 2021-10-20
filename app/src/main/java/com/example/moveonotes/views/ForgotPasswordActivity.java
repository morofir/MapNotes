package com.example.moveonotes.views;


import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.moveonotes.R;
import com.example.moveonotes.databinding.ActivityForgotPasswordBinding;
import com.example.moveonotes.viewmodel.ForgetPassViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity  {

    ActivityForgotPasswordBinding binding;
    private ForgetPassViewModel forgotPassViewModel;
    FirebaseAuth auth;
    Boolean emailSent = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        forgotPassViewModel = new ViewModelProvider(this).get(ForgetPassViewModel.class);


        auth = FirebaseAuth.getInstance();
        binding.btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEt.getText().toString().trim();
                //UI
                if(email.isEmpty()){
                    binding.emailEt.setText("");
                    Toast.makeText(getApplicationContext(),"Email Required",Toast.LENGTH_LONG).show();
                    binding.emailEt.requestFocus();
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.emailEt.setText("");
                    Toast.makeText(getApplicationContext(),"Not a Valid Email",Toast.LENGTH_LONG).show();
                    binding.emailEt.requestFocus();
                }
                binding.progressBarPass.setVisibility(View.VISIBLE);

                //viewmodel

                forgotPassViewModel.resetPass(email);

                if(emailSent)
                {
                    Toast.makeText(getApplicationContext(),"Email sent!",Toast.LENGTH_LONG).show();
                    binding.emailEt.setText("");
                }else{
                    Toast.makeText(getApplicationContext(),"No such User",Toast.LENGTH_LONG).show();
                }
                binding.progressBarPass.setVisibility(View.GONE);


            }
        });

    }


}
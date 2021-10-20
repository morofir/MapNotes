package com.example.moveonotes.views;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.moveonotes.R;
import com.example.moveonotes.databinding.ActivityForgotPasswordBinding;
import com.example.moveonotes.viewmodel.ForgetPassViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetPasswordActivity extends AppCompatActivity  {

    ActivityForgotPasswordBinding binding;
    private ForgetPassViewModel forgotPassViewModel;
    FirebaseAuth auth;
    MutableLiveData<Boolean> emailSent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        forgotPassViewModel = new ViewModelProvider(this).get(ForgetPassViewModel.class);


        auth = FirebaseAuth.getInstance();


        forgotPassViewModel.getSent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean sent) {
                if(sent){
                    Toast.makeText(getApplicationContext(),"Email sent!",Toast.LENGTH_LONG).show();
                    binding.emailEt.setText("");

                } else {
                    Toast.makeText(getApplicationContext(), "No such User", Toast.LENGTH_LONG).show();
                }


            }
        });


        binding.btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEt.getText().toString().trim();
                Log.e("sdf",email);
                binding.progressBarPass.setVisibility(View.VISIBLE);
                //UI
                if(email.isEmpty()){
                    binding.emailEt.setText("");
                    Toast.makeText(getApplicationContext(),"Email Required",Toast.LENGTH_LONG).show();
                    binding.emailEt.requestFocus();
                    binding.progressBarPass.setVisibility(View.GONE);
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.emailEt.setText("");
                    Toast.makeText(getApplicationContext(),"Not a Valid Email",Toast.LENGTH_LONG).show();
                    binding.emailEt.requestFocus();
                    binding.progressBarPass.setVisibility(View.GONE);
                }
                //viewmodel

                else{
                    forgotPassViewModel.resetPass(email);
                }
            }
        });

    }


}
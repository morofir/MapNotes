package com.example.moveonotes.model;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginAppRepository {
    private Application application;
    private FirebaseAuth firebaseAuth;
    private MutableLiveData<FirebaseUser> userMutableLiveData;

    public LoginAppRepository() {

    }


    public MutableLiveData<Boolean> getLoggedOutuserMutableLiveData() {
        return loggedOutuserMutableLiveData;
    }

    private MutableLiveData<Boolean> loggedOutuserMutableLiveData;


    //here lays all the data, firebase ....
    //should know nothing about view and how it presents info to user
    //apiService in model also (if there is)

    public LoginAppRepository(Application application){
        this.application = application;
        firebaseAuth = firebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
        loggedOutuserMutableLiveData = new MutableLiveData<>();

        if(firebaseAuth.getCurrentUser() != null){
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutuserMutableLiveData.postValue(false);
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String password,String confirmPassword) {

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
            if (password.equals(confirmPassword)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    userMutableLiveData.postValue(firebaseAuth.getCurrentUser());

                                } else {
                                    Toast.makeText(application, "registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else
                Toast.makeText(application, "Passwords don't match", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(application, "Please Fill all fields", Toast.LENGTH_SHORT).show();


    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void loginUser(String email,String password) {

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>() { //get message is enum
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());

                    } else {
                        Toast.makeText(application, "login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(application, "Invalid Email/Password", Toast.LENGTH_SHORT).show();
        }
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void logOut(){
        firebaseAuth.signOut();
        loggedOutuserMutableLiveData.setValue(true);
    }
}
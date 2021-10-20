package com.example.moveonotes.model;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginRegisterAppRepository {
    private Application application;
    private FirebaseAuth firebaseAuth;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<Boolean> loggedOutuserMutableLiveData;
    private MutableLiveData<String> toastMsgLoginObserver = new MutableLiveData();
    private MutableLiveData<String> toastMsgRegisterObserver = new MutableLiveData();


    //here lays all the data, firebase ....
    //should know nothing about view and how it presents info to user
    //apiService in model also (if there is)

    public LoginRegisterAppRepository(Application application) {
        this.application = application;
        firebaseAuth = firebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
        loggedOutuserMutableLiveData = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null) {
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutuserMutableLiveData.postValue(false);
        }


    }

    public MutableLiveData<Boolean> getLoggedOutuserMutableLiveData() {
        return loggedOutuserMutableLiveData;
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String password, String confirmPassword) {


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                        }
                        toastMsgRegisterObserver.postValue("registration failed: " + task.getException().getMessage()); //post values async

                    }
                });
    }



    @RequiresApi(api = Build.VERSION_CODES.P)
    public void loginUser(String email,String password) {

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(),
                    new OnCompleteListener<AuthResult>() { //get message is enum
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                    }else{
                        toastMsgLoginObserver.postValue("ERROR: " + task.getException().getMessage());
                    }
                }
            });
        }


    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void logOut(){
        firebaseAuth.signOut();
        loggedOutuserMutableLiveData.setValue(true);
    }

    public MutableLiveData<String> getMsg() {
        return toastMsgLoginObserver;
    }
    public MutableLiveData<String> getMsgRegister() {
        return toastMsgRegisterObserver;
    }

}
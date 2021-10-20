package com.example.moveonotes.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moveonotes.model.LoginRegisterAppRepository;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {
    private LoginRegisterAppRepository loginAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<String> toastMessageObserver = new MutableLiveData();




/*Google suggests that you use 1 ViewModel per View (i.e., Activity or Fragment)
 (see https://youtu.be/Ts-uxYiBEQ8?t=8m40s)*/

    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginAppRepository = new LoginRegisterAppRepository(application);
        userMutableLiveData = loginAppRepository.getUserMutableLiveData();
        toastMessageObserver = loginAppRepository.getMsg();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void loginUser(String email, String password){
        loginAppRepository.loginUser(email,password);
    }
    public LiveData<String> getToastObserver(){
        return toastMessageObserver;
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
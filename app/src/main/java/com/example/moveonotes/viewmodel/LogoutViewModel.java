package com.example.moveonotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moveonotes.model.LoginRegisterAppRepository;
import com.google.firebase.auth.FirebaseUser;

public class LogoutViewModel extends AndroidViewModel {
    private LoginRegisterAppRepository loginAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<Boolean> LoggedOutLiveData;


    public void logOut(){
        loginAppRepository.logOut();
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void setUserMutableLiveData(MutableLiveData<FirebaseUser> userMutableLiveData) {
        this.userMutableLiveData = userMutableLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutLiveData() {
        return LoggedOutLiveData;
    }

    public void setLoggedOutLiveData(MutableLiveData<Boolean> loggedOutLiveData) {
        LoggedOutLiveData = loggedOutLiveData;
    }

    public LogoutViewModel(@NonNull Application application) {
        super(application);
        loginAppRepository = new LoginRegisterAppRepository(application);
        userMutableLiveData = loginAppRepository.getUserMutableLiveData();
        LoggedOutLiveData = loginAppRepository.getLoggedOutuserMutableLiveData();
    }


}

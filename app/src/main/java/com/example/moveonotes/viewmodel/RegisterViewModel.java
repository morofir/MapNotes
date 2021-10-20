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

public class RegisterViewModel extends AndroidViewModel {
    private LoginRegisterAppRepository loginRegisterAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<String> toastMsgRegisterObserver = new MutableLiveData();


/*Google suggests that you use 1 ViewModel per View (i.e., Activity or Fragment)
 (see https://youtu.be/Ts-uxYiBEQ8?t=8m40s)*/

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        loginRegisterAppRepository = new LoginRegisterAppRepository(application);
        userMutableLiveData = loginRegisterAppRepository.getUserMutableLiveData();
        toastMsgRegisterObserver = loginRegisterAppRepository.getMsg();


    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String password,String confirmPassword){
        loginRegisterAppRepository.register(email,password,confirmPassword);
        toastMsgRegisterObserver = loginRegisterAppRepository.getMsgRegister();

    }
    public LiveData<String> getRegToastObserver(){
        return toastMsgRegisterObserver;
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
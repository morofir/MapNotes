package com.example.moveonotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moveonotes.model.ForgetPassRepository;

public class ForgetPassViewModel extends AndroidViewModel {
    ForgetPassRepository forgetPassRepository;
    private MutableLiveData<Boolean> sent;


    public ForgetPassViewModel(@NonNull Application application) {
        super(application);
        forgetPassRepository = new ForgetPassRepository(application);
        sent = forgetPassRepository.getSent();
    }

    public void resetPass(String email){
        forgetPassRepository.resetPass(email);
    }


    public MutableLiveData<Boolean> getSent() {
        return sent;
    }
}

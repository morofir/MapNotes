package com.example.moveonotes.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassRepository {
    private FirebaseAuth auth;
    private MutableLiveData<Boolean> sent;



    public ForgetPassRepository(Application application) {
        auth = FirebaseAuth.getInstance();
        sent = new MutableLiveData<>();
    }

    public void resetPass(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    sent.postValue(true);
                else
                    sent.postValue(false);
            }
        });


    }

    public MutableLiveData<Boolean> getSent() {
        return sent;
    }
}

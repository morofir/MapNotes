package com.example.moveonotes.viewmodel;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moveonotes.Services.getServer;
import com.example.moveonotes.model.DisplayNoteRepository;
import com.example.moveonotes.model.NoteObject;

public class DisplayNoteViewModel extends AndroidViewModel {
    private DisplayNoteRepository displayNoteRepository;
    public MutableLiveData<NoteObject> noteObjectMutableLiveData;
    public MutableLiveData<Bundle> bundleMutableLiveData;



    public DisplayNoteViewModel(@NonNull Application application) {
        super(application);
        displayNoteRepository = new DisplayNoteRepository(application);
        noteObjectMutableLiveData = new MutableLiveData<>();
    }



    public LiveData<NoteObject> getNoteObjectLiveData(String title,String photo,String body,String time,String date) {
        NoteObject note = new NoteObject(title,photo,body,time,date);

        noteObjectMutableLiveData.postValue(note); //setting note to live data
        return noteObjectMutableLiveData;
    }

    public void deleteNote(Bundle bundle,String uid) {
        String time = bundle.getString("time");
        String[] splitted = time.split("\\s"); //the second cell is the time xx:xx:xx
        String apiUrl = getServer.getConfigValue(getApplication(),"server_url");

        displayNoteRepository.deleteNote(splitted[0],uid,apiUrl);


    }

    public void updateNote(Bundle bundle,String title,String text, String uid) {
        String time = bundle.getString("time");
        String[] splitted = time.split("\\s"); //the second cell is the time xx:xx:xx

        String apiUrl = getServer.getConfigValue(getApplication(),"server_url");

        if(!text.isEmpty() && !title.isEmpty()) {
            displayNoteRepository.updateNote(splitted[0], text, title, uid, apiUrl);
        }
    }



}

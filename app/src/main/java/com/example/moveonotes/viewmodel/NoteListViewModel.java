package com.example.moveonotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moveonotes.model.NoteListRepository;
import com.example.moveonotes.model.NoteObject;

import java.util.List;

public class NoteListViewModel extends AndroidViewModel {
    private MutableLiveData<List<NoteObject>> NoteList; //live data cant be directly changed
    private NoteListRepository repository;

    public NoteListViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteListRepository();
        NoteList = repository.getNotesList();
    }


    public void getNotesList(){
        if (NoteList != null){
            return;
        }
        repository = NoteListRepository.getInstance();
        NoteList = repository.getNotesList(); //retrieve list from repo
    }

    public LiveData<List<NoteObject>> getNoteList(){ //mutable live data is sub class of live data
        return NoteList;
    }
}

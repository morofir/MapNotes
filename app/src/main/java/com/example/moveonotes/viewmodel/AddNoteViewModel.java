package com.example.moveonotes.viewmodel;


import android.app.Application;
import android.content.Intent;
import android.net.Uri;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.example.moveonotes.Services.getServer;
import com.example.moveonotes.model.AddNoteRepository;
import com.example.moveonotes.model.NoteObject;

import java.io.File;


public class AddNoteViewModel extends AndroidViewModel {

    private AddNoteRepository repository;



    private MutableLiveData<Boolean> isPhoto = new MutableLiveData();

    public AddNoteViewModel(@NonNull Application application) {
        super(application);
        repository = new AddNoteRepository();
    }


    public void saveNote(String title, String body, String date, String time, String lat, String lon, String photoPath) {
        //manipulate the strings needed
        String noteDate = date.substring(0, 10); //date format xxxx-xx-xx
        String noteTime = time.substring(11, 19); //time format xx:xx:xx

        NoteObject noteObject = new NoteObject(title, body, noteDate, noteTime, lat, lon, photoPath); //creating note object

        String apiUrl = getServer.getConfigValue(getApplication().getApplicationContext(), "server_url");
        repository.uploadNote(noteObject, apiUrl);


    }

    public String onGalleryResult(Intent data, Uri selectedImageUri) {
        if (data != null) {
            selectedImageUri = data.getData();
            getApplication().getContentResolver().takePersistableUriPermission(selectedImageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            isPhoto.postValue(true);
        }
        return selectedImageUri.toString();
    }



    public MutableLiveData<Boolean> getIsPhoto() {
        return isPhoto;
    }

    public String onCameraResult(File noteFile) {
        return noteFile.getAbsolutePath();
    }
}

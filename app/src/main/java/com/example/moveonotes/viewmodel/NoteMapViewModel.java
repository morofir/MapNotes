package com.example.moveonotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moveonotes.Services.getServer;
import com.example.moveonotes.model.NoteMapRepository;
import com.example.moveonotes.model.NoteObject;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NoteMapViewModel extends AndroidViewModel {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private NoteMapRepository noteMapRepository;
    private MutableLiveData<List<NoteObject>> noteMapList;

    public MutableLiveData<List<MarkerOptions>> getMutableMarkerLiveData() {
        return mutableMarkerLiveData;
    }

    private MutableLiveData<List<MarkerOptions>> mutableMarkerLiveData;
    private FirebaseDatabase database;
    private ArrayList<MarkerOptions> markersData;
    private MutableLiveData<List<LatLng>> latLng;


    public NoteMapViewModel(@NonNull Application application) {
        super(application);
        noteMapRepository = new NoteMapRepository();
        mutableMarkerLiveData = new MutableLiveData<>();
        String apiUrl = getServer.getConfigValue(application, "server_url");
        database = FirebaseDatabase.getInstance(apiUrl);// europe server require link
    }

    public void initNotes() {
        //viewmodel
        noteMapList = noteMapRepository.getNoteMapList(database, user.getUid());
    }

    public LiveData<List<NoteObject>> getNoteList() { //mutable live data is sub class of live data
        return noteMapList;
    }

    public ArrayList<MarkerOptions> setMarkers(List<NoteObject> list) {
        markersData = new ArrayList<>();
        for (NoteObject note : list) { //note list we got from viewmodel
            Double lat = Double.parseDouble(note.getLatitude());
            Double lon = Double.parseDouble(note.getLongitude());

            //generate double number between 0 and 1, wont affect much, except remove duplicates markers
            MarkerOptions marker = new MarkerOptions().position(new LatLng(lat+Math.random()*0.001, lon+Math.random()*0.001));

            marker.title(note.getTitle());
            marker.snippet(note.getCurrentTime()+" "+note.getCurrentDate()+" "+note.getPhoto()+" "+note.getTextBody());
            markersData.add(marker);
        }
        return markersData;
    }


    public MutableLiveData<List<LatLng>> getLiveMarker() {
        latLng = new MutableLiveData<>();
        List<LatLng> list = new ArrayList<>();


        for(MarkerOptions mark: markersData){ //list of markers
            list.add(mark.getPosition());
        }
        latLng.postValue(list);
        return latLng;
    }
}

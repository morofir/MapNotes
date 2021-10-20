package com.example.moveonotes.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoteMapRepository {
    private static NoteMapRepository instance;
    private ArrayList<NoteObject> dataSet = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference;
    MutableLiveData<List<NoteObject>> data = new MutableLiveData<>();


    //singleton pattern
    public static NoteMapRepository getInstance(){

        if (instance == null){
            instance = new NoteMapRepository();
        }
        return instance;
    }

    public MutableLiveData<List<NoteObject>> getNoteMapList(FirebaseDatabase database,String uid){
        setNotesList(database,uid); //get data from firebase
        data.setValue(dataSet); //adding to mutable live data
        return data;
    }
    private void setNotesList(FirebaseDatabase database,String uid){
        //adding to data base from server (firebase)
        //repo this function loads note per user connected
        databaseReference = database.getReference("notes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.child(uid).getChildren()) {
                    //fetch every user
                    String title = (String) dataSnapshot.child("title").getValue();
                    String body = (String) dataSnapshot.child("textBody").getValue();
                    String time = (String) dataSnapshot.child("currentTime").getValue();
                    String date = (String) dataSnapshot.child("currentDate").getValue();
                    String latitude = (String) dataSnapshot.child("latitude").getValue();
                    String longitude = (String) dataSnapshot.child("longitude").getValue();
                    String photo = (String) dataSnapshot.child("photo").getValue();
                    NoteObject noteObject = new NoteObject(title, body, date, time,latitude,longitude,photo);
                    dataSet.add(noteObject);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

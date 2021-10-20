package com.example.moveonotes.model;

import android.util.Log;

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

public class NoteListRepository {
    private static NoteListRepository instance;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference;
    private ArrayList<NoteObject> dataSet = new ArrayList<>();
    MutableLiveData<List<NoteObject>> data = new MutableLiveData<>();

    //singleton pattern
    public static NoteListRepository getInstance(){

        if (instance == null){
            instance = new NoteListRepository();
        }
        return instance;
    }

    public MutableLiveData<List<NoteObject>> getNotesList(){
        setNotesList(); //get data from firebase
        data.setValue(dataSet);

        return data;
    }
    private void setNotesList(){
        //this function loads notes per user from firebase:
        try {
            String uid = user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://moveonotes-default-rtdb.europe-west1.firebasedatabase.app/");
            // europe server require link

            databaseReference = database.getReference("notes");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.child(uid).getChildren()) { //fetch every user

                        String title = (String) dataSnapshot.child("title").getValue();
                        String body = (String) dataSnapshot.child("textBody").getValue();
                        String time = (String) dataSnapshot.child("currentTime").getValue();
                        String date = (String) dataSnapshot.child("currentDate").getValue();
                        String latitude = (String) dataSnapshot.child("latitude").getValue();
                        String longitude = (String) dataSnapshot.child("longitude").getValue();
                        String photo = (String) dataSnapshot.child("photo").getValue();
                        NoteObject noteObject = new NoteObject(title, body, date, time,latitude,longitude,photo);
                        dataSet.add(noteObject); //adding to list
                    }

                    //from worker thread (not main thread) use postValue()
                    data.postValue(dataSet);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error: ",error.getMessage()); //no notes yet
                }
            });
        }catch (Exception e){
            Log.e("error: ",e.getMessage());
        }

    }
}

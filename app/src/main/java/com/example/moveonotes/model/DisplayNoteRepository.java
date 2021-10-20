package com.example.moveonotes.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DisplayNoteRepository {

    public DisplayNoteRepository(Application application) {

    }

    public static void deleteNote(String noteTime, String uid, String apiUrl) {
        DatabaseReference dR = FirebaseDatabase.getInstance(apiUrl)
                .getReference("notes").child(uid);
        Query query = dR.orderByChild("currentTime").equalTo(noteTime);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    snap.getRef().removeValue(); //remove node from firebase
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error",error.getMessage());
            }
        });

    }


    public void updateNote(String time,String text, String title,String uid, String apiUrl) {
        DatabaseReference dR = FirebaseDatabase.getInstance(apiUrl).getReference("notes").child(uid);
        Query query = dR.orderByChild("currentTime").equalTo(time); //finding the right note by time

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if(!text.isEmpty() && !title.isEmpty()) {
                        snap.getRef().child("textBody").setValue(text); // updating the node in firebase
                        snap.getRef().child("title").setValue(title);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error",error.getMessage());
            }
        });
    }


}

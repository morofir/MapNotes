package com.example.moveonotes.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNoteRepository {


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public void uploadNote(NoteObject note,String apiUrl) {

        FirebaseDatabase database = FirebaseDatabase.getInstance(apiUrl); //europe server require link
        DatabaseReference myRef = database.getReference("notes").child(user.getUid());
        myRef.push().setValue(note); //inserting the note object to firebase

    }
}

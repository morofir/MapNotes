package com.example.moveonotes.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.moveonotes.R;
import com.example.moveonotes.databinding.FragmentShowNoteBinding;
import com.example.moveonotes.model.NoteObject;
import com.example.moveonotes.viewmodel.DisplayNoteViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DisplayNoteFragment extends Fragment implements View.OnClickListener {


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DisplayNoteViewModel displayNoteViewModel;
    FragmentShowNoteBinding binding;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShowNoteBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        Bundle bundle = this.getArguments();

        displayNoteViewModel = new ViewModelProvider(this).get(DisplayNoteViewModel.class);

        String title = bundle.getString("title");
        String photo = bundle.getString("photo");
        String body = bundle.getString("body");
        String time = bundle.getString("time");
        String date = bundle.getString("date");



        displayNoteViewModel.getNoteObjectLiveData(title,photo,body,time,date).observe(getViewLifecycleOwner(), new Observer<NoteObject>() {
            @Override
            public void onChanged(NoteObject noteObject) {

                binding.titleitemShownote.setText(noteObject.getTitle());
                binding.bodyShowinput.setText(noteObject.getTextBody());
                binding.timeShownow.setText(noteObject.getCurrentTime());

                if(noteObject.getPhoto()==null || noteObject.getPhoto().equals("")) {
                    binding.imageviewShownote.setVisibility(View.GONE);

                }
                else{
                    Glide.with(getActivity()).load(noteObject.getPhoto()).into(binding.imageviewShownote);
                    binding.imageviewShownote.setVisibility(View.VISIBLE);
                }
                if(noteObject.getCurrentTime() == null)
                    binding.timeShownow.setText("Note Originally created in \n " + noteObject.getCurrentDate());
                else
                    binding.timeShownow.setText("Note Originally created in \n" +
                            noteObject.getCurrentTime() + " " + noteObject.getCurrentDate()); //
            }
        });

        binding.deleteNote.setOnClickListener(this);
        binding.updateNote.setOnClickListener(this);

        return view;

    }


    @Override
    public void onClick(View v) {
        Bundle bundle = this.getArguments();
        switch (v.getId()) {
            case R.id.delete_note:
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Delete").setMessage("Are you sure you want to delete this note?").setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            displayNoteViewModel.deleteNote(bundle,user.getUid());


                            Toast.makeText(getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, new NoteListFragment()).commit();

                        }catch (Exception e){Log.e("e",e.getMessage());}
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing
                    }
                });
                builder.create().show();


                break;


            case R.id.update_note:
                try{
                    String updatedBodyText = binding.bodyShowinput.getText().toString();
                    String updatedTitle = binding.titleitemShownote.getText().toString();
                    if(updatedBodyText.isEmpty() || updatedTitle.isEmpty())
                        Toast.makeText(getContext(), "Must Submit all fields", Toast.LENGTH_LONG).show();
                    else {
                        displayNoteViewModel.updateNote(bundle, updatedTitle, updatedBodyText, user.getUid());

                        Toast.makeText(getContext(), "Note Updated", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction().
                                replace(R.id.frame_layout, new NoteListFragment()).commit();
                    }
                }catch (Exception e){Log.e("e",e.getMessage());}

                break;

        }

    }

}
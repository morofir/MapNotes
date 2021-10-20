package com.example.moveonotes.views;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.moveonotes.R;
import com.example.moveonotes.databinding.NoteListFragmentBinding;
import com.example.moveonotes.model.NoteObject;
import com.example.moveonotes.viewmodel.NoteListViewModel;

import java.util.List;

public class NoteListFragment extends Fragment{

    private NoteListViewModel noteListViewModel;
    NoteAdapter noteAdapter;
    NoteListFragmentBinding binding;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        initObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        noteListViewModel = new ViewModelProvider(this).get(NoteListViewModel.class);
        noteListViewModel.getNotesList();
        binding = NoteListFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        binding.newNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                startActivity(intent);
            }
        });

        initRecyclerView();
        //observe changes
        initObserver();


        setHasOptionsMenu(true);

        return view;
    }

    private void initObserver() {
        noteListViewModel.getNoteList().observe(getViewLifecycleOwner(), new Observer<List<NoteObject>>() {
            @Override
            public void onChanged(List<NoteObject> noteObjects) {
                noteAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

    }

    private void initRecyclerView() {
        noteAdapter = new NoteAdapter(getContext(), noteListViewModel.getNoteList().getValue());

        binding.recylerviewNotes.setAdapter(noteAdapter);
        binding.recylerviewNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recylerviewNotes.setHasFixedSize(true);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.recylerviewNotes.setAdapter(noteAdapter);
        noteAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(noteListViewModel.getNoteList()!=null )
            binding.noNotesYetMsg.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_bar,menu);

        MenuItem item = menu.findItem( R.id.search_bar1);
        SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());


        searchView.setQueryHint("Search Note...");
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        MenuItemCompat.setActionView(item, searchView);
//        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.getFocusable();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    if (noteAdapter != null) {
                        noteAdapter.getFilter().filter(newText);
                    }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }


}

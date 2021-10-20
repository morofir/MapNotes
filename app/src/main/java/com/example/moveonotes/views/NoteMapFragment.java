package com.example.moveonotes.views;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.moveonotes.Services.CheckNetClass;
import com.example.moveonotes.R;
import com.example.moveonotes.databinding.NoteMapFragmentBinding;
import com.example.moveonotes.model.ClusterItem;
import com.example.moveonotes.model.NoteObject;
import com.example.moveonotes.viewmodel.NoteMapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.MarkerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NoteMapFragment extends Fragment{
    private NoteMapViewModel noteMapViewModel;
    NoteMapFragmentBinding binding;
    List<NoteObject> noteList = new ArrayList<>();
    private ClusterManager<ClusterItem> clusterManager;
    MarkerManager.Collection normalMarkersCollection;
    List<MarkerOptions> markers = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteMapViewModel = new ViewModelProvider(this).get(NoteMapViewModel.class);
        if (!CheckNetClass.checknetwork(getContext())) {
            new androidx.appcompat.app.AlertDialog.Builder(getContext()).setTitle("Internet Connection Required")
                    .setMessage("This app may not work correctly without internet connection\n" +
                            "Open the network/Wifi connection").setPositiveButton("OK", null)
                    .show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NoteMapFragmentBinding.inflate(inflater,container,false);
        //initialize view
        View view = binding.getRoot();

        noteMapViewModel.initNotes();
        noteList = noteMapViewModel.getNoteList().getValue(); //getting data from viewmodel
        initObserver();

        return view;
    }

    private void initObserver() {
        noteMapViewModel.getNoteList().observe(getViewLifecycleOwner(), new Observer<List<NoteObject>>() {
            @Override
            public void onChanged(List<NoteObject> noteObjects) {
                updateMap(noteObjects);
                noteList = noteObjects;
            }
        });

    }

    private void updateMap(List<NoteObject> list) {

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        //async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("PotentialBehaviorOverride")
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.clear();
                // Point the map's listeners at the listeners implemented by the cluster manager

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.9117, 35.0516), 6));
                clusterManager = new ClusterManager<>(getContext(), googleMap);

                googleMap.setOnCameraIdleListener(clusterManager);
                googleMap.setOnMarkerClickListener(clusterManager.getMarkerManager());
                normalMarkersCollection = clusterManager.getMarkerManager().newCollection();

                markers = noteMapViewModel.setMarkers(list);
                noteMapViewModel.getLiveMarker().observe(getViewLifecycleOwner(), new Observer<List<LatLng>>() {
                    @Override
                    public void onChanged(List<LatLng> latLngs) {
                        for (int i = 0; i < latLngs.size(); i++) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLngs.get(i));
                            markerOptions.title(markers.get(i).getTitle());
                            markerOptions.snippet(markers.get(i).getSnippet());
                            normalMarkersCollection.addMarker(markerOptions);//adding marker of all notes from firebase by location written in
                            ClusterItem offsetItem = new ClusterItem(markerOptions);  //add items to cluster
                            clusterManager.addItem(offsetItem);
                            Log.e("marker added! size:", String.valueOf(latLngs.size()));
                        }
                    }
                });


                clusterManager.setAnimation(false);

                normalMarkersCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16),2200,null); //zoom in 2 second
                        clusterManager.cluster();
                        clusterManager.clearItems();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    new AlertDialog.Builder(getContext()).setTitle("Note title: " + marker.getTitle())
                                            .setMessage("Press ok to see this note").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //user clicks ok, load the bundle to the new fragment:
                                            dialog.dismiss();
                                            Bundle bundle = new Bundle();

                                            String[] splitted = marker.getSnippet().split("\\s");//separating the snippet to words array

                                            //using String manipulation to get the time and date
                                            //better than save it in hashmap ['body','date','time','photo'] // separated by space
                                            String time = splitted[0]+" "+ splitted[1];
                                            String photo = splitted[2];
                                            String[] grp = Arrays.copyOfRange(splitted, 3, splitted.length);//extracting only the note body
                                            String body = String.join(" ", grp); // concatenating


                                            bundle.putString("title",marker.getTitle());
                                            bundle.putString("body", body);
                                            bundle.putString("time",time);
                                            bundle.putString("photo",photo);


                                            marker.setSnippet(body); //back to body text
                                            DisplayNoteFragment displayNoteFragment = new DisplayNoteFragment();
                                            displayNoteFragment.setArguments(bundle);
                                            requireActivity().getSupportFragmentManager().beginTransaction().
                                                    replace(R.id.frame_layout, displayNoteFragment).addToBackStack(null).commit();
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                                }catch (Exception e){
                                    Log.e("Exception",e.getMessage());
                                }
                            }
                        }, 2700);
                        return true;
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        float zoom = googleMap.getCameraPosition().zoom;

                        if(zoom>5){
                            clusterManager.clearItems();
                            clusterManager.cluster();
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}

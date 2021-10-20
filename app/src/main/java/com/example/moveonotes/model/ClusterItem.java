package com.example.moveonotes.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ClusterItem implements com.google.maps.android.clustering.ClusterItem {
    private MarkerOptions markerOptions;

    public ClusterItem(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return markerOptions.getPosition();
    }

    @Nullable
    @Override
    public String getTitle() {
        return markerOptions.getTitle();
    }

    @Nullable
    @Override
    public String getSnippet() {
        return markerOptions.getSnippet();
    }
}


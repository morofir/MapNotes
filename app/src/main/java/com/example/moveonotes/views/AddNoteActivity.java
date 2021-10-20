package com.example.moveonotes.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.moveonotes.Services.CheckNetClass;
import com.example.moveonotes.R;
import com.example.moveonotes.databinding.ActivityAddNoteBinding;
import com.example.moveonotes.viewmodel.AddNoteViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddNoteActivity extends AppCompatActivity {
    ActivityAddNoteBinding binding;
    private FusedLocationProviderClient  mFusedLocationClient;
    int picCounter;
    final int WRITE_PERMISSION_REQUEST = 1,PICK_FROM_GALLERY = 2,CAMERA_REQUEST = 3,PERMISSION_ID = 44;;
    String PhotoPath;
    Boolean isPhoto = false;
    File noteFile;
    SharedPreferences sharedPreferences;
    Uri imageUri;
    SharedPreferences.Editor editor;
    AddNoteViewModel addNoteViewModel;
    public Uri selectedImageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestPermissions();
        addNoteViewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);


        sharedPreferences = getSharedPreferences("pic_number", MODE_PRIVATE);
        picCounter = sharedPreferences.getInt("pic_number", 0);

        // use the shared preferences and editor as you normally would
        editor = sharedPreferences.edit();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());


        binding.dateNow.setText(currentDate);
        binding.timeNow.setText(currentTime);


        addNoteViewModel.getIsPhoto().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean photo) {
                isPhoto = photo;
            }
        });

        binding.savenoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if !checkPermissions -> show alert
                requestPermissions();
                if (!checkPermissions() || !CheckNetClass.checknetwork(getApplicationContext())) {
                    new AlertDialog.Builder(AddNoteActivity.this).setTitle("Required Location/Internet Permissions")
                            .setMessage("This app may not work correctly without the requested permissions.\n" +
                                    "Open the app setting screen to modify app permission, or Internet connection").setPositiveButton("OK", null)
                            .show();
                    if(!checkPermissions())
                        requestPermissions();


                } else { //got location permission
                    Date currentTime = Calendar.getInstance().getTime();

                    //get data from UI:
                    String noteTitle = binding.titleInput.getText().toString();
                    String noteBody = binding.bodyInput.getText().toString();
                    String noteLat = binding.latUser.getText().toString();
                    String noteLon = binding.lonUser.getText().toString();

                    if (!TextUtils.isEmpty(noteTitle) && !TextUtils.isEmpty(noteBody)) { //not empty
                        addNoteViewModel.saveNote(noteTitle, noteBody, currentTime.toInstant().toString()
                                , currentTime.toString(), noteLat, noteLon, PhotoPath);  //photo path can be null
                        Toast.makeText(getApplicationContext(), "Note Added", Toast.LENGTH_SHORT).show();
                        picCounter+=1;

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Must submit title and note body!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        binding.imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWritePermission == PackageManager.PERMISSION_GRANTED) {
                    choosePicFromGallery();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                }
            }
        });


        binding.takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWritePermission == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                }
            }
        });
    }

    @SuppressLint("IntentReset")
    private void choosePicFromGallery() {

        Intent openGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGallery.setType("image/*");
        openGallery.addCategory(Intent.CATEGORY_OPENABLE);
        openGallery.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(openGallery, "Select Picture"), PICK_FROM_GALLERY);
        editor = sharedPreferences.edit();
        editor.putInt("pic_number",(picCounter+1)); //add shares preference
        editor.apply();
    }

    private void openCamera() {
        noteFile = new File(getExternalFilesDir(null),"pic_number"+picCounter+".jpg");

        imageUri = FileProvider.getUriForFile(
                AddNoteActivity.this,
                "com.example.MoveoNotes.provider", noteFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,CAMERA_REQUEST);
        editor = sharedPreferences.edit();
        editor.putInt("pic_number",(picCounter+1));
        editor.apply();
    }

    // its in the view because it prevents memory leak (or crash), if i hold on to it after the activity is destroyed
    //better to use dependency injection
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
            // check if location is enabled
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
             mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
             final Task<Location> location = mFusedLocationClient.getLastLocation();

             location.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                 @Override
                 public void onComplete(@NonNull Task<Location> task) {
                     if (task.isSuccessful()) {
                         Location location = task.getResult();
                         if(location == null){
                             requestNewLocationData();
                         }
                         else {
                             try {
                                 String lon = String.valueOf(location.getLongitude());
                                 String lat = String.valueOf(location.getLatitude());
                                 binding.latUser.setText(lat);
                                 binding.lonUser.setText(lon);

                             } catch (Exception e) {
                                 Log.e("error: ", e.getMessage());
                             }
                         }
                     }
                 }
             });

            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            binding.latUser.setText("Latitude: " + mLastLocation.getLatitude() + "");
            binding.lonUser.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };


    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions(location and files)
    // its in the view because it prevents memory leak (or crash), if i hold on to it after the activity is destroyed
    //better to use dependency injection
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FROM_GALLERY && resultCode == AppCompatActivity.RESULT_OK) //gallery option
        {
            PhotoPath = addNoteViewModel.onGalleryResult(data,selectedImageUri);
            Glide.with(this).load(PhotoPath).into(binding.imageViewNote);
            binding.imageViewNote.setVisibility(View.VISIBLE);
        }

        else {
            if (requestCode == CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK) // picture option
            {
                PhotoPath = addNoteViewModel.onCameraResult(noteFile);
                Glide.with(this).load(PhotoPath).into(binding.imageViewNote);
                binding.imageViewNote.setVisibility(View.VISIBLE);
            }
            //is photo boolean is in observer
        }
    }


    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                getLastLocation();
            }
        }
        else if (requestCode == WRITE_PERMISSION_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No Camera/Gallery Permissions", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!checkPermissions()) {
            getLastLocation();
        }
        picCounter = sharedPreferences.getInt("pic_number",0); //getting the number of the pic

    }
}
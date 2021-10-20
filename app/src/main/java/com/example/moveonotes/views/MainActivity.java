package com.example.moveonotes.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.moveonotes.Services.CheckNetClass;
import com.example.moveonotes.R;
import com.example.moveonotes.databinding.ActivityMainBinding;
import com.example.moveonotes.viewmodel.LogoutViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity  {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    private LogoutViewModel logoutViewModel;
    BottomNavigationView bottomNavigationView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(user==null){ //if user not connected get to login page
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!CheckNetClass.checknetwork(getApplicationContext())) {
            new androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Internet Connection Required")
                    .setMessage("This app may not work correctly without internet connection\n" +
                            "Open the network/Wifi connection").setPositiveButton("OK", null)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logoutViewModel = new ViewModelProvider(this).get(LogoutViewModel.class);

        if (!CheckNetClass.checknetwork(getApplicationContext())) {
            new androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Internet Connection Required")
                    .setMessage("This app may not work correctly without internet connection\n" +
                            "Open the network/Wifi connection").setPositiveButton("OK", null)
                    .show();
        }
        auth = FirebaseAuth.getInstance();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        //view handle
        logoutViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) { // todo another way to parse?
                if (firebaseUser != null && auth.getCurrentUser() != null) {
                    String username = firebaseUser.getEmail();
                    int index = username.indexOf('@');
                    username = username.substring(0, index);
                    binding.msgTop.setText("Hello " + username + "!");
                }
            }
        });

        logoutViewModel.getLoggedOutLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loggedOut) {
                if(loggedOut){
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });



        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAlert(v);
            }
        });
        getSupportActionBar().hide();

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getSupportActionBar().isShowing()){
                    getSupportActionBar().show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }else{
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                    getSupportActionBar().hide();
                }
            }
        });

        bottomNavigationView = findViewById(R.id.navigationBar);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new NoteListFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);

    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.note_list) {
            super.onBackPressed();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.note_list);
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener onNav = item -> {
        Fragment selected;

        switch (item.getItemId()){
            case R.id.note_list:
                selected = new NoteListFragment();
                binding.msgTop.setText("Notes List");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).addToBackStack(null).commit(); //UI Changes
                return true;

            case R.id.note_map:
                selected = new NoteMapFragment();
                binding.msgTop.setText("Notes Map");
                getSupportFragmentManager().beginTransaction().replace( R.id.frame_layout,selected).addToBackStack(null).commit(); //UI Changes
                return true;
        }
        return false;
    };



    public void logoutAlert(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Logout").setMessage("Are you sure you want to log out?").setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutViewModel.logOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });
        builder.create().show();

    }
}
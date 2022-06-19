package com.example.flickr;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    static final String FLICKR_QUERY = "FLICKR_QUERY";
    static final String PHOTO_TRANSFER ="PHOTO_TRANSFER";

    void activateToolbar(boolean enableHome){
        Log.d(TAG, "activateToolbar: starts");
        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(enableHome);
        }
    }
}
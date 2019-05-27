package com.arch.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.arch.kvmcdemo.R;
import com.arch.util.AppProfile;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class LiveTestActivity extends AppCompatActivity {
    public static final String TAG = "LiveTestActivity";

    public static void start( )  {
        Log.i(TAG,"LiveTestActivity starting...");
        Intent intent = new Intent(AppProfile.getContext (), LiveTestActivity.class);
        intent.setFlags (FLAG_ACTIVITY_NEW_TASK);
        AppProfile.getContext ().startActivity (intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_live_test);
        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);

        FloatingActionButton fab = findViewById (R.id.fab);
        fab.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
//                Snackbar.make (view, "Replace1 with your own action", Snackbar.LENGTH_LONG)
//                        .setAction ("Action", null).show ();
            }
        });
    }

}

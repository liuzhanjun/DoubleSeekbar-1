package com.anarchy.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.anarchy.library.DoubleSeekBar;

public class MainActivity extends AppCompatActivity {
    DoubleSeekBar mDoubleSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDoubleSeekBar  = (DoubleSeekBar) findViewById(R.id.double_seek_bar);
    }

    public void reset(View view){
        mDoubleSeekBar.reset();
    }
}

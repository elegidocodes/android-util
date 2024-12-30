package com.elegidocodes.android.util;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.elegidocodes.android.util.theme.ThemeUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);

        if (ThemeUtil.isDarkModeEnabled(this)) {
            textView.setText("Dark Mode is Enabled");
        } else {
            textView.setText("Dark Mode is Disabled");
        }

    }

}
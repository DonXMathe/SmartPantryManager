package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuggestedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("Suggested recipes will appear here.\n\n(Strict-matching logic coming soon.)");
        tv.setPadding(48, 48, 48, 48);
        tv.setTextSize(16);

        setContentView(tv);
    }
}
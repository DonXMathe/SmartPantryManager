package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerPantry;
    private TextView textEmpty;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerPantry = findViewById(R.id.recycler_pantry);
        textEmpty = findViewById(R.id.text_empty);
        fabAdd = findViewById(R.id.fab_add);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        // Placeholder: no data adapter yet. Show the empty-state message.
        textEmpty.setVisibility(TextView.VISIBLE);

        fabAdd.setOnClickListener(v -> {
            // TODO: launch AddEditItemActivity
        });
    }
}
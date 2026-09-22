package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnItemClickListener {

    private RecyclerView recyclerPantry;
    private TextView textEmpty;
    private FloatingActionButton fabAdd;
    private DatabaseHelper db;
    private PantryAdapter adapter;
    private List<PantryItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        recyclerPantry = findViewById(R.id.recycler_pantry);
        textEmpty = findViewById(R.id.text_empty);
        fabAdd = findViewById(R.id.fab_add);

        adapter = new PantryAdapter(items, this);
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddEditItemActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        items.clear();
        items.addAll(db.getAllPantryItemsAsList());
        adapter.notifyDataSetChanged();

        if (items.isEmpty()) {
            textEmpty.setVisibility(TextView.VISIBLE);
            recyclerPantry.setVisibility(RecyclerView.GONE);
        } else {
            textEmpty.setVisibility(TextView.GONE);
            recyclerPantry.setVisibility(RecyclerView.VISIBLE);
        }
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditItemActivity.class);
        intent.putExtra(AddEditItemActivity.EXTRA_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete item")
                .setMessage("Delete \"" + item.getName() + "\" from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadPantryItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

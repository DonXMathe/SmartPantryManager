package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private static final String TAG = "SUGGESTED_DEBUG";

    private RecyclerView recyclerSuggested;
    private TextView textEmpty;
    private RecipeAdapter adapter;
    private List<Recipe> recipes = new ArrayList<>();
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate START");

        try {
            setContentView(R.layout.activity_suggested);
            Log.d(TAG, "setContentView OK");

            db = new DatabaseHelper(this);
            Log.d(TAG, "DatabaseHelper created");

            recyclerSuggested = findViewById(R.id.recycler_suggested);
            textEmpty = findViewById(R.id.text_suggested_empty);
            Log.d(TAG, "Views found: recycler=" + recyclerSuggested + ", textEmpty=" + textEmpty);

            adapter = new RecipeAdapter(recipes, this);
            recyclerSuggested.setLayoutManager(new LinearLayoutManager(this));
            recyclerSuggested.setAdapter(adapter);
            Log.d(TAG, "Adapter set");
        } catch (Exception e) {
            Log.e(TAG, "onCreate EXCEPTION: " + e.getMessage(), e);
        }

        Log.d(TAG, "onCreate END");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume START");

        try {
            recipes.clear();
            Log.d(TAG, "recipes cleared");

            List<Recipe> matched = db.getStrictMatchedRecipes();
            Log.d(TAG, "matched count = " + matched.size());

            recipes.addAll(matched);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "adapter notified");

            if (recipes.isEmpty()) {
                textEmpty.setVisibility(View.VISIBLE);
                recyclerSuggested.setVisibility(View.GONE);
                Log.d(TAG, "showing empty state");
            } else {
                textEmpty.setVisibility(View.GONE);
                recyclerSuggested.setVisibility(View.VISIBLE);
                Log.d(TAG, "showing list");
            }
        } catch (Exception e) {
            Log.e(TAG, "onResume EXCEPTION: " + e.getMessage(), e);
        }

        Log.d(TAG, "onResume END");
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        startActivity(intent);
    }
}
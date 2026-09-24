package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        db = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        if (recipeId == -1) {
            finish();
            return;
        }

        Recipe recipe = db.getRecipeById(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        TextView textTitle = findViewById(R.id.text_recipe_title);
        TextView textIngredients = findViewById(R.id.text_recipe_ingredients);
        TextView textSteps = findViewById(R.id.text_recipe_steps);

        textTitle.setText(recipe.getName());

        List<RecipeIngredient> ingredients = db.getIngredientsForRecipe(recipeId);
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : ingredients) {
            sb.append("- ")
                    .append(ing.getQuantity())
                    .append(" ")
                    .append(ing.getUnit())
                    .append(" ")
                    .append(ing.getIngredientName())
                    .append("\n");
        }
        textIngredients.setText(sb.toString().trim());

        textSteps.setText(recipe.getSteps());
    }
}
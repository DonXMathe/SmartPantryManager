package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";

    private EditText editName, editQuantity, editUnit, editCategory, editExpiry;
    private Button buttonSave, buttonCancel;
    private DatabaseHelper db;
    private int editingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        db = new DatabaseHelper(this);

        editName = findViewById(R.id.edit_name);
        editQuantity = findViewById(R.id.edit_quantity);
        editUnit = findViewById(R.id.edit_unit);
        editCategory = findViewById(R.id.edit_category);
        editExpiry = findViewById(R.id.edit_expiry);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        editingId = getIntent().getIntExtra(EXTRA_ID, -1);

        // If we're editing, load the existing item and pre-fill the fields.
        if (editingId != -1) {
            PantryItem existing = db.getPantryItemById(editingId);
            if (existing != null) {
                editName.setText(existing.getName());
                editQuantity.setText(String.valueOf(existing.getQuantity()));
                editUnit.setText(existing.getUnit());
                editCategory.setText(existing.getCategory());
                editExpiry.setText(existing.getExpirationDate());
                setTitle("Edit Item");
            }
        } else {
            setTitle("Add Item");
        }

        buttonSave.setOnClickListener(v -> saveItem());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void saveItem() {
        String name = editName.getText().toString().trim();
        String qtyStr = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editName.setError("Name is required");
            editName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(qtyStr)) {
            editQuantity.setError("Quantity is required");
            editQuantity.requestFocus();
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            editQuantity.setError("Enter a valid number");
            editQuantity.requestFocus();
            return;
        }
        if (quantity <= 0) {
            editQuantity.setError("Quantity must be greater than 0");
            editQuantity.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(unit)) {
            editUnit.setError("Unit is required");
            editUnit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(category)) {
            editCategory.setError("Category is required");
            editCategory.requestFocus();
            return;
        }

        String expiryValue = TextUtils.isEmpty(expiry) ? null : expiry;

        if (editingId != -1) {
            db.updatePantryItem(editingId, name, quantity, unit, category, expiryValue);
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        } else {
            db.addPantryItem(name, quantity, unit, category, expiryValue);
            Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
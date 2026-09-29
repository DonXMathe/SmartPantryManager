# SmartPantryManager

A Java Android application that reduces food waste by suggesting recipes based strictly on the ingredients already in your pantry.

## What it does

A recipe is only shown as **suggested** when every ingredient it requires is present in the pantry, in at least the required quantity. If you're missing even one ingredient, the recipe is excluded. This strict-matching rule is the core value of the app.

## Features

- **Pantry management** — add, view, and delete pantry items (name, quantity, unit, category, expiry date).
- **Pantry List screen** — RecyclerView bound to the local database, showing all current ingredients.
- **Recipe collection** — 18 recipes seeded into the database on first launch, each with a name, required ingredients, and preparation steps.
- **Suggested Recipes screen** — runs the strict-matching algorithm against the current pantry and lists only recipes the user can make right now.
- **Recipe Detail screen** — full ingredient list and method for a selected recipe.
- **Settings screen** — toggle for expiring-soon alerts (persisted via SharedPreferences).
- **Toolbar menu navigation** between Pantry, Recipes, Suggested Recipes, and Settings.

## Database choice

**SQLite**, implemented via `SQLiteOpenHelper`.

Chosen because:
- The app is fully offline and user-owned — no cloud sync is required.
- SQLite is the standard Android on-device database, requiring no backend, no network permissions, and no authentication.
- The data is naturally relational: pantry items, recipes, and recipe ingredients map cleanly to three tables with a foreign key between `recipes` and `recipe_ingredients`.
- Full CRUD is required by the brief, and SQLite provides insert, update, delete, and query with no third-party service or API keys.

Trade-off: no cross-device sync. Acceptable for this app, since the pantry belongs to a single user on a single device.

## Strict-matching rule

A recipe appears in Suggested Recipes only if **every** ingredient it requires is present in the pantry **in at least the required quantity**.

Ingredient names are normalised before comparison (lowercased, trimmed, and simple plural forms collapsed), so "Tomatoes" in the pantry matches "tomato" in a recipe. Quantities are compared numerically — 3 eggs satisfies a recipe needing 2 eggs, but 1 egg does not.

## Setup / run instructions

1. Clone this repository:
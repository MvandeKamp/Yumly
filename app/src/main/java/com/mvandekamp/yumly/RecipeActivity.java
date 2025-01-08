package com.mvandekamp.yumly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Beispielrezepte
        String[] recipes = {"Spaghetti Bolognese", "Pizza Margherita", "Sushi", "Salat"};

        ListView listView = findViewById(R.id.recipeListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Öffnet die Zutatenliste für das ausgewählte Rezept
                Intent intent = new Intent(RecipeActivity.this, IngredientsActivity.class);
                intent.putExtra("recipeName", recipes[position]);
                startActivity(intent);
            }
        });
    }
}
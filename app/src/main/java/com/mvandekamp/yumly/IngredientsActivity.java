package com.mvandekamp.yumly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class IngredientsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        TextView ingredientsTextView = findViewById(R.id.ingredientsTextView);
        Button btnAssignTasks = findViewById(R.id.btnAssignTasks);

        // Rezeptname aus Intent abrufen
        String recipeName = getIntent().getStringExtra("recipeName");

        // Beispielzutaten
        String ingredients = "";
        switch (recipeName) {
            case "Spaghetti Bolognese":
                ingredients = "Spaghetti, Hackfleisch, Tomatensauce, Zwiebeln, Knoblauch";
                break;
            case "Pizza Margherita":
                ingredients = "Pizzateig, Tomatensauce, Mozzarella, Basilikum";
                break;
            case "Sushi":
                ingredients = "Sushireis, Nori-Blätter, Lachs, Avocado, Sojasauce";
                break;
            case "Salat":
                ingredients = "Salatblätter, Tomaten, Gurken, Dressing";
                break;
        }

        ingredientsTextView.setText(ingredients);

        btnAssignTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Öffnet die Aufgabenverteilung
                Intent intent = new Intent(IngredientsActivity.this, TaskAssignmentActivity.class);
                startActivity(intent);
            }
        });
    }
}
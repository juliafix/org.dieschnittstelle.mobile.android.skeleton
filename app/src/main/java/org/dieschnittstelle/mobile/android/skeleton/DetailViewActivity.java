package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private EditText itemNameText;
    private EditText itemDescriptionText;
    private ToDoItem item;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);

        // 1. Bedien- und Anteigeelemente auslesen
        itemNameText = findViewById(R.id.itemName);
        itemDescriptionText = findViewById(R.id.itemDescription);
        saveButton = findViewById(R.id.saveButton);

        // 2. Bedienelemente bedienbar machen
        saveButton.setOnClickListener(v -> this.onSaveItem());

        // 3. Ansicht mit Daten füllen
        item = (ToDoItem) getIntent().getSerializableExtra(ARG_ITEM);

        if (item != null) {
            itemNameText.setText(item.getName());
            itemDescriptionText.setText(item.getDescription());
        } else {
            item = new ToDoItem();
        }
    }

    protected void onSaveItem() {
        item.setName(this.itemNameText.getText().toString());
        item.setDescription(this.itemDescriptionText.getText().toString());

        // 4. Wieder an Main zurückgeben
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, item);

        this.setResult(Activity.RESULT_OK, returnIntent);

        finish();

    }
}

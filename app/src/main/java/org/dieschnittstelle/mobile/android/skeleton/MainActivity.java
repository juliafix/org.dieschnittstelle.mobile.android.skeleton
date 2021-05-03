package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity {

    private ViewGroup listView;
    private FloatingActionButton addNewItemButton;
    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.listView = findViewById(R.id.listView);
        this.addNewItemButton = findViewById(R.id.addNewItemButton);

        for (int i = 0; i < this.listView.getChildCount(); i++) {
            TextView currentChild = (TextView) this.listView.getChildAt(i);
            currentChild.setOnClickListener(v -> {
                onItemSelected(currentChild.getText().toString());
                //showFeedbackMessage(
                //        String.format(
                //                getResources()
                //                        .getString(R.string.simple_message_template), currentChild.getText()));
            });
        }

        this.addNewItemButton.setOnClickListener(v -> this.onItemCreationRequested());
    }

    protected void onItemSelected(String itemName) {
        Intent detailViewIntent = new Intent(this, DetailViewActivity.class);
        detailViewIntent.putExtra(DetailViewActivity.ARG_ITEM, itemName);
        this.startActivity(detailViewIntent);

    }

    protected void onItemCreationRequested() {
        Intent detailViewForCreate = new Intent(this, DetailViewActivity.class);
        this.startActivityForResult(detailViewForCreate, CALL_DETAILVIEW_FOR_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_DETAILVIEW_FOR_CREATE) {
            if (resultCode == Activity.RESULT_OK) {
                this.onNewItemCreated(data.getStringExtra(DetailViewActivity.ARG_ITEM));
            } else {
                showFeedbackMessage("Rückgabe von DetailView: " + requestCode);
            }

        }
    }

    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_INDEFINITE).show();
    }

    protected void onNewItemCreated(String itemName) {
        TextView newItemView = (TextView) getLayoutInflater().inflate(R.layout.activity_main_listitem, null);
        newItemView.setText(itemName);
        this.listView.addView(newItemView);
    }
}

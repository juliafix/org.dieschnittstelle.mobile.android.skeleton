package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity {

    private ViewGroup listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.listView = findViewById(R.id.listView);

        for (int i = 0; i < this.listView.getChildCount(); i++) {
            TextView currentChild = (TextView) this.listView.getChildAt(i);
            currentChild.setOnClickListener(v -> {
                //onItemSelected(currentChild.getText().toString());
                showFeedbackMessage(
                        String.format(
                                getResources()
                                        .getString(R.string.simple_message_template), currentChild.getText()));
            });
        }

    }

    protected void onItemSelected(String itemName) {
        Intent detailViewIntent = new Intent(this, DetailViewActivity.class);
        detailViewIntent.putExtra(DetailViewActivity.ARG_ITEM, itemName);
        this.startActivity(detailViewIntent);

    }

    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_INDEFINITE).show();
    }
}

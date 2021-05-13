package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<ToDoItem> items = Arrays.asList("Eins", "Zwei", "Drei", "Vier", "Fünf", "Sechs", "Sieben")
            .stream()
            .map(item -> {
                ToDoItem itemobj = new ToDoItem(item);
                itemobj.setId(ToDoItem.nextId());
                return itemobj;
            })
            .collect(Collectors.toList());
    private ArrayAdapter<ToDoItem> listViewAdapter;

    private FloatingActionButton addNewItemButton;
    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    private static final int CALL_DETAILVIEW_FOR_EDIT = 1;

    //Adapter Klasse evtl. noch in separate Datei
    private class ToDoItemsAdapter extends ArrayAdapter<ToDoItem> {
        private int layoutResource;

        public ToDoItemsAdapter(@NonNull Context context, int resource, @NonNull List<ToDoItem> objects) {
            super(context, resource, objects);
            this.layoutResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ToDoItem currentItem = getItem(position);
            View currentView = getLayoutInflater().inflate(this.layoutResource, null);
            TextView todoNameText = currentView.findViewById(R.id.itemName);
            todoNameText.setText(currentItem.getName());

            return currentView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.listView = findViewById(R.id.listView);
        this.listViewAdapter = new ToDoItemsAdapter(this, R.layout.activity_main_listitem, this.items);
        this.listView.setAdapter((this.listViewAdapter));
        this.addNewItemButton = findViewById(R.id.addNewItemButton);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDoItem selectedItem = listViewAdapter.getItem(position);
                onItemSelected(selectedItem);
            }
        });

        this.addNewItemButton.setOnClickListener(v -> this.onItemCreationRequested());
    }

    protected void onItemSelected(ToDoItem itemName) {
        Intent detailViewIntent = new Intent(this, DetailViewActivity.class);
        detailViewIntent.putExtra(DetailViewActivity.ARG_ITEM, itemName);
        this.startActivityForResult(detailViewIntent, CALL_DETAILVIEW_FOR_EDIT);

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
                this.onNewItemCreated((ToDoItem) data.getSerializableExtra(DetailViewActivity.ARG_ITEM));
            } else {
                showFeedbackMessage("Rückgabe von DetailView (Create) mit: " + requestCode);
            }
        } else if (requestCode == CALL_DETAILVIEW_FOR_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                ToDoItem editedItem = (ToDoItem) data.getSerializableExtra((DetailViewActivity.ARG_ITEM));
                showFeedbackMessage("Aktualisiertes ToDo: " + editedItem.getName());
                this.onItemEdited(editedItem);
            } else {
                showFeedbackMessage("Rückgabe von DetailView (Edit) mit: " + requestCode);
            }
        } else {
            showFeedbackMessage("Rückgabe mit RequestCode: " + requestCode + " und ResultCode: " + resultCode);
        }
    }

    protected void onItemEdited(ToDoItem item) {
        int position = this.items.indexOf(item);
        this.items.remove(position);
        this.items.add(position, item);
        this.listViewAdapter.notifyDataSetChanged();
        this.listView.setSelection(position);
    }

    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_INDEFINITE).show();
    }

    protected void onNewItemCreated(ToDoItem todo) {
        //TextView newItemView = (TextView) getLayoutInflater().inflate(R.layout.activity_main_listitem, null);
        //newItemView.setText(itemName);
        //this.listView.addView(newItemView);
        todo.setId(ToDoItem.nextId());
        this.items.add(todo);
        this.listViewAdapter.notifyDataSetChanged();
        //Scrollt dahin, wo das letzte Element hinzugefügt wurde
        this.listView.setSelection(this.listViewAdapter.getPosition(todo));
    }
}

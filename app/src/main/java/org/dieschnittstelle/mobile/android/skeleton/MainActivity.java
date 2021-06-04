package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainListitemBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperationsAsync;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.ThreadedToDoItemCRUDOperationsAsyncImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<ToDoItem> items = new ArrayList<>();
    private ArrayAdapter<ToDoItem> listViewAdapter;
    private ProgressBar progressBar;

    private FloatingActionButton addNewItemButton;
    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    private static final int CALL_DETAILVIEW_FOR_EDIT = 1;

    private IToDoItemCRUDOperationsAsync crudOperations;

    //Adapter Klasse evtl. noch in separate Datei
    private class ToDoItemsAdapter extends ArrayAdapter<ToDoItem> {
        private int layoutResource;

        public ToDoItemsAdapter(@NonNull Context context, int resource, @NonNull List<ToDoItem> objects) {
            super(context, resource, objects);
            this.layoutResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View recyclableItemView, @NonNull ViewGroup parent) {

            View itemView = null;
            ToDoItem currentItem = getItem(position);

            if (recyclableItemView != null) {
                View textView = recyclableItemView.findViewById(R.id.itemName);
                if (textView != null) {

                }
                itemView = recyclableItemView;
                ActivityMainListitemBinding recycledBinding = (ActivityMainListitemBinding) itemView.getTag();
                recycledBinding.setItem(currentItem);
            } else {
                ActivityMainListitemBinding currentBinding =
                        DataBindingUtil.inflate(getLayoutInflater(),
                                this.layoutResource,
                                null,
                                false);
                currentBinding.setItem(currentItem);
                currentBinding.setController(MainActivity.this);

                itemView = currentBinding.getRoot();
                itemView.setTag(currentBinding);

            }

            return itemView;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.listView = findViewById(R.id.listView);
        this.listViewAdapter = new ToDoItemsAdapter(this, R.layout.activity_main_listitem, this.items);
        this.listView.setAdapter((this.listViewAdapter));
        this.progressBar = findViewById(R.id.progressBar);
        this.addNewItemButton = findViewById(R.id.addNewItemButton);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDoItem selectedItem = listViewAdapter.getItem(position);
                onItemSelected(selectedItem);
            }
        });

        this.addNewItemButton.setOnClickListener(v -> this.onItemCreationRequested());

        //Daten aus readAllDataItems Methode in View hinzufügen
        IToDoItemCRUDOperations crudExecutor = ((ToDoItemApplication)this.getApplication()).getCRUDOperations();
        this.crudOperations = new ThreadedToDoItemCRUDOperationsAsyncImpl(crudExecutor, this, this.progressBar);

        this.crudOperations.readAllToDoItems(items -> listViewAdapter.addAll(items));
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

    protected void onItemEdited(ToDoItem todo) {
        this.crudOperations.updateToDoItem(todo, updated -> {
            int position = this.items.indexOf(updated);
            this.items.remove(position);
            this.items.add(position, updated);
            this.listViewAdapter.notifyDataSetChanged();
            this.listView.setSelection(position);
        });

    }

    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void onNewItemCreated(ToDoItem todo) {
        this.crudOperations.createToDoItem(todo, created -> {
            this.items.add(created);
            this.listViewAdapter.notifyDataSetChanged();
            //Scrollt dahin, wo das letzte Element hinzugefügt wurde
            this.listView.setSelection(this.listViewAdapter.getPosition(created));
        });

    }

    //Hier muss Checkstatus-Update in DB vorgenommen werden
    public void onCheckedChangedInListView(ToDoItem todo) {
        this.crudOperations.updateToDoItem(todo, updated -> {
            showFeedbackMessage("Checked changed to: " + updated.isChecked() + " for " + updated.getName());
        });
    }

    public void onFavouriteChangedInListView(ToDoItem todo) {
        this.crudOperations.updateToDoItem(todo, updated -> {
            showFeedbackMessage("Favourtie changed to: " + updated.isFavourite() + " for " + updated.getName());
        });
    }

    protected void readAllDataItems(Consumer<List<ToDoItem>> onread) {

    }
}

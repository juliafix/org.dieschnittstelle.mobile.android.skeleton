package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.adapter.ToDoItemsAdapter;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperationsAsync;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.SyncedToDoItemCURDOperationsImpl;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.ThreadedToDoItemCRUDOperationsAsyncImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    private static final int CALL_DETAILVIEW_FOR_EDIT = 1;
    private ListView listView;
    private List<ToDoItem> toDoItems = new ArrayList<>();
    private ArrayAdapter<ToDoItem> listViewAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton addNewItemButton;
    private IToDoItemCRUDOperationsAsync crudOperations;
    private String sortMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.listView = findViewById(R.id.listView);
        this.listViewAdapter = new ToDoItemsAdapter(this, R.layout.activity_main_listitem, this.toDoItems, this);
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
        IToDoItemCRUDOperations crudExecutor = ((ToDoItemApplication) this.getApplication()).getCRUDOperations();
        this.crudOperations = new ThreadedToDoItemCRUDOperationsAsyncImpl(crudExecutor, this, this.progressBar);

        this.crudOperations.readAllToDoItems(toDoItems -> {
            sortToDos(toDoItems, sortMethod);
            listViewAdapter.addAll(toDoItems);
        });


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
                boolean isDeleted = data.getBooleanExtra(DetailViewActivity.DELETED, false);
                showFeedbackMessage("Aktualisiertes ToDo: " + editedItem.getName());
                if (isDeleted) {
                    this.onItemDeleted(editedItem);
                } else {
                    this.onItemEdited(editedItem);
                }
            } else {
                showFeedbackMessage("Rückgabe von DetailView (Edit) mit: " + requestCode);
            }
        } else {
            showFeedbackMessage("Rückgabe mit RequestCode: " + requestCode + " und ResultCode: " + resultCode);
        }
    }

    protected void onItemEdited(ToDoItem todo) {
        this.crudOperations.updateToDoItem(todo, updated -> {
            int position = this.toDoItems.indexOf(updated);
            this.toDoItems.remove(position);
            this.toDoItems.add(position, updated);
            this.sortListAndScrollToItem(updated, sortMethod);
        });

    }

    protected void onItemDeleted(ToDoItem todo) {
        this.crudOperations.deleteToDoItem(todo.getId(), deleted -> {
            int position = this.toDoItems.indexOf(todo);
            this.toDoItems.remove(position);
            this.listViewAdapter.notifyDataSetChanged();
            this.listView.setSelection(position);
        });
    }

    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void onNewItemCreated(ToDoItem todo) {
        this.crudOperations.createToDoItem(todo, created -> {
            this.toDoItems.add(created);
            this.sortListAndScrollToItem(created, sortMethod);
        });

    }

    public void onCheckedChangedInListView(ToDoItem todo) {
        this.crudOperations.updateToDoItem(todo, updated -> {
            this.sortListAndScrollToItem(todo, sortMethod);
        });
    }

    public void onFavouriteChangedInListView(ToDoItem todo) {
        this.crudOperations.updateToDoItem(todo, updated -> {
            this.sortListAndScrollToItem(todo, sortMethod);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void sortToDos(List<ToDoItem> todos, String sortMethod) {
        todos.sort(Comparator.comparing(ToDoItem::isChecked).thenComparing(ToDoItem::getName));

        if (sortMethod.equals("date")) {
            todos.sort(Comparator.comparing(ToDoItem::isChecked).thenComparing(ToDoItem::getExpirationDateTime).thenComparing(todo -> !todo.isFavourite()));
        } else if (sortMethod.equals("favourite")) {
            todos.sort(Comparator.comparing(ToDoItem::isChecked).thenComparing(todo -> !todo.isFavourite()).thenComparing(ToDoItem::getExpirationDateTime));
        }
    }

    protected void sortListAndScrollToItem(ToDoItem todo, String sortMethod) {
        this.sortToDos(this.toDoItems, sortMethod);
        this.listViewAdapter.notifyDataSetChanged();
        if (todo != null) {
            int pos = this.listViewAdapter.getPosition(todo);
            this.listView.setSelection(pos);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sortToDosDate) {
            this.sortMethod = "date";
            this.sortListAndScrollToItem(null, sortMethod);
            return true;
        } else if (item.getItemId() == R.id.sortToDosFav) {
            this.sortMethod = "favourite";
            this.sortListAndScrollToItem(null, sortMethod);
            return true;
        } else if (item.getItemId() == R.id.deleteRemoteToDos) {
            this.crudOperations.deleteAllToDoItems(true, (result) -> {
                if (result) {
                    showFeedbackMessage("Alle Remote-ToDos gelöscht.");
                    this.toDoItems.clear();
                    this.listViewAdapter.notifyDataSetChanged();
                } else {
                    showFeedbackMessage("Remote-ToDos konnten nicht gelöscht werden.");
                }
            });
            return true;
        } else if (item.getItemId() == R.id.deleteLocalToDos) {
            this.crudOperations.deleteAllToDoItems(false, (result) -> {
                if (result) {
                    showFeedbackMessage("Alle lokalen ToDos gelöscht.");
                    this.toDoItems.clear();
                    this.listViewAdapter.notifyDataSetChanged();
                } else {
                    showFeedbackMessage("Lokale ToDos konnten nicht gelöscht werden.");
                }
            });
            return true;
        } else if (item.getItemId() == R.id.syncToDos) {
            crudOperations.syncToDoItems(toDoItems -> {
                sortToDos(toDoItems, sortMethod);
                this.toDoItems.clear();
                this.listViewAdapter.notifyDataSetChanged();
                listViewAdapter.addAll(toDoItems);
            });
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}

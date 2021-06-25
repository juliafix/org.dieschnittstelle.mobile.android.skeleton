package org.dieschnittstelle.mobile.android.skeleton.model.impl;

import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.List;

public class SyncedToDoItemCURDOperationsImpl implements IToDoItemCRUDOperations{

    private IToDoItemCRUDOperations localCRUD;
    private IToDoItemCRUDOperations remoteCRUD;
    private boolean synced;

    public SyncedToDoItemCURDOperationsImpl(IToDoItemCRUDOperations localCRUD, IToDoItemCRUDOperations remoteCRUD) {
        this.localCRUD = localCRUD;
        this.remoteCRUD = remoteCRUD;
    }

    @Override
    public ToDoItem createToDoItem(ToDoItem todo) {
        todo = localCRUD.createToDoItem(todo);
        remoteCRUD.createToDoItem(todo);
        return todo;
    }

    @Override
    public List<ToDoItem> readAllToDoItems() {
        if (!synced) {
            this.syncLocalAndRemote();
            this.synced = true;
        }
        return localCRUD.readAllToDoItems();
    }

    @Override
    public ToDoItem readToDoItems(long id) {
        return null;
    }

    @Override
    public ToDoItem updateToDoItem(ToDoItem todo) {
        todo = localCRUD.updateToDoItem(todo);
        remoteCRUD.updateToDoItem(todo);
        return todo;
    }

    @Override
    public boolean deleteToDoItem(long id) {
        return false;
    }

    @Override
    public boolean deleteAllToDoItems(boolean remote) {
        if(remote) {
            return this.remoteCRUD.deleteAllToDoItems(remote);
        } else {
            return this.localCRUD.deleteAllToDoItems(remote);
        }
    }

    public void syncLocalAndRemote() {
        if (localCRUD.readAllToDoItems().size() == 0) {
            for (ToDoItem todo: remoteCRUD.readAllToDoItems()) {
                localCRUD.createToDoItem(todo);
            }
        } else {
            remoteCRUD.deleteAllToDoItems(true);
            for (ToDoItem todo: localCRUD.readAllToDoItems()) {
                remoteCRUD.createToDoItem(todo);
            }
        }
    }

}

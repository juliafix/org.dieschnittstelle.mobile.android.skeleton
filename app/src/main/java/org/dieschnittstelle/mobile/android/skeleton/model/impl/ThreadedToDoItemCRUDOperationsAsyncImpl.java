package org.dieschnittstelle.mobile.android.skeleton.model.impl;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperationsAsync;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.List;
import java.util.function.Consumer;

public class ThreadedToDoItemCRUDOperationsAsyncImpl implements IToDoItemCRUDOperationsAsync {

    private IToDoItemCRUDOperations crudExecutor;
    private Activity uiThreadProvider;
    private ProgressBar progressBar;

    public ThreadedToDoItemCRUDOperationsAsyncImpl(IToDoItemCRUDOperations crudExecutor, Activity uiThreadProvider, ProgressBar progressBar) {
        this.crudExecutor = crudExecutor;
        this.uiThreadProvider = uiThreadProvider;
        this.progressBar = progressBar;
    }

    @Override
    public void createToDoItem(ToDoItem todo, Consumer<ToDoItem> oncreated) {
        this.progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            ToDoItem created = this.crudExecutor.createToDoItem(todo);
            this.uiThreadProvider.runOnUiThread(() -> {
                this.progressBar.setVisibility(View.GONE);
                oncreated.accept(created);
            });

        }).start();
    }

    @Override
    public void readAllToDoItems(Consumer<List<ToDoItem>> onread) {
        this.progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {

            List<ToDoItem> todos = crudExecutor.readAllToDoItems();

            uiThreadProvider.runOnUiThread(() -> {
                this.progressBar.setVisibility(View.GONE);
                onread.accept(todos);
            });
        }).start();

    }

    @Override
    public void readToDoItems(long id, Consumer<ToDoItem> onread) {

    }

    @Override
    public void updateToDoItem(ToDoItem todo, Consumer<ToDoItem> onupdated) {
        new Thread(() -> {
            ToDoItem updated = crudExecutor.updateToDoItem(todo);
            this.uiThreadProvider.runOnUiThread(() -> onupdated.accept(updated));
        }).start();
    }

    @Override
    public void deleteToDoItem(long id, Consumer<Boolean> ondeleted) {
        new Thread(() -> {
            boolean deleted = crudExecutor.deleteToDoItem(id);
            this.uiThreadProvider.runOnUiThread(() -> ondeleted.accept(deleted));
        }).start();
    }

    @Override
    public void deleteAllToDoItems(boolean remote, Consumer<Boolean> ondeleted) {
        new Thread(() -> {
            boolean deleted = crudExecutor.deleteAllToDoItems(remote);
            this.uiThreadProvider.runOnUiThread(() -> ondeleted.accept(deleted));
        }).start();
    }

    @Override
    public void syncToDoItems(Consumer<List<ToDoItem>> onread) {
        new Thread(() -> {
            if (crudExecutor instanceof SyncedToDoItemCURDOperationsImpl) {
                ((SyncedToDoItemCURDOperationsImpl) crudExecutor).syncLocalAndRemote();
                List<ToDoItem> todos = crudExecutor.readAllToDoItems();

                uiThreadProvider.runOnUiThread(() -> {
                    onread.accept(todos);
                });
            }
        }).start();
    }
}

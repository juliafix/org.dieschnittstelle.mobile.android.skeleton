package org.dieschnittstelle.mobile.android.skeleton.model.impl;

import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleToDoItemCRUDOperationsImpl implements IToDoItemCRUDOperations {
    @Override
    public ToDoItem createToDoItem(ToDoItem todo) {
        todo.setId(ToDoItem.nextId());
        return todo;
    }

    @Override
    public List<ToDoItem> readAllToDoItems() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<ToDoItem>  todos = Arrays.asList("Eins", "Zwei", "Drei", "Vier", "Fünf", "Sechs", "Sieben", "Acht", "Neun", "Zehn", "Elf", "Zwölf", "Dreizeihn", "Vierzehn", "Fünfzehn", "Sechzehn")
                .stream()
                .map(item -> {
                    ToDoItem itemobj = new ToDoItem(item);
                    itemobj.setId(ToDoItem.nextId());
                    return itemobj;
                })
                .collect(Collectors.toList());
        return todos;
    }

    @Override
    public ToDoItem readToDoItems(long id) {
        return null;
    }

    @Override
    public ToDoItem updateToDoItem(ToDoItem todo) {
        return todo;
    }

    @Override
    public boolean deleteToDoItem(long id) {
        return false;
    }

    @Override
    public boolean deleteAllToDoItems(boolean remote) {
        return false;
    }
}

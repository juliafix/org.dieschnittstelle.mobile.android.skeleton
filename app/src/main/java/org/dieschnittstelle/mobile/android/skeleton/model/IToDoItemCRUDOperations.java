package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

public interface IToDoItemCRUDOperations {

    public ToDoItem createToDoItem(ToDoItem todo);

    public List<ToDoItem> readAllToDoItems();

    public ToDoItem readToDoItems(long id);

    public ToDoItem updateToDoItem(ToDoItem todo);

    public boolean deleteToDoItem (long id);
}


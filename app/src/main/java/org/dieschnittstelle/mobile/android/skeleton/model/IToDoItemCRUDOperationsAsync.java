package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;
import java.util.function.Consumer;

public interface IToDoItemCRUDOperationsAsync {

    public void createToDoItem(ToDoItem todo, Consumer<ToDoItem> oncreated);

    public void readAllToDoItems(Consumer<List<ToDoItem>> onread);

    public void readToDoItems(long id, Consumer<ToDoItem> onread);

    public void updateToDoItem(ToDoItem todo, Consumer<ToDoItem> onupdated);

    public void deleteToDoItem (long id, Consumer<Boolean> ondeleted) ;
}

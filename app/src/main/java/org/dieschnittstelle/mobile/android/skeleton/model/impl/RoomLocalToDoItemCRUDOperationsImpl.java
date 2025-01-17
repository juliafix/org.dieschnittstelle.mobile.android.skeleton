package org.dieschnittstelle.mobile.android.skeleton.model.impl;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.Update;

import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoomLocalToDoItemCRUDOperationsImpl implements IToDoItemCRUDOperations {

    public static class ArrayListToStringDatabaseConverter {

        @TypeConverter
        public static ArrayList<String> fromString (String value) {
            if (value == null || value.length() == 0) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(value.split(",")));
        }

        @TypeConverter
        public static String fromArrayList(ArrayList<String> values) {
            if (values == null) {
                return "";
            }
            return values
                    .stream()
                    .collect(Collectors.joining(","));
        }
    }

    @Database(entities = {ToDoItem.class}, version = 1)
    public static abstract class RoomToDoItemDatabase extends RoomDatabase {

        public abstract RoomToDoItemCRUDAccess getDao();

    }

    @Dao
    public static interface RoomToDoItemCRUDAccess {

        @Insert
        public long createToDo(ToDoItem todo);

        @Query("select * from todoitem")
        public List<ToDoItem> readAllToDos();

        @Update
        public int updateToDo(ToDoItem todo);

        @Delete
        public void deleteAllToDos(List<ToDoItem> toDoItems);

        @Delete
        public void deleteToDo(ToDoItem todo);
    }

    private RoomToDoItemCRUDAccess roomAccessor;

    public RoomLocalToDoItemCRUDOperationsImpl(Context databaseowner) {
        RoomToDoItemDatabase db = Room
                .databaseBuilder(
                    databaseowner.getApplicationContext(),
                    RoomToDoItemDatabase.class,
                        "todoitems-database").build();

        this.roomAccessor = db.getDao();
    }

    @Override
    public ToDoItem createToDoItem(ToDoItem todo) {
        long newid = roomAccessor.createToDo(todo);
        todo.setId(newid);
        return todo;
    }

    @Override
    public List<ToDoItem> readAllToDoItems() {
        return roomAccessor.readAllToDos();
    }

    @Override
    public ToDoItem readToDoItems(long id) {
        return null;
    }

    @Override
    public ToDoItem updateToDoItem(ToDoItem todo) {
        roomAccessor.updateToDo(todo);
        return todo;
    }

    @Override
    public boolean deleteToDoItem(long id) {
        ToDoItem item = roomAccessor.readAllToDos().stream()
                .filter(toDoItem -> toDoItem.getId() == id)
                .findFirst()
                .get();
        roomAccessor.deleteToDo(item);
        return false;
    }

    @Override
    public boolean deleteAllToDoItems(boolean remote) {
        if (remote) {
            return false;
        } else {
            roomAccessor.deleteAllToDos(roomAccessor.readAllToDos());
            return true;
        }
    }
}

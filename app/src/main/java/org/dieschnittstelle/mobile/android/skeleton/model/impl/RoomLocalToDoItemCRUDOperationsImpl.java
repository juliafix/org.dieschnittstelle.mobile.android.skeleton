package org.dieschnittstelle.mobile.android.skeleton.model.impl;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
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

        //Anzahl aus DB wie viele Items aktualisiert wurden
        @Update
        public int updateToDo(ToDoItem todo);
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
        return false;
    }
}

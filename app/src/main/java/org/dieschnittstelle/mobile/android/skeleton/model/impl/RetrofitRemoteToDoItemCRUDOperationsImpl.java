package org.dieschnittstelle.mobile.android.skeleton.model.impl;

import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;
import org.dieschnittstelle.mobile.android.skeleton.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RetrofitRemoteToDoItemCRUDOperationsImpl implements IToDoItemCRUDOperations {

    public static interface ToDoWebAPI {

        @POST("/api/todos")
        public Call<ToDoItem> createTodo(@Body ToDoItem todo);

        @GET("/api/todos")
        public Call<List<ToDoItem>> readAllTodos();

        @GET("/api/todos/{id}")
        public Call<ToDoItem> readTodo(@Path("id") long id);

        @PUT("/api/todos/{id}")
        public Call<ToDoItem> updateTodo(@Path("id") long id, @Body ToDoItem todo);

        @DELETE("/api/todos/{id}")
        public Call<Boolean> deleteTodo(@Path("id") long id);

        @DELETE("/api/todos")
        public Call<Boolean> deleteAllTodos();

        @PUT("/api/users/auth")
        public Call<Boolean> authenticateUser(@Body User user);

    }

    private ToDoWebAPI webAPI;

    public RetrofitRemoteToDoItemCRUDOperationsImpl() {
        Retrofit apiBase = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.webAPI = apiBase.create(ToDoWebAPI.class);
    }

    @Override
    public ToDoItem createToDoItem(ToDoItem todo) {
        try {
            return this.webAPI.createTodo(todo).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ToDoItem> readAllToDoItems() {
        try {
            return this.webAPI.readAllTodos().execute().body();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public ToDoItem readToDoItems(long id) {
        try {
            return this.webAPI.readTodo(id).execute().body();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public ToDoItem updateToDoItem(ToDoItem todo) {
        try {
            return this.webAPI.updateTodo(todo.getId(), todo).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteToDoItem(long id) {
        try {
            return this.webAPI.deleteTodo(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticateUser(User user) {
        try {
            return this.webAPI.authenticateUser(user).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllToDoItems(boolean remote) {
        try {
            return this.webAPI.deleteAllTodos().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

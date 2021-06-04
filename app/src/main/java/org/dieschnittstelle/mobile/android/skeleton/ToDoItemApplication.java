package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;

import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.RetrofitRemoteToDoItemCRUDOperationsImpl;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.RoomLocalToDoItemCRUDOperationsImpl;

public class ToDoItemApplication extends Application {

    public IToDoItemCRUDOperations getCRUDOperations() {
        return new RoomLocalToDoItemCRUDOperationsImpl(this);
        //return new RetrofitRemoteToDoItemCRUDOperationsImpl();
    }
}

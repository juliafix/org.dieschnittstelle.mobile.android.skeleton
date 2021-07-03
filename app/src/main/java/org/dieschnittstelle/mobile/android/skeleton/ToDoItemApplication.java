package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import org.dieschnittstelle.mobile.android.skeleton.model.IToDoItemCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.RetrofitRemoteToDoItemCRUDOperationsImpl;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.RoomLocalToDoItemCRUDOperationsImpl;
import org.dieschnittstelle.mobile.android.skeleton.model.impl.SyncedToDoItemCURDOperationsImpl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ToDoItemApplication extends Application {

    protected static String logtag = "ToDoItemApplication";
    private IToDoItemCRUDOperations crudOperations;
    private boolean serverAvailable;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Verbindungsaufbau...", Toast.LENGTH_SHORT).show();
        Future<Boolean> connectivityFuture = checkConnectivityAsync();

        try {
            if (connectivityFuture.get()) {
                Log.e(logtag, "Connectivity successful");
                Toast.makeText(this, "Backend erreichbar. Nutzung der Webanwendung", Toast.LENGTH_SHORT).show();
                this.crudOperations = new SyncedToDoItemCURDOperationsImpl(new RoomLocalToDoItemCRUDOperationsImpl(this), new RetrofitRemoteToDoItemCRUDOperationsImpl());
                this.serverAvailable = true;
            } else {
                Log.e(logtag, "Connectivity failed");
                Toast.makeText(this, "Backend nicht erreichbar. Nutzung der lokalen Datenbank", Toast.LENGTH_SHORT).show();
                this.crudOperations = new RoomLocalToDoItemCRUDOperationsImpl(this);
            }
        }
        catch (Exception e) {
            Log.e(logtag, "onCreate(): Got exception: " + e, e);
            this.crudOperations = new RoomLocalToDoItemCRUDOperationsImpl(this);
        }
    }

    public IToDoItemCRUDOperations getCRUDOperations() {
        return this.crudOperations;
    }

    public Future<Boolean> checkConnectivityAsync() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new Thread(() -> {
            boolean connectionAvailable = checkConnectivity();
            future.complete(connectionAvailable);
        }).start();

        return future;
    }

    public boolean checkConnectivity() {
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL("http://10.0.2.2:8080/api/todos").openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            conn.getInputStream();
            Log.e(logtag, "checkConnectivity(): connection successful");
            return true;
        }
        catch (Exception e){
            Log.e(logtag, "checkConnectivity(): Got exception: " + e, e);
            return false;
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public boolean isServerAvailable() {
        return serverAvailable;
    }

}

package org.dieschnittstelle.mobile.android.skeleton.model;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import org.dieschnittstelle.mobile.android.skeleton.util.DateConverter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Entity
@TypeConverters(DateConverter.class)
public class ToDoItem implements Serializable {

    protected static long ID_GENERATOR = 0;

    public static long nextId() {
        return ++ID_GENERATOR;
    }

    private String name;
    private String description;
    @SerializedName("done")
    private boolean checked;

    @SerializedName("favourite")
    private boolean favourite;

    @SerializedName("expiry")
    private Date expirationDateTime;

    @PrimaryKey(autoGenerate = true)
    private long id;

    public ToDoItem() {
    }

    public ToDoItem(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getExpirationDateTimeString() {
        if (expirationDateTime!= null) {
            return expirationDateTime.toString();
        }
        return "";
    }

    public Date getExpirationDateTime() {
        return expirationDateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setExpirationDateTimeString(String expirationDateTime) {
        this.expirationDateTime = Date.from(Instant.parse(expirationDateTime));
    }

    public void setExpirationDateTime(Date expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDoItem toDoItem = (ToDoItem) o;
        return id == toDoItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package org.dieschnittstelle.mobile.android.skeleton.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import org.dieschnittstelle.mobile.android.skeleton.model.impl.RoomLocalToDoItemCRUDOperationsImpl;
import org.dieschnittstelle.mobile.android.skeleton.util.DateConverter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Entity
@TypeConverters(DateConverter.class)
public class ToDoItem implements Serializable {

    private String name;
    private String description;
    @SerializedName("done")
    private boolean checked;

    @SerializedName("favourite")
    private boolean favourite;

    @SerializedName("expiry")
    private long expirationDateTime;

    @SerializedName("contacts")
    @TypeConverters({RoomLocalToDoItemCRUDOperationsImpl.ArrayListToStringDatabaseConverter.class})
    private ArrayList<String> contacts;

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

    public long getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(long expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public String getExpirationDateTimeString() {
        if (expirationDateTime == 0) {
            return "";
        }
        SimpleDateFormat datetimeformatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return datetimeformatter.format(new Date(expirationDateTime)) + " Uhr";
    }

    public void setDate(int day, int month, int year) {
        Date current = new Date(expirationDateTime);
        current.setDate(day);
        current.setMonth(month);
        current.setYear(year - 1900);

        expirationDateTime = current.getTime();
    }

    public void setTime(int hours, int minutes) {
        Date current = new Date(expirationDateTime);
        current.setHours(hours);
        current.setMinutes(minutes);

        expirationDateTime = current.getTime();
    }

    public boolean isExpired() {
        Date current = new Date();
            if (getExpirationDateTime() < current.getTime() && getExpirationDateTime() > 0) {
                return true;
            } else {
                return false;
            }
    }

    public ArrayList<String> getContacts() {
        if (this.contacts == null) {
            this.contacts = new ArrayList<>();
        }
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
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

package org.dieschnittstelle.mobile.android.skeleton.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ToDoItem implements Serializable {

    protected static long ID_GENERATOR = 0;

    public static long nextId() {
        return ++ID_GENERATOR;
    }

    private String name;
    private String description;
    private boolean checked;
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

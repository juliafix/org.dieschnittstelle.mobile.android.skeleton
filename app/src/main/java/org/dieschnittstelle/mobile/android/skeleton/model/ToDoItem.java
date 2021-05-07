package org.dieschnittstelle.mobile.android.skeleton.model;

import java.io.Serializable;

public class ToDoItem implements Serializable {

    private String name;
    private String description;
    private boolean checked;

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
}

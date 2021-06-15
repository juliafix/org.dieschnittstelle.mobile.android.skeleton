package org.dieschnittstelle.mobile.android.skeleton.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.MainActivity;
import org.dieschnittstelle.mobile.android.skeleton.R;
import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainListitemBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.List;

public class ToDoItemsAdapter extends ArrayAdapter<ToDoItem> {

    private int layoutResource;

    private MainActivity controller;

    public ToDoItemsAdapter(@NonNull Context context, int resource, @NonNull List<ToDoItem> objects, MainActivity controller) {
        super(context, resource, objects);
        this.layoutResource = resource;
        this.controller = controller;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View recyclableItemView, @NonNull ViewGroup parent) {

        View itemView = null;
        ToDoItem currentItem = getItem(position);

        if (recyclableItemView != null) {
            View textView = recyclableItemView.findViewById(R.id.itemName);
            if (textView != null) {

            }
            itemView = recyclableItemView;
            ActivityMainListitemBinding recycledBinding = (ActivityMainListitemBinding) itemView.getTag();
            recycledBinding.setItem(currentItem);
        } else {
            ActivityMainListitemBinding currentBinding =
                    DataBindingUtil.inflate(controller.getLayoutInflater(),
                            this.layoutResource,
                            null,
                            false);
            currentBinding.setItem(currentItem);
            currentBinding.setController(controller);

            itemView = currentBinding.getRoot();
            itemView.setTag(currentBinding);

        }

        return itemView;

    }
}

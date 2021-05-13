package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private ToDoItem item;
    private ActivityDetailviewBinding dataBindingHandle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mit Databinding muss folgendes ausgeführt werden:
        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        item = (ToDoItem) getIntent().getSerializableExtra(ARG_ITEM);

        if (item == null) {
            item = new ToDoItem();
            }

        this.dataBindingHandle.setController(this);

    }

    public void onSaveItem() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, item);

        this.setResult(Activity.RESULT_OK, returnIntent);

        finish();

    }

    public ToDoItem getItem() {
        return item;
    }

    public void setItem(ToDoItem item) {
        this.item = item;
    }
}

package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputEditText;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.Calendar;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private ToDoItem item;
    private ActivityDetailviewBinding dataBindingHandle;
    private DatePickerDialog datePickerDialog;
    private TextInputEditText openDatePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mit Databinding muss folgendes ausgeführt werden:
        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview);
        openDatePicker = findViewById(R.id.dateButton);
        openDatePicker.setText(getTodaysDate());

        item = (ToDoItem) getIntent().getSerializableExtra(ARG_ITEM);

        if (item == null) {
            item = new ToDoItem();
            }

        this.dataBindingHandle.setController(this);

    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
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

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);
                openDatePicker.setText(date);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return dayOfMonth + "/" + month + "/" + year;
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }


}

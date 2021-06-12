package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputEditText;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.generated.callback.OnClickListener;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.Calendar;
import java.util.Date;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private ToDoItem item;
    private ActivityDetailviewBinding dataBindingHandle;
    private DatePickerDialog datePickerDialog;
    private TextInputEditText dateTimeText;
    private Button openDatePicker;
    private Button openTimePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mit Databinding muss folgendes ausgeführt werden:
        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        dateTimeText = findViewById(R.id.itemExpirationDateTime);
        openDatePicker = findViewById(R.id.dateButton);
        openTimePicker = findViewById(R.id.timeButton);

        item = (ToDoItem) getIntent().getSerializableExtra(ARG_ITEM);

        if (item == null) {
            item = new ToDoItem();
            }

        initDatePicker();
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

    public void showContacts() {
        //Intent contactSelectionIntent = new Intent(Intent.ACTION_PICK, );
        //startActivityForResult(contactSelectionIntent,0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                item.setDate(dayOfMonth, month, year);
                dateTimeText.setText(item.getExpirationDateTimeString());

            }
        };

        int year;
        int month;
        int day;
        int style = AlertDialog.THEME_HOLO_LIGHT;

        if (item.getExpirationDateTime() == 0) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        } else {
            Date current = new Date(item.getExpirationDateTime());
            day = current.getDate();
            month = current.getMonth();
            year = current.getYear() + 1900;
        }

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return dayOfMonth + "." + month + "." + year;
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void openTimePicker(View view) {
        int hour;
        int min;
        if (item.getExpirationDateTime() == 0) {
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);
        } else {
            Date current = new Date(item.getExpirationDateTime());
            hour = current.getHours();
            min = current.getMinutes();
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(DetailViewActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                item.setTime(hourOfDay, minute);
                dateTimeText.setText(item.getExpirationDateTimeString());
            }
        }, hour, min, true);
        timePickerDialog.show();
    }


}

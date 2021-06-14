package org.dieschnittstelle.mobile.android.skeleton;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputEditText;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ToDoItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private ToDoItem todo;
    private ActivityDetailviewBinding dataBindingHandle;
    private DatePickerDialog datePickerDialog;
    private TextInputEditText dateTimeText;
    private Button openDatePicker;
    private Button openTimePicker;
    public static final int PICK_CONTACT = 0;
    private ArrayList<String> displayNames = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mit Databinding muss folgendes ausgeführt werden:
        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview);
        dateTimeText = findViewById(R.id.itemExpirationDateTime);
        openDatePicker = findViewById(R.id.dateButton);
        openTimePicker = findViewById(R.id.timeButton);

        todo = (ToDoItem) getIntent().getSerializableExtra(ARG_ITEM);

        if (todo == null) {
            todo = new ToDoItem();
            }

        todo.getContacts().forEach(id -> {
            showContactDetailsForInternalId(Long.parseLong(id));
        });
        initDatePicker();
        this.dataBindingHandle.setController(this);

    }

    public void onSaveItem() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, todo);

        this.setResult(Activity.RESULT_OK, returnIntent);

        finish();

    }

    public ToDoItem getTodo() {
        return todo;
    }

    public void setTodo(ToDoItem todo) {
        this.todo = todo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.selectContact) {
            this.selectContact();
            return true;
        } else if (item.getItemId() == R.id.deleteToDo) {
            //ToDo löschen
        }
        return super.onOptionsItemSelected(item);
    }

    protected void selectContact() {
        Intent selectContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(selectContactIntent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            showContactDetails(data.getData());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void showContactDetails(Uri contactId) {
        int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
            return;
        }
       Cursor cursor = getContentResolver().query(contactId, null, null, null, null);
       if (cursor.moveToFirst()) {
           String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
           long internalContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));

           if (!this.todo.getContacts().contains(String.valueOf(contactId))) {
               this.todo.getContacts().add(String.valueOf(internalContactId));
           }

           Log.i("DetailViewActivity", "Kontakt mit dem Namen " + contactName);
           showContactDetailsForInternalId(internalContactId);
       } else {
           Log.i("DetailViewActivity", "Kontakt mit dem Namen: Kein Kontakt");
       }

    }

    public void showContactDetailsForInternalId(long id) {
        ListView contactList = findViewById(R.id.contactList);
        ArrayAdapter<String> listAdapter;

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + "=?", new String[]{String.valueOf(id)}, null);
        while (cursor.moveToNext()) {
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            displayNames.add(displayName);
            Log.i("DetailViewActivity", "Anzeigename gefunden: " + displayName);
        }
        listAdapter = new ArrayAdapter<String>(this, R.layout.activity_detailview_contact_listitem, displayNames);
        contactList.setAdapter(listAdapter);

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(id)}, null);
        while(cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneNumberType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

            Log.i("DetailViewActivity", "Nummer des Kontakts " + number + " Typ: " + phoneNumberType);
        }
    }


    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                todo.setDate(dayOfMonth, month, year);
                dateTimeText.setText(todo.getExpirationDateTimeString());

            }
        };

        int year;
        int month;
        int day;
        int style = AlertDialog.THEME_HOLO_LIGHT;

        if (todo.getExpirationDateTime() == 0) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        } else {
            Date current = new Date(todo.getExpirationDateTime());
            day = current.getDate();
            month = current.getMonth();
            year = current.getYear() + 1900;
        }

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
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
        if (todo.getExpirationDateTime() == 0) {
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);
        } else {
            Date current = new Date(todo.getExpirationDateTime());
            hour = current.getHours();
            min = current.getMinutes();
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(DetailViewActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                todo.setTime(hourOfDay, minute);
                dateTimeText.setText(todo.getExpirationDateTimeString());
            }
        }, hour, min, true);
        timePickerDialog.show();
    }


}

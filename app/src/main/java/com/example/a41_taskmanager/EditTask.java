package com.example.a41_taskmanager;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class EditTask extends AppCompatActivity {
    TextView heading;
    EditText title, desc;
    SQLiteDatabase database;

    MySQLiteHelper mySQLiteHelper;
    Task task = null;

    String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_task);

        mySQLiteHelper = new MySQLiteHelper(this);
        database = mySQLiteHelper.getWritableDatabase();
        heading = findViewById(R.id.heading);
        title = findViewById(R.id.et_title);
        desc = findViewById(R.id.et_description);
        Intent intent = getIntent();
        task = (Task) intent.getSerializableExtra("task");

        if(task != null) {
            heading.setText("Editing the Task: " + task.id);
            title.setText(task.title);
            desc.setText(task.description);
            date = task.dueDate;
            Button cre = findViewById(R.id.btnAddTaskToDb);
            cre.setText("Update");
        }

        findViewById(R.id.btnAddTaskToDb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataToDB();
            }
        });

        findViewById(R.id.buttonPickDateTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int sy, int sm, int sd) {
                        date = sd + "/" + sm + "/"  + sy;
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void addDataToDB() {
        String titleText = title.getText().toString();
        String descText = desc.getText().toString();
        if(titleText.isEmpty() || descText.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put("title", titleText);
        values.put("description", descText);
        values.put("dueDate", date);
        if(task == null) {
            database.insert("tasks", null, values);
            Toast.makeText(this, "Task created", Toast.LENGTH_SHORT).show();
        }else{
            database.update("tasks", values, "id = ?", new String[]{String.valueOf(task.id)});
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
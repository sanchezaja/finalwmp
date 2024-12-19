package com.example.finalexam;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class EnrollmentActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int studentId;
    private ArrayList<String> subjectsList = new ArrayList<>();
    private ArrayList<Integer> subjectIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        dbHelper = new DatabaseHelper(this);
        studentId = getIntent().getIntExtra("studentId", -1);

        // Load subjects from database
        loadSubjects();

        // Set up ListView for subjects
        ListView listView = findViewById(R.id.listViewSubjects);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjectsList);
        listView.setAdapter(adapter);

        // Handle subject selection
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> enrollSubject(position));

        findViewById(R.id.buttonViewSummary).setOnClickListener(view -> {
            Intent intent = new Intent(EnrollmentActivity.this, SummaryActivity.class);
            intent.putExtra("studentId", studentId); // Pass student ID ke SummaryActivity
            startActivity(intent);
        });
    }

    private void loadSubjects() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM subjects", null);

        while (cursor.moveToNext()) {
            subjectsList.add(cursor.getString(1)); // Subject name
            subjectIds.add(cursor.getInt(0)); // Subject ID
        }
        cursor.close();
    }

    private void enrollSubject(int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Validate total credits
        Cursor cursor = db.rawQuery(
                "SELECT SUM(credits) FROM enrollments e JOIN subjects s ON e.subject_id = s.id WHERE e.student_id = ?",
                new String[]{String.valueOf(studentId)}
        );
        if (cursor.moveToFirst() && cursor.getInt(0) + getSubjectCredits(position) > 24) {
            Toast.makeText(this, "Credit limit exceeded!", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("subject_id", subjectIds.get(position));
        db.insert("enrollments", null, values);

        Toast.makeText(this, "Subject enrolled successfully", Toast.LENGTH_SHORT).show();
        cursor.close();
    }

    private int getSubjectCredits(int position) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT credits FROM subjects WHERE id = ?", new String[]{String.valueOf(subjectIds.get(position))});
        int credits = 0;
        if (cursor.moveToFirst()) {
            credits = cursor.getInt(0);
        }
        cursor.close();
        return credits;
    }
}

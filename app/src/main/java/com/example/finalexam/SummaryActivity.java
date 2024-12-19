package com.example.finalexam;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int studentId;
    private ArrayList<String> enrolledSubjects = new ArrayList<>();
    private ArrayList<Integer> enrolledSubjectIds = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int totalCredits = 0; // Total credits state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        dbHelper = new DatabaseHelper(this);
        studentId = getIntent().getIntExtra("studentId", -1);

        loadEnrollmentSummary();
        setupRemoveSubjectListener(); // Menambahkan listener untuk penghapusan
    }

    private void loadEnrollmentSummary() {
        ListView listView = findViewById(R.id.listViewSummary);
        TextView totalCreditsView = findViewById(R.id.textViewTotalCredits);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        enrolledSubjects.clear(); // Membersihkan list sebelumnya
        enrolledSubjectIds.clear(); // Membersihkan ID sebelumnya
        totalCredits = 0;

        Cursor cursor = db.rawQuery(
                "SELECT s.id, s.subject_name, s.credits FROM enrollments e " +
                        "JOIN subjects s ON e.subject_id = s.id " +
                        "WHERE e.student_id = ?", new String[]{String.valueOf(studentId)}
        );

        Log.d("SummaryActivity", "Query Result Count: " + cursor.getCount());

        while (cursor.moveToNext()) {
            int subjectId = cursor.getInt(0); // ID Mata Kuliah
            String subjectName = cursor.getString(1);
            int credits = cursor.getInt(2);

            enrolledSubjectIds.add(subjectId); // Tambahkan ID ke daftar
            enrolledSubjects.add(subjectName + " (" + credits + " credits)");
            totalCredits += credits;
        }
        cursor.close();

        // Set data ke ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, enrolledSubjects);
        listView.setAdapter(adapter);

        // Tampilkan total kredit
        totalCreditsView.setText("Total Credits: " + totalCredits);
    }

    // Tambahkan listener untuk klik lama pada list item
    private void setupRemoveSubjectListener() {
        ListView listView = findViewById(R.id.listViewSummary);
        listView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            removeSubject(position); // Panggil fungsi removeSubject
            return true;
        });
    }

    private void removeSubject(int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Dapatkan ID mata kuliah yang akan dihapus
        int subjectId = enrolledSubjectIds.get(position);
        int credits = getSubjectCredits(subjectId); // Dapatkan jumlah kredit mata kuliah

        // Hapus dari tabel enrollments
        db.delete("enrollments", "student_id = ? AND subject_id = ?",
                new String[]{String.valueOf(studentId), String.valueOf(subjectId)});

        // Hapus dari daftar
        enrolledSubjects.remove(position);
        enrolledSubjectIds.remove(position);

        // Kurangi total kredit
        totalCredits -= credits;

        // Perbarui tampilan
        adapter.notifyDataSetChanged();
        TextView totalCreditsView = findViewById(R.id.textViewTotalCredits);
        totalCreditsView.setText("Total Credits: " + totalCredits);

        // Berikan feedback ke pengguna
        Toast.makeText(this, "Subject removed successfully. Credits reduced by " + credits, Toast.LENGTH_SHORT).show();
    }

    private int getSubjectCredits(int subjectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT credits FROM subjects WHERE id = ?", new String[]{String.valueOf(subjectId)});
        int credits = 0;
        if (cursor.moveToFirst()) {
            credits = cursor.getInt(0);
        }
        cursor.close();
        return credits;
    }
}

package com.example.finalexam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "school.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL" +
                ");");

        db.execSQL("CREATE TABLE subjects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subject_name TEXT NOT NULL, " +
                "credits INTEGER NOT NULL" +
                ");");

        db.execSQL("CREATE TABLE enrollments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER NOT NULL, " +
                "subject_id INTEGER NOT NULL, " +
                "FOREIGN KEY(student_id) REFERENCES students(id), " +
                "FOREIGN KEY(subject_id) REFERENCES subjects(id)" +
                ");");

        // Insert example subjects
        db.execSQL("INSERT INTO subjects (subject_name, credits) VALUES ('Computer Organization and Architecture', 3), ('Calculus', 3), ('Discrete Mathematic', 3), ('Economic Survival', 3), ('Website Programming', 3), ('Programming Concept', 3), ('Survival English', 0), ('Object Oriented Visual Programming', 3), ('Database System', 3), ('Server-Side Internet Programming', 3), ('Computer Network', 3), ('Linear Algebra', 3), ('Probability and Statistics', 3), ('Fluency and Speed Development', 0), ('Accuracy Development', 0), ('Academic Writing', 0), ('Network Security', 3), ('Data Structure and Algorithm', 3), ('Wireless and Mobile Programming', 3), ('3D Computer Graphics and Animation', 3);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS enrollments");
        db.execSQL("DROP TABLE IF EXISTS subjects");
        db.execSQL("DROP TABLE IF EXISTS students");
        onCreate(db);
    }
}

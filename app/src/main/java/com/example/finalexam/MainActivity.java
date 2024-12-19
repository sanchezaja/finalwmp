package com.example.finalexam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonLogin).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, LoginActivity.class)));

        findViewById(R.id.buttonRegister).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }
}

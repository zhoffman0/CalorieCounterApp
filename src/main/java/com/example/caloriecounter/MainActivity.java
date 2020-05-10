package com.example.caloriecounter;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //stetho (stetho is used to browse database. In Google Chrome, go to chrome://inspect
        // find the app, Calorie Counter, choose inspect. In the new window, click on Resources tab.
        // the database is under Web SQL on left)
        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        //Database
        DBAdapter db = new DBAdapter(this);
        db.open();
        //setup for food
        //count rows in food
        int numberRows = db.count("food");
        if(numberRows < 1){
            Toast.makeText(this, "Loading setup.....", Toast.LENGTH_LONG).show();
            DBSetupInsert setupInsert = new DBSetupInsert(this);
            setupInsert.insertAllFood();
            setupInsert.insertAllCategories();
            Toast.makeText(this, "Setup Complete", Toast.LENGTH_LONG).show();
        }

        //check if user is in table
        //count rows in the table
        numberRows = db.count("users");
        if(numberRows < 1){
            // Sign up
            Toast.makeText(this, "You are only few fields away from signing up...", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, SignUp.class);
            startActivity(i);
        }


        db.close();

        Toast.makeText(this, "Database works, food created!", Toast.LENGTH_SHORT).show();
    }
}

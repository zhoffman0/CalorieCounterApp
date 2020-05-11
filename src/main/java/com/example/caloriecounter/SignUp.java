package com.example.caloriecounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RadialGradient;
import android.os.Bundle;
import android.view.InflateException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.spec.ECField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignUp extends AppCompatActivity {


    private String[] arraySpinnerDOBDay = new String[31];
    private String[] arraySpinnerDOBDYear = new String[100];;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //fills numbers for birth days
        for(int x =0; x < 31; x++)
        {
            int day = x+1;

            //adds a zero infront of the spinner number so that we don't have to do it later and it looks nicer
            if(x < 9)
            {
                this.arraySpinnerDOBDay[x] = "0"+ day;
            }
            else
            {
                this.arraySpinnerDOBDay[x] = ""+ day;
            }

        }

        //populates day spinner
        Spinner spinnerDOBDay = (Spinner) findViewById(R.id.spinnerDOBDay);
        ArrayAdapter<String> adapterDay = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinnerDOBDay);
        spinnerDOBDay.setAdapter(adapterDay);



        //get's current year
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);

        //fills year
        for(int x =0; x < 100; x++)
        {
            int year = currentYear-x;
            this.arraySpinnerDOBDYear[x] = ""+ year;
        }

        //populates year spinner
        Spinner spinnerDOBYear = (Spinner) findViewById(R.id.spinnerDOBYear);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinnerDOBDYear);
        spinnerDOBYear.setAdapter(adapterYear);

        //Button listener
        Button buttonSignUp = (Button)findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpSubmit();
            }
        });

        //Measurements Spinner Listener
        Spinner spinnerMesurment = (Spinner)findViewById(R.id.spinnerMeasurements);
        spinnerMesurment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                measurementsChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // mesurmentChanged();
            }
        });

    }


    //class that actually processes the signup information
    public void signUpSubmit()
    {
        int intDOBDay;
        int intDOBYear;


        boolean errorBool = false;

        String DOB;

        Toast.makeText(this, "Sign up in process, please wait", Toast.LENGTH_LONG).show();

        //Gets Edit Texts
        EditText editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        EditText editTextHeight = (EditText)findViewById(R.id.editTextHeightFeet);
        EditText editTextHeightInches = (EditText)findViewById(R.id.editTextHeightInches);
        EditText editTextWeight = (EditText)findViewById(R.id.editTextWeight);

        //Gets Spinners
        Spinner DOBMonthSpinner = (Spinner)findViewById(R.id.spinnerDOBMonth);
        Spinner DOBDaySpinner = (Spinner)findViewById(R.id.spinnerDOBDay);
        Spinner DOBYearSpinner = (Spinner)findViewById(R.id.spinnerDOBYear);
        Spinner measurementSpinner = (Spinner)findViewById(R.id.spinnerMeasurements);
        Spinner activityLevelSpinner = (Spinner)findViewById(R.id.spinnerActivityLevel);

        //Gets TextViews
        TextView tvEmail = (TextView)findViewById(R.id.textViewEmail);
        TextView tvDOB = (TextView)findViewById(R.id.textViewDOB);

        //Gets radio groups
        RadioGroup rgGender = (RadioGroup)findViewById(R.id.radioGroupGender);

        //Email
        String stringEmail = editTextEmail.getText().toString();
        if(stringEmail.isEmpty()  || stringEmail.startsWith(" "))
        {
            tvEmail.setTextColor(Color.RED);
            Toast.makeText(this, "Error getting email", Toast.LENGTH_LONG).show();
            errorBool = true;
        }

        //Getting DOB form the spinners
        //DOB Month
        String stringDOBMonth = DOBMonthSpinner.getSelectedItem().toString();
        //DOB Day
        String stringDOBDay = DOBDaySpinner.getSelectedItem().toString();
        //DOB Year
        String stringDOBYear = DOBYearSpinner.getSelectedItem().toString();

        //Checks to see if any of the DOB fields are empty, cause that will crash the program if we try to parse an empty String
        if(stringDOBMonth.isEmpty() || stringDOBDay.isEmpty() || stringDOBYear.isEmpty())
        {
            tvDOB.setTextColor(Color.RED);
            Toast.makeText(this, "Error getting Date of Birth", Toast.LENGTH_LONG).show();
            errorBool = true;
        }

        //Converts the months from a name to a number
        stringDOBMonth = monthConverter(stringDOBMonth);
        //puts every thing together
        DOB = stringDOBYear + "-" + stringDOBMonth + "-" + stringDOBYear;

        //gender

        RadioGroup rbGroupGender = (RadioGroup)findViewById(R.id.radioGroupGender);
        int selectedId = rgGender.getCheckedRadioButtonId();
        RadioButton rbGender = (RadioButton) findViewById(selectedId);
        int position = rbGroupGender.indexOfChild(rbGender);

        String stringGender = "";
        if(position == 0){
            stringGender = "male";
        }
        else{
            stringGender = "female";
        }


        //height
        String stringHeight = editTextHeight.getText().toString();
        String stringHeightInches = editTextHeightInches.getText().toString();

        double heightCm = 0;
        //double heightFeet,
        double heightInches = 0;

        //convert cm
        try {
            heightCm = Math.round(Double.parseDouble(stringHeight));
        }
        catch (NumberFormatException e)
        {
            errorBool = true;
            Toast.makeText(this, "Error getting height " + e, Toast.LENGTH_LONG).show();
        }

        //convert in if it's not empty
        if(!stringHeightInches.isEmpty()) {
            try {
                heightInches = Double.parseDouble(stringHeightInches);
            } catch (NumberFormatException e) {
                errorBool = true;
                Toast.makeText(this, "Error getting height " + e, Toast.LENGTH_LONG).show();
            }
        }
        //metric or imperial
        String stringMeasurement = measurementSpinner.getSelectedItem().toString();
        boolean metric = false;


        //converts to metric if imperial
        if(stringMeasurement.startsWith("I"))
        {
            //converts feet and inches to cm
            heightCm = Math.round((heightCm*12 + heightInches)*2.54);

            //Toast.makeText(this, "In CM" + heightCm, Toast.LENGTH_LONG).show();
        }
        else
        {
            metric = true;
            //do nothing this is a place holder that can be yeeted later if need be
        }


        //weight
        String stringWeight = editTextWeight.getText().toString();
        double doubleWeight = 0;

        try {
            doubleWeight = Double.parseDouble(stringWeight);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error getting weight " + e, Toast.LENGTH_LONG).show();
        }

        if (metric == false)
        {
            doubleWeight = Math.round(doubleWeight*0.45359237);
        }

        int intActivityLevel = activityLevelSpinner.getSelectedItemPosition();

        if(!errorBool)
        {
            DBAdapter db = new DBAdapter(this);
            db.open();

            String stringEmailSQL = db.quoteSmart(stringEmail);
            String stringDateOfBirthSQL = db.quoteSmart(DOB);
            String stringGenderSQL = db.quoteSmart(stringGender);
            double heightCmSQL = db.quoteSmart(heightCm);
            int intActivityLevelSQL = db.quoteSmart(intActivityLevel);
            double doubleWeightSQL = db.quoteSmart(doubleWeight);
            String stringMeasurementSQL = db.quoteSmart(stringMeasurement);


            String stringInput =
                    "NULL, " + stringEmailSQL + "," + stringDateOfBirthSQL + "," + stringGenderSQL + "," + heightCmSQL + "," + intActivityLevelSQL + "," + doubleWeightSQL + "," + stringMeasurementSQL;

            db.insert("users","user_id, user_email, user_dob, user_gender, user_height, user_activity_level, user_weight, user_mesurment" , stringInput);


            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            String goalDate = df1.format((Calendar.getInstance().getTime()));

            String godlDate = df1.format(Calendar.getInstance().getTime());

            String goalDateSQL = db.quoteSmart(goalDate);

            stringInput = "NULL, " + doubleWeightSQL + "," + goalDateSQL;
            db.insert("goal",
                    "goal_id, goal_current_weight, goal_date",
                    stringInput);

            db.close();

            Intent i = new Intent(SignUp.this, SignUpGoal.class);
            startActivity(i);
        }

    }


    //handles the measurements Spinner being changed
    private void measurementsChanged()
    {
        //Getting all the edit texts
        EditText editTextHeight = (EditText)findViewById(R.id.editTextHeightFeet);
        EditText editTextHeightInches = (EditText)findViewById(R.id.editTextHeightInches);

        //Convert those to strings
        String stringHeight = editTextHeight.getText().toString();
        String stringHeightInches = editTextHeightInches.getText().toString();

        // Measurement spinner
        Spinner spinnerMeasurement = (Spinner)findViewById(R.id.spinnerMeasurements);
        String stringMeasurement = spinnerMeasurement.getSelectedItem().toString();

        TextView textViewFtIn = (TextView)findViewById(R.id.textViewFootInches);
        TextView textViewLb = (TextView)findViewById(R.id.textViewPounds);

        double heightCm = 0;
        double heightFeet = 0;
        double heightInches = 0;

        if(stringMeasurement.startsWith("I")){
            // Imperial
            editTextHeightInches.setVisibility(View.VISIBLE);
            textViewFtIn.setText("feet and inches");
            textViewLb.setText("pounds");

            try {
                heightCm = Double.parseDouble(stringHeight);
            }
            catch (Exception e)
            {
                //do nothing
            }

            if(heightCm != 0)
            {
                //Convert CM to Feet
                int intHeightFeet = (int) ((heightCm* 0.3937008)/12);
                editTextHeight.setText("" + intHeightFeet);
            }

        }
        else{
            // Metric
            editTextHeightInches.setVisibility(View.GONE);
            textViewFtIn.setText("cm");
            textViewLb.setText("kg");

            try{
                heightFeet = Double.parseDouble(stringHeight);
            }
            catch (Exception e)
            {
                //do nothing
            }

            try {
                heightInches = Double.parseDouble(stringHeightInches);
            }
            catch (Exception e)
            {
                //do nothing
            }

            //Convert from imperial to cm
            if(heightFeet != 0 )//&& heightInches != 0)
            {
                heightCm = ((heightFeet*12) + heightInches)* 2.54;
                heightCm = Math.round(heightCm);
                editTextHeight.setText("" + heightCm);
            }
        }


        //weight

        EditText editTextWeight = (EditText)findViewById(R.id.editTextWeight);
        String stringWeight = editTextWeight.getText().toString();

        double doubleWeight = 0;

        try {
            doubleWeight = Double.parseDouble(stringWeight);
        }
        catch (Exception e)
        {
            //do nothing

        }

        if(doubleWeight != 0)
        {
            if(stringMeasurement.startsWith("I"))
            {
                //kg to pounds
                doubleWeight = Math.round(doubleWeight / 0.45359237);
            }
            else
            {
                doubleWeight = Math.round(doubleWeight * 0.45359237);
            }

            editTextWeight.setText("" + doubleWeight);
        }

    }

    //Converts the months from a name to a number
    public String monthConverter(String month)
    {
        String monthOut = "";

        if(month.startsWith("Jan")){
            monthOut = "01";
        }
        else if(month.startsWith("Feb")){
            monthOut = "02";
        }
        else if(month.startsWith("Feb")){
            monthOut = "02";
        }
        else if(month.startsWith("Mar")){
            monthOut = "03";
        }
        else if(month.startsWith("Apr")){
            monthOut = "04";
        }
        else if(month.startsWith("May")){
            monthOut = "05";
        }
        else if(month.startsWith("Jun")){
            monthOut = "06";
        }
        else if(month.startsWith("Jul")){
            monthOut = "07";
        }
        else if(month.startsWith("Aug")){
            monthOut = "08";
        }
        else if(month.startsWith("Sep")){
            monthOut = "09";
        }
        else if(month.startsWith("Oct")){
            monthOut = "10";
        }
        else if(month.startsWith("Nov")){
            monthOut = "11";
        }
        else if(month.startsWith("Dec")){
            monthOut = "12";
        }

        return monthOut;
    }

}

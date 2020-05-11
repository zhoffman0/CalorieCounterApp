package com.example.caloriecounter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SignUpGoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_goal);


        /* Listener submit */
        Button buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signUpGoalSubmit();
            }
        });

        /* Remove error handling */
        hideErrorHandling();

        /* Mesurment used? */
        mesurmentUsed();

    } // onCreate

    /* signUpGoalSubmit ----------------------------------------------------- */
    public void signUpGoalSubmit(){
        /* Open database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        /* Error */
        ImageView imageViewError = (ImageView)findViewById(R.id.imageViewError);
        TextView textViewErrorMessage = (TextView)findViewById(R.id.textViewErrorMessage);
        String errorMessage = "";


        /* Get target weight */
        EditText editTextTargetWeight = (EditText)findViewById(R.id.editTextTargetWeight);
        String stringTargetWeight = editTextTargetWeight.getText().toString();
        double doubleTargetWeight = 0;
        try{
            doubleTargetWeight = Double.parseDouble(stringTargetWeight);
        }
        catch(NumberFormatException nfe) {
            errorMessage = "Target weight has to be a number.";
        }

        /* Spinner IWantTo */
        // 0 - Loose weight
        // 1 - Gain weight
        Spinner spinnerIWantTo = (Spinner)findViewById(R.id.spinnerIWantTo);
        int intIWantTo = spinnerIWantTo.getSelectedItemPosition();

        /* Spinner spinnerWeeklyGoal */
        Spinner spinnerWeeklyGoal = (Spinner)findViewById(R.id.spinnerWeeklyGoal);
        String stringWeeklyGoal = spinnerWeeklyGoal.getSelectedItem().toString();


        /* Update fields */
        if(errorMessage.isEmpty()){

            long goalID = 1;

            double doubleTargetWeightSQL = db.quoteSmart(doubleTargetWeight);
            db.update("goal", "goal_id", goalID, "goal_target_weight", doubleTargetWeightSQL);

            int intIWantToSQL = db.quoteSmart(intIWantTo);
            db.update("goal", "goal_id", goalID, "goal_i_want_to", intIWantToSQL);

            String stringWeeklyGoalSQL = db.quoteSmart(stringWeeklyGoal);
            db.update("goal", "goal_id", goalID, "goal_weekly_goal", stringWeeklyGoalSQL);

        }

        /* Calculate cal */
        if(errorMessage.isEmpty()){

            // Get row number one from users
            long rowID = 1;
            String fields[] = new String[] {
                    "user_id",
                    "user_dob",
                    "user_gender",
                    "user_height",
                    "user_activity_level"
            };
            Cursor c = db.selectPrimaryKey("users", "user_id", rowID, fields);
            String stringUserDob = c.getString(1);
            String stringUserGender  = c.getString(2);
            String stringUserHeight = c.getString(3);
            String stringUserActivityLevel = c.getString(4);

            // Get Age
            String[] items1 = stringUserDob.split("-");
            String stringYear = items1[0];
            String stringMonth = items1[1];
            String stringDay = items1[2];

            int intYear = 0;
            try {
                intYear = Integer.parseInt(stringYear);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            int intMonth = 0;
            try {
                intMonth = Integer.parseInt(stringMonth);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            int intDay = 0;
            try {
                intDay = Integer.parseInt(stringDay);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            String stringUserAge = getAge(intYear, intMonth, intDay);

            int intUserAge = 0;
            try {
                intUserAge = Integer.parseInt(stringUserAge);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            // Height
            double doubleUserHeight = 0;
            try {
                doubleUserHeight = Double.parseDouble(stringUserHeight);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
            //Toast.makeText(this, "DOB=" + stringUserDob + "\nAge=" + stringUserAge + "\nGender=" + stringUserGender + "\nHeight=" + stringUserHeight + "\nActivity level=" + stringUserActivityLevel, Toast.LENGTH_LONG).show();

            // Start calculation
            double bmr = 0;
            if(stringUserGender.startsWith("m")){
                // Male
                // BMR = 66.5 + (13.75 x kg body weight) + (5.003 x height in cm) - (6.755 x age)
                bmr = 66.5+(13.75*doubleTargetWeight)+(5.003*doubleUserHeight)-(6.755*intUserAge);
                //bmr = Math.round(bmr);
                //Toast.makeText(this, "BMR formula: 66.5+(13.75*" + doubleTargetWeight + ")+(5.003*" + doubleUserHeight + ")-(6.755*" + intUserAge, Toast.LENGTH_LONG).show();

            } // if(stringUserGender.startsWith("m")){
            else{
                // Female
                // BMR = 55.1 + (9.563 x kg body weight) + (1.850 x height in cm) - (4.676 x age)
                bmr = 655+(9.563*doubleTargetWeight)+(1.850*doubleUserHeight)-(4.676*intUserAge);
                //bmr = Math.round(bmr);
            }
            bmr = Math.round(bmr);
            long goalID = 1;
            double calBmrSQL = db.quoteSmart(bmr);
            db.update("goal", "goal_id", goalID, "goal_cal_bmr", calBmrSQL);
            //Toast.makeText(this, "BMR before activity: " + bmr, Toast.LENGTH_LONG).show();


            // with activity
            // Taking in to account activity
            double calWithActivity = 0;
            if(stringUserActivityLevel.equals("0")) {
                calWithActivity = bmr * 1.2;
            }
            else if(stringUserActivityLevel.equals("1")) {
                calWithActivity = bmr * 1.375; // slightly_active
            }
            else if(stringUserActivityLevel.equals("2")) {
                calWithActivity = bmr*1.55; // moderately_active
            }
            else if(stringUserActivityLevel.equals("3")) {
                calWithActivity = bmr*1.725; // active_lifestyle
            }
            else if(stringUserActivityLevel.equals("3")) {
                calWithActivity = bmr * 1.9; // very_active
            }
            calWithActivity = Math.round(calWithActivity);
            double calWithActivitySQL = db.quoteSmart(calWithActivity);
            db.update("goal", "goal_id", goalID, "goal_cal_with_activity", calWithActivitySQL);
            //Toast.makeText(this, "BMR after activity: " + bmr, Toast.LENGTH_LONG).show();

            // Loose or gain weight?
            double doubleWeeklyGoal = 0;
            try {
                doubleWeeklyGoal = Double.parseDouble(stringWeeklyGoal);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            // 1 kg fat = 7700 kcal
            double kcal = 0;
            double calWithActivityAndDiet = 0;
            kcal = 7700*doubleWeeklyGoal;
            if(intIWantTo == 0){
                // Loose weight
                calWithActivityAndDiet = Math.round(bmr - (kcal/7));

            }
            else{
                // Gain weight
                calWithActivityAndDiet = Math.round(bmr + (kcal/7));
            }

            // Update database
            double calWithActivityAndDietSQL = db.quoteSmart(calWithActivityAndDiet);
            db.update("goal", "goal_id", goalID, "goal_cal_with_activity_and_diet", calWithActivityAndDietSQL);


            // Calcualte proteins
            // 20-25 % protein
            // 40-50 % carbs
            // 25-35 % fat
            double proteins = Math.round(calWithActivityAndDiet*25/100);
            double carbs = Math.round(calWithActivityAndDiet*50/100);
            double fat = Math.round(calWithActivityAndDiet*25/100);

            double proteinsSQL = db.quoteSmart(proteins);
            double carbsSQL = db.quoteSmart(carbs);
            double fatSQL = db.quoteSmart(fat);
            db.update("goal", "goal_id", goalID, "goal_proteins", proteinsSQL);
            db.update("goal", "goal_id", goalID, "goal_carbs", carbsSQL);
            db.update("goal", "goal_id", goalID, "goal_fat", fatSQL);

        } //  /* Calculate cal */



        // Error handling
        if(!(errorMessage.isEmpty())){
            // There is error
            textViewErrorMessage.setText(errorMessage);
            imageViewError.setVisibility(View.VISIBLE);
            textViewErrorMessage.setVisibility(View.VISIBLE);

        }

        /* Close db */
        db.close();

        /* Move to main activity */
        if(errorMessage.isEmpty()){
            Intent i = new Intent(SignUpGoal.this, MainActivity.class);
            startActivity(i);
        }
    } // signUpGoalSubmit

    /* hideErrorHandling --------------------------------------------------- */
    public void hideErrorHandling(){
        /* Hide error icon and message */
        ImageView imageViewError = (ImageView)findViewById(R.id.imageViewError);
        imageViewError.setVisibility(View.GONE);

        TextView textViewErrorMessage = (TextView)findViewById(R.id.textViewErrorMessage);
        textViewErrorMessage.setVisibility(View.GONE);

    }

    /* mesurmentUsed ------------------------------------------------------- */
    public void mesurmentUsed(){
        /* Open database */
        DBAdapter db = new DBAdapter(this);
        db.open();

        /* Get row number one from users */
        long rowID = 1;
        String fields[] = new String[] {
                "user_id",
                "user_mesurment"
        };
        Cursor c = db.selectPrimaryKey("users", "user_id", rowID, fields);
        String mesurment;
        mesurment = c.getString(1);

        // Metric or imperial?
        if(mesurment.startsWith("m")){
            // Metric
        }
        else{
            // Imperial

            // Kg to punds
            TextView textViewTargetMesurmentType = (TextView)findViewById(R.id.textViewTargetMesurmentType);
            textViewTargetMesurmentType.setText("pounds");


            // Kg each week to pounds each week
            TextView textViewKgEachWeek = (TextView)findViewById(R.id.textViewKgEachWeek);
            textViewKgEachWeek.setText("pounds each week");
        }


        /* Close database */
        db.close();
    }

    /* getAge -------------------------------------------------------------- */
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
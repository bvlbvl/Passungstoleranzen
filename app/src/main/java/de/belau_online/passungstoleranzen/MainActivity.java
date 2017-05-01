package de.belau_online.passungstoleranzen;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String queryresult;
    SQLiteDatabase db;
    int firsttoleranceentry = 3;
    Spinner spinnerBore;
    Spinner spinnerShaft;
    EditText editTextDiameter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.db = handleDataBase(this.db);
        calculatePossibleEnties(this.db);

        editTextDiameter = (EditText) findViewById(R.id.editDiameter);

        spinnerBore = (Spinner) findViewById(R.id.planets_spinnerBore);
        spinnerBore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                calcPass();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinnerShaft = (Spinner) findViewById(R.id.planets_spinnerShaft);
        spinnerShaft.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                calcPass();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    */

    private SQLiteDatabase handleDataBase(SQLiteDatabase db) {
        DataBaseHelper myDbHelper;
        myDbHelper = new DataBaseHelper(this);

        try {

            myDbHelper.createDataBase();
            System.out.println("done");
            String testquery = "SELECT [4] FROM grundtoleranzen "
                    + " WHERE  von <=	450  AND bis >450";
            // testquery =
            // "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
            db = myDbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(testquery, new String[] {});
            cursor.moveToFirst();
            queryresult = cursor.getString(0);
            System.out.println(queryresult);
        } catch (IOException ioe) {

            throw new Error("Unable to create database", ioe);

        } catch (Exception e) {
            System.out.println("some other exception");
            System.out.println(e);
        }
        return db;
    }

    private void calculatePossibleEnties(SQLiteDatabase db) {
        String query;

        // bore
        query = "select * from bohrungen";
        try {
            Cursor c = db.rawQuery(query, new String[] {});
            c.moveToFirst();
            String[] columnNames = c.getColumnNames();

            ArrayList<String> boreEntryList = new ArrayList<String>();

            for (int i = firsttoleranceentry; i < columnNames.length; i++) {
                query = "SELECT " + columnNames[i]
                        + " FROM bohrungen where name =\"grundtoleranz_von\"";
                c = db.rawQuery(query, new String[] {});
                c.moveToFirst();
                int von = c.getInt(0);

                query = "SELECT " + columnNames[i]
                        + " FROM bohrungen where name =\"grundtoleranz_bis\"";
                c = db.rawQuery(query, new String[] {});
                c.moveToFirst();
                int bis = c.getInt(0);

                for (int j = von; j <= bis; j++) {
                    boreEntryList.add(columnNames[i] + j);
                }
            }

            Spinner spinner = (Spinner) findViewById(R.id.planets_spinnerBore);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_dropdown_item,
                    boreEntryList);
            spinner.setAdapter(spinnerArrayAdapter);

            // shaft
            query = "select * from wellen";
            c = db.rawQuery(query, new String[] {});
            c.moveToFirst();
            columnNames = c.getColumnNames();

            boreEntryList = new ArrayList<String>();

            for (int i = firsttoleranceentry; i < columnNames.length; i++) {
                query = "SELECT " + columnNames[i]
                        + " FROM wellen where name =\"grundtoleranz_von\"";
                c = db.rawQuery(query, new String[] {});
                c.moveToFirst();
                int von = c.getInt(0);

                query = "SELECT " + columnNames[i]
                        + " FROM wellen where name =\"grundtoleranz_bis\"";
                c = db.rawQuery(query, new String[] {});
                c.moveToFirst();
                int bis = c.getInt(0);

                for (int j = von; j <= bis; j++) {
                    boreEntryList.add(columnNames[i] + j);
                }
            }

            spinner = (Spinner) findViewById(R.id.planets_spinnerShaft);
            spinnerArrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    boreEntryList);
            spinner.setAdapter(spinnerArrayAdapter);

        } catch (Exception e) {
            System.out.println("upps2");
            e.printStackTrace();
        }
    }

    public void showToast(String text) {
        Context context = getApplicationContext();
        // CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        System.out.println(text);
    }

    private void calcPass() {

        String itwelle = "";
        String itbohrung = "";
        String grundabmasswelle = "";
        String grundabmassbohrung = "";
        double durchmesser = 0;

        String valBore = spinnerBore.getSelectedItem().toString();
        String valShaft = spinnerShaft.getSelectedItem().toString();
        String stringDiameter = editTextDiameter.getText().toString();
        if (stringDiameter.equals("")) {
            showToast("Bitte Durchmesser eingeben");
        } else {
            showToast(valBore);
            try {
                durchmesser = Double.parseDouble(stringDiameter);
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                showToast("Durchmesser hat ein falsches Format");
            }
            grundabmasswelle = valShaft.substring(0, 1);
            itwelle = valShaft.substring(1);
            grundabmassbohrung = valBore.substring(0, 1);
            itbohrung = valBore.substring(1);

            // Grundtoleranzen welle
            try {
                Cursor cursor = db.rawQuery("SELECT [" + itwelle
                        + "] FROM grundtoleranzen WHERE von <=" + durchmesser
                        + " AND bis >" + durchmesser, new String[] {});
                cursor.moveToFirst();
                String valuegrundtoleranzenwelle = cursor.getString(0);
                System.out.println("From grundtoleranzen wellen: "
                        + valuegrundtoleranzenwelle);
                double gtw = Double.parseDouble(valuegrundtoleranzenwelle);
                System.out.println("gtw: " + gtw);

                // Wellen
                cursor = db.rawQuery("SELECT " + grundabmasswelle
                        + " FROM wellen WHERE von <=" + durchmesser + " AND bis >"
                        + durchmesser, new String[] {});
                cursor.moveToFirst();
                String valuewellen = cursor.getString(0);
                System.out.println("From wellen: " + valuewellen);

                cursor = db.rawQuery("SELECT " + grundabmasswelle
                        + " FROM wellen WHERE name == \"abmass\"", new String[] {});
                cursor.moveToFirst();
                String valuewellenabmass = cursor.getString(0);
                System.out.println("From wellen: " + valuewellenabmass);

                double uaw;
                double oaw;
                if (valuewellenabmass.equals("unteres")) {
                    uaw = Double.parseDouble(valuewellen);
                    oaw = gtw + uaw;
                } else {
                    oaw = Double.parseDouble(valuewellen);
                    uaw = oaw - gtw;
                }
                System.out.println("Welle unteres abmass: " + uaw
                        + " , oberes abmass: " + oaw);

                // Grundtoleranzen bohrung
                cursor = db.rawQuery("SELECT [" + itbohrung
                        + "] FROM grundtoleranzen WHERE von <=" + durchmesser
                        + " AND bis >" + durchmesser, new String[] {});
                cursor.moveToFirst();
                String valuegrundtoleranzenbohrung = cursor.getString(0);
                System.out.println("From grundtoleranzen bohrung: "
                        + valuegrundtoleranzenbohrung);
                double gtb = Double.parseDouble(valuegrundtoleranzenbohrung);
                System.out.println("gtb: " + gtb);

                // Bohrungen
                cursor = db.rawQuery("SELECT " + grundabmassbohrung
                        + " FROM bohrungen WHERE von <=" + durchmesser
                        + " AND bis >" + durchmesser, new String[] {});
                cursor.moveToFirst();
                String valuebohrungen = cursor.getString(0);
                System.out.println("From bohrungen: " + valuebohrungen);

                if (valuebohrungen.contains("D")) {
                    System.out.println("determine Delta");
                    cursor = db.rawQuery("SELECT [" + itbohrung
                            + "] FROM delta WHERE von <=" + durchmesser
                            + " AND bis >" + durchmesser, new String[] {});
                    cursor.moveToFirst();
                    String delta = cursor.getString(0);
                    System.out.println("delta: " + delta);
                    valuebohrungen = valuebohrungen.replace("+D", "");
                    int valuebohrungenint = Integer.parseInt(valuebohrungen);
                    int deltaint = Integer.parseInt(delta);
                    valuebohrungenint += deltaint;
                    System.out.println("new from bohrungen:" + valuebohrungenint);
                    valuebohrungen = String.valueOf(valuebohrungenint);
                }

                cursor = db.rawQuery("SELECT " + grundabmassbohrung
                                + " FROM bohrungen WHERE name == \"abmass\"",
                        new String[] {});
                cursor.moveToFirst();
                String valuebohrungenabmass = cursor.getString(0);
                System.out.println("From bohrungen: " + valuebohrungenabmass);

                double uab;
                double oab;
                if (valuebohrungenabmass.equals("unteres")) {
                    uab = Double.parseDouble(valuebohrungen);
                    oab = gtb + uab;
                } else {
                    oab = Double.parseDouble(valuebohrungen);
                    uab = oab - gtb;
                }
                System.out.println("Bohrung unteres abmass: " + uab
                        + " , oberes abmass: " + oab);

                //set values
                TextView textView;
                textView = (TextView) findViewById(R.id.editUpperToleranceBore);
                textView.setText(String.valueOf((int)oab));

                textView = (TextView) findViewById(R.id.editUpperToleranceShaft);
                textView.setText(String.valueOf((int)oaw));

                textView = (TextView) findViewById(R.id.editlowerToleranceBore);
                textView.setText(String.valueOf((int)uab));

                textView = (TextView) findViewById(R.id.editlowerToleranceShaft);
                textView.setText(String.valueOf((int)uaw));

                textView = (TextView) findViewById(R.id.editMaxLash);
                textView.setText(String.valueOf((int)(oab-uaw)));

                textView = (TextView) findViewById(R.id.maxInterferenceFit);
                textView.setText(String.valueOf((int)(oaw-uab)));

            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (SQLException e) {
                System.out.println("sqlite exception");
                e.printStackTrace();

            }


        }

    }
}

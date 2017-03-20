/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetContract.PetEntry;

import org.w3c.dom.Text;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        mDbHelper= new PetDbHelper(this);



    }
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.


        String[] projection={
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_WEIGHT,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_BREED
        };
        //content resolver points to the right content provider with the help of inserted URI
        Cursor cursor=getContentResolver().query(PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        TextView textView=(TextView)findViewById(R.id.text_view_pet);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).

            textView.setText("Number of rows in pets database table: " + cursor.getCount());
            textView.append(PetEntry._ID+"-"+PetEntry.COLUMN_PET_NAME+
                            "-"+PetEntry.COLUMN_PET_BREED+"-"+PetEntry.COLUMN_PET_GENDER+
                            "-"+PetEntry.COLUMN_PET_WEIGHT+"\n\n");

            int idColumnIndex=cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex=cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int weightColumnIndex=cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
            int genderColumnIndex=cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int breedColumnIndex=cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);

            while(cursor.moveToNext()){

                int currentID= cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentWeight= cursor.getInt(weightColumnIndex);
                int currentGender=cursor.getInt(genderColumnIndex);
                String currentBreed= cursor.getString(breedColumnIndex);

                textView.append("\n"+currentID+"-"+currentName+"-"+currentBreed+"-"
                        +currentGender+"-"+currentWeight);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet(){
        //Get the data repository in write mode
        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        ContentValues values= new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME,"Toto");
        values.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT,7);

        long newrowId=db.insert(PetEntry.TABLE_NAME,null,values);

        Log.v("CatalogActivity","New Row Id"+newrowId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

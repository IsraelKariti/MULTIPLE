package com.example.izi.multiple;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Calendar;

import static com.example.izi.multiple.MyContract.INITIALS.COLUMN_PARENT;
import static com.example.izi.multiple.MyContract.INITIALS.COLUMN_PHRASE;
import static com.example.izi.multiple.MyContract.INITIALS.TABLE_NAME_INITIALS;
import static com.example.izi.multiple.MyContract.INITIALS._ID;
import static com.example.izi.multiple.MyContract.LOG.COLUMN_DAY;
import static com.example.izi.multiple.MyContract.LOG.COLUMN_MONTH;
import static com.example.izi.multiple.MyContract.LOG.COLUMN_TOTAL_DAY;
import static com.example.izi.multiple.MyContract.LOG.COLUMN_PHRASE_ID;
import static com.example.izi.multiple.MyContract.LOG.COLUMN_YEAR;
import static com.example.izi.multiple.MyContract.LOG.TABLE_NAME_LOG;

public class MainActivity extends AppCompatActivity {
    Context context;
    MenuItem MenuItemUp;
    LinearLayout layout;
    EditText editText;
    Button edit;
    int count;
    SQL sql;
    SQLiteDatabase db;
    Button btn_add;
    ListView listView;
    SimpleCursorAdapter simpleCursorAdapter;
    int parent;
    Toolbar tb;
    Cursor cursor;
    View viewForAlertDialog;
    CustomView title_animated;
    boolean editRow;
    Button note;
    int countDummies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.entire);
        listView = findViewById(R.id.main_list);
        title_animated = findViewById(R.id.title_animated);
        tb = findViewById(R.id.tb);
        editRow = false;
        setSupportActionBar(tb);
        sql = new SQL(this, null, null, 1  );
        count = 1;
        parent = 0;
        countDummies = 0;
        retrieveInfo();

        db = sql.getWritableDatabase();

        editText = findViewById(R.id.editText);

        btn_add = findViewById(R.id.btn);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(  TextUtils.isEmpty(editText.getText().toString().trim().replace("\n", "") ) ){
                    Toast.makeText(context, "BOX IS EMPTY", Toast.LENGTH_SHORT).show();
                }
                else {
                    closeSoftKeyboard(view);

                    CharSequence charSeq = editText.getText();
                    editText.setText("");
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_PARENT, parent);
                    cv.put(COLUMN_PHRASE, charSeq.toString());
                    db.insertWithOnConflict(TABLE_NAME_INITIALS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

                    cursor = db.query(TABLE_NAME_INITIALS, new String[]{_ID, COLUMN_PHRASE}, COLUMN_PARENT+"=?", new String[]{String.valueOf(parent)}, null, null, null );
                    simpleCursorAdapter.changeCursor(cursor);
                }
                btn_add.requestFocus();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tbmenu, menu);
        MenuItemUp = menu.findItem(R.id.up);
        MenuItemUp.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.up:
                // it all depends on the parent...
                db = sql.getReadableDatabase();
                cursor = db.query(TABLE_NAME_INITIALS, new String[]{MyContract.INITIALS._ID, COLUMN_PARENT}, _ID+"=?", new String[]{String.valueOf(parent)}, null, null, null );
                cursor.moveToNext();
                int grandfather = cursor.getInt(1);
                cursor = db.query(TABLE_NAME_INITIALS, new String[]{_ID, COLUMN_PHRASE}, COLUMN_PARENT+"=?", new String[]{String.valueOf(grandfather)}, null, null, null );
                simpleCursorAdapter.changeCursor(cursor);
                parent = grandfather;
                updateTitle();
                if(parent == 0)
                    MenuItemUp.setVisible(false);
                break;
            case R.id.log:
                Intent intent = new Intent(this, LogActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    // get STATES from DATABASE (table::Initials) and INSERRT to the LISTVIEW (via ADAPTER)
    private void retrieveInfo() {
        db = sql.getReadableDatabase();
        cursor = db.query(TABLE_NAME_INITIALS, new String[]{MyContract.INITIALS._ID, COLUMN_PHRASE}, COLUMN_PARENT+"=?", new String[]{String.valueOf(parent)}, null, null, null );
        String[] from_columns = new String[]{ _ID, COLUMN_PHRASE };
        int[] to_view = new int[]{R.id.state, R.id.state};

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from_columns, to_view, 0);
        listView.setAdapter(simpleCursorAdapter);

        updateTitle();
    }

    // VERIFY if user is SURE about deleting the row
    public void try_delete_row(View view) {
        viewForAlertDialog = view;

        closeSoftKeyboard(view);

        //dialogAlert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("גבררר!")
                .setMessage("מיליון אחוז שאתה רוצה למחוק?")
                .setPositiveButton("בדוק אחי", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        deleteRow(viewForAlertDialog);
                    }
                })
                .setNegativeButton("תכלס לא", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.mipmap.sure)
                .show();
    }

    // delete row
    private void deleteRow(View view) {
        // get state of selected button
        String line = ((Button)((ConstraintLayout)view.getParent()).findViewById(R.id.state)).getText().toString();
        db = sql.getReadableDatabase();
        cursor = db.query(TABLE_NAME_INITIALS, new String[]{MyContract.INITIALS._ID}, COLUMN_PHRASE+"=?", new String[]{line}, null, null, null );
        cursor.moveToNext();
        // get id of the state which will be deleted
        int rec_id = cursor.getInt(0);
        delete_row_recursive(rec_id);


        //reload states from database
        db = sql.getReadableDatabase();
        cursor = db.query(TABLE_NAME_INITIALS, new String[]{MyContract.INITIALS._ID, COLUMN_PHRASE}, COLUMN_PARENT+"=?", new String[]{String.valueOf(parent)}, null, null, null );
        simpleCursorAdapter.changeCursor(cursor);
    }

    //delete the row and ALL CHILDREN in a recursive manner
    private void delete_row_recursive(int rec_id){
        db = sql.getWritableDatabase();
        db.delete(TABLE_NAME_INITIALS, _ID+"=?", new String[]{String.valueOf(rec_id)} );

        //check if there are rows whose parent is equal to rec_id
        db = sql.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_INITIALS, new String[]{MyContract.INITIALS._ID}, COLUMN_PARENT+"=?", new String[]{String.valueOf(rec_id)}, null, null, null );
        while (cursor.moveToNext()){
            delete_row_recursive(cursor.getInt(0));
        }
    }

    // once a state was clicked -> get all children of state from database and INSERT TO CURSOR ADAPTER
    public void enter_state(View view){

        closeSoftKeyboard(view);

        db = sql.getReadableDatabase();
        cursor = db.query(TABLE_NAME_INITIALS, new String[]{_ID}, COLUMN_PHRASE +"=?", new String[]{((Button) view).getText().toString()}, null, null, null );

        cursor.moveToNext();
        parent = cursor.getInt(0);

        cursor = db.query(TABLE_NAME_INITIALS, new String[]{MyContract.INITIALS._ID, COLUMN_PARENT, COLUMN_PHRASE}, COLUMN_PARENT+"=?", new String[]{String.valueOf(parent)}, null, null, null );
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                simpleCursorAdapter.changeCursor(cursor);
                MenuItemUp.setVisible(true);
                updateTitle();
            }
        }, 160);
    }

    public void edit_state(View view){
        EditText editText = (EditText)((ConstraintLayout)view.getParent()).findViewById(R.id.edit_row) ;
        Button btn = (Button)((ConstraintLayout)view.getParent()).findViewById(R.id.state);
        if(editRow == false){
            btn.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            editText.setText(btn.getText());
            openSoftKeyboard(editText);
            view.setBackgroundResource(R.mipmap.approve);
            editRow = true;
        }
        else{
            btn.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);

            //update sqlitedatabase
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_PHRASE, editText.getText().toString());
            db = sql.getWritableDatabase();
            db.update(TABLE_NAME_INITIALS, cv, COLUMN_PHRASE+"=?", new String[]{btn.getText().toString()});

            btn.setText(editText.getText());
            view.setBackgroundResource(R.mipmap.edit);
            closeSoftKeyboard(view);
            editRow = false;
        }
    }

    private void openSoftKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void closeSoftKeyboard(View view) {
        InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateTitle(){
        if(parent == 0){
            title_animated.setText("שתף אותי");
            title_animated.startAnimation();
        }
        else{
            db = sql.getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME_INITIALS, new String[]{COLUMN_PHRASE}, _ID+"=?", new String[]{String.valueOf(parent)}, null, null, null);
            cursor.moveToNext();
            title_animated.setText(cursor.getString(0));
            title_animated.startAnimation();
        }
    }

    public void takeNote(View view){
        // vibate phone
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(500);
        }
        Toast.makeText(context, "Note taken!", Toast.LENGTH_SHORT).show();
        //what was it??? retrieve state id by utilizing button text
        Button btn_note = (Button)view;
        Button btn_state = (Button) ((ConstraintLayout)btn_note.getParent()).findViewById(R.id.state);
        String state = btn_state.getText().toString();

        db = sql.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_INITIALS, new String[]{_ID, COLUMN_PARENT}, COLUMN_PHRASE+"=?", new String[]{state}, null, null, null);

        //when was it??? take the time from the system
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int day_of_year = calendar.get(Calendar.DAY_OF_YEAR);
        int totalDay = year*365+day_of_year;

        // encapsulate date data
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TOTAL_DAY, totalDay);
        cv.put(COLUMN_DAY, day);
        cv.put(COLUMN_MONTH, month);
        cv.put(COLUMN_YEAR, year);

        //send id of chosen state + time stamp to recursive method
        takeNoteRecursive(cursor, cv);
}

    // a recursive method for locating all ancestors of chosen state and "taking a note" of them also; namely, inserting them to log as if they were pressed directly
    private void takeNoteRecursive(Cursor cursor, ContentValues cv){
        // put the state and the time in which state occured in database
        cursor.moveToFirst();
        Log.i("EEEEEEEEEEEEEEEEEEEEEE", String.valueOf(cursor.getInt(0)));

        cv.put(COLUMN_PHRASE_ID, cursor.getInt(0));
        db = sql.getWritableDatabase();
        db.insertWithOnConflict(TABLE_NAME_LOG, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        // if state parent is 0 stop; otherwise insert parent also
        int parent = cursor.getInt(1);
        if( parent == 0)
            return;
        else{
            // get parent state
            db = sql.getReadableDatabase();
            Cursor cursor_parent = db.query(TABLE_NAME_INITIALS, new String[]{_ID, COLUMN_PARENT}, _ID+"=?", new String[]{String.valueOf(parent)}, null, null, null);
            takeNoteRecursive(cursor_parent, cv);
        }
    }
}


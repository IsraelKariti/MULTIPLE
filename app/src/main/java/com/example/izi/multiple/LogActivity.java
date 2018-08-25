package com.example.izi.multiple;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

public class LogActivity extends AppCompatActivity {
    NestedScrollView nestedScrollView;
    HorizontalScrollView horizontalScrollView;
    TableLayout tableLayout;
    TableRow tableRow;
    SQL sql;
    SQLiteDatabase db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        horizontalScrollView = nestedScrollView.findViewById(R.id.horizontal_scroll_view);
        tableLayout = horizontalScrollView.findViewById(R.id.table_layout);
        tableRow = tableLayout.findViewById(R.id.table_row);

        sql = new SQL(this, null, null, 1);
        db = sql.getReadableDatabase();
        Cursor cursor = db.query( TABLE_NAME_INITIALS , new String[]{_ID, COLUMN_PHRASE}, COLUMN_PARENT+"=?", new String[]{String.valueOf(0)}, null, null, null );

        // create label "Date"
        CustomButton btn_date = new CustomButton(this);
        TableRow.LayoutParams params_date = new TableRow.LayoutParams(300, TableRow.LayoutParams.MATCH_PARENT);
        params_date.setMargins(10, 10, 10, 10);
        btn_date.setLayoutParams(params_date);
        btn_date.setBackgroundColor(0xffbdbdbd);
        btn_date.setGravity(Gravity.CENTER);
        btn_date.setText("DATE");
        btn_date.databaseID = -1;
        btn_date.indexInRow = 0;
        btn_date.setMinHeight(200);
        btn_date.setOnClickListener(expand);
        btn_date.layer = 0;
        tableRow.addView(btn_date, 0);

        createHeaders(cursor, 0, 1);
        addNotes();
    }

    // create the first line in the table, which are column names
    private void createHeaders(Cursor cursor, int layer, int index){
        if(cursor.getCount() == 0){
            return;
        }

        // create column names by iteration on database
        while(cursor.moveToNext()){
            CustomButton btn = new CustomButton(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(300, TableRow.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 10+50*layer, 10, 10);
            btn.setLayoutParams(params);
            btn.setBackgroundColor(0xffbdbdbd);
            btn.setGravity(Gravity.CENTER);
            btn.setText(cursor.getString(1));
            btn.databaseID = cursor.getInt(0);
            btn.indexInRow = index;
            btn.setMinHeight(200);
            btn.setOnClickListener(expand);
            btn.layer = layer;
            tableRow.addView(btn, index++);
        }
    }

    View.OnClickListener expand = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomButton btn = (CustomButton)view;
            int index = ((TableRow)btn.getParent()).indexOfChild(btn);
            if(btn.expand){
                db = sql.getReadableDatabase();
                // find all children of btn
                Cursor cursor = db.query(TABLE_NAME_INITIALS, new String[]{_ID, COLUMN_PHRASE}, COLUMN_PARENT+"=?", new String[]{String.valueOf(btn.databaseID)}, null, null, null);
                if(cursor.getCount() != 0){
                    createHeaders(cursor, btn.layer+1, ++index );
                    btn.expand = false;
                    deleteNotes();
                    addNotes();
                }
            }
            else{
                btn.expand = true;
                index++;
                CustomButton toDelete = ((CustomButton) tableRow.getChildAt(index));
                boolean foundCellToDelete = false;
                while(toDelete != null){
                    if(toDelete.layer>btn.layer){
                        foundCellToDelete = true;
                        ((TableRow)btn.getParent()).removeViewAt(index);
                        toDelete = (CustomButton) tableRow.getChildAt(index);
                    }
                    else{
                        break;
                    }
                }
                if( foundCellToDelete ){
                    deleteNotes();
                    addNotes();
                }
            }

        }
    };


    private void addNotes(){
        Cursor cursor = db.query( TABLE_NAME_LOG , new String[]{_ID, COLUMN_TOTAL_DAY, COLUMN_PHRASE_ID, COLUMN_DAY, COLUMN_MONTH, COLUMN_YEAR}, null, null, null, null, null );
        if(cursor.getCount() == 0)
            return;

        boolean empty;

        while(true){
            empty=true;
            Log.i("EEEEEEEEEEEEEEEE", "ENTER LOOP");

            //create empty row (with vertical dividers) length same as states
            Resources res = getResources();
            TableRow temp_tableRow = new TableRow(this);
            temp_tableRow.setDividerDrawable(res.getDrawable(R.drawable.vertical_divider, null));
            temp_tableRow.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            for(int i=0; i<tableRow.getChildCount(); i++){
                CustomButton button = new CustomButton(this);
                button.setBackgroundColor(0x00000000);
                temp_tableRow.addView(button);
            }


            // move into first line of new day (there are multiple lines for each day) and collect data about the day
            cursor.moveToNext();
            int totalDay = cursor.getInt(1);//change 3 to 1
            int day = cursor.getInt(3);
            int month = cursor.getInt(4);
            int year = cursor.getInt(5);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            Date date = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String date_format = sdf.format(date);
            cursor.moveToPrevious();

            // set specific day to first cell in row to indicate the day
            ((CustomButton)temp_tableRow.getChildAt(0)).setText(String.valueOf(date_format));

            //iterate rows of database and populate cells
            while(cursor.moveToNext() && cursor.getInt(1)==totalDay){ //change 3 to 1
                int phraseIdInDatabase = cursor.getInt(2);
                for(int i = 0 ; i<tableRow.getChildCount(); i++){
                    if( ((CustomButton)tableRow.getChildAt(i)).databaseID == phraseIdInDatabase){
                        CustomButton button = (CustomButton)temp_tableRow.getChildAt(i);
                        button.setBackgroundResource(R.drawable.happened_today);
                        button.countAppearancesPerDay++;
                        if(button.countAppearancesPerDay>1){
                            button.setText(String.valueOf(button.countAppearancesPerDay));
                            button.setTextColor(Color.WHITE);
                        }
                        empty = false;
                    }
                }
            }

            //add row to table
            if(empty == false)
                tableLayout.addView(temp_tableRow);

            if(cursor.isAfterLast()){
                break;
            }
            else{
                // reorganize loop settings
                cursor.moveToPrevious();
            }
        }
    }

    private void deleteNotes(){
        int rows = tableLayout.getChildCount();
        for(int i = 0 ; i < rows-1 ; i++){
            tableLayout.removeViewAt(1);
        }
    }
}

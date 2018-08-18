package com.example.izi.multiple;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.izi.multiple.MyContract.INITIALS.CREATE_TABLE_INITIALS;
import static com.example.izi.multiple.MyContract.LOG.CREATE_TABLE_LOG;

/**
 * Created by izi on 8/6/2018.

 אז ככה
 אני יוצר טבלה חדשה
 עם 2 עמודות
 עמודה ראשונה תאריך
 עמודה שניה ה - ID של הסטייט שנבחר
 בכל פעם שנבחר סטייט אז גם האבא והסבא שלו נרשמים
 ואז מה
 ואז אני בונה ROW בלTABLE
 באורך של הכותרות
 ואני רץ בלולאה על השורות בטבלה
 ואז אני לוקח מהשורה הראשונה את הID
 מחפש למי מהעמודות יש את הID  הזה
 ואז אני שם שם סימן
 או רקע שחור
 וככה רץ בלולאה עד שאני מגיע לתאריך אחר
 הגעתי לתאריך אחר אני יוצר שורה חדשה שוב פעם עם מספר איברים כמספר הכותרות

 אבל מה עושים עם כל הקטע של האקספנדבל
 אולי פשוט אני מדפיס את כל השורות מחדש?
 כןיש מצב
 פשוט מחפש מחדש בטבלה של הלוג
 מוחק את כל השורות בלוג_אקטיביטי ומדפיס אותן מחדש

 אז מחר דבר ראשון
 להבין איך בכלל מתקשרים עם התאריך
 אחר כך להוסיף כפתור לכל סטייט ששומר את התאריך בטבלה חדשה
 ואחרי שזה עובד טוב אז לקרוא ממנה ולייצר שורות

 בתכלס זה לא מסובך פשוט ארוך
 */

public class SQL extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gradient.db";

    public SQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL( CREATE_TABLE_INITIALS );
        db.execSQL( CREATE_TABLE_LOG );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

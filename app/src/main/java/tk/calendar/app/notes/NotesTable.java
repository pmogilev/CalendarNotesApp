package tk.calendar.app.notes;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Pasha on 5/3/14.
 */
public class NotesTable {

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_REMIND = "remind";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_EDITED = "edited";
    public static final String COLUMN_ATTACHED_FILE = "attachments";

    public static final String[] COLUMNS = {COLUMN_DATE, COLUMN_CREATED, COLUMN_CONTENT,
                COLUMN_ATTACHED_FILE, COLUMN_REMIND, COLUMN_REMIND, COLUMN_EDITED, COLUMN_ID};


    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null,"
            + COLUMN_CONTENT + " text, "
            + COLUMN_DATE + " text, "
            + COLUMN_REMIND + " integer default 0, "
            + COLUMN_CREATED + " text not null, "
            + COLUMN_EDITED + " text not null, "
            + COLUMN_ATTACHED_FILE + " text"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(NotesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(database);
    }

}

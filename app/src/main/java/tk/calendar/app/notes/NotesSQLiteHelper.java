package tk.calendar.app.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pasha on 5/3/14.
 */
public class NotesSQLiteHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    public NotesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        NotesTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NotesTable.onUpgrade(db, oldVersion, newVersion);
    }
}

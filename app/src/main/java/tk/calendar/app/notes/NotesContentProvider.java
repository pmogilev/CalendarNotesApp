package tk.calendar.app.notes;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Pasha on 5/3/14.
 */
public class NotesContentProvider extends ContentProvider {


    private NotesSQLiteHelper mDb;
    private static final String AUTHORITY = "tk.calendar.app.notes.provider";

    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/notes";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/note";

    //Codes for URI matcher
    private static final int NOTES = 14;
    private static final int NOTEID = 15;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTEID);
    }


    @Override
    public boolean onCreate() {
        mDb = new NotesSQLiteHelper(getContext());
        return false;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // check if the caller has requested a column which does not exists
        checkColumns(projection);
        // Set the table
        queryBuilder.setTables(NotesTable.TABLE_NOTES);

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case NOTES:
                break;
            case NOTEID:
                // adding the ID to the original query
                queryBuilder.appendWhere(NotesTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDb.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDb.getWritableDatabase();
        long id;
        switch (uriType) {
            case NOTES:
                id = sqlDB.insert(NotesTable.TABLE_NOTES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted = 0;
        try {
            int uriType = sURIMatcher.match(uri);
            SQLiteDatabase sqlDB = mDb.getWritableDatabase();
            switch (uriType) {
                case NOTES:
                    rowsDeleted = sqlDB.delete(NotesTable.TABLE_NOTES, selection,
                            selectionArgs);
                    break;
                case NOTEID:
                    String id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = sqlDB.delete(NotesTable.TABLE_NOTES,
                                NotesTable.COLUMN_ID + "=" + id,
                                null);
                    } else {
                        rowsDeleted = sqlDB.delete(NotesTable.TABLE_NOTES,
                                NotesTable.COLUMN_ID + "=" + id
                                        + " and " + selection,
                                selectionArgs
                        );
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
        }catch (Exception ex){
            throw new IllegalArgumentException("Failed to delete " + ex.getMessage());
        }
        return rowsDeleted;


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int rowsUpdated = 0;

        try {
            int uriType = sURIMatcher.match(uri);
            SQLiteDatabase sqlDB = mDb.getWritableDatabase();

            switch (uriType) {
                case NOTES:

                    rowsUpdated = sqlDB.update(NotesTable.TABLE_NOTES,
                            values,
                            selection,
                            selectionArgs);
                    break;
                case NOTEID:

                    String id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = sqlDB.update(NotesTable.TABLE_NOTES,
                                values,
                                NotesTable.COLUMN_ID + "=" + id,
                                null);
                    }
                    else {
                        rowsUpdated = sqlDB.update(NotesTable.TABLE_NOTES,
                                values,
                                NotesTable.COLUMN_ID + "=" + id
                                        + " and "
                                        + selection,
                                selectionArgs
                        );
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Update failed: " + ex.getMessage());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;


    }


    /**
     * Check if schema contains all the columns in projection
     *
     * @param projection
     */
    private void checkColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(NotesTable.COLUMNS));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}

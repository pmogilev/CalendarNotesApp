package tk.calendar.app;

import android.content.AsyncQueryHandler;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import tk.calendar.app.notes.NotesContentProvider;
import tk.calendar.app.notes.NotesTable;

import java.lang.ref.WeakReference;

/**
 * Created by Pasha on 5/3/14.
 *
 * This is more abstract {@link AsyncQueryHandler} that helps keep a
 * {@link WeakReference} back to a listener. Will properly close any
 * {@link Cursor} if the listener ceases to exist.
 * <p>
 * This pattern can be used to perform background queries without leaking
 * {@link Context} objects.
 */
public class CalendarAsyncQueryHandler extends AsyncQueryHandler {
    private static final String TAG = CalendarAsyncQueryHandler.class.getSimpleName();


    private WeakReference<AsyncQueryListener> mListener;

    //Query identifiers
    private static final int GET_CALENDARS = 0;
    private static final int CREATE_NOTE = 1;
    private static final int DELETE_NOTE = 2;
    private static final int UPDATE_NOTE = 3;
    private static final int GET_NOTES = 4;


    // Projection array.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


    public static final String[] NOTE_PROJECTION = new String[]{
            NotesTable.COLUMN_ID,
            NotesTable.COLUMN_DATE,
            NotesTable.COLUMN_CONTENT,
            NotesTable.COLUMN_TITLE
    };

    /**
     * Interface to listen for completed query operations.
     */
    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    public CalendarAsyncQueryHandler(Context context, AsyncQueryListener listener) {
        super(context.getContentResolver());
        setQueryListener(listener);
    }

    /**
     * Assign the given {@link AsyncQueryListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setQueryListener(AsyncQueryListener listener) {
        mListener = new WeakReference<AsyncQueryListener>(listener);
    }

    /** {@inheritDoc} */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        final AsyncQueryListener listener = mListener.get();

        switch(token){
            case GET_CALENDARS:
                getUserCalendars(cursor);
                break;
            case CREATE_NOTE:
                getNote(cursor);
            default:
                break;
        }

        if (listener != null) {
            listener.onQueryComplete(token, cookie, cursor);
        } else if (cursor != null) {
            cursor.close();
        }

    }

    private void getNote(Cursor cursor) {
        //TODO : validate note if created
    }

    private void getUserCalendars(Cursor cursor) {

        while (cursor.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cursor.getLong(PROJECTION_ID_INDEX);
            displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            Log.d(TAG, "index " + calID + " display " + displayName + " account " + accountName + " owner " + ownerName);
        }

    }

    /**
     *
     * @param account account name :
     *
     * @param type
     * @param owner
     */
    public void getCalendars(String account, String type, String owner ){

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {account,type,type};

        startQuery(GET_CALENDARS, null, uri, EVENT_PROJECTION, selection, selectionArgs, null);
    }


    public void getLocalNotes(){
        }

    public void createNote(){

        ContentValues values = new ContentValues();
        values.put(NotesTable.COLUMN_TITLE, "Note 1");
        values.put(NotesTable.COLUMN_CREATED, new Date().toGMTString());
        values.put(NotesTable.COLUMN_EDITED, new Date().toGMTString());

        startInsert(CREATE_NOTE, null, NotesContentProvider.CONTENT_URI, values);
    }


}
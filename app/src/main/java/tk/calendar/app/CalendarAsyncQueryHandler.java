package tk.calendar.app;

import android.content.AsyncQueryHandler;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.LoginFilter;
import android.util.Log;
import tk.calendar.app.notes.NotesContentProvider;
import tk.calendar.app.notes.NotesTable;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Pasha on 5/3/14.
 * <p/>
 * This is more abstract {@link AsyncQueryHandler} that helps keep a
 * {@link WeakReference} back to a listener. Will properly close any
 * {@link Cursor} if the listener ceases to exist.
 * <p/>
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
    public static final String[] EVENT_PROJECTION = new String[]{
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

    private static final int NOTE_PROJECTION_ID = 0;
    private static final int NOTE_PROJECTION_DATE = 1;
    private static final int NOTE_PROJECTION_CONTENT = 2;
    private static final int NOTE_PROJECTION_TITLE = 3;




    /**
     * Interface to listen for completed query operations.
     */
    public interface AsyncQueryListener {
        void onQueryComplete(List<Note> notes);

        void onInsertComplete(boolean result);

        void onUpdateComplete(boolean result);

        void onDeleteComplete(boolean result);

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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

        switch (token) {
            case GET_CALENDARS:
                getCalendarsResult(cursor);
                break;
            case GET_NOTES:
                getNotesResult(cursor);
            default:
                break;
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {

        final AsyncQueryListener listener = mListener.get();
        switch (token) {
            case CREATE_NOTE:
                getNote(uri);
            default:
                break;
        }

        if (listener != null) {
            listener.onInsertComplete(true);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        final AsyncQueryListener listener = mListener.get();

        switch (token) {
            case UPDATE_NOTE:
                Log.d(TAG, "Update complete");
                break;
            default:
                break;
        }

        if (listener != null && result == 1) {
            listener.onUpdateComplete(true);
        }else
            listener.onUpdateComplete(false);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {

        final AsyncQueryListener listener = mListener.get();

        switch (token) {
            case DELETE_NOTE:
                Log.d(TAG, "DELETE complete");
                break;
            default:
                break;
        }

        if (listener != null && result == 1) {
            listener.onDeleteComplete(true);
        }else
            listener.onDeleteComplete(false);

    }

    /**
     * Get Note by Uri
     * @param uri
     */
    private void getNote(Uri uri) {
        //TODO: think about something smart to do ...
    }

    /**
     * Future feature - fetch calendars and sync with a local calendar
     *
     * @param cursor
     */
    private void getCalendarsResult(Cursor cursor) {

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
     * Reponse from a QueryProvider
     *
     * @param cursor
     */
    private void getNotesResult(Cursor cursor) {

        ArrayList<Note> notes = new ArrayList<Note>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long noteID = 0;
                String title, content, date;

                // Get the field values
                noteID = cursor.getLong(NOTE_PROJECTION_ID);
                title = cursor.getString(NOTE_PROJECTION_TITLE);
                content = cursor.getString(NOTE_PROJECTION_CONTENT);
                date = cursor.getString(NOTE_PROJECTION_DATE);

                notes.add(new Note(date, title, content, noteID));

                Log.d(TAG, "index " + noteID + " display " + title + " account " + content + " owner " + date);
            }
        }

        final AsyncQueryListener listener = mListener.get();

        if (listener != null) {
            listener.onQueryComplete(notes);
        }
    }

    /**
     * Get Calendars for this user
     *
     * @param account account name :
     * @param type
     * @param owner
     */
    public void getCalendars(String account, String type, String owner) {

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{account, type, type};

        startQuery(GET_CALENDARS, null, uri, EVENT_PROJECTION, selection, selectionArgs, null);
    }


    public void getAllNotes() {
        Uri uri = NotesContentProvider.CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;
        startQuery(GET_NOTES, null, uri, NOTE_PROJECTION, selection, selectionArgs, null);
    }


    /**
     *
     * @param key - Date value
     */
    public void getNotesByKey(String key) {

        Uri uri = NotesContentProvider.CONTENT_URI;
        String selection = "((" + NotesTable.COLUMN_DATE + " = ?))";
        String[] selectionArgs = new String[]{key};

        startQuery(GET_NOTES, null, uri, NOTE_PROJECTION, selection, selectionArgs, null);
    }


    /**
     * Create a new note
     *
     * @param title
     * @param content
     * @param date
     */
    public void createNote(String title, String content, String date) {

        ContentValues values = new ContentValues();
        values.put(NotesTable.COLUMN_TITLE, title);
        values.put(NotesTable.COLUMN_DATE, date);
        values.put(NotesTable.COLUMN_CONTENT, content);
        values.put(NotesTable.COLUMN_CREATED, new Date().toGMTString());
        values.put(NotesTable.COLUMN_EDITED, new Date().toGMTString());

        startInsert(CREATE_NOTE, null, NotesContentProvider.CONTENT_URI, values);
    }

    /**
     * Update an existing note
     *
     * @param title
     * @param content
     * @param date
     */
    public void updateNote(String title, String content, String date, Long id) {
        ContentValues values = new ContentValues();
        values.put(NotesTable.COLUMN_TITLE, title);
        values.put(NotesTable.COLUMN_DATE, date);
        values.put(NotesTable.COLUMN_EDITED, new Date().toGMTString());
        values.put(NotesTable.COLUMN_ID, id);
        values.put(NotesTable.COLUMN_CONTENT, content);
        Uri uri = Uri.parse( NotesContentProvider.CONTENT_URI + "/" + id);
        String selection = null;
        String[] selectionArgs = null;
        startUpdate(UPDATE_NOTE, null, uri, values, selection, selectionArgs);

    }

    /**
     * Delete a note
     *
     * @param id
     */
    public void deleteNote(Long id) {
        Uri uri = Uri.parse( NotesContentProvider.CONTENT_URI + "/" + id);
        startDelete(DELETE_NOTE, null, uri, null, null);
    }

}
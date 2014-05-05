package tk.calendar.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Manipulation with a single note
 * Cancel (Back)
 * Save
 * Delete
 */
public class NoteDetailsActivity extends Activity implements CalendarAsyncQueryHandler.AsyncQueryListener {


    private static final long DEFAULT_ID = -1;
    CalendarAsyncQueryHandler mHandler;
    private EditText mTitle;
    private EditText mContent;
    private TextView mDate;
    private Long mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        mTitle = (EditText) findViewById(R.id.etxtNoteTitleDetails);
        mContent = (EditText) findViewById(R.id.etxtNoteContentDetails);
        mDate = (TextView) findViewById(R.id.txtNoteDateDetails);

        Intent intent = getIntent();
        mID = intent.getLongExtra("id", DEFAULT_ID);
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String date = intent.getStringExtra("date");

        if (mID != DEFAULT_ID) {
            mTitle.setText(title);
            mContent.setText(content);
            mDate.setText(date);
        } else {
            //date should be always there....
            mDate.setText(date);
        }

        mHandler = new CalendarAsyncQueryHandler(this, this);

        Button btnSave = (Button) findViewById(R.id.btnSaveNote);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave();
            }
        });

        final Context ctx = this;
        Button btnDetele = (Button) findViewById(R.id.btnDelete);
        btnDetele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDelete();
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, CalendarMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_details, menu);
        return true;
    }

    /**
     * Attempts to delete a new note.
     * If there are form errors (invalid id, missing fields, etc.), the
     * errors are presented and no actual delete is made.
     */
    private void attemptDelete() {
        if (mID == DEFAULT_ID) {

            Toast.makeText(NoteDetailsActivity.this, getResources().getText(R.string.msg_not_saved),
                    Toast.LENGTH_SHORT).show();
        } else {
            mHandler.deleteNote(mID);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to save a new note.
     * If there are form errors (invalid title, missing fields, etc.), the
     * errors are presented and no actual save is made.
     */
    public void attemptSave() {


        // Store values at the time of the login attempt.
        String title = mTitle.getText().toString();
        String content = mContent.getText().toString();
        String date = mDate.getText().toString();

        boolean cancel = false;
        View focusView = null;

        validateData(title, content);

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(title)) {
            mTitle.setError(getString(R.string.error_invalid_title));
            focusView = mTitle;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt to save a note and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //if it s anew note - save as a new
            if (mID == DEFAULT_ID) {
                //Save a new note
                mHandler.createNote(title, content, date);
            } else {
                mHandler.updateNote(title, content, date, mID);
            }

        }
    }

    private void validateData(String title, String content) {
    }

    @Override
    public void onQueryComplete(List<Note> notes) {

    }

    @Override
    public void onInsertComplete(boolean result) {
        Intent intent = new Intent(this, CalendarMainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUpdateComplete(boolean result) {

        if (result) {
            Toast.makeText(NoteDetailsActivity.this, getResources().getText(R.string.msg_saved),
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CalendarMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(NoteDetailsActivity.this, getResources().getText(R.string.msg_failed),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDeleteComplete(boolean result) {
        if (result) {
            Toast.makeText(NoteDetailsActivity.this, getResources().getText(R.string.msg_deleted),
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CalendarMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(NoteDetailsActivity.this, getResources().getText(R.string.msg_failed),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, CalendarMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}

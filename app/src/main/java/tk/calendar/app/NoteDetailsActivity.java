package tk.calendar.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;


public class NoteDetailsActivity extends Activity implements CalendarAsyncQueryHandler.AsyncQueryListener {


    CalendarAsyncQueryHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        mHandler = new CalendarAsyncQueryHandler(this, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_details, menu);
        return true;
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

    @Override
    public void onQueryComplete(List<Note> notes) {

    }

    @Override
    public void onInsertComplete(boolean result) {

    }

    @Override
    public void onUpdateComplete(boolean result) {

    }

    @Override
    public void onDeleteComplete(boolean result) {

    }
}

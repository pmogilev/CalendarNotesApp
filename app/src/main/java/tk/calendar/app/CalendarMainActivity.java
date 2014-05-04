package tk.calendar.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Pasha on 5/3/14.
 *
 * A calendar screen, host one fragment with CalendarView
 */
public class CalendarMainActivity extends Activity implements CalendarViewFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calendar_main, menu);
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
    public void onFragmentInteraction() {

    }
}

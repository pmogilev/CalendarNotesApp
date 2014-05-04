package tk.calendar.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Created by Pasha on 5/4/14.
 */

public class NotesArrayAdapter extends ArrayAdapter<Note> {
    private final Context mContext;
    private final Note[] mNotes;

    public NotesArrayAdapter(Context context, Note[] values) {
        super(context, R.layout.note_row, values);
        this.mContext = context;
        this.mNotes = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.note_row, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.txtNoteTitleRow);
        title.setText(mNotes[position].getTitle());
        TextView date = (TextView) rowView.findViewById(R.id.txtNoteDateRow);
        date.setText(mNotes[position].getDate());
        return rowView;
    }
}
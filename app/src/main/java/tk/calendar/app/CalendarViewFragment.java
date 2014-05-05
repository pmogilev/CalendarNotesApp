package tk.calendar.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;


/**
 * Created by Pasha on 5/3/14.
 * <p/>
 * Activities that contain this fragment must implement the
 * {@link CalendarViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarViewFragment extends Fragment implements CalendarAsyncQueryHandler.AsyncQueryListener, CalendarView.OnDateChangeListener {


    private static final String TAG = CalendarViewFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    CalendarAsyncQueryHandler mHandler;
    CalendarView mCalendar;
    //Hold selected date
    String mSelectedDate;
    //List of notes
    ListView mNotes;
    //Notes Adapter
    NotesArrayAdapter mAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarViewFragment newInstance(String param1, String param2) {
        CalendarViewFragment fragment = new CalendarViewFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CalendarViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mHandler = new CalendarAsyncQueryHandler(getActivity(), this);
        mAdapter = new NotesArrayAdapter(getActivity(), new Note[]{new Note("aaa", "bbbb", "ccc", new Long(2))});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_calendar_view, container, false);
        mNotes = (ListView) root.findViewById(R.id.listNotes);
        mNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
                Note n = mAdapter.getItem(position);
                intent.putExtra("id", n.getID());
                intent.putExtra("title", n.getTitle());
                intent.putExtra("content", n.getContent());
                intent.putExtra("date", n.getDate());
                startActivity(intent);
            }
        });

        mNotes.setAdapter(mAdapter);
        Button btnGetAll = (Button) root.findViewById(R.id.btnGetNotes);
        Button btnAddNote = (Button) root.findViewById(R.id.btnAddNote);

        //get all notes
        btnGetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Get all notes button clicked");
                mHandler.getAllNotes();
            }
        });
        //create a new note button
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Create a new note");
                Intent i = new Intent(getActivity(), NoteDetailsActivity.class);
                i.putExtra("date", mSelectedDate);
                startActivity(i);
            }
        });

        //initialize the calendarview and fetch notes
        mCalendar = (CalendarView) root.findViewById(R.id.calendarView);
        mCalendar.setOnDateChangeListener(this);
        mSelectedDate = Utils.convertDateToKey(new Date());
        mHandler.getNotesByKey(mSelectedDate);

        // Inflate the layout for this fragment
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //callback on AsyncHandler
    @Override
    public void onQueryComplete(List<Note> notes) {
        Log.d(TAG, "onQueryComplete() size " + notes.size());
        mAdapter = new NotesArrayAdapter(getActivity(), notes.toArray(new Note[notes.size()]));
        mNotes.setAdapter(mAdapter);
    }

    //callback on AsyncHandler
    @Override
    public void onInsertComplete(boolean result) {
        Log.d(TAG, "onInsertComplete() result " + result);
        if (mHandler != null) {
            mHandler.getNotesByKey(mSelectedDate);
        }
    }

    //callback on AsyncHandler
    @Override
    public void onUpdateComplete(boolean result) {
        Log.d(TAG, "onUpdateComplete() result " + result);
    }

    //callback on AsyncHandler
    @Override
    public void onDeleteComplete(boolean result) {
        Log.d(TAG, "onDeleteComplete() result " + result);
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

        mSelectedDate = Utils.convertDateToKey(year, month, dayOfMonth);
        if (mHandler != null) {
            mHandler.getNotesByKey(mSelectedDate);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();
    }

}

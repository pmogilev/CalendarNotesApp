package tk.calendar.app;

/**
 * Created by Pasha on 5/3/14.
 *
 * A single Note class
 */
public class Note {

    private String mDate;

    private String mTitle;

    private String mContent;

    private Long mID;

    public Note(String date, String title, String content, Long id) {
        this.mDate = date;
        this.mTitle = title;
        this.mContent = content;
        this.mID = id;
    }
}

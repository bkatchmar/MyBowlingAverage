package bjk.MyBowlingAverage3;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class ScoreDates extends Activity
{
	ListView lstDates;
	int selectedLeague;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dateorganizor);
        
        //Set Widget Objects
        lstDates = (ListView)findViewById(R.id.lstDates);
        selectedLeague = getIntent().getExtras().getInt("Index");
        
        String[] uniqueScores = getDates();
        lstDates.setAdapter(new DateAdapter(this, R.layout.daterow, uniqueScores));
    }
    
    private String[] getDates()
    {
    	Cursor scoresCursor;
    	Vector<String> v = new Vector<String>();
    	
    	//Set Cursor initially
        scoresCursor = managedQuery(BowlingConn.Constants.CONTENT_URI, 
        		new String[] {BowlingConn.Constants.BOWLING_SCORE_COL, BowlingConn.Constants.BOWLING_SCORE_DATE}, 
        		"WHERE league = " + selectedLeague, 
        		null, 
        		"ORDER BY " + BowlingConn.Constants.BOWLING_SCORE_DATE + " ASC");
        
        if(scoresCursor.getCount() > 0)
        {
        	int dateCol = scoresCursor.getColumnIndex(BowlingConn.Constants.BOWLING_SCORE_DATE);
        	scoresCursor.moveToFirst();
        	
        	for (int i=0; i < scoresCursor.getCount(); i++)
        	{
            	String scoreDate = scoreDateAsString(scoresCursor.getLong(dateCol));
            	
            	if(!v.contains(scoreDate))
            		v.add(scoreDate);
            	
            	scoresCursor.moveToNext();
        	}
        }
        
        scoresCursor.close();
    	
        String[] datesToReturn = new String[v.size()];
        
        for(int i = 0; i < datesToReturn.length; i++)
        	datesToReturn[i] = v.elementAt(i);
        
    	return datesToReturn;
    }
    private String scoreDateAsString(long scoreDateAsLong)
    {
    	DateFormat dfLabelTimeFormat = DateFormat.getDateInstance();
		Date d = new Date(scoreDateAsLong);
		
    	return dfLabelTimeFormat.format(d.getTime());
    }
    
    class DateAdapter extends ArrayAdapter<String>
    {
    	Activity m_context;
    	String[] m_uniqueScoreDates;
    	
    	public DateAdapter(Activity context, int layoutID, String[] objects)
    	{
    		super(context, layoutID, objects);
    		this.m_uniqueScoreDates = objects;
    		this.m_context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent)
    	{
    		View row = convertView;
    		
    		if(row == null)
    		{
    			LayoutInflater inflater = m_context.getLayoutInflater();
    			row = inflater.inflate(R.layout.daterow, null);
    		}
    		
    		TextView lblDate = (TextView)row.findViewById(R.id.lblDate);
    		TextView lblAdaptAverage = (TextView)row.findViewById(R.id.lblAdaptAverage);
    		TextView lblAdaptHighest = (TextView)row.findViewById(R.id.lblAdaptHighest);
    		
    		lblDate.setText(m_uniqueScoreDates[position]);
    		
    		AverageCalculator ac = new AverageCalculator(GetCursorObject(m_uniqueScoreDates[position]));
    		lblAdaptAverage.setText("Average: " + ac.getAverage());
    		lblAdaptHighest.setText("Highest Score: " + ac.getHighestScore());
    		
    		return(row);
    	}
    	
    	private Cursor GetCursorObject(String scoreDate)
    	{
    		//Get Cursor object
    		long currentDate = Date.parse(scoreDate);
    		long nextDay;
    		
    		Cursor individual;
    		Calendar c = Calendar.getInstance();
    		c.setTimeInMillis(currentDate);
    		c.add(Calendar.DATE, 1);
    		
    		nextDay = c.getTimeInMillis();
    		
    		//Set Cursor initially
    		individual = managedQuery(BowlingConn.Constants.CONTENT_URI, 
            		new String[] {BowlingConn.Constants.BOWLING_SCORE_COL, BowlingConn.Constants.BOWLING_SCORE_DATE}, 
            		"WHERE " + BowlingConn.Constants.BOWLING_SCORE_DATE + " >= " + currentDate + " AND " + BowlingConn.Constants.BOWLING_SCORE_DATE + " < " + nextDay + " AND league = " + selectedLeague, 
            		null, 
            		"ORDER BY " + BowlingConn.Constants.BOWLING_SCORE_DATE + " ASC");
    		
    		return individual;
    	}
    }
}
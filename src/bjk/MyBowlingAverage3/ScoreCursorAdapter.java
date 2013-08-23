package bjk.MyBowlingAverage3;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;

public class ScoreCursorAdapter extends SimpleCursorAdapter
{
	private Context m_context;
	private Cursor m_cursor;
	
	public ScoreCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to)
	{
		super(context, layout, c, from, to);
		this.m_context = context;
		this.m_cursor = c;
	}
	
	public void bindView(View view, Context context, Cursor cursor)
	{
		super.bindView(view, context, cursor);
		
		TextView txtScoreDate = (TextView)view.findViewById(R.id.rowScoreDate);
		TextView txtRowId = (TextView)view.findViewById(R.id.rowScoreID);
		Button btnDelete = (Button)view.findViewById(R.id.btnDelete);
		
		int scoreId = Integer.parseInt(txtRowId.getText().toString());
		DateFormat dfLabelTimeFormat = DateFormat.getDateInstance();
		long scoreDateAsLong = Long.parseLong(txtScoreDate.getText().toString());
		Date d = new Date(scoreDateAsLong);
		
		txtScoreDate.setText(dfLabelTimeFormat.format(d.getTime()));
		btnDelete.setTag(scoreId);
		
		btnDelete.setOnClickListener(new View.OnClickListener()
        {
        	@Override
			public void onClick(View v)
        	{        		
        		Button btnThis = (Button)v;
        		
        		String[] params = new String[1];
        		params[0] = btnThis.getTag().toString();
        		
        		m_context.getContentResolver().delete(BowlingConn.Constants.CONTENT_URI, "_id=?", params);
        		m_cursor.requery();
        		
        		MainScreen mscrn = (MainScreen)m_context;
        		mscrn.updateScores();
        	}
		});
	}
}
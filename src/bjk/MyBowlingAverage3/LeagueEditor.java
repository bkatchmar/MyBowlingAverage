package bjk.MyBowlingAverage3;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LeagueEditor extends Activity
{
	EditText txtLeague;
	Button btnAddLeague;
	ListView lstLeagues;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leagueeditor);
        
        //Set Widget Objects
        txtLeague = (EditText)findViewById(R.id.txtLeague);
        btnAddLeague = (Button)findViewById(R.id.btnAddLeague);
        lstLeagues = (ListView)findViewById(R.id.lstLeagues);
        
        btnAddLeague.setOnClickListener(new View.OnClickListener()
        {
        	@Override
			public void onClick(View v)
        	{
        		if(txtLeague.getText().toString().length() == 0)
        		{
        			Toast.makeText(LeagueEditor.this, "League Must Have A Name", Toast.LENGTH_SHORT).show();
        		}
        		else
        		{
        			ContentValues values = new ContentValues(1);
            		values.put(LeagueConn.Constants.BOWLING_LEAGUE_NAME, txtLeague.getText().toString());
            		getContentResolver().insert(LeagueConn.Constants.CONTENT_URI, values);
            		BindGrid();
        			Toast.makeText(LeagueEditor.this, "League Has Been Entered", Toast.LENGTH_SHORT).show();
        		}
        	}
		});
        
        BindGrid();
    }
    
    private void BindGrid()
    {
    	Cursor leagueCursor = managedQuery(LeagueConn.Constants.CONTENT_URI, 
        		new String[] {LeagueConn.Constants.BOWLING_LEAGUE_NAME}, 
        		"", null, "");
        
        ListAdapter leagueAdapter = new LeagueCursorAdapter(LeagueEditor.this, 
				R.layout.cursorrow, 
				leagueCursor, 
				new String[] {LeagueConn.Constants.ID_COL, LeagueConn.Constants.BOWLING_LEAGUE_NAME},
				new int[] {R.id.rowScoreID, R.id.rowScore});
		
        lstLeagues.setAdapter(leagueAdapter);
    }
}
package bjk.MyBowlingAverage3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Vector;

import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;

public class MainScreen extends Activity
{
	Button btnAdd;
	EditText txtScore;
	TextView lblAverage, lblHighest;
	ListView lstScores;
	Spinner ddlLeagues;
	AdView ad;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Set Widget Objects
        btnAdd = (Button)findViewById(R.id.btnAdd);
        txtScore = (EditText)findViewById(R.id.txtScore);
        lblAverage = (TextView)findViewById(R.id.lblAverage);
        lblHighest = (TextView)findViewById(R.id.lblHighest);
        lstScores = (ListView)findViewById(R.id.lstScores);
        ddlLeagues = (Spinner)findViewById(R.id.ddlLeagues);
        
        //Sets up spinner
        SetUpSpinner();
        updateScores();
        
        btnAdd.setOnClickListener(new View.OnClickListener()
        {
        	@Override
			public void onClick(View v)
        	{
        		if(txtScore.getText().toString().length() > 0)
        		{
        			EnterScore(Integer.parseInt(txtScore.getText().toString()));
        			updateScores();
        			bindList();
        		}
        		else
        			setUpMessageScreen("Please enter a score to add it", "Error");
        	}
		});
        
        //Spinner Selected Listener
        ddlLeagues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				updateScores();
				bindList();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				
			}
		});
        
        bindList();
        
        AdManager.setTestDevices( new String[] { 
        		AdManager.TEST_EMULATOR,
        		"E83D20734F72FB3108F104ABC0FFC738", 
        });
        
        ad = (AdView)findViewById(R.id.adv);
        ad.requestFreshAd();
    }
    @Override
	public boolean onCreateOptionsMenu(Menu menu)
    {
    	new MenuInflater(getApplication()).inflate(R.menu.bowlingmenu, menu);
    	return(super.onCreateOptionsMenu(menu));
    }
    @Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{
    		case R.id.close:
    			finish();
    			return true;
    			
    		case R.id.goToDates:
    			setUpScoreDateOrganizeScreen();
    			return true;
    		
    		case R.id.allScores:
    			setUpYesNoDialog();
    			return true;
    			
    		case R.id.backup:
    			WriteBackup();
    			return true;
    			
    		case R.id.revert:
    			setUpPurgeAndRebuild();
    			return true;
    			
    		case R.id.ledit:
    			setUpLeagueEditorScreen();
    			return true;
    	}
    	return(super.onOptionsItemSelected(item));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	SetUpSpinner();
    }
    
    private void EnterScore(int score)
    {
    	if(score >= 0 && score <= 300)
    	{
    		ContentValues values = new ContentValues(2);
    		Calendar scoreDate = Calendar.getInstance();
    		
    		values.put(BowlingConn.Constants.BOWLING_SCORE_COL, score);
    		values.put(BowlingConn.Constants.BOWLING_SCORE_DATE, scoreDate.getTimeInMillis());
    		values.put(BowlingConn.Constants.BOWLING_SCORE_LEAGUE, ddlLeagues.getSelectedItemPosition());
    		getContentResolver().insert(BowlingConn.Constants.CONTENT_URI, values);
    		txtScore.setText("");
    	}
    	else
    		setUpMessageScreen("A Bowling Score Must Be Between 0 and 300", "Error");
    }
    private void EnterScore(BowlingScore score)
    {
    	ContentValues values = new ContentValues(2);
		values.put(BowlingConn.Constants.BOWLING_SCORE_COL, score.GetScore());
		values.put(BowlingConn.Constants.BOWLING_SCORE_DATE, score.GetScoreDate());
		values.put(BowlingConn.Constants.BOWLING_SCORE_LEAGUE, score.GetLeague());
		getContentResolver().insert(BowlingConn.Constants.CONTENT_URI, values);
    }
    //A Disclaimer Screen
    private void setUpMessageScreen(String msg, String title)
    {
    	new AlertDialog.Builder(MainScreen.this).setTitle(title)
		.setMessage(msg).setNeutralButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//This will close on its own
			}
		}).show();
    }
    private void setUpYesNoDialog()
    {
    	new AlertDialog.Builder(MainScreen.this).setTitle("Are You Sure?")
		.setMessage("Do You Really Want To Delete All Scores?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				deleteAllScores();
				updateScores();
				bindList();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		}).show();
    }
    
    private void setUpPurgeAndRebuild()
    {
    	new AlertDialog.Builder(MainScreen.this).setTitle("Are You Sure?")
		.setMessage("Purge All Score and Divert To Backup?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				WriteToCardSD Writer = new WriteToCardSD();
				
				if(Writer.HasFile())
				{
					deleteAllScores();
					Vector<BowlingScore> Scores = Writer.GetBackupValues();
					
					for(int i = 0; i < Scores.size(); i++)
						EnterScore(Scores.elementAt(i));
					
					bindList();
				}
				else
					setUpMessageScreen("Backup does not exists", "Error");
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		}).show();
    }
    private void RebuildDB()
    {
    	WriteToCardSD Writer = new WriteToCardSD();
    	Vector<BowlingScore> Scores = Writer.GetBackupValues();
		
		for(int i = 0; i < Scores.size(); i++)
			EnterScore(Scores.elementAt(i));
    }
    public void updateScores()
    {
    	Cursor scoresCursor;
    	
    	//Set Cursor initially
        scoresCursor = managedQuery(BowlingConn.Constants.CONTENT_URI, 
        		new String[] {BowlingConn.Constants.BOWLING_SCORE_COL}, 
        		"WHERE league = " + ddlLeagues.getSelectedItemPosition(), 
        		null, 
        		"ORDER BY " + BowlingConn.Constants.BOWLING_SCORE_DATE + " ASC");
        
        AverageCalculator ac = new AverageCalculator(scoresCursor);
        lblAverage.setText(ac.getAverage() + "");
        lblHighest.setText(ac.getHighestScore() + "");
        scoresCursor.close();
    }
    private void deleteAllScores()
    {
    	getContentResolver().delete(BowlingConn.Constants.CONTENT_URI, null, null);
    }
    private void bindList()
    {
    	Cursor ScoresCursor;
    	
    	//Set Cursor initially
    	ScoresCursor = managedQuery(BowlingConn.Constants.CONTENT_URI, 
        		new String[] {BowlingConn.Constants.BOWLING_SCORE_COL}, 
        		"WHERE league = " + ddlLeagues.getSelectedItemPosition(), 
        		null, 
        		"ORDER BY " + BowlingConn.Constants.BOWLING_SCORE_DATE + " ASC");
    	
    	if(ScoresCursor.getCount() == 0 && ddlLeagues.getSelectedItemPosition() == 0)
    	{
    		RebuildDB();
    		
    		ScoresCursor = managedQuery(BowlingConn.Constants.CONTENT_URI, 
    				new String[] {BowlingConn.Constants.BOWLING_SCORE_COL}, 
    				"WHERE league = " + ddlLeagues.getSelectedItemPosition(),
            		null, 
            		"ORDER BY " + BowlingConn.Constants.BOWLING_SCORE_DATE + " ASC");
    	}
        
    	ListAdapter scoreAdapter = new ScoreCursorAdapter(MainScreen.this, 
				R.layout.cursorrow, 
				ScoresCursor, 
				new String[] {BowlingConn.Constants.ID_COL, BowlingConn.Constants.BOWLING_SCORE_COL, BowlingConn.Constants.BOWLING_SCORE_DATE},
				new int[] {R.id.rowScoreID, R.id.rowScore, R.id.rowScoreDate});
		
    	lstScores.setAdapter(scoreAdapter);
    }
    
    //New Activities
    private void setUpScoreDateOrganizeScreen()
    {
    	try
    	{
    		final Intent goToScoreDate = new Intent();
    		goToScoreDate.setClass(this, ScoreDates.class);
    		goToScoreDate.putExtra("Index", ddlLeagues.getSelectedItemPosition());
    		startActivity(goToScoreDate);
    	}
    	catch(Exception e)
    	{
    		lblAverage.setText(e.getMessage());
    	}
    }
    private void setUpLeagueEditorScreen()
    {
    	try
    	{
    		final Intent goToLeagueEdit = new Intent();
    		goToLeagueEdit.setClass(this, LeagueEditor.class);
    		startActivityForResult(goToLeagueEdit, ActivityCodes.REQUEST_STANDARD);
    	}
    	catch(Exception e)
    	{
    		lblAverage.setText(e.getMessage());
    	}
    }
    private void WriteBackup()
    {
    	Cursor ScoresCursor;
    	
    	//Set Cursor initially
    	ScoresCursor = managedQuery(BowlingConn.Constants.CONTENT_URI, 
        		new String[] {BowlingConn.Constants.BOWLING_SCORE_COL, BowlingConn.Constants.BOWLING_SCORE_DATE}, 
        		"", 
        		null, 
        		"ORDER BY " + BowlingConn.Constants.BOWLING_SCORE_DATE + " ASC");
    	
    	WriteToCardSD Writer = new WriteToCardSD();
    	Writer.WriteBackup(ScoresCursor);
    	
    	Toast.makeText(MainScreen.this, "Backup Created", Toast.LENGTH_SHORT).show();
    }
    private String[] GetAllLeagues()
	{
    	try
    	{
	    	Cursor leagueCursor;
	    	
	    	//Set Cursor initially
	    	leagueCursor = managedQuery(LeagueConn.Constants.CONTENT_URI, 
	        		new String[] {LeagueConn.Constants.BOWLING_LEAGUE_NAME}, 
	        		"", null, "");
	        
	    	if(leagueCursor.getCount() > 0)
	    	{
	    		int leagueNameCol = leagueCursor.getColumnIndex(LeagueConn.Constants.BOWLING_LEAGUE_NAME);
	    		String[] LeagueNames = new String[leagueCursor.getCount()];
	    		
	    		leagueCursor.moveToFirst();
	    		
	    		for(int i = 0; i < leagueCursor.getCount(); i++)
	    		{
	    			LeagueNames[i] = leagueCursor.getString(leagueNameCol);
	    			leagueCursor.moveToNext();
	        	}
	    		
	    		return LeagueNames;
	    	}
	    	else
	    	{
	    		SetupPracticeLeague();
	    		String[] LeagueNames = {"Practice"};
	    		return LeagueNames;
	    	}
    	}
    	catch(Exception e)
    	{
    		SetupPracticeLeague();
    		String[] LeagueNames = {"Practice"};
    		return LeagueNames;
    	}
	}
    private void SetUpSpinner()
    {
    	//Sets up spinner
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, 
        		GetAllLeagues());
        
        
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddlLeagues.setAdapter(aa);
    }
    private void SetupPracticeLeague()
    {
    	ContentValues values = new ContentValues(1);
		values.put(LeagueConn.Constants.BOWLING_LEAGUE_NAME, "Practice");
		getContentResolver().insert(LeagueConn.Constants.CONTENT_URI, values);
    }
}
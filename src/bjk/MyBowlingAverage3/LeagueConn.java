package bjk.MyBowlingAverage3;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public class LeagueConn extends ContentProvider
{
	private static final String DATABASE_NAME = "bkbowlingavg.db";
	private static final int CONSTANTS = 1;
	private static final int CONSTANT_ID = 2;
	private static final UriMatcher MATCHER;
	private static HashMap<String, String> BOWLING_LIST;
	
	private SQLiteDatabase bowlingDb;
	
	//**********************
	//Columns Constant Class
	public static final class Constants implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://bjk.bowling.LeagueConn/bkbowlingleague");
		public static final String ID_COL = "_id";
		public static final String BOWLING_LEAGUE_NAME = "leaguename";
	}
	//Columns Constant Class
	//**********************
	
	//******
	//Static
	static
	{
		MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
		MATCHER.addURI("bjk.bowling.LeagueConn", "bkbowlingleague", CONSTANTS);
		MATCHER.addURI("bjk.bowling.LeagueConn", "bkbowlingleague/#", CONSTANT_ID);

		BOWLING_LIST=new HashMap<String, String>();
		BOWLING_LIST.put(LeagueConn.Constants._ID, LeagueConn.Constants._ID);
		BOWLING_LIST.put(LeagueConn.Constants.BOWLING_LEAGUE_NAME, LeagueConn.Constants.BOWLING_LEAGUE_NAME);
	}
	//Static
	//******
	
	//**********************
	//SQLiteOpenHelper Class
	private class LeagueHelperDB extends SQLiteOpenHelper
	{
		public LeagueHelperDB(Context context)
		{
			super(context, DATABASE_NAME, null, 1);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			Cursor c=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='bkbowlingleague'", null);
			
			//Create Score Table
			try
			{
				if (c.getCount()==0)
				{
					db.execSQL("CREATE TABLE bkbowlingleague (_id INTEGER PRIMARY KEY AUTOINCREMENT, leaguename TEXT);");
					
					ContentValues cv = new ContentValues();
					cv.put(LeagueConn.Constants.BOWLING_LEAGUE_NAME, "Practice");
					db.insert("leaguename", "leaguename", cv);
				}
			}
			finally
			{
				c.close();
			}
			
			Cursor c2=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='bkbowlingavg'", null);
			
			//Create Score Table
			try
			{
				if (c2.getCount()==0)
					db.execSQL("CREATE TABLE bkbowlingavg (_id INTEGER PRIMARY KEY AUTOINCREMENT, score INTEGER, scoredate INTEGER, league INTEGER);");
			}
			finally
			{
				c2.close();
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			android.util.Log.w("Bowling", "Upgrading database, which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS bkbowlingavg");
			db.execSQL("DROP TABLE IF EXISTS bkbowlingleague");
			onCreate(db);
		}
	}
	//SQLiteOpenHelper Class
	//**********************
	
	//****************
	//Provider Methods
	@Override
	public int delete(Uri url, String where, String[] whereArgs)
	{
		int count;
		
		if (MATCHER.match(url)==CONSTANTS)
			count=bowlingDb.delete("bkbowlingleague", where, whereArgs);
		else
			count = 0;

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}
	@Override
	public String getType(Uri uri)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) 
	{
		long rowID = 0;
		
		if (MATCHER.match(uri)!=CONSTANTS)
			throw new IllegalArgumentException("Unknown URL");
		
		if(initialValues.size() == 1)
		{
			//We Insert A New Record
			rowID = bowlingDb.insert("bkbowlingleague", "leaguename", initialValues);
			
			if (rowID > 0)
			{
				Uri nUri=ContentUris.withAppendedId(Constants.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(nUri, null);
				return nUri;
			}
			else
				throw new SQLException("Failed to insert row");
		}
		else
			throw new SQLException("Failed to insert row");
	}
	@Override
	public boolean onCreate()
	{
		bowlingDb=(new LeagueHelperDB(getContext())).getWritableDatabase();
		return (bowlingDb == null) ? false : true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, 
			String selection, String[] selectionArgs, String sortOrder)
	{
		Cursor c;
		c = bowlingDb.rawQuery("SELECT _id, leaguename FROM bkbowlingleague " + selection, null);
		return c;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection, 
			String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	//Provider Methods
	//****************
}
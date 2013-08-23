package bjk.MyBowlingAverage3;

import android.database.Cursor;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Vector;

public class WriteToCardSD
{
	private boolean m_hasCard;
	private String m_directory, m_filename;
	
	//Constructor
	public WriteToCardSD()
	{
		m_filename = "backup.txt";
			
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			m_hasCard = true;
			
			File SdCard = Environment.getExternalStorageDirectory();
        	File BackupDir = new File (SdCard.getAbsolutePath() + "/mybowling");
        	
        	if(!BackupDir.exists())
        		BackupDir.mkdir();
        	
        	m_directory  = SdCard.getAbsolutePath() + "/mybowling/";
		}
    	else
    	{
    		m_hasCard = false;
    		m_directory = "";
    	}
	}
	public boolean WriteBackup(Cursor c)
	{
		boolean ReturnValue;
		try
		{
			File file = new File(m_directory, m_filename);
			FileOutputStream f = new FileOutputStream(file);
			
			if(c.getCount() > 0)
			{
				int scoreCol = c.getColumnIndex(BowlingConn.Constants.BOWLING_SCORE_COL);
				int dateCol = c.getColumnIndex(BowlingConn.Constants.BOWLING_SCORE_DATE);
				int leagueCol = c.getColumnIndex(BowlingConn.Constants.BOWLING_SCORE_LEAGUE);
				String CurrentLine;
				c.moveToFirst();
				
				//Iterate through the columns
				for (int i=0; i < c.getCount(); i++)
				{
					CurrentLine = c.getString(scoreCol) + "," + c.getLong(dateCol) + "," + c.getInt(leagueCol) + "\n";
					f.write(CurrentLine.getBytes(), 0, CurrentLine.getBytes().length);
					c.moveToNext();
				}
			}
			
			f.close();
			ReturnValue = true;
		}
		catch(Exception e)
		{
			ReturnValue = false;
		}
		
		return ReturnValue;
	}
	public Vector<BowlingScore> GetBackupValues()
	{
		Vector<BowlingScore> scores = new Vector<BowlingScore>();
		String Delimiter1 = ",";
		String[] fields;
		
		try
		{
			File fBowlingFile = new File(m_directory + m_filename);
			FileInputStream fBowlingInputStream = new FileInputStream(fBowlingFile);
			BufferedReader bReader = new BufferedReader(new InputStreamReader(fBowlingInputStream));
			
			String strNextLine;
			
			while((strNextLine = bReader.readLine()) != null)
			{
				fields = strNextLine.split(Delimiter1);
				
				if(fields.length == 2)
					scores.add(new BowlingScore(Integer.parseInt(fields[0]), Long.parseLong(fields[1]), 0));
				else
					scores.add(new BowlingScore(Integer.parseInt(fields[0]), Long.parseLong(fields[1]), Integer.parseInt(fields[2])));
			}
		}
		catch(Exception e)
		{
		}
		
		return scores;
	}
	
	public boolean HasCardSD()
	{
		return m_hasCard;
	}
	public String FileName()
	{
		return m_directory + m_filename;
	}
	public boolean HasFile()
	{
		File fBowlingFile = new File(m_directory + m_filename);
		return fBowlingFile.exists();
	}
}
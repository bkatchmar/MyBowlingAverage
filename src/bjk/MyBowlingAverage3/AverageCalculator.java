package bjk.MyBowlingAverage3;

import android.database.Cursor;

public class AverageCalculator
{
	//Private Variables
	private Cursor m_scores;
	private int m_highestScore;
	private double m_average, m_scoreTotal;
	
	//Constructor
	public AverageCalculator(Cursor c)
	{
		m_scores = c;
		m_highestScore = 0;
		m_scoreTotal = 0.0;
		m_average = 0.0;
		crunchNumbers();
	}
	
	//Private Methods
	private void crunchNumbers()
	{
		if(m_scores != null)
		{
			if(m_scores.getCount() > 0)
			{
				int scoreCol = m_scores.getColumnIndex(BowlingConn.Constants.BOWLING_SCORE_COL);
				int currentScore = 0;
				
				m_scores.moveToFirst();
				
				//Iterate through the columns
				for (int i=0; i < m_scores.getCount(); i++)
				{
					//Get the current score
					currentScore = m_scores.getInt(scoreCol);
					
					//Add current score to the total
					m_scoreTotal += currentScore;
					
					if(currentScore > m_highestScore)
						m_highestScore = currentScore;
					
					m_scores.moveToNext();
				}
				
				m_average = (m_scoreTotal / m_scores.getCount());
			}
		}
	}
	
	//Public Methods
	public double getAverage()
	{
		return m_average;
	}
	public int getHighestScore()
	{
		return m_highestScore;
	}
}
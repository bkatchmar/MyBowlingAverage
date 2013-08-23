package bjk.MyBowlingAverage3;

public class BowlingScore
{
	private int m_bowlingScore;
	private long m_bowlingDate;
	private int m_league;
	
	public BowlingScore()
	{
		m_bowlingScore = 0;
		m_bowlingDate = 0;
		m_league = 1;
	}
	public BowlingScore(int Score, long DateOfScore, int League)
	{
		m_bowlingScore = Score;
		m_bowlingDate = DateOfScore;
		m_league = League;
	}
	
	public int GetScore()
	{
		return m_bowlingScore;
	}
	public long GetScoreDate()
	{
		return m_bowlingDate;
	}
	public int GetLeague()
	{
		return m_league;
	}
}
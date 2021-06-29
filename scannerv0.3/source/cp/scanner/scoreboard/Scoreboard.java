package cp.scanner.scoreboard;
import java.util.HashMap;
public class Scoreboard
{
	private String name;
	private HashMap<Long,Long>scores;
	public Scoreboard(String name)
	{
		this.name = name;
		this.scores = new HashMap<>();
	}
	public int hashCode()
	{
		return this.name.hashCode() ^ this.scores.hashCode();
	}
	public String getName()
	{
		return this.name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public HashMap<Long,Long>getScores()
	{
		return this.scores;
	}
}
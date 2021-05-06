package cp.scanner.algo;
import java.util.Arrays;
public class StringAlgs
{
	/**
	 * Gets all permutations of a string
	 * @param s The string dumbass
	 * @return A string containing all permutations in alphabetical order
	 */
	public static final String permutes(String s)
	{
		char[]arr=s.toCharArray();
		String res = s;
		StringAlgs.nextPermutation(arr);
		while(!Arrays.equals(arr, s.toCharArray()))
		{
			res += System.getProperty("line.separator") + new String(arr);
			StringAlgs.nextPermutation(arr);
		}
		return res;
	}
	public static final void nextPermutation(char[]arr)
	{
	}
}
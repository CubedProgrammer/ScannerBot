package cp.scanner.algo;
import java.util.Arrays;
public class StringAlgs
{
	/**
	 * Arithmancy calculator
	 * @param name Your name
	 * @return the character, heart, and social number
	 */
	public static native int arithmancy(String name);
	/**
	 * Next permutation of a char array
	 * @param arr An array
	 */
	public static final void nextPermutation(char[]arr)
	{
		int pos = 0; // in case for last permutation, reverse whole array
		for(int i = arr.length - 1; i > 0; i--)
		{
			if(arr[i-1]<arr[i])
			{
				pos = i;
				i = 1;
			}
		}
		char tmp = 0;
		for(int i = pos; i < arr.length + pos >> 1; i++)
		{
			tmp = arr[i];
			arr[i] = arr[arr.length + pos - i - 1];
			arr[arr.length + pos - i - 1] = tmp;
		}
		if(pos > 0)
		{
			int swap = 0;
			for(int i=pos;i<arr.length;i++)
			{
				if(arr[pos - 1] < arr[i])
				{
					swap = i;
					i = arr.length;
				}
			}
			--pos;
			tmp = arr[pos];
			arr[pos] = arr[swap];
			arr[swap] = tmp;
		}
	}
	/**
	 * Gets all permutations of a string
	 * @param s The string dumbass
	 * @return A string containing all permutations in alphabetical order
	 */
	public static final String permutes(String s)
	{
		char[]arr=s.toCharArray();
		Arrays.sort(arr);
		String res = new String(arr);
		StringAlgs.nextPermutation(arr);
		while(!Arrays.equals(arr, s.toCharArray()))
		{
			res += System.getProperty("line.separator") + new String(arr);
			StringAlgs.nextPermutation(arr);
		}
		return res;
	}
}
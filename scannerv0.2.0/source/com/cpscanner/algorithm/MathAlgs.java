package com.cpscanner.algorithm;
import java.math.*;
/**
 * Class with static methods for mathematical algorithms.
 * @author CubedProgrammer
 */
public class MathAlgs
{
	/**
	 * Finds an integer root of a BigDecimal.
	 * @param x The BigDecimal to find the root of.
	 * @param n The number on the top left of the radicand. In other words, this method finds the nth root of x.
	 * @return The BigDecimal that is x raised to the power of 1/n.
	 */
	public static final BigDecimal yroot(BigDecimal x,int n)
	{
		BigDecimal y=x.divide(new BigDecimal(n),MathContext.DECIMAL128);
		BigDecimal f=y.pow(n).subtract(x);
		while(f.abs().compareTo(new BigDecimal(0.000000059604644775390625/1099511627776d))>=0)
		{
			y=y.subtract(f.divide(y.pow(n-1).multiply(new BigDecimal(n)),MathContext.DECIMAL128));
			f=y.pow(n).subtract(x);
		}
		return y;
	}
}
package com.cpscanner.algorithm;
import java.math.*;
/**
 * Class with static methods for mathematical algorithms.
 * @author CubedProgrammer
 */
public class MathAlgs
{
	static
	{
		System.out.println("variable java.library.path is "+System.getProperty("java.library.path"));
		if(System.getProperty("os.name").length()>=7&&System.getProperty("os.name").substring(0,7).equalsIgnoreCase("windows"))
			System.loadLibrary("lib_triangle_solver");
		else
			System.loadLibrary("_triangle_solver");
		System.loadLibrary("MathAlgs");
	}
	/**
	 * Finds a power of a BigDecimal.
	 * @param x The base
	 * @param n The exponent
	 * @return The BigDecimal that is base raised to the power of exp.
	 */
	public static final BigDecimal pow(BigDecimal base,BigDecimal exp)
	{
		BigDecimal ans=BigDecimal.ONE;
		if(exp.compareTo(new BigDecimal(Integer.MAX_VALUE))>0)
		{
			if(BigDecimal.ZERO.equals(base))
			{
				ans = BigDecimal.ZERO.equals(exp)?new BigDecimal(Double.NaN):BigDecimal.ZERO;
			}
			else if(base.equals(BigDecimal.ONE))
			{
				ans = BigDecimal.ONE;
			}
		}
		else
		{
			BigDecimal denom=BigDecimal.ONE;
			BigDecimal pow=base;
			for(int i=1;i<100000;i++)
			{
				denom=denom.multiply(new BigDecimal(i));
				ans=ans.add(pow.divide(denom,MathContext.DECIMAL128));
				pow=pow.multiply(base);
			}
		}
		return ans;
	}
	/**
	 * Finds the solution of a linear equation with two unknowns.
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param e
	 * @param f
	 * @return The array containing two numbers which are the solution to the equation.
	 */
	public static final BigDecimal[]solveLinearEquation(BigDecimal a,BigDecimal b,BigDecimal c,BigDecimal d,BigDecimal e,BigDecimal f)
	{
		BigDecimal det=a.multiply(d).subtract(b.multiply(c));
		return new BigDecimal[]{d.multiply(e).subtract(b.multiply(f)).divide(det,MathContext.DECIMAL128),a.multiply(f).subtract(c.multiply(e)).divide(det,MathContext.DECIMAL128)};
	}
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
	/**
	 * Solves a triangle.
	 * @param r The array containing the angles, in degrees, and the lengths of the sides.
	 */
	public static native void solveTriangle(double[]arr);
}
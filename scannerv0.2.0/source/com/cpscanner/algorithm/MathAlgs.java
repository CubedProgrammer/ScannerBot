package com.cpscanner.algorithm;
import java.math.*;
public class MathAlgs
{
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
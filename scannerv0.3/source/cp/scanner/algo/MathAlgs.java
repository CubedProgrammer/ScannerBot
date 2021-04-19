package cp.scanner.algo;
import java.math.BigDecimal;
import java.math.MathContext;
public class MathAlgs
{
	public static final BigDecimal PI = new BigDecimal("3.1415926535897932384629433832795");
	public static final BigDecimal PI_BY_180 = PI.divide(BigDecimal.valueOf(180), MathContext.DECIMAL128);
	public static final int TRIG_TAYLOR_LIMIT=60;
	public static final int ITRIG_TAYLOR_LIMIT=3628800;
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
			else
			{
				ans = null;
			}
		}
		else
		{
			ans = exp(log(base).multiply(exp,MathContext.DECIMAL128));
		}
		return ans;
	}
	/**
	 * @param exp The exponent
	 * @return The answer to exp(exp)
	 */
	public static final BigDecimal exp(BigDecimal exp)
	{
		BigDecimal ans=BigDecimal.ONE;
		BigDecimal denom=BigDecimal.ONE;
		BigDecimal pow=exp;
		for(int i=1;i<100;i++)
		{
			denom=denom.multiply(new BigDecimal(i));
			ans=ans.add(pow.divide(denom,MathContext.DECIMAL128),MathContext.DECIMAL128);
			pow=pow.multiply(exp);
		}
		return ans;
	}
	/**
	 * @param x The number to take the logarithm of
	 * @return The answer to log(x)
	 */
	public static final BigDecimal log(BigDecimal x)
	{
		BigDecimal guess=BigDecimal.TEN;
		BigDecimal check=exp(guess).subtract(x,MathContext.DECIMAL128);
		while(check.abs().compareTo(new BigDecimal("0.0000000000000000000000001"))>0)
		{
			guess=guess.subtract(check.divide(check.add(x,MathContext.DECIMAL128),MathContext.DECIMAL128),MathContext.DECIMAL128);
			check=exp(guess).subtract(x,MathContext.DECIMAL128);
		}
		return guess;
	}
	/**
	 * Adds 2D vectors in polar form
	 * @param angles The list of angles
	 * @param magnitudes The list of magnitude
	 * @param An array of size two, stores angle in first position and magnitude in second, of resultant vector
	 */
	public static final void vectorPolarAddition(BigDecimal[]angles,BigDecimal[]mags,BigDecimal[]result)
	{
		BigDecimal x=BigDecimal.ZERO;
		BigDecimal y=BigDecimal.ZERO;
	    for(int i=0;i<mags.length;i++)
	    {
	        x=x.add(mags[i].multiply(cos(angles[i])),MathContext.DECIMAL128);
	        y=y.add(mags[i].multiply(sin(angles[i])),MathContext.DECIMAL128);
	    }
	    result[1]=x.multiply(x).add(y.multiply(y),MathContext.DECIMAL128).sqrt(MathContext.DECIMAL128);
	    result[0]=x.compareTo(BigDecimal.ZERO)<0?MathAlgs.PI.subtract(MathAlgs.asin(y.divide(result[1],MathContext.DECIMAL128)),MathContext.DECIMAL128):MathAlgs.asin(y.divide(result[1],MathContext.DECIMAL128));
	}
	/**
	 * Solves a triangle.
	 * @param shape Angle and sides of the triangle.
	 */
	public static final void solvet(BigDecimal[] shape)
	{
		BigDecimal A = shape[0], B = shape[1], C = shape[2];
		BigDecimal a = shape[3], b = shape[4], c = shape[5];
		int au=(A.equals(BigDecimal.valueOf(-1)) ? 1 : 0)+(B.equals(BigDecimal.valueOf(-1)) ? 1 : 0)+(C.equals(BigDecimal.valueOf(-1)) ? 1 : 0);
		int su=(a.equals(BigDecimal.valueOf(-1)) ? 1 : 0)+(b.equals(BigDecimal.valueOf(-1)) ? 1 : 0)+(c.equals(BigDecimal.valueOf(-1)) ? 1 : 0);
		if(A.compareTo(BigDecimal.ZERO)>=0)
		{
			A = A.multiply(MathAlgs.PI_BY_180);
		}
		if(B.compareTo(BigDecimal.ZERO)>=0)
		{
			B = B.multiply(MathAlgs.PI_BY_180);
		}
		if(C.compareTo(BigDecimal.ZERO)>=0)
		{
			C = C.multiply(MathAlgs.PI_BY_180);
		}
		if(au<=1)
		{
			if(au==1)
			{
				if(A.equals(BigDecimal.valueOf(-1)))
				{
					A=MathAlgs.PI.subtract(B).subtract(C);
				}
				else if(B.equals(BigDecimal.valueOf(-1)))
				{
					B=MathAlgs.PI.subtract(B).subtract(C);
				}
				else
				{
					C=MathAlgs.PI.subtract(A).subtract(B);
				}
				au--;
			}
			if(su<=2)
			{
				if(a.compareTo(BigDecimal.ZERO)>=0)
				{
					b=a.multiply(MathAlgs.sin(B)).divide(MathAlgs.sin(A),MathContext.DECIMAL128);
					c=a.multiply(MathAlgs.sin(C)).divide(MathAlgs.sin(A),MathContext.DECIMAL128);
				}
				else if(b.compareTo(BigDecimal.ZERO)>=0)
				{
					a=b.multiply(MathAlgs.sin(A)).divide(MathAlgs.sin(B),MathContext.DECIMAL128);
					c=b.multiply(MathAlgs.sin(C)).divide(MathAlgs.sin(B),MathContext.DECIMAL128);
				}
				else
				{
					b=c.multiply(MathAlgs.sin(B)).divide(MathAlgs.sin(C),MathContext.DECIMAL128);
					a=c.multiply(MathAlgs.sin(A)).divide(MathAlgs.sin(C),MathContext.DECIMAL128);
				}
			}
		}
		else if(au==2)
		{
			if(su<=1)
			{
				if(A.compareTo(BigDecimal.ZERO)>=0)
				{
					if(a.compareTo(BigDecimal.ZERO)>=0)
					{
						if(b.compareTo(BigDecimal.ZERO)>=0)
						{
							B=MathAlgs.asin(b.multiply(MathAlgs.sin(A)).divide(a,MathContext.DECIMAL128));
							C=MathAlgs.PI.subtract(A).subtract(B);
							c=a.multiply(MathAlgs.sin(C)).divide(MathAlgs.sin(A),MathContext.DECIMAL128);
						}
						else//,MathContext.DECIMAL128
						{
							C=MathAlgs.asin(c.multiply(MathAlgs.sin(A)).divide(a,MathContext.DECIMAL128));
							B=MathAlgs.PI.subtract(A).subtract(C);
							b=a.multiply(MathAlgs.sin(B)).divide(MathAlgs.sin(A),MathContext.DECIMAL128);
						}
					}
					else
					{
						a=b.multiply(b).add(c.multiply(c)).subtract(BigDecimal.valueOf(2).multiply(b).multiply(c).multiply(MathAlgs.cos(A))).sqrt(MathContext.DECIMAL128);
						B=MathAlgs.asin(b.multiply(MathAlgs.sin(A)).divide(a,MathContext.DECIMAL128));
						C=MathAlgs.asin(c.multiply(MathAlgs.sin(A)).divide(a,MathContext.DECIMAL128));
					}
				}
				else if(B.compareTo(BigDecimal.ZERO)>=0)
				{
					if(b.compareTo(BigDecimal.ZERO)>=0)
					{
						if(a.compareTo(BigDecimal.ZERO)>=0)
						{
							A=MathAlgs.asin(a.multiply(MathAlgs.sin(B)).divide(b,MathContext.DECIMAL128));
							C=MathAlgs.PI.subtract(A).subtract(B);
							c=a.multiply(MathAlgs.sin(C)).divide(MathAlgs.sin(A),MathContext.DECIMAL128);
						}
						else
						{
							C=MathAlgs.asin(c.multiply(MathAlgs.sin(B)).divide(b,MathContext.DECIMAL128));
							A=MathAlgs.PI.subtract(B).subtract(C);
							a=b.multiply(MathAlgs.sin(A)).divide(MathAlgs.sin(B),MathContext.DECIMAL128);
						}
					}
					else
					{
						b=a.multiply(a).add(c.multiply(c)).subtract(BigDecimal.valueOf(2).multiply(a).multiply(c).multiply(MathAlgs.cos(B))).sqrt(MathContext.DECIMAL128);
						A=MathAlgs.asin(a.multiply(MathAlgs.sin(B)).divide(b,MathContext.DECIMAL128));
						C=MathAlgs.asin(c.multiply(MathAlgs.sin(A)).divide(a,MathContext.DECIMAL128));
					}
				}
				else
				{
					if(c.compareTo(BigDecimal.ZERO)>=0)
					{
						if(a.compareTo(BigDecimal.ZERO)>=0)
						{
							A=MathAlgs.asin(a.multiply(MathAlgs.sin(B)).divide(b,MathContext.DECIMAL128));
							B=MathAlgs.PI.subtract(A).subtract(C);
							b=a.multiply(MathAlgs.sin(B)).divide(MathAlgs.sin(A),MathContext.DECIMAL128);
						}
						else
						{
							B=MathAlgs.asin(b.multiply(MathAlgs.sin(C)).divide(c,MathContext.DECIMAL128));
							A=MathAlgs.PI.subtract(B).subtract(C);
							a=b.multiply(MathAlgs.sin(A)).divide(MathAlgs.sin(B),MathContext.DECIMAL128);
						}
					}
					else
					{
						c=a.multiply(a).add(b.multiply(b)).subtract(BigDecimal.valueOf(2).multiply(a).multiply(b).multiply(MathAlgs.cos(C))).sqrt(MathContext.DECIMAL128);
						A=MathAlgs.asin(a.multiply(MathAlgs.sin(B)).divide(b,MathContext.DECIMAL128));
						B=MathAlgs.asin(b.multiply(MathAlgs.sin(C)).divide(c,MathContext.DECIMAL128));
					}
				}
			}
		}
		else if(su==0)
		{
			A=MathAlgs.acos(b.multiply(b).add(c.multiply(c)).subtract(a.multiply(a)).divide(BigDecimal.valueOf(2).multiply(b).multiply(c),MathContext.DECIMAL128));
			B=MathAlgs.acos(a.multiply(a).add(c.multiply(c)).subtract(b.multiply(b)).divide(BigDecimal.valueOf(2).multiply(a).multiply(c),MathContext.DECIMAL128));
			C=MathAlgs.acos(a.multiply(a).add(b.multiply(b)).subtract(c.multiply(c)).divide(BigDecimal.valueOf(2).multiply(a).multiply(b),MathContext.DECIMAL128));
		}
		A=A.divide(MathAlgs.PI_BY_180,MathContext.DECIMAL128);
		B=B.divide(MathAlgs.PI_BY_180,MathContext.DECIMAL128);
		C=C.divide(MathAlgs.PI_BY_180,MathContext.DECIMAL128);
		shape[0] = A;
		shape[1] = B;
		shape[2] = C;
		shape[3] = a;
		shape[4] = b;
		shape[5] = c;
	}
	/**
	 * Computes the sine of an angle
	 * @param theta The angle, duh
	 * @return The sine of theta
	 */
	public static final BigDecimal sin(BigDecimal theta)
	{
		if(theta.compareTo(BigDecimal.ZERO)<0||theta.compareTo(BigDecimal.ONE)>0)
			return null;
		else
		{
			BigDecimal len=BigDecimal.ZERO;
			BigDecimal num=theta;
			BigDecimal denom=BigDecimal.ONE;
			for(int i=0;i<MathAlgs.TRIG_TAYLOR_LIMIT;i++)
			{
				len=len.add(num.divide(denom,MathContext.DECIMAL128),MathContext.DECIMAL128);
				num=num.multiply(theta).multiply(theta).multiply(BigDecimal.ONE.negate(),MathContext.DECIMAL128);
				denom=denom.multiply(BigDecimal.valueOf(2*i+2)).multiply(BigDecimal.valueOf(2*i+3),MathContext.DECIMAL128);
			}
			return len;
		}
	}
	/**
	 * Computes the cosine of an angle
	 * @param theta The angle, duh
	 * @return The cosine of theta
	 */
	public static final BigDecimal cos(BigDecimal theta)
	{
		return MathAlgs.sin(new BigDecimal("1.5707963267948966192313216916398").subtract(theta,MathContext.DECIMAL128));
	}
	/**
	 * Computes the tangent of an angle
	 * @param theta The angle, duh
	 * @return The tangent of theta
	 */
	public static final BigDecimal tan(BigDecimal theta)
	{
		return MathAlgs.sin(theta).divide(MathAlgs.cos(theta),MathContext.DECIMAL128);
	}
	/**
	 * Computes the cotangent of an angle
	 * @param theta The angle, duh
	 * @return The cotangent of theta
	 */
	public static final BigDecimal cot(BigDecimal theta)
	{
		return MathAlgs.cos(theta).divide(MathAlgs.sin(theta),MathContext.DECIMAL128);
	}
	/**
	 * Computes the secant of an angle
	 * @param theta The angle, duh
	 * @return The secant of theta
	 */
	public static final BigDecimal sec(BigDecimal theta)
	{
		return BigDecimal.ONE.divide(MathAlgs.cos(theta),MathContext.DECIMAL128);
	}
	/**
	 * Computes the cosecant of an angle
	 * @param theta The angle, duh
	 * @return The cosecant of theta
	 */
	public static final BigDecimal csc(BigDecimal theta)
	{
		return BigDecimal.ONE.divide(MathAlgs.sin(theta),MathContext.DECIMAL128);
	}
	/**
	 * Computes the inverse sine of a number
	 * @param bd The number
	 * @return Theta
	 */
	public static final BigDecimal asin(BigDecimal bd)
	{
		BigDecimal ans=bd;
		BigDecimal s=MathAlgs.sin(ans).subtract(bd);
		while(s.abs().compareTo(new BigDecimal("0.000000000000000000000000000000001"))>0)
		{
			ans=ans.subtract(s.divide(MathAlgs.cos(ans),MathContext.DECIMAL128));
			s=MathAlgs.sin(ans).subtract(bd);
		}
		return ans;
	}
	/**
	 * Computes the inverse cosine of a number
	 * @param bd The number
	 * @return Theta
	 */
	public static final BigDecimal acos(BigDecimal bd)
	{
		return new BigDecimal("1.5707963267948966192313216916398").subtract(MathAlgs.asin(bd),MathContext.DECIMAL128);
	}
	/**
	 * Finds the solution of a system of linear equations, given as an augmented matrix.
	 * @param mat
	 * @author Yash Varyani
	 */
	public static final void solveLinearEquation(BigDecimal[][]mat)
	{
		/* reduction into r.e.f. */
		int singular_flag = forwardElim(mat); 

		/* if matrix is singular */
		if (singular_flag != -1) 
		{
			for(int i=0;i<mat.length;i++)
			{
				for(int j=0;j<mat[i].length;j++)
				{
					mat[i][j]=BigDecimal.ZERO.subtract(BigDecimal.ONE);
				}
			}
		}
		else
		{
			/* get solution to system and print it using 
			backward substitution */
			MathAlgs.backSub(mat); 
		}
		
	}
	/**
	 * Swap rows in an augmented matrix.
	 * @param mat Some random matrix.
	 * @param u One row to swap.
	 * @param v The other row to swap.
	 */
	public static final void swapRows(BigDecimal[][]mat,int u,int v)
	{
		BigDecimal temp=null;
		for(int i=0;i<=mat.length;i++)
		{
			temp=mat[u][i];
			mat[u][i]=mat[v][i];
			mat[v][i]=temp;
		}
	}
	/**
	 * Does forward elimination on an augmented matrix.
	 * @param mat Some random matrix.
	 * @author Yash Varyani
	 * @return The position of the zero in a singular matrix or -1.
	 */
	public static final int forwardElim(BigDecimal[][]mat) 
	{
		int i_max = 0;
		BigDecimal v_max = null;
		BigDecimal f = null;
		int N = mat.length;
		int singular = -1;
		
		for (int k=0; k<N; k++) 
		{ 
			// Initialize maximum value and index for pivot 
			i_max = k;
			v_max = mat[i_max][k]; 

			/* find greater amplitude for pivot if any */
			for (int i = k+1; i < N; i++) 
				if (mat[i][k].abs().compareTo(v_max) > 0) 
				{
					v_max = mat[i][k].abs();
					i_max = i; 
				}

			/* if a prinicipal diagonal element is zero, 
			* it denotes that matrix is singular, and 
			* will lead to a division-by-zero later. */
			if (mat[k][i_max].equals(BigDecimal.ZERO)) 
			{
				singular = k;
				k = N;
				continue;
			}

			/* Swap the greatest value row with current row */
			if (i_max != k) 
				swapRows(mat, k, i_max); 


			for (int i=k+1; i<N; i++) 
			{ 
				/* factor f to set current row kth element to 0, 
				* and subsequently remaining kth column to 0 */
				f = mat[i][k].divide(mat[k][k],MathContext.DECIMAL128);

				/* subtract fth multiple of corresponding kth 
				row element*/
				for (int j=k+1; j<=N; j++) 
					mat[i][j] = mat[i][j].subtract(mat[k][j].multiply(f),MathContext.DECIMAL128);

				/* filling lower triangular matrix with zeros*/
				mat[i][k] = BigDecimal.ZERO;
			}

		}
		return singular; 
	}
	/**
	 * Does back substitution.
	 * @param mat Some random matrix.
	 * @author Yash Varyani
	 */
	public static final void backSub(BigDecimal[][]mat)
	{ 
		int N = mat.length;

		/* Start calculating from last equation up to the 
		first */
		for (int i = N-1; i >= 0; i--) 
		{ 
			/* Initialize j to i+1 since matrix is upper 
			triangular*/
			for (int j=i+1; j<N; j++) 
			{ 
				/* subtract all the lhs values 
				* except the coefficient of the variable 
				* whose value is being calculated */
				mat[i][N] = mat[i][N].subtract(mat[i][j].multiply(mat[j][N]),MathContext.DECIMAL128);
			}

			/* divide the RHS by the coefficient of the 
			unknown being calculated */
			mat[i][N] = mat[i][N].divide(mat[i][i],MathContext.DECIMAL128);
		}
		
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
		while(f.abs().compareTo(new BigDecimal("0.00000000000000000000000000001"))>=0)
		{
			y=y.subtract(f.divide(y.pow(n-1).multiply(new BigDecimal(n)),MathContext.DECIMAL128));
			f=y.pow(n).subtract(x);
		}
		return y;
	}
}
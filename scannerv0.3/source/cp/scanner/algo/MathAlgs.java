package cp.scanner.algo;
import java.math.BigDecimal;
import java.math.MathContext;
public class MathAlgs
{
	public static final BigDecimal PI = new BigDecimal("3.1415926535897932384629433832795");
	public static final BigDecimal PI_BY_180 = PI.divide(BigDecimal.valueOf(180), MathContext.DECIMAL128);
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
	 * Solves a triangle.
	 * @param shape Angle and sides of the triangle.
	 */
	/*public static final void solvet(BigDecimal[] shape)
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
				if(a>=0)
				{
					b=a*sin(B)/sin(A);
					c=a*sin(C)/sin(A);
				}
				else if(b>=0)
				{
					a=b*sin(A)/sin(B);
					c=b*sin(C)/sin(B);
				}
				else
				{
					b=c*sin(B)/sin(C);
					a=c*sin(A)/sin(C);
				}
			}
		}
		else if(au==2)
		{
			if(su<=1)
			{
				if(A>=0)
				{
					if(a>=0)
					{
						if(b>=0)
						{
							B=asin(b*sin(A)/a);
							C=3.1415926535897932-A-B;
							c=a*sin(C)/sin(A);
						}
						else
						{
							C=asin(c*sin(A)/a);
							B=3.1415926535897932-A-C;
							b=a*sin(B)/sin(A);
						}
					}
					else
					{
						a=sqrt(b*b+c*c-2*b*c*cos(A));
						B=asin(b*sin(A)/a);
						C=asin(c*sin(A)/a);
					}
				}
				else if(B>=0)
				{
					if(b>=0)
					{
						if(a>=0)
						{
							A=asin(a*sin(B)/b);
							C=3.1415926535897932-A-B;
							c=a*sin(C)/sin(A);
						}
						else
						{
							C=asin(c*sin(B)/b);
							A=3.1415926535897932-B-C;
							a=b*sin(A)/sin(B);
						}
					}
					else
					{
						b=sqrt(a*a+c*c-2*a*c*cos(B));
						A=asin(a*sin(B)/b);
						C=asin(c*sin(B)/b);
					}
				}
				else
				{
					if(c>=0)
					{
						if(a>=0)
						{
							A=asin(a*sin(B)/b);
							B=3.1415926535897932-A-C;
							b=a*sin(B)/sin(A);
						}
						else
						{
							B=asin(b*sin(C)/c);
							A=3.1415926535897932-B-C;
							a=b*sin(A)/sin(B);
						}
					}
					else
					{
						c=sqrt(a*a+b*b-2*a*b*cos(C));
						A=asin(a*sin(C)/c);
						B=asin(b*sin(C)/c);
					}
				}
			}
		}
		else if(su==0)
		{
			A=acos((b*b+c*c-a*a)/(2*b*c));
			B=acos((a*a+c*c-b*b)/(2*a*c));
			C=acos((a*a+b*b-c*c)/(2*a*b));
		}
		A*=180/3.1415926535897932;
		B*=180/3.1415926535897932;
		C*=180/3.1415926535897932;
		shape[0] = A;
		shape[1] = B;
		shape[2] = C;
		shape[3] = a;
		shape[4] = b;
		shape[5] = c;
	}*/
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
			backSub(mat); 
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
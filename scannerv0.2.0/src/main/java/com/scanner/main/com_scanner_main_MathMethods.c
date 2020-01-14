#include<math.h>
#include<stdbool.h>
#include<stdlib.h>
#include"com_scanner_main_MathMethods.h"
#define min(x,y)((x)<(y)?(x):(y))
#define max(x,y)((x)>(y)?(x):(y))
#define invPLog(d)((d)*exp(d))
#define plogInvDriv(d)(((d)+1)*exp(d))
#define inssort(x,y){unsigned int ind=0;for(unsigned int i=0;i+1<y;i++){ind=i;while(x[ind+1]<x[ind]){x[ind+1]+=x[ind];x[ind]=x[ind-1]-x[ind];x[ind+1]-=x[ind];if(ind!=0){ind--;}}}}
JNIEXPORT jdouble JNICALL Java_com_scanner_main_MathMethods_plog(JNIEnv*env,jclass cls,jdouble d)
{
	double e=log(d+1);
	double f=invPLog(e);
	while(round(f*1000000000000)!=0)
	{
		e-=f/plogInvDriv(e);
		f=invPLog(e)-d;
	}
	return e;
}
JNIEXPORT jdouble JNICALL Java_com_scanner_main_MathMethods_fact(JNIEnv*env,jclass cls,jdouble d)
{
	return tgamma(d+1);
}
JNIEXPORT jdoubleArray JNICALL Java_com_scanner_main_MathMethods_fndpoi(JNIEnv*env,jclass cls,jdouble a,jdouble b,jdouble c,jdouble d,jdouble e,jdouble f)
{
	jdouble dt=(b*c-a*d);
	jdouble ans[2]={b*c-d*e/dt,c*e-a*f/dt};
	jdoubleArray da=(*env)->NewDoubleArray(env,2);
	(*env)->SetDoubleArrayRegion(env,da,0,2,ans);
	return da;
}
JNIEXPORT jdouble JNICALL Java_com_scanner_main_MathMethods_median(JNIEnv*env,jclass cls,jdoubleArray vs)
{
	jboolean cp=true;
	jdouble*dvs=(*env)->GetDoubleArrayElements(env,vs,&cp);
	unsigned int sz=(*env)->GetArrayLength(env,vs);
	double*vals=(double*)malloc(sizeof(double)*sz);
	for(unsigned int i=0;i<sz;vals[i]=dvs[i],i++);
	(*env)->ReleaseDoubleArrayElements(env,vs,dvs,2);
	printf("before insertion sort\n");
	fflush(stdout);
	inssort(vals,sz)
	printf("after insertion sort\n");
	fflush(stdout);
	jdouble ans=vals[sz>>1];
	if((sz&1)==0&&sz!=0)
	{
		ans=(vals[sz-1>>1]+vals[sz>>1])/2;
		if(vals[0]>0)
		{
			double mindiff=HUGE_VAL,maxdiff=HUGE_VAL*-1;
			double minratio=HUGE_VAL,maxratio=0;
			for(unsigned int i=0;i+1<sz;i++)
			{
				mindiff=min(mindiff,vals[i+1]-vals[i]);
				maxdiff=max(maxdiff,vals[i+1]-vals[i]);
				minratio=min(minratio,vals[i+1]/vals[i]);
				maxratio=max(maxratio,vals[i+1]/vals[i]);
				//printf("%f %f %f %f\n",maxdiff,mindiff,maxratio,minratio);
				fflush(stdout);
			}
			if(maxratio-minratio<maxdiff-mindiff&&minratio>0)
			{
				ans=sqrt(vals[sz-1>>1]*vals[sz>>1]);
			}
		}
	}
	return ans;
}
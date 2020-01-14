#include<algorithm>
#include<cstdlib>
#include<ctime>
#include<iostream>
#include<fstream>
#include<map>
#include<sstream>
#include<string>
#include<vector>
#include<jni.h>
#include"com_scanner_main_OtherMethods.h"
#define apnd push_back
#define nperm std::next_permutation
typedef std::vector<std::string> slist;
std::string replace(std::string str,char t,char r)
{
	for(unsigned int i=0;i<str.length();i++)
	{
		if(str[i]==t)
		{
			str[i]=r;
		}
	}
	return str;
}
time_t tm=time(nullptr);
std::string tms=replace(std::string(ctime(&tm)),':','_');
std::string path=std::string("data_logs/Session_")+(tms=tms.substr(0,tms.length()-1))+std::string(".log");
std::ofstream fout=std::ofstream(path);
std::ifstream rf;
std::vector<char>*roledat;
unsigned int sri=0;
char buf[8192];
unsigned int bufsz=0;
std::ifstream*moneyin;
std::ofstream*moneyout;
std::map<jlong,jlong>*player_net_worths;
JNIEXPORT void JNICALL Java_com_scanner_main_OtherMethods_init(JNIEnv*env,jclass cls)
{
	rf=std::ifstream("roledat.dat");
	roledat=new std::vector<char>();
	int b=rf.get();
	while(b!=-1)
	{
		roledat->apnd((char)b);
	}
	rf.close();
	player_net_worths=new std::map<jlong,jlong>();
	moneyin=new std::ifstream("player_grind_stats/money.scanbot");
	char cnum[8];
	moneyin->get(cnum,4);
	unsigned int ppl=cnum[0]<<24|cnum[1]<<16|cnum[2]<<8|cnum[3];
	jlong usern=0,userm=0;
	for(unsigned int i=0;i<ppl;i++)
	{
		moneyin->get(cnum,8);
		usern=(jlong)cnum[0]<<56|(jlong)cnum[1]<<48|(jlong)cnum[2]<<40|(jlong)cnum[3]<<32|cnum[4]<<24|cnum[5]<<16|cnum[6]<<8|cnum[7];
		moneyin->get(cnum,8);
		userm=(jlong)cnum[0]<<56|(jlong)cnum[1]<<48|(jlong)cnum[2]<<40|(jlong)cnum[3]<<32|cnum[4]<<24|cnum[5]<<16|cnum[6]<<8|cnum[7];
		(*player_net_worths)[usern]=userm;
	}
}
JNIEXPORT jbyteArray JNICALL Java_com_scanner_main_OtherMethods_permute(JNIEnv*env,jclass cls,jbyteArray ba)
{
	jboolean cp=false;
	jbyte*bt=env->GetByteArrayElements(ba,&cp);
	std::string str=std::string(reinterpret_cast<char*>(bt),env->GetArrayLength(ba));
	std::string tmp=str;
	env->ReleaseByteArrayElements(ba,bt,0);
	slist*perms=new slist();
	perms->apnd(str);
	nperm(tmp.begin(),tmp.end());
	while(tmp!=str)
	{
		perms->apnd(tmp);
		nperm(tmp.begin(),tmp.end());
	}
	std::sort(perms->begin(),perms->end());
	std::string*ps=new std::string((*perms)[0]);
	for(unsigned int i=1;i<perms->size();*ps+="\n"+(*perms)[i],i++);
	jbyteArray r=env->NewByteArray(ps->size());
	env->SetByteArrayRegion(r,0,ps->size(),reinterpret_cast<const jbyte*>(ps->c_str()));
	delete ps;
	delete perms;
	return r;
}
void flushbuf()
{
	fout.write(buf,bufsz);
	bufsz=0;
}
void save_role_data()
{
	std::ofstream ofs=std::ofstream("roledat.dat");
	ofs.write(&(*roledat)[0],roledat->size());
	ofs.close();
}
JNIEXPORT void JNICALL Java_com_scanner_main_OtherMethods_flushbuf(JNIEnv*env,jclass cls)
{
	flushbuf();
}
JNIEXPORT void JNICALL Java_com_scanner_main_OtherMethods_logdat(JNIEnv*env,jclass cls,jint b)
{
	if(bufsz==8192)
	{
		flushbuf();
	}
	buf[bufsz++]=(char)b;
}
JNIEXPORT void JNICALL Java_com_scanner_main_OtherMethods_setAutoRole(JNIEnv*env,jclass cls,jlong r)
{
	(*roledat)[0]=(char)(r>>56);
	(*roledat)[1]=(char)(r>>48);
	(*roledat)[2]=(char)(r>>40);
	(*roledat)[3]=(char)(r>>32);
	(*roledat)[4]=(char)(r>>24);
	(*roledat)[5]=(char)(r>>16);
	(*roledat)[6]=(char)(r>>8);
	(*roledat)[7]=(char)r;
	save_role_data();
}
JNIEXPORT jlong JNICALL Java_com_scanner_main_OtherMethods_getAutoRole(JNIEnv*env,jclass cls)
{
	return(signed long long int)(*roledat)[0]<<56|(signed long long int)(*roledat)[1]<<48|(signed long long int)(*roledat)[2]<<40|(signed long long int)(*roledat)[3]<<32|(*roledat)[4]<<24|(*roledat)[5]<<16|(*roledat)[6]<<8|(*roledat)[7];
}
JNIEXPORT jlong JNICALL Java_com_scanner_main_OtherMethods_nextSelfRole(JNIEnv*env,jclass cls)
{
	signed long long int r=(signed long long int)(*roledat)[sri<<3]<<56|(signed long long int)(*roledat)[sri<<3+1]<<48|(signed long long int)(*roledat)[sri<<3+2]<<40|(signed long long int)(*roledat)[sri<<3+3]<<32|(*roledat)[sri<<3+4]<<24|(*roledat)[sri<<3+5]<<16|(*roledat)[sri<<3+6]<<8|(*roledat)[sri<<3+7];
	sri++;
	if(sri+1<<3==roledat->size())
	{
		sri=0;
		return-1;
	}
	else
	{
		return r;
	}
}
JNIEXPORT void JNICALL Java_com_scanner_main_OtherMethods_changeSelfRole(JNIEnv*env,jclass cls,jlong r)
{
	std::vector<signed long long int>rs=std::vector<signed long long int>();
	unsigned int ind=99999999;
	for(unsigned int i=0;i<roledat->size();i+=8)
	{
		rs.apnd((signed long long int)(*roledat)[i<<3]<<56|(signed long long int)(*roledat)[i<<3+1]<<48|(signed long long int)(*roledat)[i<<3+2]<<40|(signed long long int)(*roledat)[i<<3+3]<<32|(*roledat)[i<<3+4]<<24|(*roledat)[i<<3+5]<<16|(*roledat)[i<<3+6]<<8|(*roledat)[i<<3+7]);
	}
	for(unsigned int i=0;i<rs.size();i++)
	{
		if(rs[i]==r)
		{
			ind=i;
			i=rs.size();
		}
	}
	if(ind=99999999)
	{
		std::vector<char>rba=std::vector<char>();
		for(unsigned int i=0;i<8;i++)
		{
			rba.apnd((char)(r>>(7-i<<3)));
		}
		roledat->insert(roledat->end(),rba.begin(),rba.end());
	}
	else
	{
		roledat->erase(roledat->begin()+ind,roledat->begin()+ind+8);
	}
	save_role_data();
}
JNIEXPORT jboolean JNICALL Java_com_scanner_main_OtherMethods_testSelfRole(JNIEnv*env,jclass cls,jlong r)
{
	std::vector<signed long long int>rs=std::vector<signed long long int>();
	jboolean found=JNI_FALSE;
	for(unsigned int i=0;i<roledat->size();i+=8)
	{
		rs.apnd((signed long long int)(*roledat)[i<<3]<<56|(signed long long int)(*roledat)[i<<3+1]<<48|(signed long long int)(*roledat)[i<<3+2]<<40|(signed long long int)(*roledat)[i<<3+3]<<32|(*roledat)[i<<3+4]<<24|(*roledat)[i<<3+5]<<16|(*roledat)[i<<3+6]<<8|(*roledat)[i<<3+7]);
	}
	for(unsigned int i=0;i<rs.size();i++)
	{
		if(rs[i]==r)
		{
			found=JNI_TRUE;
			i=rs.size();
		}
	}
	save_role_data();
	return found;
}
JNIEXPORT jlong JNICALL Java_com_scanner_main_OtherMethods_work(JNIEnv*env,jclass cls,jlong author)
{
	return 0;
}
JNIEXPORT jlong JNICALL Java_com_scanner_main_OtherMethods_crime(JNIEnv*env,jclass cls,jlong author)
{
	return 0;
}
JNIEXPORT jlong JNICALL Java_com_scanner_main_OtherMethods_fight(JNIEnv*env,jclass cls,jlong author)
{
	return 0;
}
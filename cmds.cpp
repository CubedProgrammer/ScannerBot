#include<nlohmann/json.hpp>
#include"algo/str.hpp"
#include"cmds.hpp"

using std::size_t;
using std::stod;
using std::string;
using namespace dpp;
using nlohmann::json;

extern guildmap allguilds;

string Prefixcmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		allguilds[gid]["pref"] = *args;
		return"New prefix has been set.";
	}
	else
		return(string)allguilds[gid]["pref"];
}

string Productcmd::operator()(const message& og, const string* args, size_t size)const
{
	double product = 1;
	for(size_t i=0;i<size;i++)
		product *= tonum(args[i]);
	return tostr(product);
}

string Sumcmd::operator()(const message& og, const string* args, size_t size)const
{
	double sum = 0;
	for(size_t i=0;i<size;i++)
		sum += tonum(args[i]);
	return tostr(sum);
}

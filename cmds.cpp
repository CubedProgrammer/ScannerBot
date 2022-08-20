#include<cmath>
#include<stdexcept>
#include<nlohmann/json.hpp>
#include"algo/discord.hpp"
#include"algo/str.hpp"
#include"cmds.hpp"

using std::size_t;
using std::stod;
using std::string;
using std::string_literals::operator""s;
using nlohmann::json;
using namespace dpp;

extern guildmap allguilds;

string Modulocmd::operator()(const message& og, const string* args, size_t size)const
{
	double q = 0;
	string resstr = "0";
	try
	{
		if(size == 2)
		{
			q = tonum(args[1]);
			if(q)
			{
				q = std::fmod(tonum(args[0]), q);
				resstr = tostr(q);
			}
			else
				resstr = "Trying to divide by zero are we?";
		}
		else
			resstr = "You need two numbers, the dividend and the divisor.";
	}
	catch(std::invalid_argument&e)
	{
		resstr = e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = e.what() + " out of range."s;
	}
	return resstr;
}

string Quotientcmd::operator()(const message& og, const string* args, size_t size)const
{
	double q = 0;
	string resstr = "0";
	try
	{
		if(size == 2)
		{
			q = tonum(args[1]);
			if(q)
			{
				q = tonum(args[0]) / q;
				resstr = tostr(q);
			}
			else
				resstr = "Trying to divide by zero are we?";
		}
		else
			resstr = "You need two numbers, the dividend and the divisor.";
	}
	catch(std::invalid_argument&e)
	{
		resstr = e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = e.what() + " out of range."s;
	}
	return resstr;
}

string Prefixcmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			allguilds[gid]["pref"] = *args;
			return"New prefix has been set.";
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return(string)allguilds[gid]["pref"];
}

string Productcmd::operator()(const message& og, const string* args, size_t size)const
{
	double product = 1;
	string resstr = "0";
	try
	{
		for(size_t i=0;i<size;i++)
			product *= tonum(args[i]);
		resstr = tostr(product);
	}
	catch(std::invalid_argument&e)
	{
		resstr = e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = e.what() + " out of range."s;
	}
	return resstr;
}

string Sumcmd::operator()(const message& og, const string* args, size_t size)const
{
	double sum = 0;
	string resstr = "0";
	try
	{
		for(size_t i=0;i<size;i++)
			sum += tonum(args[i]);
		resstr = tostr(sum);
	}
	catch(std::invalid_argument&e)
	{
		resstr = e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = e.what() + " out of range."s;
	}
	return resstr;
}

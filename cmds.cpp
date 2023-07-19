#include<cmath>
#include<numeric>
#include<sstream>
#include<stdexcept>
#include<nlohmann/json.hpp>
#include"algo/discord.hpp"
#include"algo/math.hpp"
#include"algo/str.hpp"
#include"cmds.hpp"

using std::gcd;
using std::pair;
using std::size_t;
using std::stod;
using std::string;
using std::string_literals::operator""s;
using std::vector;
using nlohmann::json;
using namespace dpp;

extern guildmap allguilds;

string HMeancmd::operator()(const message& og, const string* args, size_t size)const
{
    double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
            num += 1.0 / tonum(args[i]);
		num = size / num;
		resstr = tostr(num);
    }
	catch(std::invalid_argument&e)
	{
		resstr = tostr(num) + e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = tostr(num) + e.what() + " out of range."s;
	}
	return resstr;
}

string GMeancmd::operator()(const message& og, const string* args, size_t size)const
{
    double num = 1;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
            num *= tonum(args[i]);
		num = pow(num, 1.0 / size);
		resstr = tostr(num);
    }
	catch(std::invalid_argument&e)
	{
		resstr = tostr(num) + e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = tostr(num) + e.what() + " out of range."s;
	}
	return resstr;
}

string AMeancmd::operator()(const message& og, const string* args, size_t size)const
{
    double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
            num += tonum(args[i]);
		num /= size;
		resstr = tostr(num);
    }
	catch(std::invalid_argument&e)
	{
		resstr = tostr(num) + e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = tostr(num) + e.what() + " out of range."s;
	}
	return resstr;
}

string Baseconvcmd::operator()(const message& og, const string* args, size_t size)const
{
    if(size < 3)
        return "Three arguments are required. Number, base the number is in, base the number is to be displayed in.";
    else
    {
        int from = toint(args[1]), to = toint(args[2]);
        if(from >= 2 && from <= 36 && to >= 2 && to <= 36)
        {
            long num = std::stol(args[0], nullptr, from);
            return numstr(num, to);
        }
        else
            return "Base must be in the interval [2,36].";
    }
}

string Factorcmd::operator()(const message& og, const string* args, size_t size)const
{
    long num = 0;
    string resstr;
	std::ostringstream oss;
	vector<pair<long, int>>factors;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = toint(args[i]);
			factors = pfactor(num);
			for(const auto&[p, e]: factors)
			{
				oss << p;
				if(e > 1)
					oss << '^' << e;
				oss << ' ';
			}
			oss << '\n';
        }
		resstr = oss.str();
    }
	catch(std::invalid_argument&e)
	{
		resstr = oss.str() + e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = oss.str() + e.what() + " out of range."s;
	}
	return resstr;
}

string Primecmd::operator()(const message& og, const string* args, size_t size)const
{
    long num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = toint(args[i]);
			if(i)
				resstr += ", ";
			resstr += checkprime(num) ? "true" : "false";
        }
    }
	catch(std::invalid_argument&e)
	{
		resstr += e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr += e.what() + " out of range."s;
	}
	return resstr;
}

string Gcdcmd::operator()(const message& og, const string* args, size_t size)const
{
    long num = 0, next = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            next = toint(args[i]);
            num = gcd(num, next);
        }
        resstr = tostr(num);
    }
	catch(std::invalid_argument&e)
	{
		resstr = tostr(num) + e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = tostr(num) + e.what() + " out of range."s;
	}
	return resstr;
}

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
			if(args[0].size() <= 3)
			{
				allguilds[gid]["pref"] = *args;
				return"New prefix has been set.";
			}
			else
				return"Prefix must be no more than three characters.";
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
		resstr = tostr(product) + e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = tostr(product) + e.what() + " out of range."s;
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
		resstr = tostr(sum) + e.what() + " invalid argument"s;
	}
	catch(std::out_of_range&e)
	{
		resstr = tostr(sum) + e.what() + " out of range."s;
	}
	return resstr;
}

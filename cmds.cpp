#include<cmath>
#include<numeric>
#include<regex>
#include<sstream>
#include<stdexcept>
#include<thread>
#include<nlohmann/json.hpp>
#include"algo/discord.hpp"
#include"algo/math.hpp"
#include"algo/str.hpp"
#include"cmds.hpp"

using std::cos;
using std::find;
using std::gcd;
using std::pair;
using std::sin;
using std::size_t;
using std::stod;
using std::string;
using std::string_literals::operator""s;
using std::tan;
using std::vector;
using nlohmann::json;
using namespace dpp;

extern guildmap allguilds;
extern gdatamap gdata;
std::chrono::time_point<std::chrono::system_clock>lastfetch;

string Muterolecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& mrole = allguilds[gid]["muterole"];
			json numid;
			std::optional<role>roleid = findrole(*og.owner, gid, args[0]);
			if(!roleid)
			{
				return args[0] + " could not be found,\n";
			}
			else
			{
				numid = (std::uint64_t)roleid->id;
				if(numid == mrole)
					mrole = nullptr;
				else
					mrole = numid;
				return"Successfully updated muterole.";
			}
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return tostr(allguilds[gid]["muterole"]);
}

string Bancmd::operator()(const message& og, const string* args, size_t size)const
{
    unsigned failed = 0;
	long unsigned luid;
	snowflake id;
	// () >
	if(hasperm(*og.owner, og.member, permissions::p_ban_members))
    {
		string retstr;
        for(size_t i = 0; i < size; ++i)
        {
			auto& arg = args[i];
			if(arg.size() > 3 && arg[1] == '@')
			{
				luid = std::stoul(arg.substr(2, arg.size() - 3));
				id = luid;
				if(member_cmpr(*og.owner, og.member, og.owner->guild_get_member_sync(og.guild_id, id)) > 0)
					og.owner->guild_ban_add(og.guild_id, id);
				else
					retstr += "You have insufficient rank to ban " + arg + ".\n";
			}
			else
				++failed;
        }
		retstr += failed ? tostr(failed) + " failed, you must ping the users." : "Successfully banned them.";
		return retstr;
    }
	else
		return"You do not have permission to use this command.";
}

string Kickcmd::operator()(const message& og, const string* args, size_t size)const
{
    unsigned failed = 0;
	long unsigned luid;
	snowflake id;
	if(hasperm(*og.owner, og.member, permissions::p_kick_members))
    {
		string retstr;
        for(size_t i = 0; i < size; ++i)
        {
			auto& arg = args[i];
			if(arg.size() > 3 && arg[1] == '@')
			{
				luid = std::stoul(arg.substr(2, arg.size() - 3));
				id = luid;
				if(member_cmpr(*og.owner, og.member, og.owner->guild_get_member_sync(og.guild_id, id)) > 0)
					og.owner->guild_member_kick(og.guild_id, id);
				else
					retstr += "You have insufficient rank to kick " + arg + ".\n";
			}
			else
				++failed;
        }
		retstr += failed ? tostr(failed) + " failed, you must ping the users." : "Successfully kicked them.";
		return retstr;
    }
	else
		return"You do not have permission to use this command.";
}

string DLOptionscmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(hasperm(*og.owner, og.member, permissions::p_manage_guild))
	{
		auto& options = allguilds[gid];
		std::ostringstream oss;
		oss << options;
		string cont = oss.str();
		oss.str("");
		oss << std::hex << (std::uint64_t)gid << ".json";
		string fname = oss.str();
		std::cout << 1;
		message m(og.channel_id, "Here are the options you have set for your server.");
		std::cout << 2;
		m.add_file(fname, cont);
		std::cout << 3;
		og.owner->message_create(m);
		std::cout << 4;
		return"";
	}
	else
		return"You do not have permission to use this command.";
}

string Infocmd::operator()(const message& og, const string* args, size_t size)const
{
	std::ostringstream oss;
	cluster &bot = *og.owner;
	snowflake gid = og.guild_id, id;
	long unsigned luid;
	string idstr;
	guild_member mem;
	role r;
	channel ch;
	if(size > 0)
	{
		role_map rmap = bot.roles_get_sync(gid);
		std::regex digitonly("[^0-9]");
		for(size_t i = 0; i < size; ++i)
		{
			if(args[i].size() > 4)
			{
				idstr = regex_replace(args[i], digitonly, "");
				luid = std::stoul(idstr);
				id = luid;
				switch(args[i][1])
				{
					case '#':
						ch = bot.channel_get_sync(id);
						oss << "ID: " << luid << '\n';
						oss << "Member Count: " << ch.get_members().size() << '\n';
						oss << "Rate Limit in Seconds: " << ch.rate_limit_per_user << '\n';
						break;
						break;
					default:
						if(args[i][2] == '&')
						{
							r = rmap.at(id);
							oss << "ID: " << luid << '\n';
							oss << "Colour: 0x" << std::hex << r.colour << '\n';
							oss << "Permissions: 0x" << (std::uint64_t)r.permissions << std::dec << '\n';
							oss << "Position: " << (int)r.position << '\n';
						}
						else
						{
							mem = bot.guild_get_member_sync(gid, id);
							oss << "ID: " << luid << '\n';
							oss << "Joined at: " << mem.joined_at << '\n';
							oss << "Number of roles: " << mem.roles.size() << '\n';
						}
				}
			}
		}
	}
	else
	{
		using namespace std::chrono;
		using namespace std::literals::chrono_literals;
		auto currtm = system_clock::now();
		auto elapsed = currtm - lastfetch;
		if(elapsed > 6s)
		{
			fetch_guilds(bot);
			lastfetch = currtm;
			std::this_thread::sleep_for(997ms);
		}
		auto& currguild = gdata[gid];
		oss << "Name: " << currguild.name << '\n';
		oss << currguild.description << '\n';
		oss << "Roles: " << currguild.roles.size() << '\n';
		oss << "Channels: " << currguild.channels.size() << '\n';
		oss << "Threads: " << currguild.threads.size() << '\n';
		oss << "Emoji: " << currguild.emojis.size() << '\n';
		oss << "Owner ID: " << (std::uint64_t)currguild.owner_id << '\n';
		oss << "Member Count: " << currguild.member_count << '\n';
		oss << "ID: " << currguild.id << '\n';
		oss << "Creation Time: " << currguild.get_creation_time() << '\n';
		oss << "Rules Channel: " << (std::uint64_t)currguild.rules_channel_id << '\n';
		oss << "System Channel: " << (std::uint64_t)currguild.system_channel_id << '\n';
		oss << "Icon URL: " << currguild.get_icon_url() << '\n';
		oss << "Splash URL: " << currguild.get_splash_url() << '\n';
	}
	return oss.str();
}

string Selfrolecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		auto& sroles = allguilds[gid]["selfroles"];
		json numid;
		std::optional<role>roleid;
		string retstr;
		for(size_t i = 0; i < size; ++i)
		{
			roleid = findrole(*og.owner, gid, args[i]);
			if(!roleid)
			{
				retstr += args[i] + " could not be found,\n";
				continue;
			}
			numid = (std::uint64_t)roleid->id;
			auto& memrole = og.member.roles;
			auto it = find(sroles.begin(), sroles.end(), numid);
			if(it == sroles.end())
			{
				retstr += args[i] + " could not be found,\n";
				continue;
			}
			else if(find(memrole.begin(), memrole.end(), roleid->id) == memrole.end())
				og.owner->guild_member_add_role(og.guild_id, og.author.id, roleid->id);
			else
				og.owner->guild_member_remove_role(og.guild_id, og.author.id, roleid->id);
		}
		return retstr.size() ? retstr : "Successfully updated your roles.";
	}
	else
		return"Name a role to give yourself.";
}

string Toggleselfrolecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& sroles = allguilds[gid]["selfroles"];
			json numid;
			std::optional<role>roleid;
			string retstr;
			for(size_t i = 0; i < size; ++i)
			{
				roleid = findrole(*og.owner, gid, args[i]);
				if(!roleid)
				{
					retstr += args[i] + " could not be found,\n";
					continue;
				}
				numid = (std::uint64_t)roleid->id;
				auto it = find(sroles.begin(), sroles.end(), numid);
				if(it == sroles.end())
					sroles.push_back(numid);
				else
					sroles.erase(it);
			}
			return retstr.size() ? retstr : "Successfully toggled selfroles.";
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return tostr(allguilds[gid]["selfroles"]);
}

string Autorolecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& aroles = allguilds[gid]["autoroles"];
			json numid;
			std::optional<role>roleid;
			string retstr;
			for(size_t i = 0; i < size; ++i)
			{
				roleid = findrole(*og.owner, gid, args[i]);
				if(!roleid)
				{
					retstr += args[i] + " could not be found,\n";
					continue;
				}
				numid = (std::uint64_t)roleid->id;
				auto it = find(aroles.begin(), aroles.end(), numid);
				if(it == aroles.end())
					aroles.push_back(numid);
				else
					aroles.erase(it);
			}
			return retstr.size() ? retstr : "Successfully toggled autoroles.";
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return tostr(allguilds[gid]["autoroles"]);
}

string Atan2cmd::operator()(const message& og, const string* args, size_t size)const
{
    if(size < 2)
        return "Two arguments are required, y and x.";
    else
    {
        double x = tonum(args[1]), y = tonum(args[0]);
		return tostr(std::atan2(y, x));
    }
}

string Atancmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(std::atan(num));
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

string Acoscmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(std::acos(num));
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

string Asincmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(std::asin(num));
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

string Csccmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(1.0 / sin(num));
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

string Seccmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(1.0 / cos(num));
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

string Cotcmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(1.0 / tan(num));
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

string Tancmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(tan(num));
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

string Coscmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(cos(num));
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

string Sincmd::operator()(const message& og, const string* args, size_t size)const
{
	double num = 0;
    string resstr;
    try
    {
        for(size_t i = 0; i < size; ++i)
        {
            num = tonum(args[i]);
			if(i)
				resstr += ' ';
			resstr += tostr(sin(num));
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

string Logcmd::operator()(const message& og, const string* args, size_t size)const
{
    if(size == 0)
        return"Provide the base and then the power.";
    else
    {
        if(size == 1)
            return tostr(std::log(tonum(args[0])));
        else
        {
            double base = tonum(args[0]), p = tonum(args[1]);
            return tostr(std::log(p) / std::log(base));
        }
    }
}

string Powcmd::operator()(const message& og, const string* args, size_t size)const
{
    if(size == 0)
        return"Provide the base and then the exponent.";
    else
    {
        if(size == 1)
            return tostr(std::exp(tonum(args[0])));
        else
        {
            double base = tonum(args[0]), exp = tonum(args[1]);
            return tostr(std::pow(base, exp));
        }
    }
}

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

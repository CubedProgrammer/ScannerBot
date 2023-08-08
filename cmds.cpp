// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#include<cmath>
#include<map>
#include<numeric>
#include<regex>
#include<sstream>
#include<stdexcept>
#include<thread>
#include<nlohmann/json.hpp>
#include"algo/discord.hpp"
#include"algo/math.hpp"
#include"algo/str.hpp"
#include"algo/utils.hpp"
#include"cmds.hpp"

using std::cos;
using std::find;
using std::gcd;
using std::map;
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

string Mutecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& mrole = allguilds[gid]["muterole"];
			if(mrole.is_null())
				return"Mute role has not been set, use the muterole command to set a mute role.";
			else if(args[0].size() > 3 && args[0][1] == '@')
			{
				using namespace std::chrono;
				long unsigned uid = std::stoul(args[0].substr(2, args[0].size() - 3));
				int mtime = (int)allguilds[gid]["mutetime"];
				if(size > 1)
				{
					string s = args[1];
					int mult = 1;
					if(s.back() == 'H' || s.back() == 'h')
					{
						mult = 60;
						s.pop_back();
					}
					else if(s.back() == 'D' || s.back() == 'd')
					{
						mult = 1440;
						s.pop_back();
					}
					mtime = std::stoi(s) * mult;
				}
				cluster& bot = *og.owner;
				long unsigned rid = (long unsigned)mrole;
				bot.guild_member_add_role(gid, uid, rid);
				auto calllater = [&bot, gid, uid, rid]()
				{
					bot.guild_member_remove_role(gid, uid, rid);
				};
				setTimeout(calllater, minutes(mtime));
				return"Successfully muted user.";
			}
			else
				return"Mention the user you wish to mute.";
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return"Specify a member to mute.";
}

string Mutetimecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			string s = args[0];
			int mult = 1;
			if(s.back() == 'H' || s.back() == 'h')
			{
				mult = 60;
				s.pop_back();
			}
			else if(s.back() == 'D' || s.back() == 'd')
			{
				mult = 1440;
				s.pop_back();
			}
			allguilds[gid]["mutetime"] = std::stoi(s) * mult;
			return"Successfully set the new mute time.";
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return tostr(allguilds[gid]["mutetime"]) + " minutes";
}

string Mutablecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& m = allguilds[gid]["mutable"];
			json str;
			for(size_t i = 0; i < size; ++i)
			{
				str = args[i];
				auto it = find(m.begin(), m.end(), str);
				if(it == m.end())
					m.push_back(str);
				else
					m.erase(it);
			}
			return"Updated mute words.";
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return tostr(allguilds[gid]["mutable"]);
}

string Allrolecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		cluster& bot = *og.owner;
		std::optional<role>roleid;
		string retstr;
		vector<snowflake>rolelist;
		auto gmems = bot.guild_get_members_sync(gid, 999, 0);
		role_map roles = og.owner->roles_get_sync(og.guild_id);
		for(size_t i = 0; i < size; ++i)
		{
			roleid = findrole(roles, *og.owner, gid, args[i]);
			if(!roleid)
			{
				retstr += args[i] + " could not be found,\n";
				continue;
			}
			auto& r = *roleid;
			rolelist.push_back(r.id);
		}
		retstr += "Here is the list of members.\n";
		for(const auto& [id, m]: gmems)
		{
			if(has_all(m.roles.cbegin(), m.roles.cend(), rolelist.begin(), rolelist.end()))
				retstr += tostr((std::uint64_t)id) + ' ' + m.get_user()->username + '\n';
		}
		return retstr;
	}
	else
		return"Give a list of roles.";
}

string Anyrolecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		cluster& bot = *og.owner;
		std::optional<role>roleid;
		string retstr;
		vector<snowflake>rolelist;
		auto gmems = bot.guild_get_members_sync(gid, 999, 0);
		role_map roles = og.owner->roles_get_sync(og.guild_id);
		for(size_t i = 0; i < size; ++i)
		{
			roleid = findrole(roles, *og.owner, gid, args[i]);
			if(!roleid)
			{
				retstr += args[i] + " could not be found,\n";
				continue;
			}
			auto& r = *roleid;
			rolelist.push_back(r.id);
		}
		retstr += "Here is the list of members.\n";
		for(const auto& [id, m]: gmems)
		{
			if(has_any(m.roles.cbegin(), m.roles.cend(), rolelist.begin(), rolelist.end()))
				retstr += tostr((std::uint64_t)id) + ' ' + m.get_user()->username + '\n';
		}
		return retstr;
	}
	else
		return"Give a list of roles.";
}

string Purgecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_messages))
		{
			int cnt = std::stoi(args[0]);
			if(cnt > 100)
				return"No more than one hundred messages.";
			else if(cnt < 2)
				return"At least one message.";
			else
			{
				using namespace std::chrono;
				using namespace std::chrono_literals;
				cluster& bot = *og.owner;
				auto msgmap = bot.messages_get_sync(og.channel_id, 0, og.id, 0, cnt);
				vector<snowflake>recent, older;
				auto currtm = to_time(og.id);
				for(const auto&[id, msg]: msgmap)
				{
					auto tm = to_time(id);
					if(tm - currtm > 336h)
						older.push_back(id);
					else
						recent.push_back(id);
				}
				if(recent.size() > 1)
					bot.message_delete_bulk_sync(recent, og.channel_id);
				else if(recent.size() == 1)
					bot.message_delete_sync(recent[0], og.channel_id);
				for(auto id: older)
					bot.message_delete_sync(id, og.channel_id);
				return"Purged messages successfully.";
			}
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return"Specify the number of messages to purge.";
}

string Takerolecmd::operator()(const message& og, const string* args, size_t size)const
{
	if(size >= 2)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_roles))
		{
			if(args[0][1] == '@')
			{
				std::optional<role>oprole;
				std::uint64_t id = std::stoul(args[0].substr(2, args[0].size() - 3));
				string retstr;
				role_map roles = og.owner->roles_get_sync(og.guild_id);
				role memtop = highrole(roles, og.member);
    			for(size_t i = 1; i < size; ++i)
				{
					oprole = findrole(roles, *og.owner, og.guild_id, args[i]);
					if(oprole)
					{
						if(*oprole < memtop)
							og.owner->guild_member_remove_role(og.guild_id, id, oprole->id);
						else
							retstr += args[i] + " is too high for your rank.\n";
					}
					else
						retstr += args[i] + " could not be found.\n";
				}
				return retstr.size() ? retstr : "Succesfully updated member.";
			}
			else
				return"Mention the user you wish to take the role from.";
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return"Name a member and a role to take from that member.";
}

string Giverolecmd::operator()(const message& og, const string* args, size_t size)const
{
	if(size >= 2)
	{
		if(hasperm(*og.owner, og.member, permissions::p_manage_roles))
		{
			if(args[0][1] == '@')
			{
				std::optional<role>oprole;
				std::uint64_t id = std::stoul(args[0].substr(2, args[0].size() - 3));
				string retstr;
				role_map roles = og.owner->roles_get_sync(og.guild_id);
				role memtop = highrole(roles, og.member);
    			for(size_t i = 1; i < size; ++i)
				{
					oprole = findrole(roles, *og.owner, og.guild_id, args[i]);
					if(oprole)
					{
						if(*oprole < memtop)
							og.owner->guild_member_add_role(og.guild_id, id, oprole->id);
						else
							retstr += args[i] + " is too high for your rank.\n";
					}
					else
						retstr += args[i] + " could not be found.\n";
				}
				return retstr.size() ? retstr : "Succesfully updated member.";
			}
			else
				return"Mention the user you wish to give the role to.";
		}
		else
			return"You do not have permission to use this command.";
	}
	else
		return"Name a member and a role to give to that member.";
}

string Macrolscmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	auto& macros = allguilds[gid]["macros"];
	string retstr;
	for(auto it = macros.begin(); it != macros.end(); ++it)
	{
		string name = it.key(), expand = (string)it.value();
		retstr += name + ' ' + expand + '\n';
	}
	return retstr;
}

string Undefcmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	auto& macros = allguilds[gid]["macros"];
    for(size_t i = 0; i < size; ++i)
	{
		string name = args[i].substr(1);
		macros.erase(name);
	}
	return"Successfully removed macros.";
}

string Definecmd::operator()(const message& og, const string* args, size_t size)const
{
	snowflake gid = og.guild_id;
	auto& macros = allguilds[gid]["macros"];
	if(size >= 2)
	{
		macros[args[0]] = args[1];
		return"Defined new macro.";
	}
	else
		return"Give the macro a name and specify what it should expand to.";
}

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
    bool perm = true;
	long unsigned luid;
	snowflake id;
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
				{
					retstr += "You have insufficient rank to ban " + arg + ".\n";
					perm = false;
				}
			}
			else
				++failed;
        }
        if(perm)
        	retstr += failed ? tostr(failed) + " failed, you must ping the users." : "Successfully banned them.";
		return retstr;
    }
	else
		return"You do not have permission to use this command.";
}

string Kickcmd::operator()(const message& og, const string* args, size_t size)const
{
    unsigned failed = 0;
    bool perm = true;
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
				{
					retstr += "You have insufficient rank to kick " + arg + ".\n";
					perm = false;
				}
			}
			else
				++failed;
        }
        if(perm)
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
		if(elapsed > 24s)
		{
			fetch_guilds(bot);
			lastfetch = currtm;
			std::this_thread::sleep_for(997ms);
		}
		auto& currguild = gdata[gid];
		long rolecnt = currguild.roles.size();
		long channelcnt = currguild.channels.size();
		long threadcnt = currguild.threads.size();
		long emojicnt = currguild.emojis.size();
		long memcnt = bot.guild_get_members_sync(gid, 999, 0).size();
		oss << "Name: " << currguild.name << '\n';
		oss << currguild.description << '\n';
		oss << "Roles: " << rolecnt << '\n';
		oss << "Channels: " << channelcnt << '\n';
		oss << "Threads: " << threadcnt << '\n';
		oss << "Emoji: " << emojicnt << '\n';
		oss << "Owner ID: " << (std::uint64_t)currguild.owner_id << '\n';
		oss << "Member Count: " << memcnt << '\n';
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
		role_map roles = og.owner->roles_get_sync(og.guild_id);
		for(size_t i = 0; i < size; ++i)
		{
			roleid = findrole(roles, *og.owner, gid, args[i]);
			if(!roleid)
			{
				retstr += args[i] + " could not be found.\n";
				continue;
			}
			numid = (std::uint64_t)roleid->id;
			auto& memrole = og.member.roles;
			auto it = find(sroles.begin(), sroles.end(), numid);
			if(it == sroles.end())
			{
				retstr += args[i] + " is not a selfrole.\n";
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
			role_map roles = og.owner->roles_get_sync(og.guild_id);
			for(size_t i = 0; i < size; ++i)
			{
				roleid = findrole(roles, *og.owner, gid, args[i]);
				if(!roleid)
				{
					retstr += args[i] + " could not be found.\n";
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
			role_map roles = og.owner->roles_get_sync(og.guild_id);
			for(size_t i = 0; i < size; ++i)
			{
				roleid = findrole(roles, *og.owner, gid, args[i]);
				if(!roleid)
				{
					retstr += args[i] + " could not be found.\n";
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

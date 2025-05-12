// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#include<cmath>
#include <dpp/channel.h>
#include <dpp/guild.h>
#include <dpp/restresults.h>
#include <dpp/role.h>
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
task<string> Findusercmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake uid;
	cluster& bot = *og.owner;
	std::ostringstream oss;
	for(unsigned long i=0;i<size;i++)
	{
		uid = snowflake(std::stol(args[i]));
		confirmation_callback_t ret = co_await bot.co_user_get_cached(uid);
		oss << get<user_identified>(ret.value) << '\n';
	}
	co_return oss.str();
}
task<string> RecallMessagecmd::operator()(const message& og, const string* args, size_t size)
{
	auto &saved = allguilds[og.guild_id]["saved_message_ids"];
	std::ostringstream oss;
	if(size == 0)
	{
		for(auto&[name, msg]:saved.items())
			oss << name << ": " << msg << '\n';
	}
	else
	{
		auto it = saved.find(args[0]);
		if(it == saved.end())
			oss << "Could not find that message.";
		else
			oss << *it;
	}
	co_return oss.str();
}
task<string> SaveMessagecmd::operator()(const message& og, const string* args, size_t size)
{
	string ret("You do not have permission to use this command.");
	if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
	{
		if(size < 1)
			ret = "Grab the message link.";
		else
		{
			auto &saved = allguilds[og.guild_id]["saved_message_ids"];
			string link = args[0];
			string name = size < 2 ? "message " + numstr(saved.size() + 1, 10) : args[1];
			auto slash = link.rfind('/');
			if(slash == string::npos)
				ret = "Invalid message link.";
			else
			{
				link[slash] = ' ';
				slash = link.rfind('/');
				if(slash == string::npos)
					ret = "Invalid message link.";
				else
				{
					std::istringstream iss(link.substr(slash + 1));
					std::uint64_t chid, mid;
					iss >> chid >> mid;
					saved[name] = json::array();
					saved[name].push_back(chid);
					saved[name].push_back(mid);
					ret = "Successfully recorded message.";
				}
			}
		}
	}
	co_return ret;
}
task<string> Randcmd::operator()(const message& og, const string* args, size_t size)
{
	string ret;
	std::uint64_t num, cnt = 1;
	std::uint64_t upper, lower = 0;
	switch(size)
	{
		default:
			cnt = toint(args[2]);
		case 2:
			lower = toint(args[0]);
			++args;
		case 1:
			upper = toint(args[0]);
			for(std::uint64_t i=0;i<cnt;i++)
			{
				num = dice() % (upper - lower) + lower;
				ret += tostr(num) + '\n';
			}
			break;
		case 0:
			num = dice() & 0x1fffffffffffff;
			ret = tostr((double)num / 0x20000000000000);
			break;
	}
	co_return ret;
}
task<string> Epochcmd::operator()(const message& og, const string* args, size_t size)
{
	using namespace std::chrono;
	auto curr = high_resolution_clock::now().time_since_epoch();
	long currns = duration_cast<nanoseconds>(curr).count();
	long denom = 1000000000;
	if(size > 0)
	{
		switch(args[0][0])
		{
			case'm':
				denom = 1000000;
				break;
			case'u':
				denom = 1000;
				break;
			default:
				denom = 1;
				break;
		}
	}
	co_return tostr(currns / denom);
}
task<string> Mutecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& mrole = allguilds[gid]["muterole"];
			if(mrole.is_null())
				co_return"Mute role has not been set, use the muterole command to set a mute role.";
			else if(args[0].size() > 3 && args[0][1] == '@')
			{
				using namespace std::chrono;
				cluster& bot = *og.owner;
				long unsigned uid = std::stoul(args[0].substr(2, args[0].size() - 3));
				long unsigned rid = (long unsigned)mrole;
				confirmation_callback_t ret = co_await bot.co_guild_get_member(gid, uid);
				guild_member mem = get<guild_member>(ret.value);
				if(find(mem.get_roles().begin(), mem.get_roles().end(), rid) == mem.get_roles().end())
				{
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
	                give_role_temp(bot, gid, uid, rid, duration_cast<system_clock::duration>(minutes(mtime)));
					co_return"Successfully muted user.";
				}
				else
					co_return"User is already muted.";
			}
			else
				co_return"Mention the user you wish to mute.";
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return"Specify a member to mute.";
}
task<string> Mutetimecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
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
			co_return"Successfully set the new mute time.";
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return tostr(allguilds[gid]["mutetime"]) + " minutes";
}
task<string> Mutablecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
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
			co_return"Updated mute words.";
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return tostr(allguilds[gid]["mutable"]);
}
task<string> Allrolecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		cluster& bot = *og.owner;
		std::optional<role>roleid;
		string retstr;
		vector<snowflake>rolelist;
		confirmation_callback_t cct = co_await bot.co_guild_get_members(gid, 999, 0);
		guild_member_map gmems = get<guild_member_map>(cct.value);
		cct = co_await og.owner->co_roles_get(og.guild_id);
		role_map roles = get<role_map>(cct.value);
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
			if(has_all(m.get_roles().cbegin(), m.get_roles().cend(), rolelist.begin(), rolelist.end()))
				retstr += tostr((std::uint64_t)id) + ' ' + m.get_user()->username + '\n';
		}
		co_return retstr;
	}
	else
		co_return"Give a list of roles.";
}
task<string> Anyrolecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		cluster& bot = *og.owner;
		std::optional<role>roleid;
		string retstr;
		vector<snowflake>rolelist;
		confirmation_callback_t cct = co_await bot.co_guild_get_members(gid, 999, 0);
		auto gmems = get<guild_member_map>(cct.value);
		cct = co_await og.owner->co_roles_get(og.guild_id);
		role_map roles = get<role_map>(cct.value);
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
			if(has_any(m.get_roles().cbegin(), m.get_roles().cend(), rolelist.begin(), rolelist.end()))
				retstr += tostr((std::uint64_t)id) + ' ' + m.get_user()->username + '\n';
		}
		co_return retstr;
	}
	else
		co_return"Give a list of roles.";
}
task<string> Purgecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		confirmation_callback_t cct = co_await og.owner->co_channels_get(gid);
		channel_map chmap = get<channel_map>(cct.value);
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_messages, chmap[og.channel_id].permission_overwrites))
		{
			int cnt = std::stoi(args[0]);
			if(cnt > 100)
				co_return"No more than one hundred messages.";
			else if(cnt < 2)
				co_return"At least one message.";
			else
			{
				using namespace std::chrono;
				using namespace std::chrono_literals;
				cluster& bot = *og.owner;
				confirmation_callback_t cct = co_await bot.co_messages_get(og.channel_id, 0, og.id, 0, cnt);
				auto msgmap = get<message_map>(cct.value);
				vector<snowflake>recent, older;
				auto currtm = to_time(og.id);
				for(const auto&[id, msg]: msgmap)
				{
					auto tm = to_time(id);
					if(currtm - tm > 336h)
						older.push_back(id);
					else
						recent.push_back(id);
				}
				if(recent.size() > 1)
					co_await bot.co_message_delete_bulk(recent, og.channel_id);
				else if(recent.size() == 1)
					co_await bot.co_message_delete(recent[0], og.channel_id);
				for(auto id: older)
					co_await bot.co_message_delete(id, og.channel_id);
				co_return"Purged messages successfully.";
			}
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return"Specify the number of messages to purge.";
}
task<string> Takerolecmd::operator()(const message& og, const string* args, size_t size)
{
	if(size >= 2)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_roles))
		{
			if(args[0][1] == '@')
			{
				std::optional<role>oprole;
				std::uint64_t id = std::stoul(args[0].substr(2, args[0].size() - 3));
				string retstr;
				confirmation_callback_t cct = co_await og.owner->co_roles_get(og.guild_id);
				role_map roles = get<role_map>(cct.value);
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
				co_return retstr.size() ? retstr : "Succesfully updated member.";
			}
			else
				co_return"Mention the user you wish to take the role from.";
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return"Name a member and a role to take from that member.";
}
task<string> Giverolecmd::operator()(const message& og, const string* args, size_t size)
{
	if(size >= 2)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_roles))
		{
			if(args[0][1] == '@')
			{
				std::optional<role>oprole;
				std::uint64_t id = std::stoul(args[0].substr(2, args[0].size() - 3));
				string retstr;
				confirmation_callback_t cct = co_await og.owner->co_roles_get(og.guild_id);
				role_map roles = get<role_map>(cct.value);
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
				co_return retstr.size() ? retstr : "Succesfully updated member.";
			}
			else
				co_return"Mention the user you wish to give the role to.";
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return"Name a member and a role to give to that member.";
}
task<string> Macrolscmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	auto& macros = allguilds[gid]["macros"];
	string retstr;
	for(auto it = macros.begin(); it != macros.end(); ++it)
	{
		string name = it.key(), expand = (string)it.value();
		retstr += name + ' ' + expand + '\n';
	}
	co_return retstr;
}
task<string> Undefcmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	auto& macros = allguilds[gid]["macros"];
    for(size_t i = 0; i < size; ++i)
	{
		string name = args[i].substr(1);
		macros.erase(name);
	}
	co_return"Successfully removed macros.";
}
task<string> Definecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	auto& macros = allguilds[gid]["macros"];
	if(size >= 2)
	{
		macros[args[0]] = args[1];
		co_return"Defined new macro.";
	}
	else
		co_return"Give the macro a name and specify what it should expand to.";
}
task<string> Muterolecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& mrole = allguilds[gid]["muterole"];
			json numid;
			std::optional<role>roleid = co_await findrole(*og.owner, gid, args[0]);
			if(!roleid)
			{
				co_return args[0] + " could not be found,\n";
			}
			else
			{
				numid = (std::uint64_t)roleid->id;
				if(numid == mrole)
					mrole = nullptr;
				else
					mrole = numid;
				co_return"Successfully updated muterole.";
			}
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return tostr(allguilds[gid]["muterole"]);
}
task<string> Bancmd::operator()(const message& og, const string* args, size_t size)
{
    unsigned failed = 0;
    bool perm = true;
	long unsigned luid;
	snowflake id;
	if(co_await hasperm(*og.owner, og.member, permissions::p_ban_members))
    {
		string retstr;
        for(size_t i = 0; i < size; ++i)
        {
			auto& arg = args[i];
			if(arg.size() > 3 && arg[1] == '@')
			{
				luid = std::stoul(arg.substr(2, arg.size() - 3));
				id = luid;
				confirmation_callback_t cct = co_await og.owner->co_guild_get_member(og.guild_id, id);
				guild_member mem = get<guild_member>(cct.value);
				if(co_await member_cmpr(*og.owner, og.member, mem) > 0)
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
		co_return retstr;
    }
	else
		co_return"You do not have permission to use this command.";
}
task<string> Kickcmd::operator()(const message& og, const string* args, size_t size)
{
    unsigned failed = 0;
    bool perm = true;
	long unsigned luid;
	snowflake id;
	if(co_await hasperm(*og.owner, og.member, permissions::p_kick_members))
    {
		string retstr;
        for(size_t i = 0; i < size; ++i)
        {
			auto& arg = args[i];
			if(arg.size() > 3 && arg[1] == '@')
			{
				luid = std::stoul(arg.substr(2, arg.size() - 3));
				id = luid;
				confirmation_callback_t cct = co_await og.owner->co_guild_get_member(og.guild_id, id);
				guild_member mem = get<guild_member>(cct.value);
				if(co_await member_cmpr(*og.owner, og.member, mem) > 0)
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
		co_return retstr;
    }
	else
		co_return"You do not have permission to use this command.";
}
task<string> DLOptionscmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
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
		co_return"";
	}
	else
		co_return"You do not have permission to use this command.";
}
task<string> Infocmd::operator()(const message& og, const string* args, size_t size)
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
		confirmation_callback_t cct = co_await bot.co_roles_get(gid);
		role_map rmap = get<role_map>(cct.value);
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
						ch = get<channel>((co_await bot.co_channel_get(id)).value);
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
							mem = get<guild_member>((co_await bot.co_guild_get_member(gid, id)).value);
							oss << "ID: " << luid << '\n';
							oss << "Joined at: " << mem.joined_at << '\n';
							oss << "Number of roles: " << mem.get_roles().size() << '\n';
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
		confirmation_callback_t cct = co_await bot.co_guild_get_members(gid, 999, 0);
		long memcnt = get<guild_member_map>(cct.value).size();
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
	co_return oss.str();
}
task<string> Selfrolecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		auto& sroles = allguilds[gid]["selfroles"];
		json numid;
		std::optional<role>roleid;
		string retstr;
		confirmation_callback_t cct = co_await og.owner->co_roles_get(og.guild_id);
		role_map roles = get<role_map>(cct.value);
		for(size_t i = 0; i < size; ++i)
		{
			roleid = findrole(roles, *og.owner, gid, args[i]);
			if(!roleid)
			{
				retstr += args[i] + " could not be found.\n";
				continue;
			}
			numid = (std::uint64_t)roleid->id;
			auto& memrole = og.member.get_roles();
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
		co_return retstr.size() ? retstr : "Successfully updated your roles.";
	}
	else
		co_return"Name a role to give yourself.";
}
task<string> Toggleselfrolecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& sroles = allguilds[gid]["selfroles"];
			json numid;
			std::optional<role>roleid;
			string retstr;
			confirmation_callback_t cct = co_await og.owner->co_roles_get(og.guild_id);
			role_map roles = get<role_map>(cct.value);
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
			co_return retstr.size() ? retstr : "Successfully toggled selfroles.";
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return tostr(allguilds[gid]["selfroles"]);
}
task<string> Autorolecmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			auto& aroles = allguilds[gid]["autoroles"];
			json numid;
			std::optional<role>roleid;
			string retstr;
			confirmation_callback_t cct = co_await og.owner->co_roles_get(og.guild_id);
			role_map roles = get<role_map>(cct.value);
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
			co_return retstr.size() ? retstr : "Successfully toggled autoroles.";
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return tostr(allguilds[gid]["autoroles"]);
}
task<string> Atan2cmd::operator()(const message& og, const string* args, size_t size)
{
    if(size < 2)
        co_return "Two arguments are required, y and x.";
    else
    {
        double x = tonum(args[1]), y = tonum(args[0]);
		co_return tostr(std::atan2(y, x));
    }
}
task<string> Atancmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Acoscmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Asincmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Csccmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Seccmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Cotcmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Tancmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Coscmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Sincmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Logcmd::operator()(const message& og, const string* args, size_t size)
{
    if(size == 0)
        co_return"Provide the base and then the power.";
    else
    {
        if(size == 1)
            co_return tostr(std::log(tonum(args[0])));
        else
        {
            double base = tonum(args[0]), p = tonum(args[1]);
            co_return tostr(std::log(p) / std::log(base));
        }
    }
}
task<string> Powcmd::operator()(const message& og, const string* args, size_t size)
{
    if(size == 0)
        co_return"Provide the base and then the exponent.";
    else
    {
        if(size == 1)
            co_return tostr(std::exp(tonum(args[0])));
        else
        {
            double base = tonum(args[0]), exp = tonum(args[1]);
            co_return tostr(std::pow(base, exp));
        }
    }
}
task<string> HMeancmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> GMeancmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> AMeancmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Baseconvcmd::operator()(const message& og, const string* args, size_t size)
{
    if(size < 3)
        co_return "Three arguments are required. Number, base the number is in, base the number is to be displayed in.";
    else
    {
        int from = toint(args[1]), to = toint(args[2]);
        if(from >= 2 && from <= 36 && to >= 2 && to <= 36)
        {
            long num = std::stol(args[0], nullptr, from);
            co_return numstr(num, to);
        }
        else
            co_return "Base must be in the interval [2,36].";
    }
}
task<string> Factorcmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Primecmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Gcdcmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Modulocmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Quotientcmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Prefixcmd::operator()(const message& og, const string* args, size_t size)
{
	snowflake gid = og.guild_id;
	if(size > 0)
	{
		if(co_await hasperm(*og.owner, og.member, permissions::p_manage_guild))
		{
			if(args[0].size() <= 3)
			{
				allguilds[gid]["pref"] = *args;
				co_return"New prefix has been set.";
			}
			else
				co_return"Prefix must be no more than three characters.";
		}
		else
			co_return"You do not have permission to use this command.";
	}
	else
		co_return(string)allguilds[gid]["pref"];
}
task<string> Productcmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
task<string> Sumcmd::operator()(const message& og, const string* args, size_t size)
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
	co_return resstr;
}
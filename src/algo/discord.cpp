// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#include<algorithm>
#include <dpp/restresults.h>
#include<vector>
#include<nlohmann/json.hpp>
#include"discord.hpp"
#include"str.hpp"
#include"utils.hpp"

using std::optional;
using std::string;
using std::uint64_t;
using namespace dpp;

extern std::unordered_map<dpp::snowflake,nlohmann::json>allguilds;
extern std::unordered_map<dpp::snowflake,dpp::guild>gdata;

int get_mute_time(nlohmann::json& dat, snowflake user)
{
	std::size_t offense = 0;
    auto &offenseMap = dat["muteoff"];
    string uidstr = std::to_string(uint64_t(user));
    auto it = offenseMap.find(uidstr);
    if(it != offenseMap.end())
    	offense = unsigned(*it);
    else
    	offenseMap[uidstr] = 0;
	auto &times = dat["mutetime"];
	offense = std::min(offense, times.size() - 1);
    offenseMap[uidstr] = offense + 1;
    return(int)times[offense];
}

std::chrono::time_point<std::chrono::system_clock>to_time(snowflake id)
{
	using namespace std::chrono;
	uint64_t tm = (uint64_t)id >> 22;
	tm += discord_epoch;
	seconds x(tm);
	return time_point<system_clock>(duration_cast<system_clock::duration>(x));
}

task<bool>hasperm(cluster& bot, const guild_member& member, permission perm)
{
	co_return co_await hasperm(bot, member, perm, std::vector<permission_overwrite>{});
}

task<bool>hasperm(cluster& bot, const guild_member& member, permission perm, const std::vector<permission_overwrite>& over)
{
	if(gdata[member.guild_id].owner_id == member.user_id)
		co_return true;
	else
	{
		permission mperms;
		permission roleallow, roledeny;
		permission memallow, memdeny;
		confirmation_callback_t cct = co_await bot.co_roles_get(member.guild_id);
		auto rmap = get<role_map>(cct.value);
		for(auto x:over)
		{
			if(x.type == ot_role)
			{
				if(find(member.get_roles().cbegin(), member.get_roles().cend(), x.id) == member.get_roles().cend())
				{
					roleallow.add(x.allow);
					roledeny.add(x.deny);
				}
			}
			else if(x.id == member.user_id)
			{
				memallow = x.allow;
				memdeny = x.deny;
			}
		}
		for(auto x:member.get_roles())
			mperms.add(rmap.at(x).permissions);
		if(mperms.has(permissions::p_administrator))
			co_return true;
		else
		{
			mperms.remove(roledeny);
			mperms.add(roleallow);
			mperms.remove(memdeny);
			mperms.add(memallow);
			co_return mperms.has(perm);
		}
	}
}

void give_role_temp(cluster& bot, snowflake gid, snowflake uid, snowflake rid, std::chrono::system_clock::duration dura)
{
	using namespace std::chrono;
	auto cb = [&bot, gid, uid, rid, dura](const confirmation_callback_t& evt)
	{
		const guild_member& mem = std::get<guild_member>(evt.value);
		if(find(mem.get_roles().cbegin(), mem.get_roles().cend(), rid) == mem.get_roles().cend())
		{
		    bot.guild_member_add_role(gid, uid, rid);
			json& templist = allguilds[gid]["temprole"];
			json tempentry = json::object();
			tempentry["user"] = (uint64_t)uid;
			tempentry["role"] = (uint64_t)rid;
			tempentry["release"] = time_point_cast<seconds>(system_clock::now() + dura).time_since_epoch().count();
			templist.push_back(tempentry);
		    auto calllater = [&bot, &templist, gid, uid, rid]()
		    {
		    	for(std::size_t i=0;i<templist.size();i++)
				{
		    		if((uint64_t)templist[i]["user"] == (uint64_t)uid && (uint64_t)templist[i]["role"] == (uint64_t)rid)
		    			templist.erase(i);
				}
		        bot.guild_member_remove_role(gid, uid, rid);
		    };
		    setTimeout(calllater, dura);
		}
	};
	bot.guild_get_member(gid, uid, cb);
}

task<role>getrole(cluster& bot, snowflake guild, snowflake value)
{
	confirmation_callback_t cct = co_await bot.co_roles_get(guild);
	const auto& roles = get<role_map>(cct.value);
	co_return roles.at(value);
}

task<optional<role>>findrole(cluster& bot, snowflake guild, string value)
{
	confirmation_callback_t cct = co_await bot.co_roles_get(guild);
	const auto& roles = get<role_map>(cct.value);
	co_return findrole(roles, bot, guild, value);
}

optional<role> findrole(const role_map& roles, cluster& bot, snowflake guild, string value)
{
    optional<role>ret;
	snowflake id;
    if(value.size() >= 5 && value[1] == '@')
	{
		id = std::stoul(value.substr(3, value.size() - 4));
		if(roles.find(id) != roles.end())
			ret = roles.at(id);
	}
	else
	{
		for(const auto& [u, v]: roles)
		{
			if(are_equal_ignore_case(value, v.name))
				ret = v;
		}
	}
    return ret;
}

role highrole(const role_map& roles, const guild_member& mem)
{
	role r;
	for(auto x:mem.get_roles())
		r = std::max(roles.at(x), r);
	return r;
}

task<int>member_cmpr(cluster& bot, const guild_member& x, const guild_member& y)
{
	using namespace std;
	snowflake gid = x.guild_id;
	confirmation_callback_t cct = co_await bot.co_roles_get(gid);
	const auto& roles = get<role_map>(cct.value);
	role xhigh = highrole(roles, x), yhigh = highrole(roles, y);
	co_return xhigh > yhigh ? 1 : xhigh < yhigh ? -1 : 0;
}

void fetch_guilds(cluster& bot)
{
	snowflake idagain;
	using namespace std::chrono;
	time_point<system_clock>currtime = system_clock::now(), removetime;
    for(const auto &[id, j]: allguilds)
    {
		idagain = id;
		json& tmproledat = allguilds[id]["temprole"];
		for(std::size_t i=0;i<tmproledat.size();i++)
		{
			json& dat = tmproledat[i];
			removetime = system_clock::from_time_t((std::time_t)dat["release"]);
		    auto calllater = [&bot, &dat, &tmproledat, idagain]()
		    {
		        bot.guild_member_remove_role(idagain, (uint64_t)dat["user"], (uint64_t)dat["role"]);
		    	for(std::size_t i=0;i<tmproledat.size();i++)
				{
		    		if(tmproledat[i] == dat)
		    		{
		    			tmproledat.erase(i);
		    			i = 0xffffffff;
					}
				}
		    };
		    if(removetime < currtime)
		    	calllater();
		    else
		    	setTimeout(calllater, removetime - currtime);
		}
		auto callback = [&bot, idagain](const confirmation_callback_t& res)
		{
			guild& g = gdata[idagain] = std::get<guild>(res.value);
			auto rcallback = [&g](const confirmation_callback_t& res)
			{
				const role_map& rmap = std::get<role_map>(res.value);
				for(const auto&[id, v]:rmap)
					g.roles.push_back(id);
			};
			auto ccallback = [&g](const confirmation_callback_t& res)
			{
				const channel_map& cmap = std::get<channel_map>(res.value);
				for(const auto&[id, v]:cmap)
					g.channels.push_back(id);
			};
			auto tcallback = [&g](const confirmation_callback_t& res)
			{
				/*const dpp::active_threads& tmap = std::get<dpp::active_threads>(res.value);
				for(const auto&[id, v]:tmap)
					g.threads.push_back(id);*/
			};
			auto ecallback = [&g](const confirmation_callback_t& res)
			{
				const emoji_map& emap = std::get<emoji_map>(res.value);
				for(const auto&[id, v]:emap)
					g.emojis.push_back(id);
			};
	        bot.roles_get(idagain, rcallback);
	        bot.channels_get(idagain, ccallback);
	        bot.threads_get_active(idagain, tcallback);
	        bot.guild_emojis_get(idagain, ecallback);
    	};
        bot.guild_get(id, callback);
    }
}

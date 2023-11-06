// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#include<algorithm>
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

std::chrono::time_point<std::chrono::system_clock>to_time(snowflake id)
{
	using namespace std::chrono;
	uint64_t tm = (uint64_t)id >> 22;
	tm += discord_epoch;
	seconds x(tm);
	return time_point<system_clock>(duration_cast<system_clock::duration>(x));
}

bool hasperm(cluster& bot, const guild_member& member, permission perm)
{
	permission mperms;
	auto rmap = bot.roles_get_sync(member.guild_id);
	for(auto x:member.roles)
		mperms.add(rmap.at(x).permissions);
	return mperms.has(perm);
}

void give_role_temp(cluster& bot, snowflake gid, snowflake uid, snowflake rid, std::chrono::system_clock::duration dura)
{
	using namespace std::chrono;
	auto cb = [&bot, gid, uid, rid, dura](const confirmation_callback_t& evt)
	{
		const guild_member& mem = std::get<guild_member>(evt.value);
		if(std::find(mem.roles.cbegin(), mem.roles.cend(), rid) == mem.roles.cend())
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

role getrole(cluster& bot, snowflake guild, snowflake value)
{
	return bot.roles_get_sync(guild).at(value);
}

optional<role> findrole(cluster& bot, snowflake guild, string value)
{
	return findrole(bot.roles_get_sync(guild), bot, guild, value);
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
	for(auto x:mem.roles)
		r = std::max(roles.at(x), r);
	return r;
}

int member_cmpr(cluster& bot, const guild_member& x, const guild_member& y)
{
	using namespace std;
	snowflake gid = x.guild_id;
	auto roles = bot.roles_get_sync(gid);
	role xhigh = highrole(roles, x), yhigh = highrole(roles, y);
	return xhigh > yhigh ? 1 : xhigh < yhigh ? -1 : 0;
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
		    auto calllater = [&bot, &dat, idagain]()
		    {
		        bot.guild_member_remove_role(idagain, (uint64_t)dat["user"], (uint64_t)dat["role"]);
		    };
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
				const thread_map& tmap = std::get<active_threads>(res.value).threads;
				for(const auto&[id, v]:tmap)
					g.threads.push_back(id);
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

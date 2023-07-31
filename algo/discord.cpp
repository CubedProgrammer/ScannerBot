// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#include<algorithm>
#include<vector>
#include<nlohmann/json.hpp>
#include"str.hpp"
#include"discord.hpp"

using std::optional;
using std::string;
using namespace dpp;

extern std::unordered_map<dpp::snowflake,nlohmann::json>allguilds;
extern std::unordered_map<dpp::snowflake,dpp::guild>gdata;

std::chrono::time_point<std::chrono::system_clock>to_time(dpp::snowflake id)
{
	using namespace std::chrono;
	std::uint64_t tm = (std::uint64_t)id >> 22;
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

role getrole(cluster& bot, snowflake guild, snowflake value)
{
	return bot.roles_get_sync(guild).at(value);
}

optional<role> findrole(cluster& bot, snowflake guild, string value)
{
    role_map roles = bot.roles_get_sync(guild);
    optional<role>ret;
	snowflake id;
    if(value.size() >= 5 && value[1] == '@')
	{
		id = std::stoul(value.substr(3, value.size() - 4));
		if(roles.find(id) != roles.end())
			ret = roles[id];
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

int member_cmpr(cluster& bot, const dpp::guild_member& x, const dpp::guild_member& y)
{
	using namespace std;
	snowflake gid = x.guild_id;
	auto roles = bot.roles_get_sync(gid);
	role xhigh = roles.at(gid), yhigh = xhigh;
	for(auto rid : x.roles)
		xhigh = max(xhigh, roles.at(rid));
	for(auto rid : y.roles)
		yhigh = max(yhigh, roles.at(rid));
	return xhigh > yhigh ? 1 : xhigh < yhigh ? -1 : 0;
}

void fetch_guilds(cluster& bot)
{
	snowflake idagain;
    for(const auto &[id, j]: allguilds)
    {
		idagain = id;
		auto callback = [idagain](const confirmation_callback_t& res)
		{
			gdata[idagain] = std::get<guild>(res.value);
    	};
        bot.guild_get(id, callback);
    }
}
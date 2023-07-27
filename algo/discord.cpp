#include<iostream>
#include<nlohmann/json.hpp>
#include"str.hpp"
#include"discord.hpp"

using std::optional;
using std::string;
using namespace dpp;

extern std::unordered_map<dpp::snowflake,nlohmann::json>allguilds;
extern std::unordered_map<dpp::snowflake,dpp::guild>gdata;

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

void fetch_guilds(cluster& bot)
{
	snowflake idagain;
    for(const auto &[id, j]: allguilds)
    {
		idagain = id;
		auto callback = [idagain](const confirmation_callback_t& res)
		{
			gdata[idagain] = res.get<guild>();
    	};
        bot.guild_get(id, callback);
    }
}
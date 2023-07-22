#include"str.hpp"
#include"discord.hpp"

using std::optional;
using std::string;
using namespace dpp;

bool hasperm(cluster& bot, const guild_member& member, permission perm)
{
	permission mperms;
	for(auto x:member.roles)
		mperms.add(getrole(bot, member.guild_id, x).permissions);
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

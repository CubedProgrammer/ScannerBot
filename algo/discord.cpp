#include"discord.hpp"
using namespace dpp;

bool hasperm(cluster& bot, const guild_member& member, permission perm)
{
	permission mperms;
	for(auto x:member.roles)
		mperms.add(getrole(bot, member.guild_id, x).permissions);
	return mperms.has(perm);
}

dpp::role getrole(cluster& bot, snowflake guild, snowflake value)
{
	return bot.roles_get_sync(guild).at(value);
}

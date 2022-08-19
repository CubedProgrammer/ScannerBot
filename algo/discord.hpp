#ifndef ALGO_DISCORD_HPP_
#define ALGO_DISCORD_HPP_
#include<dpp/dpp.h>

bool hasperm(dpp::cluster& bot, const dpp::guild_member& member, dpp::permission perm);
dpp::role getrole(dpp::cluster& bot, dpp::snowflake guild, dpp::snowflake value);

#endif

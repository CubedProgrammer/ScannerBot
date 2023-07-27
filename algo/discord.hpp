#ifndef ALGO_DISCORD_HPP_
#define ALGO_DISCORD_HPP_
#include<optional>
#include<dpp/dpp.h>

bool hasperm(dpp::cluster& bot, const dpp::guild_member& member, dpp::permission perm);
dpp::role getrole(dpp::cluster& bot, dpp::snowflake guild, dpp::snowflake value);
std::optional<dpp::role> findrole(dpp::cluster& bot, dpp::snowflake guild, std::string value);
void fetch_guilds(dpp::cluster& bot);

#endif

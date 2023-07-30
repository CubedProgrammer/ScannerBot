#ifndef ALGO_DISCORD_HPP_
#define ALGO_DISCORD_HPP_
#include<chrono>
#include<optional>
#include<dpp/dpp.h>

constexpr long discord_epoch = 1420070400000;

std::chrono::time_point<std::chrono::system_clock>to_time(dpp::snowflake id);
bool hasperm(dpp::cluster& bot, const dpp::guild_member& member, dpp::permission perm);
dpp::role getrole(dpp::cluster& bot, dpp::snowflake guild, dpp::snowflake value);
std::optional<dpp::role> findrole(dpp::cluster& bot, dpp::snowflake guild, std::string value);
int member_cmpr(dpp::cluster& bot, const dpp::guild_member& x, const dpp::guild_member& y);
void fetch_guilds(dpp::cluster& bot);

#endif

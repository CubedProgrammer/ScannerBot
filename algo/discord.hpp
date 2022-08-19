#ifndef ALGO_DISCORD_HPP_
#define ALGO_DISCORD_HPP_
#include<dpp/dpp.h>

bool hasperm(const dpp::guild_member& member, dpp::permission perm);
dpp::role getrole(const dpp::cluster& bot);

#endif

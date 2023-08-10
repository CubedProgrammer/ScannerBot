// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#ifndef ALGO_DISCORD_HPP_
#define ALGO_DISCORD_HPP_
#include<chrono>
#include<optional>
#include<dpp/dpp.h>

constexpr long discord_epoch = 1420070400000;

std::chrono::time_point<std::chrono::system_clock>to_time(dpp::snowflake id);
bool hasperm(dpp::cluster& bot, const dpp::guild_member& member, dpp::permission perm);
void give_role_temp(dpp::cluster& bot, dpp::snowflake gid, dpp::snowflake uid, dpp::snowflake rid, std::chrono::system_clock::duration dura);
dpp::role getrole(dpp::cluster& bot, dpp::snowflake guild, dpp::snowflake value);
std::optional<dpp::role> findrole(dpp::cluster& bot, dpp::snowflake guild, std::string value);
std::optional<dpp::role> findrole(const dpp::role_map& roles, dpp::cluster& bot, dpp::snowflake guild, std::string value);
dpp::role highrole(const dpp::role_map& roles, const dpp::guild_member& mem);
int member_cmpr(dpp::cluster& bot, const dpp::guild_member& x, const dpp::guild_member& y);
void fetch_guilds(dpp::cluster& bot);

#endif

// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#ifndef COMMANDPARSER_HPP_
#define COMMANDPARSER_HPP_
#include<memory>
#include<string>
#include<unordered_map>
#include<vector>
#include<dpp/dpp.h>

struct Command
{
	std::string description;
	Command(std::string desc);
	Command()=default;
	Command(const Command& cmd)=default;
	Command(Command&& cmd)=default;
	Command& operator=(const Command& cmd)=default;
	Command& operator=(Command&& cmd)=default;
	virtual std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)=0;
	virtual~Command()=default;
};

using ptrCommand = std::unique_ptr<Command>;
using cmdmap = std::unordered_map<std::string,ptrCommand>;
using guildmap = std::unordered_map<dpp::snowflake,json>;
using gdatamap = std::unordered_map<dpp::snowflake,dpp::guild>;

class CommandParser
{
	std::string verstr;
	cmdmap cmds;
public:
	CommandParser(std::string verstr, const std::vector<std::string>& names, std::vector<ptrCommand>& cmds);
	CommandParser(const CommandParser& parser)=default;
	CommandParser(CommandParser&& parser)=default;
	CommandParser()=default;
	CommandParser& operator=(const CommandParser& cmd)=default;
	CommandParser& operator=(CommandParser&& cmd)=default;
	std::string operator()(const dpp::message& og, std::string cmd);
	void help(std::string& res, const std::vector<std::pair<std::size_t,std::size_t>>& pending, std::string *args)const;
	std::string run(const dpp::message& og,std::string* args, std::size_t size);
};

#endif

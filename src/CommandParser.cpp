// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#include<regex>
#include<stdexcept>
#include"CommandParser.hpp"

using std::move;
using std::pair;
using std::size_t;
using std::string;
using std::string_literals::operator""s;
using std::vector;
using nlohmann::json;
using namespace dpp;

const string ENDCMD = "__end__";
extern guildmap allguilds;

Command::Command(string desc)
	:description(move(desc))
{}

CommandParser::CommandParser(string verstr,const vector<string>& names, vector<ptrCommand>& cmds)
	:verstr(move(verstr)), cmds()
{
	for(size_t i=0;i<names.size();i++)
		this->cmds[names[i]] = move(cmds[i]);
}

task<string> CommandParser::operator()(const message& og,string cmd)
{
	vector<string>tokens;
	string curr;
	json& macroobj = allguilds[og.guild_id]["macros"];
	bool esc = false;
	bool closed = false;
	cmd = ' ' + cmd + ' ';
	for(auto it = macroobj.begin(); it != macroobj.end(); ++it)
	{
		string name = it.key(), expand = ' ' + (string)it.value() + ' ';
		std::regex tofind("\\s+" + name + "\\s+");
		cmd = std::regex_replace(cmd, tofind, expand);
	}
	for(char c : cmd)
	{
		switch(c)
		{
			case '"':
				if(esc)
					curr += c;
				else
					closed = !closed;
				esc = false;
				break;
			case ' ':
				if(closed || esc)
					curr += c;
				else if(curr.size())
				{
					tokens.push_back(curr);
					curr.clear();
				}
				esc = false;
				break;
			case '\\':
				esc = !esc;
				if(esc)
					break;
			default:
				curr += c;
				esc = false;
				break;
		}
	}
	if(curr.size())
		tokens.push_back(curr);
	try
	{
		co_return co_await this->run(og, tokens.data(),tokens.size());
	}
	catch(std::invalid_argument&e)
	{
		co_return e.what() + " invalid argument"s;
	}
	catch(std::exception&e)
	{
		co_return"Exception occurred: "s + e.what();
	}
}

void CommandParser::help(string& res, const vector<pair<size_t,size_t>>& pending, string *args)const
{
	if(pending.back().second > 0)
		res = this->cmds.at(args[pending.back().first + 1])->description;
	else
	{
		res = "Scanner Bot https://github.com/CubedProgrammer/ScannerBot\n";
#ifdef BUILDER
		res += "Built by "s + BUILDER + '\n';
#endif
		res += "Language: C++" + std::to_string(__cplusplus / 100 % 100) + "\nThe list of commands are as follows";
		for(const auto&[x,y]:this->cmds)
			res += "\r\n" + x;
	}
}

task<string>CommandParser::run(const message& og,string* args, size_t size)
{
	string res;
	vector<pair<size_t,size_t>>pending;
	pending.emplace_back(0,0);
	bool notcmd = args[0] == "help";
	for(size_t i=1;i<size;i++)
	{
		const auto &token = args[i];
		if(token == ENDCMD)
		{
			const auto &cmdname = args[pending.back().first];
			if(cmdname == "version")
				res = this->verstr;
			else if(cmdname == "help")
			{
				help(res, pending, args);
				notcmd = false;
			}
			else
			{
				auto &cmd = *this->cmds.at(cmdname);
				res = co_await cmd(og, args + pending.back().first + 1, pending.back().second);
			}
			pending.pop_back();
			if(pending.size() > 0)
				args[pending.back().second+pending.back().first] = move(res);
		}
		else
		{
			++pending.back().second;
			if(!notcmd && (token == "help" || token == "version" || this->cmds.find(token) != this->cmds.cend()))
			{
				pending.emplace_back(i,0);
				notcmd = token == "help";
			}
			else if(pending.back().first + pending.back().second != i)
				args[pending.back().first+pending.back().second] = move(args[i]);
		}
	}
	while(pending.size() > 0)
	{
		const auto &cmdname = args[pending.back().first];
		if(cmdname == "version")
			res = this->verstr;
		else if(cmdname == "help")
			help(res, pending, args);
		else if(this->cmds.find(cmdname)==this->cmds.cend())
			res = "Command not found.";
		else
		{
			auto &cmd = *this->cmds.at(cmdname);
			res = co_await cmd(og, args + pending.back().first + 1, pending.back().second);
		}
		pending.pop_back();
		if(pending.size() > 0)
			args[pending.back().second+pending.back().first] = move(res);
	}
	co_return res;
}

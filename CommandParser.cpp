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

std::string CommandParser::operator()(const message& og,string cmd)const
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
				break;
			case ' ':
				if(closed || esc)
					curr += c;
				else if(curr.size())
				{
					tokens.push_back(curr);
					curr.clear();
				}
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
		return this->run(og, tokens.data(),tokens.size());
	}
	catch(std::invalid_argument&e)
	{
		return e.what() + " invalid argument"s;
	}
	catch(std::exception&e)
	{
		return"Exception occurred: "s + e.what();
	}
}

void CommandParser::help(string& res, const vector<pair<size_t,size_t>>& pending, string *args)const
{
	if(pending.back().second > 0)
		res = this->cmds.at(args[pending.back().first + 1])->description;
	else
	{
		res = "The list of commands are as follows";
		for(const auto&[x,y]:this->cmds)
			res += "\r\n" + x;
	}
}

string CommandParser::run(const message& og,string* args, size_t size)const
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
				const auto &cmd = *this->cmds.at(cmdname);
				res = cmd(og, args + pending.back().first + 1, pending.back().second);
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
			const auto &cmd = *this->cmds.at(cmdname);
			res = cmd(og, args + pending.back().first + 1, pending.back().second);
		}
		pending.pop_back();
		if(pending.size() > 0)
			args[pending.back().second+pending.back().first] = move(res);
	}
	return res;
}

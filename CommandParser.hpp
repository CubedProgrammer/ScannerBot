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
	virtual std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const=0;
	virtual~Command()=default;
};

using ptrCommand = std::unique_ptr<Command>;
using cmdmap = std::unordered_map<std::string,ptrCommand>;
using guildmap = std::unordered_map<dpp::snowflake,json>;

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
	std::string operator()(const dpp::message& og, const std::string& cmd)const;
	void help(std::string& res, const std::vector<std::pair<std::size_t,std::size_t>>& pending, std::string *args)const;
	std::string run(const dpp::message& og,std::string* args, std::size_t size)const;
};

#endif

#ifndef COMMANDPARSER_HPP_
#define COMMANDPARSER_HPP_
#include<memory>
#include<string>
#include<unordered_map>
#include<vector>

struct Command
{
	std::string description;
	Command(std::string desc);
	Command()=default;
	Command(const Command& cmd)=default;
	Command(Command&& cmd)=default;
	Command& operator=(const Command& cmd)=default;
	Command& operator=(Command&& cmd)=default;
	virtual std::string operator()(const std::string* args, std::size_t size)const=0;
	virtual~Command()=default;
};

using ptrCommand = std::unique_ptr<Command>;
using cmdmap = std::unordered_map<std::string,ptrCommand>;

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
	std::string operator()(const std::string& cmd)const;
	std::string run(std::string* args, std::size_t size)const;
};

#endif

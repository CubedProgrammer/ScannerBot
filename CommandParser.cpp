#include"CommandParser.hpp"

using std::move;
using std::size_t;
using std::string;
using std::vector;

Command::Command(string desc)
	:description(move(desc))
{}

CommandParser::CommandParser(string verstr,const vector<string>& names, vector<ptrCommand>& cmds)
	:verstr(move(verstr)), cmds()
{
	for(size_t i=0;i<names.size();i++)
		this->cmds[names[i]] = move(cmds[i]);
}

std::string CommandParser::operator()(const std::string& cmd)const
{
	vector<string>tokens;
	return this->run(tokens.data(),tokens.size());
}

std::string CommandParser::run(const std::string* args, std::size_t size)const
{
	string res;
	return res;
}

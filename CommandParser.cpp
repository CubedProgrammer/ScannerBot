#include"CommandParser.hpp"

using std::move;
using std::pair;
using std::size_t;
using std::string;
using std::vector;

#if __cplusplus >= 202002L
constexpr
#else
const
#endif
string ENDCMD = "__end__";

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
	string curr;
	bool esc = false;
	for(char c : cmd)
	{
		switch(c)
		{
			case' ':
				if(esc)
					tokens.push_back(curr);
				else
					curr += c;
				break;
			case'\\':
				esc = !esc;
				if(esc)
					break;
			default:
				curr += c;
				break;
		}
	}
	if(curr.size())
		tokens.push_back(curr);
	return this->run(tokens.data(),tokens.size());
}

string CommandParser::run(string* args, size_t size)const
{
	string res;
	size_t argbegin = 1;
	vector<pair<size_t,size_t>>pending;
	pending.emplace_back(0,0);
	for(size_t i=1;i<size;i++)
	{
		const auto &token = args[i];
		if(this->cmds.find(token) != this->cmds.cend())
		{
			pending.emplace_back(i,0);
			argbegin = i + 1;
		}
		else if(token == ENDCMD)
		{
			const auto &cmd = *this->cmds.at(args[pending.back().first]);
			res = cmd(args + argbegin, i - argbegin);
			pending.pop_back();
		}
	}
	return res;
}

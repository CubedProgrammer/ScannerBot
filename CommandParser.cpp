#include"CommandParser.hpp"

using std::move;
using std::pair;
using std::size_t;
using std::string;
using std::vector;
using namespace dpp;

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

std::string CommandParser::operator()(const message& og,const std::string& cmd)const
{
	vector<string>tokens;
	string curr;
	bool esc = false;
	bool closed = false;
	for(char c : cmd)
	{
		switch(c)
		{
			case'"':
				if(esc)
					curr += c;
				else
					closed = !closed;
				break;
			case' ':
				if(closed || esc)
					curr += c;
				else if(curr.size())
				{
					tokens.push_back(curr);
					curr.clear();
				}
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
	return this->run(og, tokens.data(),tokens.size());
}

string CommandParser::run(const message& og,string* args, size_t size)const
{
	string res;
	vector<pair<size_t,size_t>>pending;
	pending.emplace_back(0,0);
	for(size_t i=1;i<size;i++)
	{
		const auto &token = args[i];
		if(token == ENDCMD)
		{
			const auto &cmdname = args[pending.back().first];
			if(cmdname == "version")
				res = this->verstr;
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
			if(token == "version" || this->cmds.find(token) != this->cmds.cend())
				pending.emplace_back(i,0);
			else if(pending.back().first + pending.back().second != i)
				args[pending.back().first+pending.back().second] = move(args[i]);
		}
	}
	while(pending.size() > 0)
	{
		const auto &cmdname = args[pending.back().first];
		if(cmdname == "version")
			res = this->verstr;
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
	if(res.size() == 0)
		res = "k";
	return res;
}

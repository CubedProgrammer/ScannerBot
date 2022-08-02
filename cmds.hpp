#ifndef CMDS_HPP_
#define CMDS_HPP_
#include"CommandParser.hpp"

struct Productcmd:Command
{
	Productcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const std::string* args, std::size_t size)const;
};

struct Sumcmd:Command
{
	Sumcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const std::string* args, std::size_t size)const;
};

#endif

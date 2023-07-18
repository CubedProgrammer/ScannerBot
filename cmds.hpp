#ifndef CMDS_HPP_
#define CMDS_HPP_
#include"CommandParser.hpp"

struct Factorcmd:Command
{
	Factorcmd()
		:Command("Prime factors a number.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Primecmd:Command
{
	Primecmd()
		:Command("Checks if a number is prime or composite.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Gcdcmd:Command
{
	Gcdcmd()
		:Command("Computes the greatest common divisor of a sequence of numbers.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Modulocmd:Command
{
	Modulocmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Quotientcmd:Command
{
	Quotientcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Prefixcmd:Command
{
	Prefixcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Productcmd:Command
{
	Productcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message &og, const std::string* args, std::size_t size)const;
};

struct Sumcmd:Command
{
	Sumcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

#endif

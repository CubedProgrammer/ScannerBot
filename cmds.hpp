#ifndef CMDS_HPP_
#define CMDS_HPP_
#include"CommandParser.hpp"

struct Logcmd:Command
{
	Logcmd()
		:Command("Computes log base x of y.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Powcmd:Command
{
	Powcmd()
		:Command("Computes x raised to the power of y.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct HMeancmd:Command
{
	HMeancmd()
		:Command("Evaluates the harmonic mean of a list of numbers.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct GMeancmd:Command
{
	GMeancmd()
		:Command("Evaluates the geometric mean of a list of numbers.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct AMeancmd:Command
{
	AMeancmd()
		:Command("Evaluates the arithmetic mean of a list of numbers.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Baseconvcmd:Command
{
	Baseconvcmd()
		:Command("<number> <m> <n>\nConverts the number from base m to n.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

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

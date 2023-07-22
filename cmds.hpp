#ifndef CMDS_HPP_
#define CMDS_HPP_
#include"CommandParser.hpp"

struct Autorolecmd:Command
{
	Autorolecmd()
		:Command("Displays all autoroles with no arguments. Toggles if a role is an autorole.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Atan2cmd:Command
{
	Atan2cmd()
		:Command("Computes inverse tangent with two arguments, y and x, in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Atancmd:Command
{
	Atancmd()
		:Command("Computes inverse tangent in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Acoscmd:Command
{
	Acoscmd()
		:Command("Computes inverse cosine in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Asincmd:Command
{
	Asincmd()
		:Command("Computes inverse sine in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Csccmd:Command
{
	Csccmd()
        :Command("Computes cosecant of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Seccmd:Command
{
	Seccmd()
        :Command("Computes secant of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Cotcmd:Command
{
	Cotcmd()
        :Command("Computes cotangent of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Tancmd:Command
{
	Tancmd()
        :Command("Computes tangent of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Coscmd:Command
{
	Coscmd()
        :Command("Computes cosine of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

struct Sincmd:Command
{
	Sincmd()
        :Command("Computes sine of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const;
};

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

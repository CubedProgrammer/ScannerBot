// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#ifndef CMDS_HPP_
#define CMDS_HPP_
#include<chrono>
#include<random>
#include"CommandParser.hpp"

struct Randcmd:Command
{
	std::mt19937_64 dice;
	Randcmd()
		:Command("Generate random numbers, one argument is upper bound, two is lower and upper, third is quanity of numbers."),
		 dice(std::chrono::system_clock::now().time_since_epoch().count())
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Epochcmd:Command
{
	Epochcmd()
		:Command("Get time since Unix Epoch, specify m, u, or n for unit, or no argument for seconds.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Mutecmd:Command
{
	Mutecmd()
		:Command("Mutes a member for a specified amount of time, first argument must be a mention to the member.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Mutetimecmd:Command
{
	Mutetimecmd()
		:Command("Set the mute time or see the mute time, in minutes, suffix h for hour and d for day.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Mutablecmd:Command
{
	Mutablecmd()
		:Command("Displays mute words with no arguments, toggless mute words otherwise.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Allrolecmd:Command
{
	Allrolecmd()
		:Command("Lists all members that have all of the specified roles.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Anyrolecmd:Command
{
	Anyrolecmd()
		:Command("Lists all members that have any of the specified roles.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Purgecmd:Command
{
	Purgecmd()
		:Command("Purges up to one hundred messages from a channel, specify number of messages to remove.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Takerolecmd:Command
{
	Takerolecmd()
		:Command("Takes a role from a member, first argument is a mention of the target user.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Giverolecmd:Command
{
	Giverolecmd()
		:Command("Gives a member a role, first argument is a mention of the target user.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Macrolscmd:Command
{
	Macrolscmd()
		:Command("Displays all macros of this server.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Undefcmd:Command
{
	Undefcmd()
		:Command("Removes a macro from your server, place a dash in front of the name so it does not get expanded.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Definecmd:Command
{
	Definecmd()
		:Command("Defines a macro for your server, first argument is name, second is the macro expansion.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Muterolecmd:Command
{
	Muterolecmd()
		:Command("Displays the current muterole with no arguments. Sets the new muterole otherwise to the first argument.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Bancmd:Command
{
	Bancmd()
		:Command("Bans a user from this server.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Kickcmd:Command
{
	Kickcmd()
		:Command("Kicks a user from this server.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct DLOptionscmd:Command
{
	DLOptionscmd()
		:Command("Download the options file for this server.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Infocmd:Command
{
	Infocmd()
		:Command("Gives information on the server with no arguments, or a user, channel, or role by mention.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Selfrolecmd:Command
{
	Selfrolecmd()
		:Command("Give yourself a selfrole.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Toggleselfrolecmd:Command
{
	Toggleselfrolecmd()
		:Command("Displays all selfroles with no arguments. Toggles if a role is an selfrole.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Autorolecmd:Command
{
	Autorolecmd()
		:Command("Displays all autoroles with no arguments. Toggles if a role is an autorole.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Atan2cmd:Command
{
	Atan2cmd()
		:Command("Computes inverse tangent with two arguments, y and x, in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Atancmd:Command
{
	Atancmd()
		:Command("Computes inverse tangent in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Acoscmd:Command
{
	Acoscmd()
		:Command("Computes inverse cosine in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Asincmd:Command
{
	Asincmd()
		:Command("Computes inverse sine in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Csccmd:Command
{
	Csccmd()
        :Command("Computes cosecant of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Seccmd:Command
{
	Seccmd()
        :Command("Computes secant of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Cotcmd:Command
{
	Cotcmd()
        :Command("Computes cotangent of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Tancmd:Command
{
	Tancmd()
        :Command("Computes tangent of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Coscmd:Command
{
	Coscmd()
        :Command("Computes cosine of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Sincmd:Command
{
	Sincmd()
        :Command("Computes sine of an angle in radians.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Logcmd:Command
{
	Logcmd()
		:Command("Computes log base x of y.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Powcmd:Command
{
	Powcmd()
		:Command("Computes x raised to the power of y.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct HMeancmd:Command
{
	HMeancmd()
		:Command("Evaluates the harmonic mean of a list of numbers.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct GMeancmd:Command
{
	GMeancmd()
		:Command("Evaluates the geometric mean of a list of numbers.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct AMeancmd:Command
{
	AMeancmd()
		:Command("Evaluates the arithmetic mean of a list of numbers.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Baseconvcmd:Command
{
	Baseconvcmd()
		:Command("<number> <m> <n>\nConverts the number from base m to n.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Factorcmd:Command
{
	Factorcmd()
		:Command("Prime factors a number.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Primecmd:Command
{
	Primecmd()
		:Command("Checks if a number is prime or composite.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Gcdcmd:Command
{
	Gcdcmd()
		:Command("Computes the greatest common divisor of a sequence of numbers.")
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Modulocmd:Command
{
	Modulocmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Quotientcmd:Command
{
	Quotientcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Prefixcmd:Command
{
	Prefixcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

struct Productcmd:Command
{
	Productcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message &og, const std::string* args, std::size_t size);
};

struct Sumcmd:Command
{
	Sumcmd(std::string desc)
		:Command(move(desc))
	{}
	std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
};

#endif

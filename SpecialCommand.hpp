// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>.

#ifndef SPECIALCOMMAND_HPP_
#define SPECIALCOMMAND_HPP_
#include<functional>
#include"CommandParser.hpp"
// Variadic List Command
// Applies the same function for every value in a list
struct VListCommand:public Command
{
	std::function<std::string(const std::string&)>op;
	VListCommand(std::function<std::string(const std::string&)>op, std::string desc);
	VListCommand()=default;
	VListCommand(const VListCommand& cmd)=default;
	VListCommand(VListCommand&& cmd)=default;
	VListCommand& operator=(const VListCommand& cmd)=default;
	VListCommand& operator=(VListCommand&& cmd)=default;
	virtual std::string operator()(const dpp::message& og, const std::string* args, std::size_t size);
	virtual~VListCommand()=default;
};

struct VListMathFunction:public VListCommand
{
	VListMathFunction(std::function<double(double)>op, std::string desc);
	VListMathFunction()=default;
	VListMathFunction(const VListMathFunction& cmd)=default;
	VListMathFunction(VListMathFunction&& cmd)=default;
	VListMathFunction& operator=(const VListMathFunction& cmd)=default;
	VListMathFunction& operator=(VListMathFunction&& cmd)=default;
	virtual~VListMathFunction()=default;
};
#endif

// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>.

#include"algo/str.hpp"
#include"SpecialCommand.hpp"
using namespace std;
struct MathToStrFunc
{
	function<double(double)>func;
	MathToStrFunc()=default;
	MathToStrFunc(function<double(double)>f)
		:func(move(f)) {}
	MathToStrFunc(const MathToStrFunc& cmd)=default;
	MathToStrFunc(MathToStrFunc&& cmd)=default;
	MathToStrFunc& operator=(const MathToStrFunc& cmd)=default;
	MathToStrFunc& operator=(MathToStrFunc&& cmd)=default;
	string operator()(const string &s)const
	{
		return tostr(func(stod(s)));
	}
};
VListCommand::VListCommand(function<string(const string&)>op, string desc)
	:op(move(op)), Command(move(desc)){}
string VListCommand::operator()(const dpp::message& og, const string* args, size_t size)const
{
	string retstr;
	for(unsigned long i=0;i<size;i++)
	{
		retstr += op(args[i]);
		retstr += '\n';
	}
	return retstr;
}
VListMathFunction::VListMathFunction(function<double(double)>op, string desc)
	:VListCommand(MathToStrFunc(move(op)), move(desc)){}

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
	virtual std::string operator()(const dpp::message& og, const std::string* args, std::size_t size)const=0;
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

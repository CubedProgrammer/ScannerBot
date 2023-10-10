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

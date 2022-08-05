#include"algo/str.hpp"
#include"cmds.hpp"

using std::size_t;
using std::stod;
using std::string;
using namespace dpp;

string Prefixcmd::operator()(const message& og, const string* args, size_t size)const
{
	if(size > 0)
	{
		const auto &author = og.author;
		return"New prefix has been set.";
	}
	else
		return"";
}

string Productcmd::operator()(const message& og, const string* args, size_t size)const
{
	double product = 1;
	for(size_t i=0;i<size;i++)
		product *= tonum(args[i]);
	return tostr(product);
}

string Sumcmd::operator()(const message& og, const string* args, size_t size)const
{
	double sum = 0;
	for(size_t i=0;i<size;i++)
		sum += tonum(args[i]);
	return tostr(sum);
}

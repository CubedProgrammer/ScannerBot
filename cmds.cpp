#include"cmds.hpp"

using std::size_t;
using std::stod;
using std::string;

string Productcmd::operator()(const string* args, size_t size)const
{
	double product = 1;
	for(size_t i=0;i<size;i++)
	{
		product *= stod(args[i]);
	}
	return std::to_string(product);
}

string Sumcmd::operator()(const string* args, size_t size)const
{
	double sum = 0;
	for(size_t i=0;i<size;i++)
	{
		sum += stod(args[i]);
	}
	return std::to_string(sum);
}

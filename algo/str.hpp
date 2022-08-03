#ifndef ALGO_STR_HPP_
#define ALGO_STR_HPP_
#include<sstream>

double tonum(const std::string& str)
{
	if(str.size() > 0 && str[0] == '0')
	{
		if(str.size() > 1 && str[1] == 'x')
			return std::stol(str.substr(2), nullptr, 16);
		else if(str.size() > 1 && str[1] == 'b')
			return std::stol(str.substr(2), nullptr, 2);
		else if(str.size() > 1 && str[1] == '.')
			return std::stod(str);
		else
			return std::stol(str.substr(1), nullptr, 8);
	}
	else
		return std::stod(str);
}

template<typename T>
std::string tostr(T&& x)
{
	std::ostringstream oss;
	oss << x;
	return oss.str();
}

#endif

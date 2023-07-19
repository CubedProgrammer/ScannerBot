#ifndef ALGO_STR_HPP_
#define ALGO_STR_HPP_
#include<algorithm>
#include<sstream>

std::string numstr(long num, int base)
{
    static const char digits[] = "0123456789abcdefghijklmnopqrstuvwxyz";
    std::ostringstream oss;
    bool nega = false;
    if(num < 0)
    {
        num *= -1;
        nega = true;
    }
    while(num > 0)
    {
        oss << digits[num % base];
        num /= base;
    }
    if(nega)
        oss << '-';
    std::string str = oss.str();
    std::reverse(str.begin(), str.end());
    return str;
}

long toint(const std::string& str)
{
	if(str.size() > 0 && str[0] == '0')
	{
		if(str.size() > 1 && str[1] == 'x')
			return std::stol(str.substr(2), nullptr, 16);
		else if(str.size() > 1 && str[1] == 'b')
			return std::stol(str.substr(2), nullptr, 2);
		else
			return std::stol(str.substr(1), nullptr, 8);
	}
	else
		return std::stol(str);
}

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

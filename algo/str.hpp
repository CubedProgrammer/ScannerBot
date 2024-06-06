// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#ifndef ALGO_STR_HPP_
#define ALGO_STR_HPP_
#include<algorithm>
#include<cctype>
#include<cstdint>
#include<sstream>
#include<type_traits>
#include<vector>
#include<dpp/dpp.h>

inline std::vector<std::string>split(const std::string& str, const std::string& pattern)
{
    using namespace std;
    vector<string>vec;
    string tmp;
    for(char c : str)
    {
        if(pattern.find(c) == string::npos)
            tmp.push_back(c);
        else if(tmp.size() > 0)
        {
            vec.push_back(tmp);
            tmp.clear();
        }
    }
    if(tmp.size() > 0)
        vec.push_back(tmp);
    return vec;
}

inline bool are_equal_ignore_case(const std::string& l, const std::string& r)
{
    using std::size_t;
    using std::tolower;
    if(l.size() == r.size())
    {
        bool eq = true;
        for(size_t i = 0; i < l.size(); ++i)
            eq = eq && tolower(l[i]) == tolower(r[i]);
        return eq;
    }
    else
        return false;
}

namespace nlohmann
{
	template<typename OS>
#if __cplusplus >= 202002L
		requires std::is_base_of_v<std::ostream, OS>
	OS&
#else
	std::enable_if_t<std::is_base_of_v<std::ostream, OS>, OS>&
#endif
	operator<<(OS& os, const nlohmann::json& j)
	{
		return static_cast<OS&>(static_cast<std::ostream&>(os) << j);
	}
}

template<typename OS>
#if __cplusplus >= 202002L
	requires std::is_base_of_v<std::ostream, OS>
OS&
#else
std::enable_if_t<std::is_base_of_v<std::ostream, OS>, OS>&
#endif
operator<<(OS& os, const dpp::user& u)
{
	return static_cast<OS&>(os << std::uint64_t(u.id) << ' ' << u.username << ' ' << u.get_avatar_url());
}

#if __cplusplus >= 202002L
bool badstr(const std::string& msg, auto&& bad)
#else
template<typename T>
bool badstr(const std::string& msg, T&& bad)
#endif
{
	auto tokens = split(msg, "?!,. \n");
	bool isbad = false;
	for(std::string s:bad)
	{
        if(std::find_if(tokens.begin(), tokens.end(), [&s](const std::string& x){return are_equal_ignore_case(s, x);}) != tokens.end())
            isbad = true;
	}
	return isbad;
}

inline std::string numstr(long num, int base)
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

inline long toint(const std::string& str)
{
	if(str.size() > 1 && str[0] == '0')
	{
		if(str.size() > 2 && str[1] == 'x')
			return std::stol(str.substr(2), nullptr, 16);
		else if(str.size() > 2 && str[1] == 'b')
			return std::stol(str.substr(2), nullptr, 2);
		else
			return std::stol(str.substr(1), nullptr, 8);
	}
	else
		return std::stol(str);
}

inline double tonum(const std::string& str)
{
	if(str.size() > 1 && str[0] == '0')
	{
		if(str.size() > 2 && str[1] == 'x')
			return std::stol(str.substr(2), nullptr, 16);
		else if(str.size() > 2 && str[1] == 'b')
			return std::stol(str.substr(2), nullptr, 2);
		else if(str.size() > 2 && str[1] == '.')
			return std::stod(str);
		else
			return std::stol(str.substr(1), nullptr, 8);
	}
	else
		return std::stod(str);
}
static_assert(std::is_convertible_v<nlohmann::json, dpp::user>);
template<typename T>
std::string tostr(T&& x)
{
	std::ostringstream oss;
	oss.precision(16);
	oss << x;
	return oss.str();
}

#endif

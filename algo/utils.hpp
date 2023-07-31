// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#ifndef utils_HPP_INCLUDED
#define utils_HPP_INCLUDED
#include<algorithm>
#include<chrono>
#include<functional>
#include<map>

extern std::map<std::chrono::time_point<std::chrono::system_clock>, std::function<void()>>registeredTimeout;

void startTimeout();

template<typename IterX, typename IterY>
bool has_any(IterX first, IterX last, IterY setfirst, IterY setlast)
{
    using namespace std;
    auto pred = [&first, &last](auto x)
    {
        return find(first, last, x) != last;
    };
    return any_of(setfirst, setlast, pred);
}

template<typename IterX, typename IterY>
bool has_all(IterX first, IterX last, IterY setfirst, IterY setlast)
{
    using namespace std;
    auto pred = [&first, &last](auto x)
    {
        return find(first, last, x) != last;
    };
    return all_of(setfirst, setlast, pred);
}

template<typename Rep, typename Period, typename Func>
void setTimeout(Func f, std::chrono::duration<Rep, Period>dura)
{
    using namespace std::chrono;
    auto tm = duration_cast<system_clock::duration>(dura);
    auto happen = system_clock::now() + tm;
    registeredTimeout[happen] = std::move(f);
}

#endif

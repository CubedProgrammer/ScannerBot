// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#include<thread>
#include"utils.hpp"

std::map<std::chrono::time_point<std::chrono::system_clock>, std::function<void()>>registeredTimeout;

void startTimeout()
{
    using namespace std::chrono;
    using namespace std::chrono_literals;
    auto func = []()
    {
        for(;;)
        {
            auto curr = system_clock::now();
            auto it = registeredTimeout.lower_bound(curr);
            if(it == registeredTimeout.end())
                registeredTimeout.clear();
            else if(it->first - curr < 1min)
                it->second();
            std::this_thread::sleep_for(1min);
        }
    };
    std::thread th(func);
    th.detach();
}

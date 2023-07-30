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
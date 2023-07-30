#ifndef utils_HPP_INCLUDED
#define utils_HPP_INCLUDED
#include<chrono>
#include<functional>
#include<map>

extern std::map<std::chrono::time_point<std::chrono::system_clock>, std::function<void()>>registeredTimeout;
void startTimeout();

template<typename Rep, typename Period, typename Func>
void setTimeout(Func f, std::chrono::duration<Rep, Period>dura)
{
    using namespace std::chrono;
    auto tm = duration_cast<system_clock::duration>(dura);
    auto happen = system_clock::now() + tm;
    registeredTimeout[happen] = std::move(f);
}

#endif

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

#include<cmath>
#include<vector>

using std::vector;
using std::pair;

bool checkprime(long num)
{
    // hardcode small primes
    bool p = num > 1 && (num == 2 || num == 3 || num == 5 || num == 7|| num == 11 || num == 13 || num == 17 || num == 19 || num == 23 || num >= 29);
    if(p)
    {
        long stop = std::sqrt(num);
        for(long i = 5; i <= stop; i += 6)
        {
            if(num % i == 0 || num % (i + 2) == 0)
            {
                p = false;
                break;
            }
        }
    }
    return p;
}

vector<pair<long,int>>pfactor(long num)
{
    int cnt = 0;
    vector<pair<long,int>>factors;
    while((num & 1) == 0)
        num >>= 1, ++cnt;
    if(cnt > 0)
        factors.emplace_back(2, cnt);
    long stop = std::sqrt(num);
    for(long i = 3; i <= stop; i += 2)
    {
        cnt = 0;
        while(num % i == 0)
            num /= i, ++cnt;
        if(cnt > 0)
            factors.emplace_back(i, cnt);
    }
    if(num > 1)
        factors.emplace_back(num, 1);
    return factors;
}
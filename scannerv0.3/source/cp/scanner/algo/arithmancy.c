#include<ctype.h>
#include<stdio.h>
#include<string.h>
int pythagorean_arithmancy(const char *name);
int digital_root(long unsigned n);
int pythagorean_arithmancy(const char *name)
{
    char c;
    int character = 0, heart = 0, social = 0;
    int tmp;
    for(size_t i = 0; name[i] != 0; i++)
    {
        c = tolower(name[i]);
        if(c >= 'a' && c <= 'z')
        {
            tmp = (c - 'a') % 9 + 1;
            character += tmp;
            if(strchr("aeiou", c) != NULL)
                heart += tmp;
            else
                social += tmp;
        }
    }
    return digital_root(character) * 100 + digital_root(heart) * 10 + digital_root(social);
}
int digital_root(long unsigned n)
{
    if(n == 0)
        return 0;
    else if(n % 9 == 0)
        return 9;
    else
        return n % 9;
}

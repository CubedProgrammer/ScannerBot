# ScannerBot
Offers moderation commands, information commands, and mathematical commands.
## Compilation
C++17 or above is required. This program uses D++, https://github.com/brainboxdotcc/DPP

Compile all the .cpp files and link them, nothing fancy required.

Files will be created in the present working directory of the process, so make sure that is the same every time.
## Terminology
Some of the terminology used in commands.

Autoroles are roles that are automatically given to users as they join the server.

Selfroles are roles that members can give themselves.
These roles usually only offer aesthetic changes.

The mute role is a role given to users to prevent them from sending messages.

The mute words or mutables (Yes I know mutable means mutate-able and not mute-able) are words that if said, will be muted by the bot.

A macro is a string that expands to another string in commands.
The macro has a name and expansion, if the name is found by itself surrounded by whitespace, the program will treat it as its expansion.
For example, a macro is PI=3.1415926535897932.
If PI is used in a command, it will be treated as if the user typed out 3.1415926535897932.
## Permissions
Most commands that do require permissions require the manage guild permission.

Kick and ban command obviously require the kick and ban permissions, as do the giverole and takerole commands.

Purge requires the manage message permission.
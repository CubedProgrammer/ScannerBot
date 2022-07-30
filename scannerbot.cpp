#include<cstddef>
#include<cstdlib>
#include<filesystem>
#include<fstream>
#include<iostream>
#include<sstream>
#include<unordered_map>
#include<vector>
#include<nlohmann/json.hpp>
#include<dpp/dpp.h>

using std::byte;
using std::endl;
using std::cin;
using std::cout;
using std::ostream;
using std::ostringstream;
using std::string;
using nlohmann::json;
using guildmap = std::unordered_map<dpp::snowflake,json>;

constexpr byte VERSION_MAJOR = (byte)1;
constexpr byte VERSION_MINOR = (byte)0;
constexpr byte VERSION_PATCH = (byte)0;

ostream &operator<<(ostream &os, byte b)
{
    os << static_cast<unsigned>(b);
    return os;
}

guildmap load_guilds()
{
	using namespace std::filesystem;
	path curr(".");
	guildmap map;
	dpp::snowflake gid = 0;
	for(path p : directory_iterator(curr))
	{
		if(p.extension() == ".json")
		{
			gid = std::stoul(p.string().substr(0, p.string().size() - 5),nullptr, 16);
			std::ifstream ifs(p);
			map[gid] = json::parse(ifs);
		}
	}
	return map;
}

string get_version_string(byte major, byte minor, byte patch)
{
    ostringstream oss;
    oss << major << '.' << minor;
    if(static_cast<bool>(patch))
        oss << '.' << patch;
    return oss.str();
}

void save(const guildmap &map);

int main(int argl,char**argv)
{
    using namespace dpp;
    string bot_token = getenv("SBTOKEN"), verstr = get_version_string(VERSION_MAJOR, VERSION_MINOR, VERSION_PATCH);
    guildmap guilds = load_guilds();
    cluster scannerbot(bot_token, i_all_intents);
    auto selfuser = scannerbot.current_user_get_sync();
    string mention = selfuser.get_mention();
    cout << selfuser.id << ' ' << selfuser.username << endl;
    for(const auto &p : guilds)
    	cout << p.first << ' ' << p.second["pref"] << endl;
    auto evtr = [&guilds,&mention,&selfuser,&verstr](const message_create_t &evt)
    {
        if(selfuser.id != evt.msg.author.id)
        {
            const auto &msg = evt.msg.content;
#if __cplusplus >= 202002L
            if(!guilds.contains(evt.msg.guild_id))
#else
            if(guilds.end() == guilds.find(evt.msg.guild_id))
#endif
            {
            	cout << "Added guild " << evt.msg.guild_id << endl;
            	guilds[evt.msg.guild_id]["pref"] = "--";
			}
#if __cplusplus >= 202002L
            if(msg.starts_with(mention))
#else
            auto itx = mention.size() > msg.size() ? mention.cbegin() : std::mismatch(mention.cbegin(), mention.cend(), msg.cbegin()).first;
            if(itx == mention.cend())
#endif
            {
            }
        }
    };
    scannerbot.on_message_create(evtr);
    scannerbot.start();
    cout << "Scanner Bot v" << verstr << " has begun." << endl;
    cout.flush();
    cin.get();
    save(guilds);
    return 0;
}

void save(const guildmap &map)
{
	string fname;
	ostringstream oss;
    for(const auto &[gid, dat] : map)
    {
    	oss.str("");
    	oss << std::hex << gid;
    	fname = oss.str();
    	fname += ".json";
    	std::ofstream ofs(fname);
    	ofs << dat << endl;
	}
}

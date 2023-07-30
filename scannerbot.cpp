#include<cstddef>
#include<cstdlib>
#include<filesystem>
#include<fstream>
#include<iostream>
#include<stdexcept>
#include<sstream>
#include<unordered_map>
#include<vector>
#include<nlohmann/json.hpp>
#include<dpp/dpp.h>
#include"algo/discord.hpp"
#include"cmds.hpp"

using std::byte;
using std::endl;
using std::cin;
using std::cout;
using std::make_unique;
using std::ostream;
using std::ostringstream;
using std::string;
using std::uint64_t;
using std::vector;
using nlohmann::json;

constexpr byte VERSION_MAJOR = (byte)1;
constexpr byte VERSION_MINOR = (byte)0;
constexpr byte VERSION_PATCH = (byte)0;
guildmap allguilds;
gdatamap gdata;
extern std::chrono::time_point<std::chrono::system_clock>lastfetch;

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
	string fname;
	dpp::snowflake gid = 0;
	for(path p : directory_iterator(curr))
	{
		if(p.extension() == ".json")
		{
			cout << "Loaded " << p.string() << endl;
			fname = p.filename();
			gid = std::stoul(fname.substr(0, fname.size() - 5), nullptr, 16);
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
    using namespace std::string_literals;
    char *tokenptr = getenv("SBTOKEN");
    if(tokenptr == nullptr)
		throw std::logic_error("Environment variable SBTOKEN is undefined.");
    string bot_token = tokenptr, verstr = get_version_string(VERSION_MAJOR, VERSION_MINOR, VERSION_PATCH);
    allguilds = load_guilds();
    auto &guilds = allguilds;
    cluster scannerbot(bot_token, i_all_intents);
    auto selfuser = scannerbot.current_user_get_sync();
    string mention = selfuser.get_mention();
    cout << selfuser.id << ' ' << selfuser.username << endl;
    for(const auto &p : guilds)
    	cout << p.first << ' ' << p.second["pref"] << endl;
    ptrCommand purgecmd(new Purgecmd());
    ptrCommand takerolecmd(new Takerolecmd());
    ptrCommand giverolecmd(new Giverolecmd());
    ptrCommand macrolscmd(new Macrolscmd());
    ptrCommand undefcmd(new Undefcmd());
    ptrCommand definecmd(new Definecmd());
    ptrCommand muterolecmd(new Muterolecmd());
    ptrCommand bancmd(new Bancmd());
    ptrCommand kickcmd(new Kickcmd());
    ptrCommand dloptionscmd(new DLOptionscmd());
    ptrCommand infocmd(new Infocmd());
    ptrCommand selfrolecmd(new Selfrolecmd());
    ptrCommand toggleselfrolecmd(new Toggleselfrolecmd());
    ptrCommand autorolecmd(new Autorolecmd());
    ptrCommand atan2cmd(new Atan2cmd());
    ptrCommand atancmd(new Atancmd());
    ptrCommand acoscmd(new Acoscmd());
    ptrCommand asincmd(new Asincmd());
    ptrCommand csccmd(new Csccmd());
    ptrCommand seccmd(new Seccmd());
    ptrCommand cotcmd(new Cotcmd());
    ptrCommand tancmd(new Tancmd());
    ptrCommand coscmd(new Coscmd());
    ptrCommand sincmd(new Sincmd());
    ptrCommand logcmd(new Logcmd());
    ptrCommand powcmd(new Powcmd());
    ptrCommand hmeancmd(new HMeancmd());
    ptrCommand gmeancmd(new GMeancmd());
    ptrCommand ameancmd(new AMeancmd());
    ptrCommand baseconvcmd(new Baseconvcmd());
    ptrCommand factorcmd(new Factorcmd());
    ptrCommand primecmd(new Primecmd());
    ptrCommand gcdcmd(new Gcdcmd());
    ptrCommand modulocmd(new Modulocmd("Remainder of first number divided by second."));
    ptrCommand quotientcmd(new Quotientcmd("First number divided by second."));
    ptrCommand prefixcmd(new Prefixcmd("Sets the prefix for the bot."));
    ptrCommand productcmd(new Productcmd("Computes the product of all numbers given."));
    ptrCommand sumcmd(new Sumcmd("Computes the sum of all numbers given."));
    vector<string>cmdnamevec{"purge", "takerole", "giverole", "macrols", "undef", "define", "muterole", "ban", "kick", "dloptions", "info", "selfrole", "toggleselfrole", "autorole", "atan2", "atan", "acos", "asin", "csc", "sec", "cot", "tan", "cos", "sin", "log", "pow", "harmean", "geomean", "mean", "baseconv", "factor", "prime", "gcd", "remainder", "quotient", "prefix", "product", "sum"};
    vector<ptrCommand>cmdvec;
    cmdvec.push_back(move(purgecmd));
    cmdvec.push_back(move(takerolecmd));
    cmdvec.push_back(move(giverolecmd));
    cmdvec.push_back(move(macrolscmd));
    cmdvec.push_back(move(undefcmd));
    cmdvec.push_back(move(definecmd));
    cmdvec.push_back(move(muterolecmd));
    cmdvec.push_back(move(bancmd));
    cmdvec.push_back(move(kickcmd));
    cmdvec.push_back(move(dloptionscmd));
    cmdvec.push_back(move(infocmd));
    cmdvec.push_back(move(selfrolecmd));
    cmdvec.push_back(move(toggleselfrolecmd));
    cmdvec.push_back(move(autorolecmd));
    cmdvec.push_back(move(atan2cmd));
    cmdvec.push_back(move(atancmd));
    cmdvec.push_back(move(acoscmd));
    cmdvec.push_back(move(asincmd));
    cmdvec.push_back(move(csccmd));
    cmdvec.push_back(move(seccmd));
    cmdvec.push_back(move(cotcmd));
    cmdvec.push_back(move(tancmd));
    cmdvec.push_back(move(coscmd));
    cmdvec.push_back(move(sincmd));
    cmdvec.push_back(move(logcmd));
    cmdvec.push_back(move(powcmd));
    cmdvec.push_back(move(hmeancmd));
    cmdvec.push_back(move(gmeancmd));
    cmdvec.push_back(move(ameancmd));
    cmdvec.push_back(move(baseconvcmd));
    cmdvec.push_back(move(factorcmd));
    cmdvec.push_back(move(primecmd));
    cmdvec.push_back(move(gcdcmd));
    cmdvec.push_back(move(modulocmd));
    cmdvec.push_back(move(quotientcmd));
    cmdvec.push_back(move(prefixcmd));
    cmdvec.push_back(move(productcmd));
    cmdvec.push_back(move(sumcmd));
    CommandParser parser(verstr, cmdnamevec, cmdvec);
    auto memjoin = [&scannerbot, &guilds](const guild_member_add_t &evt)
    {
        json& guild_dat = guilds[evt.added.guild_id];
        for(const auto& roleid : guild_dat["autoroles"])
            scannerbot.guild_member_add_role(evt.added.guild_id, evt.added.user_id, snowflake((uint64_t)roleid));
    };
    auto evtr = [&parser,&guilds,&mention,&selfuser,&verstr](const message_create_t &evt)
    {
        if(selfuser.id != evt.msg.author.id)
        {
            const auto &msg = evt.msg.content;
            string sendstr;
#if __cplusplus >= 202002L
            if(!guilds.contains(evt.msg.guild_id))
#else
            if(guilds.end() == guilds.find(evt.msg.guild_id))
#endif
            {
            	cout << "Added guild " << evt.msg.guild_id << endl;
            	guilds[evt.msg.guild_id]["pref"] = "--";
                guilds[evt.msg.guild_id]["autoroles"] = json::array();
                guilds[evt.msg.guild_id]["selfroles"] = json::array();
            	guilds[evt.msg.guild_id]["muterole"] = 0;
            	guilds[evt.msg.guild_id]["macros"] = json::object();
                json &macroobj = guilds[evt.msg.guild_id]["macros"];
                macroobj["PI"] = "3.1415926535897932";
                macroobj["E"] = "2.7182818245904524";
                macroobj["SQRT2"] = "1.41421356237309505";
                macroobj["SQRT3"] = "1.73205080756887729";
                macroobj["prefreset"] = "prefix --";
			}
            const string &pref = guilds[evt.msg.guild_id]["pref"];
#if __cplusplus >= 202002L
            if(msg.starts_with(mention))
#else
            auto itx = mention.size() > msg.size() ? mention.cbegin() : std::mismatch(mention.cbegin(), mention.cend(), msg.cbegin()).first;
            if(itx == mention.cend())
#endif
            	sendstr = parser(evt.msg, msg.substr(mention.size()));
#if __cplusplus >= 202002L
            if(msg.starts_with(pref))
#else
            auto ity = pref.size() > msg.size() ? pref.cbegin() : std::mismatch(pref.cbegin(), pref.cend(), msg.cbegin()).first;
            if(ity == pref.cend())
#endif
            	sendstr = parser(evt.msg, msg.substr(pref.size()));
            if(sendstr.size() > 0)
                evt.send(sendstr);
        }
    };
    scannerbot.on_message_create(evtr);
    scannerbot.on_guild_member_add(memjoin);
    scannerbot.start();
    cout << "Scanner Bot v" << verstr << " has begun." << endl;
    lastfetch = std::chrono::system_clock::now();
    fetch_guilds(scannerbot);
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

// This file is part of ScannerBot.
// Copyright (C) 2018-2023, github.com/CubedProgrammer, owner of said account.

// ScannerBot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// ScannerBot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

// You should have received a copy of the GNU General Public License along with ScannerBot. If not, see <https://www.gnu.org/licenses/>. 

#include<cstddef>
#include<cstdlib>
#include <dpp/message.h>
#include<filesystem>
#include<fstream>
#include<iostream>
#include<stdexcept>
#include<sstream>
#include <sys/types.h>
#include<unordered_map>
#include<vector>
#include<nlohmann/json.hpp>
#include<dpp/dpp.h>
#include"algo/discord.hpp"
#include"algo/str.hpp"
#include"algo/utils.hpp"
#include"cmds.hpp"
#include"SpecialCommand.hpp"

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
constexpr byte VERSION_MINOR = (byte)1;
constexpr byte VERSION_PATCH = (byte)0;
constexpr const char* VERSION_NAME = "The Mathematics Update";
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
            // backwards compatibility, remove this in the next patch
            if(map[gid]["mutetime"].is_number())
            {
                auto arr = json::array();
                arr.push_back(map[gid]["mutetime"]);
                map[gid]["mutetime"] = std::move(arr);
            }
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
    oss << ' ' << VERSION_NAME;
    return oss.str();
}

void save(const guildmap &map);

dpp::task<void>run()
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
    auto selfuserVariant = co_await scannerbot.co_current_user_get();
    auto selfuser = get<user_identified>(selfuserVariant.value);
    string mention = selfuser.get_mention();
    cout << selfuser.id << ' ' << selfuser.username << endl;
    for(const auto &p : guilds)
    	cout << p.first << ' ' << p.second["pref"] << endl;
    ptrCommand dlmessagecmd(new DLMessagecmd());
    ptrCommand findusercmd(new Findusercmd());
    ptrCommand recallmsgcmd(new RecallMessagecmd());
    ptrCommand savemsgcmd(new SaveMessagecmd());
    ptrCommand randcmd(new Randcmd());
    ptrCommand atanhcmd(new VListMathFunction((double(*)(double))std::atanh, "Inverse hyperbolic tangent."));
    ptrCommand acoshcmd(new VListMathFunction((double(*)(double))std::acosh, "Inverse hyperbolic cosine."));
    ptrCommand asinhcmd(new VListMathFunction((double(*)(double))std::asinh, "Inverse hyperbolic sine."));
    ptrCommand tanhcmd(new VListMathFunction((double(*)(double))std::tanh, "Hyperbolic tangent."));
    ptrCommand coshcmd(new VListMathFunction((double(*)(double))std::cosh, "Hyperbolic cosine."));
    ptrCommand sinhcmd(new VListMathFunction((double(*)(double))std::sinh, "Hyperbolic sine."));
    ptrCommand epochcmd(new Epochcmd());
    ptrCommand mutecmd(new Mutecmd());
    ptrCommand mutetimecmd(new Mutetimecmd());
    ptrCommand mutablecmd(new Mutablecmd());
    ptrCommand allrolecmd(new Allrolecmd());
    ptrCommand anyrolecmd(new Anyrolecmd());
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
    vector<string>cmdnamevec{"dlmsg", "finduser", "recallmsg", "savemsg", "rand", "atanh", "acosh", "asinh", "tanh", "cosh", "sinh", "epoch", "mute",
    	"mutetime", "mutable", "allrole", "anyrole", "purge", "takerole", "giverole", "macrols", "undef", "define", "muterole", "ban", "kick",
		"dloptions", "info", "selfrole", "toggleselfrole", "autorole", "atan2", "atan", "acos", "asin", "csc", "sec", "cot", "tan", "cos", "sin",
		"log", "pow", "harmean", "geomean", "mean", "baseconv", "factor", "prime", "gcd", "remainder", "quotient", "prefix", "product", "sum"};
    vector<ptrCommand>cmdvec;
    cmdvec.push_back(std::move(dlmessagecmd));
    cmdvec.push_back(std::move(findusercmd));
    cmdvec.push_back(std::move(recallmsgcmd));
    cmdvec.push_back(std::move(savemsgcmd));
    cmdvec.push_back(std::move(randcmd));
    cmdvec.push_back(std::move(atanhcmd));
    cmdvec.push_back(std::move(acoshcmd));
    cmdvec.push_back(std::move(asinhcmd));
    cmdvec.push_back(std::move(tanhcmd));
    cmdvec.push_back(std::move(coshcmd));
    cmdvec.push_back(std::move(sinhcmd));
    cmdvec.push_back(std::move(epochcmd));
    cmdvec.push_back(std::move(mutecmd));
    cmdvec.push_back(std::move(mutetimecmd));
    cmdvec.push_back(std::move(mutablecmd));
    cmdvec.push_back(std::move(allrolecmd));
    cmdvec.push_back(std::move(anyrolecmd));
    cmdvec.push_back(std::move(purgecmd));
    cmdvec.push_back(std::move(takerolecmd));
    cmdvec.push_back(std::move(giverolecmd));
    cmdvec.push_back(std::move(macrolscmd));
    cmdvec.push_back(std::move(undefcmd));
    cmdvec.push_back(std::move(definecmd));
    cmdvec.push_back(std::move(muterolecmd));
    cmdvec.push_back(std::move(bancmd));
    cmdvec.push_back(std::move(kickcmd));
    cmdvec.push_back(std::move(dloptionscmd));
    cmdvec.push_back(std::move(infocmd));
    cmdvec.push_back(std::move(selfrolecmd));
    cmdvec.push_back(std::move(toggleselfrolecmd));
    cmdvec.push_back(std::move(autorolecmd));
    cmdvec.push_back(std::move(atan2cmd));
    cmdvec.push_back(std::move(atancmd));
    cmdvec.push_back(std::move(acoscmd));
    cmdvec.push_back(std::move(asincmd));
    cmdvec.push_back(std::move(csccmd));
    cmdvec.push_back(std::move(seccmd));
    cmdvec.push_back(std::move(cotcmd));
    cmdvec.push_back(std::move(tancmd));
    cmdvec.push_back(std::move(coscmd));
    cmdvec.push_back(std::move(sincmd));
    cmdvec.push_back(std::move(logcmd));
    cmdvec.push_back(std::move(powcmd));
    cmdvec.push_back(std::move(hmeancmd));
    cmdvec.push_back(std::move(gmeancmd));
    cmdvec.push_back(std::move(ameancmd));
    cmdvec.push_back(std::move(baseconvcmd));
    cmdvec.push_back(std::move(factorcmd));
    cmdvec.push_back(std::move(primecmd));
    cmdvec.push_back(std::move(gcdcmd));
    cmdvec.push_back(std::move(modulocmd));
    cmdvec.push_back(std::move(quotientcmd));
    cmdvec.push_back(std::move(prefixcmd));
    cmdvec.push_back(std::move(productcmd));
    cmdvec.push_back(std::move(sumcmd));
    CommandParser parser(verstr, cmdnamevec, cmdvec);
    auto memjoin = [&scannerbot](const guild_member_add_t &evt)
    {
        json& guild_dat = guilds[evt.added.guild_id];
        for(const auto& roleid : guild_dat["autoroles"])
            scannerbot.guild_member_add_role(evt.added.guild_id, evt.added.user_id, snowflake((uint64_t)roleid));
    };
    auto memleave = [&scannerbot](const guild_member_remove_t &evt)
    {
        json& guild_dat = guilds[evt.removing_guild.id];
        const guild& g = evt.removing_guild;
        const user& u = evt.removed;
        if(guild_dat["exitmsg"])
        {
            message m(g.system_channel_id, u.username + " has left.");
            scannerbot.message_create(m);
        }
    };
    auto roledel = [](const guild_role_delete_t &evt)
	{
    	snowflake gid = evt.deleting_guild.id, rid = evt.role_id;
    	std::cout << static_cast<uint64_t>(rid) << std::endl;
    	auto& gdat = guilds[gid];
    	if(gdat["muterole"] == static_cast<uint64_t>(rid))
    		gdat["muterole"] = nullptr;
    	std::size_t ind = -1;
    	for(std::size_t i=0;i<gdat["autoroles"].size();i++)
		{
    		if(gdat["autoroles"][i] == static_cast<uint64_t>(rid))
    			ind = i;
		}
    	if(ind != -1)
    		gdat["autoroles"].erase(ind);
    	ind = -1;
    	for(std::size_t i=0;i<gdat["selfroles"].size();i++)
		{
    		if(gdat["selfroles"][i] == static_cast<uint64_t>(rid))
    			ind = i;
		}
    	if(ind != -1)
    		gdat["selfroles"].erase(ind);
	};
    auto maybeRespond = [&scannerbot](const message &msg)
    {
        if(badstr(msg.content, guilds[msg.guild_id]["mutable"]))
        {
        	using namespace std::chrono;
            message m(msg.channel_id, "You used a bad word, you shall now be muted.");
            snowflake gid = msg.guild_id, uid = msg.author.id, rid = (std::uint64_t)guilds[msg.guild_id]["muterole"];
            m.message_reference = message::message_ref{mrt_default, msg.id, msg.channel_id, msg.guild_id, false};
            int mins = get_mute_time(guilds[msg.guild_id], uid);
            give_role_temp(scannerbot, gid, uid, rid, duration_cast<system_clock::duration>(minutes(mins)));
            scannerbot.guild_member_move(snowflake(0), gid, uid);
            scannerbot.message_create(m);
        }
    };
    auto edited = [&scannerbot,&maybeRespond](const message_update_t &evt)
    {
        maybeRespond(evt.msg);
	};
    auto evtr = [&scannerbot,&parser,&mention,&selfuser,&maybeRespond](const message_create_t &evt) -> task<void>
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
            	guilds[evt.msg.guild_id]["muterole"] = nullptr;
            	guilds[evt.msg.guild_id]["macros"] = json::object();
            	guilds[evt.msg.guild_id]["mutable"] = json::array();
            	guilds[evt.msg.guild_id]["mutetime"] = json::array();
                guilds[evt.msg.guild_id]["muteoff"] = json::object();
            	guilds[evt.msg.guild_id]["exitmsg"] = false;
            	guilds[evt.msg.guild_id]["temprole"] = json::array();
            	guilds[evt.msg.guild_id]["saved_message_ids"] = json::object();
            	guilds[evt.msg.guild_id]["saved_messages"] = json::object();
                guilds[evt.msg.guild_id]["mutetime"].push_back(30);
                json &macroobj = guilds[evt.msg.guild_id]["macros"];
                macroobj["PI"] = "3.1415926535897932";
                macroobj["E"] = "2.7182818245904524";
                macroobj["SQRT2"] = "1.41421356237309505";
                macroobj["SQRT3"] = "1.73205080756887729";
                macroobj["prefreset"] = "prefix --";
			}
            maybeRespond(evt.msg);
            const string &pref = guilds[evt.msg.guild_id]["pref"];
#if __cplusplus >= 202002L
            if(msg.starts_with(mention))
#else
            auto itx = mention.size() > msg.size() ? mention.cbegin() : std::mismatch(mention.cbegin(), mention.cend(), msg.cbegin()).first;
            if(itx == mention.cend())
#endif
            	sendstr = co_await parser(evt.msg, msg.substr(mention.size()));
#if __cplusplus >= 202002L
            if(msg.starts_with(pref))
#else
            auto ity = pref.size() > msg.size() ? pref.cbegin() : std::mismatch(pref.cbegin(), pref.cend(), msg.cbegin()).first;
            if(ity == pref.cend())
#endif
            	sendstr = co_await parser(evt.msg, msg.substr(pref.size()));
            if(sendstr.size() > 2000)
            {
            	message m;
            	m.add_file("message.txt", sendstr);
            	evt.send(m);
			}
            else if(sendstr.size() > 0)
                evt.send(sendstr);
        }
    };
    scannerbot.on_message_create(evtr);
    scannerbot.on_message_update(edited);
    scannerbot.on_guild_member_add(memjoin);
    scannerbot.on_guild_member_remove(memleave);
    scannerbot.on_guild_role_delete(roledel);
    startTimeout();
    lastfetch = std::chrono::system_clock::now();
    fetch_guilds(scannerbot);
    scannerbot.start();
    cout << "Scanner Bot v" << verstr << " has begun." << endl;
}

int main(int argl,char**argv)
{
    auto t = run();
    t.sync_wait();
    save(allguilds);
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

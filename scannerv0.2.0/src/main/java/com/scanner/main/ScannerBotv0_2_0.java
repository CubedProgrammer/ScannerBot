package com.scanner.main;
import static java.lang.Math.*;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.function.UnaryOperator;
import javax.security.auth.login.LoginException;
import javax.swing.JFrame;
import com.scanner.commands.Command;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.HierarchyException;
/**
 * The class with all the bot commands.
 * This class is the main class.
 * @author (DSOI)UNUNOCTIUM
 */
public class ScannerBotv0_2_0 extends Canvas {
	
	public static final long serialVersionUID=-1224784432946802l;
	public static final String token="MzY3NDI2MTc4MDE5MjI5Njk3.DuSQ2w.hjmrZBrDSrRZRibXbls_rccXBM0";
	public static final HashMap<String,String>constants;
	
	/**
	 * Initialize static variables, such as hash map of constants.
	 */
	static {
		
		constants=new HashMap<>();
		constants.put("PI","3.141592653589793");
		constants.put("E","2.718281828459045");
		constants.put("PHI","1.61803398874989");
		
	}
	
	private JDA jda;
	private String last;
	private boolean rendering;
	private Thread thread;
	private Command[]commands;
	private Vector<Long>selfroles;
	
	/**
	 * Initializes an instance of the bot from the token.
	 * @param token The token used to connect to the bot.
	 * @throws LoginException If there is an error while logging in.
	 */
	public ScannerBotv0_2_0(String token)throws LoginException {
		
		this.jda=new JDABuilder(AccountType.BOT).setToken(token).build();
		this.jda.addEventListener(new MessageListener());
		this.selfroles=new Vector<Long>();
		this.commands=new Command[29];
		this.commands[0]=new Command("sum",this::parseSum);
		this.commands[1]=new Command("product",this::parseProduct);
		this.commands[2]=new Command("median",this::parseMedian);
		this.commands[3]=new Command("power",this::parsePower);
		this.commands[4]=new Command("logarithm",this::parseLog);
		this.commands[5]=new Command("productexp",(guild,author,channel,args)->{return this.parseUnary(d->d*exp(d),guild,author,channel,args);});
		this.commands[6]=new Command("productlog",(guild,author,channel,args)->{return this.parseUnary(MathMethods::plog,guild,author,channel,args);});
		this.commands[7]=new Command("sin",(guild,author,channel,args)->{return this.parseUnary(Math::sin,guild,author,channel,args);});
		this.commands[8]=new Command("sec",(guild,author,channel,args)->{return this.parseUnary(d->1/cos(d),guild,author,channel,args);});
		this.commands[9]=new Command("tan",(guild,author,channel,args)->{return this.parseUnary(Math::tan,guild,author,channel,args);});
		this.commands[10]=new Command("cos",(guild,author,channel,args)->{return this.parseUnary(Math::cos,guild,author,channel,args);});
		this.commands[11]=new Command("csc",(guild,author,channel,args)->{return this.parseUnary(d->1/sin(d),guild,author,channel,args);});
		this.commands[12]=new Command("cot",(guild,author,channel,args)->{return this.parseUnary(d->1/tan(d),guild,author,channel,args);});
		this.commands[13]=new Command("get_all_permutations",this::parsePermute);
		this.commands[14]=new Command("rename",this::parseRename);
		this.commands[15]=new Command("kick",this::parseKick);
		this.commands[16]=new Command("ban",this::parseBan);
		this.commands[17]=new Command("unban",this::parseUnban);
		this.commands[18]=new Command("role",this::parseRole);
		this.commands[19]=new Command("avatar",this::parseAvatar);
		this.commands[20]=new Command("getid",this::parseInfo);
		this.commands[21]=new Command("join",this::parseJoin);
		this.commands[22]=new Command("sum_up_to",(guild,author,channel,args)->{return this.parseUnary(d->(d*d+d)/2,guild,author,channel,args);});
		this.commands[23]=new Command("factorial",(guild,author,channel,args)->{return this.parseUnary(MathMethods::fact,guild,author,channel,args);});
		this.commands[24]=new Command("poi",this::parsePOI);
		this.commands[25]=new Command("autorole",this::parseAutorole);
		this.commands[26]=new Command("selfrole",this::parseSelfrole);
		this.commands[27]=new Command("changeselfrole",this::parseModifySelfRole);
		this.commands[28]=new Command("work",this::parseWork);
		this.last="";
		this.thread=new Thread(this::run);
		this.setSize(1025,630);
		this.setPreferredSize(this.getSize());
		this.setVisible(true);
		
	}
	
	public int commandIndex(String command) {
		
		for(int i=0;i<this.commands.length;i++) {
			
			if(this.commands[i].name.equals(command)) {
				return i;
			}
			
		}
		
		return-1;
		
	}
	
	public double parseString(String s)throws NumberFormatException {
		
		double d=1;
		String[]terms=s.split("/");
		boolean numerator=true;
		
		for(String term:terms) {
			//System.out.println(term);
			if(numerator) {
				
				d*=Double.parseDouble(constants.containsKey(term)?constants.get(term):term);
				numerator=false;
				
			} else {
				d/=Double.parseDouble(constants.containsKey(term)?constants.get(term):term);
			}
			
		}
		
		return d;
		
	}
	
	public String parseSum(Guild guild,long author,long channel,String...args) {
		
		double result=0;
		int breaker=0;
		int skip=0;
		//System.out.println("sum");
		
		for(String arg:args) {
			
			if(skip>0) {
				skip--;
			} else {
				
				if(arg.equals("end")) {
					
					breaker++;
					break;
					
				} else if(this.commandIndex(arg)>=0) {
					
					breaker++;
					String[]tmp=new String[args.length-breaker];
					for(int i=0;i<tmp.length;tmp[i]=args[breaker+i++]);
					
					String ans=this.commands[this.commandIndex(arg)].readCommand(guild,author,channel,tmp);
					//System.out.println(ans.substring(0,2));
					breaker+=skip=Integer.parseInt(ans.substring(0,2));
					result+=this.parseString(ans.substring(2));
					
				} else {
					
					//System.out.printf("%s %d\r\n",arg,breaker);
					
					try {
						result+=this.parseString(arg);
					} catch(Exception e) {
						
						e.printStackTrace();
						return(breaker<16?"0":"")+Integer.toHexString(breaker)+e.toString();
						
					}
					
					breaker++;
					
				}
				
			}
			
		}
		
		System.out.println(result);
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+Double.toString(result);
		
	}
	
	public String parseProduct(Guild guild,long author,long channel,String...args) {
		
		double result=1;
		int breaker=0;
		int skip=0;
		System.out.println("product");
		
		for(String arg:args) {
			
			if(skip>0) {
				skip--;
			} else {
				
				if(arg.equals("end")) {
					
					breaker++;
					break;
					
				} else if(this.commandIndex(arg)>=0) {
					
					breaker++;
					String[]tmp=new String[args.length-breaker];
					for(int i=0;i<tmp.length;tmp[i]=args[breaker+i++]);
					
					String ans=this.commands[this.commandIndex(arg)].readCommand(guild,author,channel,tmp);
					//System.out.println(ans.substring(0,2));
					breaker+=skip=Integer.parseInt(ans.substring(0,2));
					result*=this.parseString(ans.substring(2));
					
				} else {
					
					try {
						result*=this.parseString(arg);
					} catch(Exception e) {
						
						e.printStackTrace();
						return(breaker<16?"0":"")+Integer.toHexString(breaker)+e.toString();
						
					}
					
					breaker++;
					
				}
				
			}
			
		}
		
		System.out.println(result);
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+Double.toString(result);
		
	}
	
	public String parseMedian(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		int skip=0;
		Vector<Double>vs=new Vector<Double>();
		
		for(String arg:args) {
			
			if(skip>0) {
				skip--;
			} else {
				
				if(arg.equals("end")) {
					
					breaker++;
					break;
					
				} else if(this.commandIndex(arg)>=0) {
					
					breaker++;
					String[]tmp=new String[args.length-breaker];
					for(int i=0;i<tmp.length;tmp[i]=args[breaker+i++]);
					
					String ans=this.commands[this.commandIndex(arg)].readCommand(guild,author,channel,tmp);
					//System.out.println(ans.substring(0,2));
					breaker+=skip=Integer.parseInt(ans.substring(0,2));
					vs.add(this.parseString(ans.substring(2)));
					
				} else {
					
					//System.out.printf("%s %d\r\n",arg,breaker);
					
					try {
						vs.add(this.parseString(arg));
					} catch(Exception e) {
						
						e.printStackTrace();
						return(breaker<16?"0":"")+Integer.toHexString(breaker)+e.toString();
						
					}
					
					breaker++;
					
				}
				
			}
			
		}
		
		double[]dvs=new double[vs.size()];
		for(int i=0;i<dvs.length;dvs[i]=vs.get(i++));
		
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+Double.toString(MathMethods.median(dvs));
		
	}
	
	public String parsePower(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		double result=0;
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[0])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			result=this.parseString(ans.substring(2));
			
		} else {
			result=this.parseString(args[breaker++]);
		}
		
		if(breaker<args.length) {
			
			if(this.commandIndex(args[breaker])>=0) {
				
				String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
				breaker+=Integer.parseInt(ans.substring(0,2),16);
				result=pow(result,this.parseString(ans.substring(2)));
				
			} else {
				result=pow(result,this.parseString(args[breaker++]));
			}
			
		} else {
			result=exp(result);
		}
		
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+Double.toString(result);
		
	}
	
	public String parseLog(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		double result=0;
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[0])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			result=this.parseString(ans.substring(2));
			
		} else {
			result=this.parseString(args[breaker++]);
		}
		
		if(breaker<args.length) {
			
			if(this.commandIndex(args[breaker])>=0) {
				
				String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
				breaker+=Integer.parseInt(ans.substring(0,2),16);
				result=log(this.parseString(ans.substring(2)))/log(result);
				
			} else {
				result=log(this.parseString(args[breaker++]))/log(result);
			}
			
		} else {
			result=log(result);
		}
		
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+Double.toString(result);
		
	}
	
	public String parseUnary(UnaryOperator<Double>funcToCall,Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		double result=0;
		
		try {
			
			if(this.commandIndex(args[0])>=0) {
				
				String ans=this.commands[this.commandIndex(args[0])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
				breaker+=Integer.parseInt(ans.substring(0,2),16);
				result=this.parseString(ans.substring(2));
				
			} else {
				result=this.parseString(args[breaker++]);
			}
			
		} catch(NumberFormatException e) {
			
			e.printStackTrace();
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+e.toString();
			
		}
		
		result=funcToCall.apply(result);
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+Double.toString(result);
		
	}
	
	public String parsePermute(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String result="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[0])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			result=ans.substring(2);
			
		} else {
			result=args[breaker++];
		}
		
		result=OtherMethods.permute(result);
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+result;
		
	}
	
	public String parseRename(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		String result="";
		
		if(this.commandIndex(args[breaker])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		if(this.commandIndex(args[breaker])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			result=ans.substring(2);
			
		} else {
			result=args[breaker++];
		}
		
		try {
			
			guild.getController().setNickname(guild.getMemberById(target.replace("@","").replace("!","").replace("<","").replace(">","")),result).queue();
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Successfully renamed "+target+" to "+result+"!";
			
		} catch(Exception e) {
			
			e.printStackTrace();
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+e.toString();
			
		}
		
	}
	
	public String parseKick(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		long targid=Long.parseLong(target.replace("@","").replace("!","").replace("<","").replace(">",""));
		
		if(guild.getMemberById(targid).hasPermission(Permission.KICK_MEMBERS)) {
			
			try {
				
				guild.getController().kick(guild.getMemberById(targid)).queue();
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Successfully kicked "+target+" from this server!";
				
			} catch(HierarchyException ex) {
				
				ex.printStackTrace();
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+ex.toString();
				
			}
			
		} else {
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+"You do not have the permission to use this command.";
		}
		
	}
	
	public String parseBan(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		long targid=Long.parseLong(target.replace("@","").replace("!","").replace("<","").replace(">",""));
		
		if(guild.getMemberById(targid).hasPermission(Permission.BAN_MEMBERS)) {
			
			try {
				
				guild.getController().ban(guild.getMemberById(targid).getUser(),0).queue();
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Successfully banned "+target+" from this server!";
				
			} catch(HierarchyException ex) {
				
				ex.printStackTrace();
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+ex.toString();
				
			}
			
		} else {
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+"You do not have the permission to use this command.";
		}
		
	}
	
	public String parseUnban(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		long targid=Long.parseLong(target.replace("@","").replace("!","").replace("<","").replace(">",""));
		
		if(guild.getMemberById(targid).hasPermission(Permission.BAN_MEMBERS)) {
			
			try {
				
				guild.getController().unban(guild.getMemberById(targid).getUser()).queue();
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Successfully unbanned "+target+" from this server!";
				
			} catch(HierarchyException ex) {
				
				ex.printStackTrace();
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+ex.toString();
				
			}
			
		} else {
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+"You do not have the permission to use this command.";
		}
		
	}
	
	public String parseRole(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		String result="";
		
		if(this.commandIndex(args[breaker])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		if(this.commandIndex(args[breaker])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			result=ans.substring(2);
			
		} else {
			result=args[breaker++];
		}
		
		try {
			
			if(guild.getMemberById(target.replace("@","").replace("!","").replace("<","").replace(">","")).getRoles().contains(guild.getRoleById(result.replace("@","").replace("&","").replace("<","").replace(">","")))) {
				
				guild.getController().removeSingleRoleFromMember(guild.getMemberById(target.replace("@","").replace("!","").replace("<","").replace(">","")),guild.getRoleById(result.replace("@","").replace("&","").replace("<","").replace(">",""))).queue();
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Successfully removed "+result+" from "+target+"!";
				
			} else {
				
				guild.getController().addSingleRoleToMember(guild.getMemberById(target.replace("@","").replace("!","").replace("<","").replace(">","")),guild.getRoleById(result.replace("@","").replace("&","").replace("<","").replace(">",""))).queue();
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Successfully added "+result+" to "+target+"!";
				
			}
			
		} catch(Exception e) {
			
			e.printStackTrace();
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+e.toString();
			
		}
		
	}
	
	public String parseAvatar(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		
		if(args.length==0||"end".equals(args[0])) {
			target=Long.toString(author);
		} else if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		long targid=Long.parseLong(target.replace("@","").replace("!","").replace("<","").replace(">",""));
		
		try {
			
			String url=guild.getMemberById(targid).getUser().getAvatarUrl();
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+(url==null?"This user has no avatar!":url);
			
		} catch(Exception ex) {
			
			ex.printStackTrace();
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+ex.toString();
			
		}
		
	}
	
	public String parseInfo(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		try {
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+target.replace("@","").replace("&","").replace("#","").replace("!","").replace("<","").replace(">","");
		} catch(Exception ex) {
			
			ex.printStackTrace();
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+ex.toString();
			
		}
		
	}
	
	public String parseJoin(Guild guild,long author,long channel,String...args) {
		
		String result="";
		int breaker=0;
		int skip=0;
		//System.out.println("sum");
		
		for(String arg:args) {
			
			if(skip>0) {
				skip--;
			} else {
				
				if(arg.equals("end")) {
					
					breaker++;
					break;
					
				} else if(this.commandIndex(arg)>=0) {
					
					breaker++;
					String[]tmp=new String[args.length-breaker];
					for(int i=0;i<tmp.length;tmp[i]=args[breaker+i++]);
					
					String ans=this.commands[this.commandIndex(arg)].readCommand(guild,author,channel,tmp);
					//System.out.println(ans.substring(0,2));
					breaker+=skip=Integer.parseInt(ans.substring(0,2));
					result+=ans.substring(2)+" ";
					
				} else {
					
					try {
						result+=arg+" ";
					} catch(Exception e) {
						
						e.printStackTrace();
						return(breaker<16?"0":"")+Integer.toHexString(breaker)+e.toString();
						
					}
					
					breaker++;
					
				}
				
			}
			
		}
		
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+result.substring(0,result.length()-1);
		
	}
	
	public String parsePOI(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String f="";
		String s="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			f=ans.substring(2);
			
		} else {
			f=args[breaker++];
		}
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			s=ans.substring(2);
			
		} else {
			s=args[breaker++];
		}
		
		char[]fc=f.toCharArray();
		char[]sc=s.toCharArray();
		
		for(int i=0;i<fc.length;i++) {
			
			if(fc[i]>57||fc[i]<48) {
				fc[i]=32;
			}
			
		}
		
		for(int i=0;i<sc.length;i++) {
			
			if(sc[i]>57||sc[i]<48) {
				sc[i]=32;
			}
			
		}
		
		String[]ft=new String(fc).split(" "),st=new String(sc).split(" ");
		double[]fv=new double[3],sv=new double[3];
		int fs=0,ss=0;
		
		for(int i=0;i<ft.length&&fs<3;i++) {
			
			if(ft[i].length()>0) {
				fv[fs++]=Double.parseDouble(ft[i]);
			}
			
		}
		
		for(int i=0;i<st.length&&ss<3;i++) {
			
			if(st[i].length()>0) {
				sv[ss++]=Double.parseDouble(st[i]);
			}
			
		}
		
		double[]result=MathMethods.fndpoi(fv[0],fv[1],sv[0],sv[1],fv[2],sv[2]);
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+"("+result[0]+","+result[1]+")";
		
	}
	
	public String parseAutorole(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		long roleid=Long.parseLong(target.replace("@","").replace("&","").replace("<","").replace(">",""));
		OtherMethods.setAutoRole(roleid);
		
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Set the autorole to "+target+".";
		
	}
	
	public String parseSelfrole(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		long roleid=Long.parseLong(target.substring(3,target.length()-1));
		Role r=guild.getRoleById(roleid);
		
		if(OtherMethods.testSelfRole(roleid)) {
			
			if(guild.getMemberById(author).getRoles().contains(r)) {
				
				guild.getController().removeSingleRoleFromMember(guild.getMemberById(author),r);
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Successfully removed selfrole "+r.getAsMention()+"!";
				
			} else {
				
				guild.getController().addSingleRoleToMember(guild.getMemberById(author),r);
				return(breaker<16?"0":"")+Integer.toHexString(breaker)+"Successfully added selfrole "+r.getAsMention()+"!";
				
			}
			
		}
		
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+"That is not a selfrole!";
		
	}
	
	public String parseModifySelfRole(Guild guild,long author,long channel,String...args) {
		
		int breaker=0;
		String target="";
		
		if(this.commandIndex(args[0])>=0) {
			
			String ans=this.commands[this.commandIndex(args[breaker])].readCommand(guild,author,channel,Arrays.copyOfRange(args,++breaker,args.length));
			breaker+=Integer.parseInt(ans.substring(0,2),16);
			target=ans.substring(2);
			
		} else {
			target=args[breaker++];
		}
		
		long roleid=Long.parseLong(target.substring(2,target.length()-1));
		OtherMethods.changeSelfRole(roleid);
		Role r=guild.getRoleById(roleid);
		
		if(OtherMethods.testSelfRole(roleid)) {
			return(breaker<16?"0":"")+Integer.toHexString(breaker)+r.getAsMention()+"is now a selfrole.";
		}
		
		return(breaker<16?"0":"")+Integer.toHexString(breaker)+r.getAsMention()+"is no longer a selfrole.";
		
	}
	
	public String parseWork(Guild guild,long author,long channel,String...args) {
		return"";
	}
	
	public String runCommand(String...cmd) {
		
		MessageListener listener=(MessageListener)this.jda.getRegisteredListeners().get(0);
		String[]args=new String[cmd.length-1];
		
		for(int i=0;i<args.length;i++) {
			args[i]=cmd[i+1];
		}
		
		int c=this.commandIndex(cmd[0]);
		
		if(c>=0) {
			return this.commands[c].readCommand(this.jda.getGuildById(listener.getLastGuildId()),listener.getLastUserId(),listener.getLastChannelId(),args);
		}
		
		return"00Command does not exist!";
		
	}
	
	public void paint(Graphics2D g) {
		
		g.setColor(new Color(18,18,18));
		g.fillRect(0,0,50,50);
		
	}
	
	public void tick() {
		
		MessageListener listener=(MessageListener)this.jda.getRegisteredListeners().get(0);
		this.last=listener.getLastMessage();
		
		if(!this.last.equals("")) {
			
			if(this.last.length()>22) {
				
				if(this.last.substring(0,22).equals("<@!367426178019229697>")||this.last.substring(0,21).equals("<@367426178019229697>")) {
					
					String[]args=this.last.substring(this.last.charAt(2)=='!'?23:21).split(" ");
					this.jda.getTextChannelById(listener.getLastChannelId()).sendMessage(this.runCommand(args).substring(2)).queue();
					
				}
				
			}
			
			System.out.println(this.last);
			listener.setLastMessage(this.last="");
			
		}
		
	}
	
	public void render() {
		
		BufferStrategy bs=this.getBufferStrategy();
		if(bs==null) {
			
			this.createBufferStrategy(3);
			return;
			
		}
		
		Graphics g=bs.getDrawGraphics();
		g.setColor(new Color(238,238,238));
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		this.paint((Graphics2D)g);
		g.dispose();
		bs.show();
		
	}
	
	public void run() {
		
		long last=System.nanoTime();
		long now;
		double timepassed=0;
		while(this.rendering) {
			
			now=System.nanoTime();
			timepassed+=(double)(now-last)/1000000;
			last=now;
			
			if(timepassed>16.66667) {
				
				this.tick();
				this.render();
				timepassed-=16.66667;
				
			}
			
		}
		
	}
	
	public synchronized void start() {
		
		this.thread.start();
		this.rendering=true;
		
	}
	
	public synchronized void stop() {
		System.exit(0);
	}
	
	public static void main(String...args) {
		
		try {
			
			JFrame frame=new JFrame("Robot scannerv0.2.0#8610");
			ScannerBotv0_2_0 bot=new ScannerBotv0_2_0(args.length>0?args[0]:ScannerBotv0_2_0.token);
			
			frame.add(bot);
			frame.pack();
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(3);
			frame.setVisible(true);
			bot.start();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
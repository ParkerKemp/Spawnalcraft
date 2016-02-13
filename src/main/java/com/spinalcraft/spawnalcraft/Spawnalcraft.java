package com.spinalcraft.spawnalcraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.spinalcraft.spinalpack.Co;
import com.spinalcraft.spinalpack.Spinalpack;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Spawnalcraft extends JavaPlugin implements Listener{

	public static Location spawn = null;
	private static List<String> motd = null;
	boolean forceSpawn;
	
	ConsoleCommandSender console;
	
	@Override
	public void onEnable(){
		console = Bukkit.getConsoleSender();
		
		console.sendMessage(Spinalpack.code(Co.BLUE) + "Spawnalcraft online!");
		getDataFolder().mkdirs();
		getMOTD();
		saveDefaultConfig();
		loadConfig();
		getServer().getPluginManager().registerEvents((Listener)this,  this);
	}
	
	@Override
	public void onDisable(){
		HandlerList.unregisterAll((JavaPlugin)this);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		
		if(!player.hasPlayedBefore()){
			if(spawn != null)
				player.teleport(spawn);
		}else if(forceSpawn)
			player.teleport(spawn);
		
		sendMOTD(player);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(spawn == null)
			return;
		if(!event.isBedSpawn())
			event.setRespawnLocation(spawn);
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("spawnalize")){
			if(sender instanceof Player){
				Player player = (Player)sender;
				player.sendMessage(Spinalpack.code(Co.GOLD) + "World spawn set!");
				spawn = player.getLocation();
				writeSpawn(spawn);
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("spawnal")){
			if(args.length > 0){
				Player player = Bukkit.getPlayer(args[0]);
				if(player == null){
					sender.sendMessage(ChatColor.RED + "Player not found!");
					return true;
				}
				player.sendMessage(ChatColor.GOLD + "Teleporting to spawn.");
				player.teleport(spawn);
			}
			else if(sender instanceof Player){
				Player player = (Player)sender;
				if(spawn != null){
					player.sendMessage(Spinalpack.code(Co.GOLD) + "Teleporting to spawn.");
					player.teleport(spawn);
				}
				else{
					player.sendMessage(Spinalpack.code(Co.GOLD) + "No spawnal set! Teleporting to default world spawn.");
					player.teleport(Bukkit.getWorld("world").getSpawnLocation());
				}
			}
			else
				return false;
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("spawnalcraft")){
			if(args.length < 1)
				return false;
			if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("spawnalcraft.reload")){
				loadConfig();
				return true;
			}
			return false;
		}
		
		if(cmd.getName().equalsIgnoreCase("updatemotd")){
			if(getMOTD())
				sender.sendMessage(Spinalpack.code(Co.GREEN) + "Updated message of the day successfully!");
			else
				sender.sendMessage(Spinalpack.code(Co.RED) + "Couldn't find or open motd.txt!");
			return true;
		}
		return false;
	}
	
	private void loadConfig(){
		reloadConfig();
		FileConfiguration config = getConfig();
		if(config.isSet("spawn.x") && config.isSet("spawn.y") && config.isSet("spawn.z")){
			float x = (float) config.getDouble("spawn.x");
			float y = (float) config.getDouble("spawn.y");
			float z = (float) config.getDouble("spawn.z");
			float pitch = (float) config.getDouble("spawn.pitch");
			float yaw = (float) config.getDouble("spawn.yaw");
			spawn = new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
		}
		forceSpawn = config.getBoolean("force-spawn-on-join");
	}
	
	private void writeSpawn(Location location){
		FileConfiguration config = getConfig();
		config.set("spawn.x", location.getX());
		config.set("spawn.y", location.getY());
		config.set("spawn.z", location.getZ());
		config.set("spawn.pitch", location.getPitch());
		config.set("spawn.yaw", location.getYaw());
		saveConfig();
	}
	
	private void sendMOTD(Player player){
		if(motd == null)
			return;
		for(String line : motd){
			player.sendMessage(line);
		}
	}
	
	private boolean getMOTD(){
		try {
			motd = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/plugins/Spinalpack/motd.txt"), StandardCharsets.UTF_8);
			for(int i = 0; i < motd.size(); i++){
				motd.set(i, Spinalpack.parseColorTags(motd.get(i)));
			}
			return true;
		} catch (IOException e) {
			console.sendMessage(Spinalpack.code(Co.RED) + "Unable to open motd.txt!");
			return false;
		}
	}
}

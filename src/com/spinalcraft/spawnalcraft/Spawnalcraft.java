package com.spinalcraft.spawnalcraft;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.spinalcraft.spinalpack.Co;
import com.spinalcraft.spinalpack.Spinalpack;


public class Spawnalcraft extends JavaPlugin implements Listener{

	public static Location spawn = null;
	
	ConsoleCommandSender console;
	
	@Override
	public void onEnable(){
		console = Bukkit.getConsoleSender();
		
		console.sendMessage(Spinalpack.code(Co.BLUE) + "Spawnalcraft online!");
		
		spawn = readSpawnFile();
		getServer().getPluginManager().registerEvents((Listener)this,  this);
	}
	
	@Override
	public void onDisable(){
		HandlerList.unregisterAll((JavaPlugin)this);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event){
		if(spawn == null)
			return;
		Player player = event.getPlayer();
		if(!player.hasPlayedBefore()){
			Bukkit.broadcastMessage(Spinalpack.code(Co.AQUA) + "Welcome our newest Spinaling, " + Spinalpack.code(Co.BLUE) + player.getName());
			player.teleport(spawn);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(spawn == null)
			return;
		if(!event.isBedSpawn())
			event.setRespawnLocation(spawn);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("spawnalize")){
			if(sender instanceof Player){
				Player player = (Player)sender;
				player.sendMessage(Spinalpack.code(Co.GOLD) + "World spawn set!");
				spawn = player.getLocation();
				writeSpawnFile(spawn);
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("spawnal")){
			if(sender instanceof Player){
				Player player = (Player)sender;
				if(spawn != null){
					player.sendMessage(Spinalpack.code(Co.GOLD) + "Teleporting to spawnal.");
					player.teleport(spawn);
				}
				else{
					player.sendMessage(Spinalpack.code(Co.GOLD) + "No spawnal set! Teleporting to default world spawn.");
					player.teleport(Bukkit.getWorld("world").getSpawnLocation());
				}
				return true;
			}
		}
		return false;
	}
	
	private void writeSpawnFile(Location location){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(System.getProperty("user.dir") + "/plugins/Spinalpack/spawn.txt"));
			writer.println(location.getX());
			writer.println(location.getY());
			writer.println(location.getZ());
			writer.println(location.getPitch());
			writer.println(location.getYaw());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Location readSpawnFile(){
		double x, y, z;
		float pitch, yaw;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/plugins/Spinalpack/spawn.txt"));
			x = Float.parseFloat(reader.readLine());
			y = Float.parseFloat(reader.readLine());
			z = Float.parseFloat(reader.readLine());
			pitch = Float.parseFloat(reader.readLine());
			yaw = Float.parseFloat(reader.readLine());
			
			reader.close();
		} catch (Exception e1) {
			return null;
		}
		return new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
	}
	
	/*public void logToFile(String file, String message){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file, true));
			writer.println(dateAndTime() + " " + message);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getWatchdogPid(){
		String temp = "(invalid)";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getDataFolder().toString() + "/watchdogPid.txt")));
			temp = reader.readLine();
			getLogger().info(temp);
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}*/
}

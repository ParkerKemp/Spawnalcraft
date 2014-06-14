package com.spinalcraft.spawnalcraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
	private static List<String> motd = null;
	
	ConsoleCommandSender console;
	
	@Override
	public void onEnable(){
		console = Bukkit.getConsoleSender();
		
		console.sendMessage(Spinalpack.code(Co.BLUE) + "Spawnalcraft online!");
		
		getMOTD();
		
		spawn = readSpawnFile();
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
			Bukkit.broadcastMessage(Spinalpack.code(Co.AQUA) + "Welcome our newest Spinaling, " + Spinalpack.code(Co.BLUE) + player.getName() + Spinalpack.code(Co.AQUA) + "!");
			player.sendMessage(Spinalpack.code(Co.GOLD) + "Welcome to Spinalcraft!");
			if(spawn != null)
				player.teleport(spawn);
		}
		
		sendMOTD(player);
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
	
	private void sendMOTD(Player player){
		if(motd == null)
			return;
		for(String line : motd){
			player.sendMessage(line);
		}
	}
	
	private void getMOTD(){
		try {
			motd = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/plugins/Spinalpack/motd.txt"), StandardCharsets.UTF_8);
			for(int i = 0; i < motd.size(); i++){
				motd.set(i, Spinalpack.parseColorTags(motd.get(i)));
			}
		} catch (IOException e) {
			console.sendMessage(Spinalpack.code(Co.RED) + "Unable to open motd.txt!");
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
}

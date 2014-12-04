package de.lostco.bukkitexpanse;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class BukkitExpanse extends JavaPlugin {
	
	int currentRadius;
	double priceConstant;
	double timeConstant;
	
	@Override
	public void onEnable() {
		getLogger().info("BukkitExpanse Enabled!");
		if (getConfig().contains("priceConstant")) {
			priceConstant = getConfig().getDouble("priceConstant");
		}
		if (getConfig().contains("timeConstant")) {
			timeConstant = getConfig().getDouble("timeConstant");
		}
	}
	@Override
	public void onDisable() {
		getConfig().set("priceConstant", priceConstant);
		getConfig().set("timeConstant", timeConstant);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("BukkitExpanse") && (sender.hasPermission("bukkitexpanse.*") || sender.hasPermission(cmd.getPermission()))) {
			sender.sendMessage("Use /expand [additional radius] {time} or /expandprice [additional radius] {time}.");
			sender.sendMessage("Price = (blocks * blockConstant) * (timeConstant / time).");
			sender.sendMessage("timeConstant is the period of time at which each block costs blockConstant.");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("expandprice") && (sender.hasPermission("bukkitexpanse.*") || sender.hasPermission(cmd.getPermission()))) {
			int time = (int)timeConstant;
			if (args.length >= 2) {
				time = Integer.parseInt(args[1]);
			}
			sender.sendMessage("Expanding the radius by " + args[0] + " blocks will cost: " + getPrice(Integer.parseInt(args[0]), 1));
			return true;
		} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("totalxp") && (sender.hasPermission("bukkitexpanse.*") || sender.hasPermission(cmd.getPermission()))) {
			sender.sendMessage("Your total xp is: " + ((Player)sender).getTotalExperience());
			return true;
		}
		return false;
	}
	public double getPrice(int additionalRadius, int time) {
		double deltaBlock = ((currentRadius + additionalRadius) * (currentRadius + additionalRadius)) - (currentRadius * currentRadius);
		double blockPrice = deltaBlock * priceConstant;
		double timeMod = timeConstant / (double)time;
		return timeMod * blockPrice;
	}
	public void setBorder(int radius, int time) {
		getServer().dispatchCommand(getServer().getConsoleSender(), "worldborder set " + radius + "" + time); // no api for world border yet :(
	}
	public void setBorder(int radius) {
		setBorder(radius, (int)timeConstant);
	}
}

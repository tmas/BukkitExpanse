package de.lostco.bukkitexpanse;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;


public class BukkitExpanse extends JavaPlugin {
	
	private int currentRadius;
	private double priceConstant;
	private double timeConstant;
	private Economy econ;
	
	@Override
	public void onEnable() {
		this.getConfig().addDefault("priceConstant", 2.5);
		this.getConfig().addDefault("timeConstant", 30.0);
		this.getConfig().addDefault("currentRadius", 16.0);
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().info("BukkitExpanse requires Vault! Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().info("BukkitExpanse Enabled!");
		priceConstant = this.getConfig().getDouble("priceConstant");
		getLogger().info("Price: " + priceConstant);
		timeConstant = this.getConfig().getDouble("timeConstant");
		getLogger().info("Time: " + timeConstant);
		currentRadius = (int)this.getConfig().getDouble("currentRadius");
		getLogger().info("Radius: " + currentRadius);
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if ((rsp == null) || (rsp.getProvider() == null)) {
			getLogger().info("[BukkitExpanse] No economy plugin found! Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		econ = rsp.getProvider();
	}
	@Override
	public void onDisable() {
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("BukkitExpanse") && (sender.hasPermission("bukkitexpanse.*") || sender.hasPermission(cmd.getPermission()))) {
			sender.sendMessage("Use /expand [additional radius] {time} or /expandprice [additional radius] {time}.");
			sender.sendMessage("Price = (blocks * blockConstant) * (timeConstant / time).");
			sender.sendMessage("timeConstant is the period of time at which each block costs blockConstant.");
			getLogger().info("BukkitExpanse Config: " + getConfig().getCurrentPath());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("expandprice") && (sender.hasPermission("bukkitexpanse.*") || sender.hasPermission(cmd.getPermission()))) {
			int time = (int)timeConstant;
			if (args.length >= 2) {
				time = Integer.parseInt(args[1]);
			}
			int newRadius = currentRadius+Integer.parseInt(args[0]);
			sender.sendMessage("Expanding the radius by " + args[0] + " blocks over " + time + " seconds will cost: " + getPrice(Integer.parseInt(args[0]), time));
			sender.sendMessage("Current radius is: " + currentRadius + ". New radius would be: " + newRadius);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("expand") && (sender.hasPermission("bukkitexpanse.*") || sender.hasPermission(cmd.getPermission()))) {
			int time = (int)timeConstant;
			if (args.length >= 2) {
				time = Integer.parseInt(args[1]);
			}
			double price = getPrice(Integer.parseInt(args[0]), time);
			double bal = econ.getBalance((Player)sender);
			int newRadius = currentRadius+Integer.parseInt(args[0]);
			sender.sendMessage("Expanding the radius by " + args[0] + " blocks over " + time + " seconds will cost: " + price);
			sender.sendMessage("New radius will be: " + newRadius);
			sender.sendMessage("You have: " + bal);
			if (takePayment(price, (Player)sender)) {
				setBorder(newRadius, time);
			}
			return true;
		}
		return false;
	}
	public double getPrice(int additionalRadius, int time) {
		double oldSideLength = 2*currentRadius + 1;
		double newSideLength = 2*(currentRadius+additionalRadius) + 1;
		double deltaBlock = newSideLength*newSideLength - oldSideLength*oldSideLength;
		double blockPrice = deltaBlock * priceConstant;
		double timeMod = timeConstant / (double)time;
		return timeMod * blockPrice;
	}
	public void setBorder(int radius, int time) {
		int newSide = radius*2;
		newSide++;
		getServer().dispatchCommand(getServer().getConsoleSender(), "worldborder set " + newSide + " " + time); // no api for world border yet :(
		currentRadius = radius;
		this.getConfig().set("currentRadius", (double)currentRadius);
		saveConfig();
	}
	public void setBorder(int radius) {
		setBorder(radius, (int)timeConstant);
	}
	public boolean takePayment(double amount, Player p) {
		if (!econ.has(p, amount)) {
			return false;
		}
		econ.withdrawPlayer(p, amount);
		return true;
	}
}

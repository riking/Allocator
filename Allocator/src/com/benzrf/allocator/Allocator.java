package com.benzrf.allocator;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

public class Allocator extends JavaPlugin
{
	Map<Location, Material> allocatorMap = new HashMap<Location, Material>();
	AllocatorListener thelistener = new AllocatorListener(this);

	public void onDisable()
	{
		Map<String, String> allocatorMapS = new HashMap<String, String>();
		for (Location l : this.allocatorMap.keySet())
		{
			allocatorMapS.put(convertLocation(l), ((Material)this.allocatorMap.get(l)).name());
		}
		String allocators = new Yaml().dump(allocatorMapS);
		getConfig().set("data", allocators);
		saveConfig();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this.thelistener, this);
		
		HashMap<String, String> allocatorMapS = (HashMap)new Yaml().loadAs(getConfig().getString("data", "{}"), HashMap.class);
		for (String s : allocatorMapS.keySet())
		{
			this.allocatorMap.put(convertString(s), Material.getMaterial((String)allocatorMapS.get(s)));
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!sender.hasPermission("allocator.create"))
		{
			return false;
		}
		try
		{
			Player p = (Player)sender;
			Block b = p.getTargetBlock(null, 5);
			if (!b.getType().equals(Material.PUMPKIN))
			{
				sender.sendMessage(ChatColor.RED + "Either that's not a pumpkin, you're too far away, or there's a non-full block in the way.");
				return true;
			}
			if (this.allocatorMap.containsKey(p.getTargetBlock(null, 5).getLocation()))
			{
				sender.sendMessage(ChatColor.RED + "That pumpkin is already an allocator!");
				return true;
			}
			this.allocatorMap.put(p.getTargetBlock(null, 5).getLocation(), p.getItemInHand().getType());
			sender.sendMessage(ChatColor.GREEN + "Allocator added!");
		}
		catch (ClassCastException e)
		{
			sender.sendMessage("You can only use this command as a player!");
		}
		return true;
	}

	String convertLocation(Location l)
	{
		String out = "";
		out = out + l.getWorld().getName() + ",";
		out = out + l.getBlockX() + ",";
		out = out + l.getBlockY() + ",";
		out = out + l.getBlockZ();
		return out;
	}

	Location convertString(String s)
	{
		String[] parts = s.split(",");
		return new Location(getServer().getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
	}
}
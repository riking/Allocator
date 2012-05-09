package com.benzrf.allocator;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.ItemStack;

public class AllocatorListener implements Listener
{
	List<Material> powerList = new ArrayList<Material>();
	CheckIO CIO = new CheckIO();
	Input I = new Input();
	Output O = new Output();
	Allocator theplugin;
	List<Location> dupePreventer = new ArrayList<Location>();
	
	public AllocatorListener(Allocator plugin)
	{
		this.theplugin = plugin;
		this.powerList.add(Material.REDSTONE_WIRE);
		this.powerList.add(Material.REDSTONE_TORCH_ON);
		this.powerList.add(Material.REDSTONE_TORCH_OFF);
		this.powerList.add(Material.WOOD_PLATE);
		this.powerList.add(Material.STONE_PLATE);
		this.powerList.add(Material.DETECTOR_RAIL);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (this.theplugin.allocatorMap.containsKey(event.getBlock().getLocation()))
		{
			this.theplugin.allocatorMap.remove(event.getBlock().getLocation());
			event.getPlayer().sendMessage(ChatColor.GREEN + "Allocator removed!");
		}
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (this.theplugin.allocatorMap.containsKey(event.getBlock().getLocation()))
		{
			if (!this.powerList.contains(event.getChangedType()))
			{
				return;
			}
			if ((!event.getBlock().isBlockPowered()) && (!event.getBlock().isBlockIndirectlyPowered()))
			{
				return;
			}
			for (Location l : this.dupePreventer)
			{
				if (l.distance(event.getBlock().getLocation()) < 3.0D)
				{
					return;
				}
			}
			this.dupePreventer.add(event.getBlock().getLocation());
			allocate(event);
		}
	}

	@SuppressWarnings("unchecked")
	void allocate(final BlockPhysicsEvent event)
	{
		Block b = event.getBlock();
		Material filter = (Material) theplugin.allocatorMap.get(event.getBlock().getLocation());
		List<ItemStack> input = new ArrayList<ItemStack>();
		List<Runnable> removers = new ArrayList<Runnable>();

		int xAdd = 0;
		int zAdd = 0;
		switch (event.getBlock().getData())
		{
		case 0:
			zAdd = -1;
			break;
		case 1:
			xAdd = 1;
			break;
		case 2:
			zAdd = 1;
			break;
		case 3:
			xAdd = -1;
		}

		b.getWorld().playEffect(b.getLocation(), Effect.CLICK1, 0);

		if (this.CIO.isIOChest(b, xAdd, zAdd))
		{
			Object[] o = this.I.getInputFromChest(b, xAdd, zAdd, filter);
			input = (List<ItemStack>) o[0];
			removers = (List<Runnable>) o[1];
		}
		else if (this.CIO.isIOFurnace(b, xAdd, zAdd))
		{
			Object[] o = this.I.getInputFromFurnace(b, xAdd, zAdd, filter);
			input = (List<ItemStack>) o[0];
			removers = (List<Runnable>) o[1];		}
		else if (this.CIO.isIOStorageMinecart(b, xAdd, zAdd) != null)
		{
			Object[] o = this.I.getInputFromStorageMinecart(this.CIO.isIOStorageMinecart(b, xAdd, zAdd), filter);
			input = (List<ItemStack>) o[0];
			removers = (List<Runnable>) o[1];
		}
		else
		{
			Object[] o = this.I.getInputFromDroppedItems(b, xAdd, zAdd, filter);
			input = (List<ItemStack>) o[0];
			removers = (List<Runnable>) o[1];		}

		if (this.CIO.isIOChest(b, -xAdd, -zAdd))
		{
			this.O.outputToChest(b, xAdd, zAdd, input, removers);
		}
		else if (this.CIO.isIOFurnace(b, -xAdd, -zAdd))
		{
			this.O.outputToFurnace(b, xAdd, zAdd, input, removers);
		}
		else if (this.CIO.isIOStorageMinecart(b, -zAdd, -zAdd) != null)
		{
			this.O.outputToStorageMinecart(this.CIO.isIOStorageMinecart(b, -zAdd, -zAdd), input, removers);
		}
		else
		{
			this.O.outputAsItemEntities(b, xAdd, zAdd, input, removers);
		}
		theplugin.getServer().getScheduler().scheduleSyncDelayedTask(theplugin, new Runnable() {
			public void run()
			{
				dupePreventer.remove(event.getBlock().getLocation());
			}
		}, 3L);
	}
}
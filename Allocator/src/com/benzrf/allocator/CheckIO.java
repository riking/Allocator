package com.benzrf.allocator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.util.Vector;

public class CheckIO
{
	StorageMinecart isIOStorageMinecart(Block b, int xAdd, int zAdd)
	{
		Location l = b.getLocation().add(0.5D + xAdd, 0.0D, 0.5D + zAdd);
		Arrow a = l.getWorld().spawnArrow(l, new Vector(0, 0, 0), 0.0F, 0.0F);
		for (Entity e : a.getNearbyEntities(0.5D, 0.5D, 0.5D))
		{
			if (!(e instanceof StorageMinecart))
				continue;
			a.remove();
			return (StorageMinecart)e;
		}

		a.remove();
		return null;
	}

	boolean isIOFurnace(Block b, int xAdd, int zAdd)
	{
		return (b.getWorld().getBlockTypeIdAt(b.getX() + xAdd, b.getY(), b.getZ() + zAdd) == Material.FURNACE.getId()) || (b.getWorld().getBlockTypeIdAt(b.getX() + xAdd, b.getY(), b.getZ() + zAdd) == Material.BURNING_FURNACE.getId());
	}

	boolean isIOChest(Block b, int xAdd, int zAdd)
	{
		return b.getWorld().getBlockTypeIdAt(b.getX() + xAdd, b.getY(), b.getZ() + zAdd) == Material.CHEST.getId();
	}
}
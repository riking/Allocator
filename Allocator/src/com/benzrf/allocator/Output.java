package com.benzrf.allocator;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.ItemStack;

public class Output
{
	public void outputAsItemEntities(Block b, float xAdd, float zAdd, List<ItemStack> input, List<Runnable> removers)
	{
		xAdd = -xAdd + 0.5F;
		zAdd = -zAdd + 0.5F;

		for (ItemStack i : input)
		{
			Location l = b.getLocation().add(xAdd, 0.0D, zAdd);
			b.getWorld().dropItem(l, i);
		}
		for (Runnable r2 : removers)
		{
			r2.run();
		}
	}

	public void outputToChest(Block b, int xAdd, int zAdd, List<ItemStack> input, List<Runnable> removers)
	{
		Chest c = (Chest)b.getWorld().getBlockAt(b.getX() - xAdd, b.getY(), b.getZ() - zAdd).getState();
		for (ItemStack i : input)
		{
			if (c.getInventory().addItem(new ItemStack[] { i }).containsValue(i))
				continue;
			((Runnable)removers.get(input.indexOf(i))).run();
		}
	}

	public void outputToStorageMinecart(StorageMinecart sm, List<ItemStack> input, List<Runnable> removers)
	{
		for (ItemStack i : input)
		{
			if (sm.getInventory().addItem(i).containsValue(i))
			{
				((Runnable)removers.get(input.indexOf(i))).run();
			}
		}
	}

	public void outputToFurnace(Block b, int xAdd, int zAdd, List<ItemStack> input, List<Runnable> removers)
	{
		Block b2 = b.getWorld().getBlockAt(b.getX() - xAdd, b.getY(), b.getZ() - zAdd);
		int aDir = b.getData();
		aDir = aDir == 3 ? 1 : aDir == 1 ? -1 : aDir == 0 ? -2 : -2;
		int fDir = b2.getData();
		fDir = fDir == 5 ? 1 : fDir == 4 ? -1 : fDir == 3 ? -2 : -2;
		Furnace f = (Furnace) b2.getState();
		if (input.size() > 0)
		{
			ItemStack i = (ItemStack)input.get(0);
			if (aDir == fDir)
			{
				if (f.getInventory().getItem(2) == null)
				{
					f.getInventory().setItem(2, i);
					((Runnable)removers.get(0)).run();
				}
			}
			else if (aDir == -fDir)
			{
				if (f.getInventory().getItem(1) == null)
				{
					f.getInventory().setItem(1, i);
					((Runnable)removers.get(0)).run();
				}

			}
			else if (f.getInventory().getItem(0) == null)
			{
				f.getInventory().setItem(0, i);
				((Runnable)removers.get(0)).run();
			}
		}
	}
}
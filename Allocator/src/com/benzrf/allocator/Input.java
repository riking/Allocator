package com.benzrf.allocator;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Input
{
	Object[] getInputFromDroppedItems(Block b, int xAdd, int zAdd, Material filter)
	{
		List<ItemStack> toReturn = new ArrayList<ItemStack>();
		List<Runnable> alsoToReturn = new ArrayList<Runnable>();
		Arrow a = b.getWorld().spawnArrow(b.getLocation().add(0.5D + xAdd, 0.0D, 0.5D + zAdd), new Vector(0, 0, 0), 0.0F, 0.0F);
		for (Entity e : a.getNearbyEntities(0.5D, 0.5D, 0.5D))
		{
			if (e instanceof Item && (filter.equals(Material.AIR) || ((Item) e).getItemStack().getType().equals(filter)))
			{
				toReturn.add(((Item)e).getItemStack());
				alsoToReturn.add(new Runnable() {
					public Entity i;
					public void run()
					{
						i.remove();
					}
					public Runnable seti(Entity e2)
					{
						i = e2;
						return this;
					}
				}.seti(e));
			}
		}
		a.remove();
		return new Object[] { toReturn, alsoToReturn };
	}

	Object[] getInputFromFurnace(Block b, int xAdd, int zAdd, Material filter)
	{
		List<ItemStack> toReturn = new ArrayList<ItemStack>();
		List<Runnable> alsoToReturn = new ArrayList<Runnable>();
		Block b2 = b.getWorld().getBlockAt(b.getX() + xAdd, b.getY(), b.getZ() + zAdd);
		Furnace f = (Furnace)b2.getState();
		int aDir = b.getData();
		aDir = aDir == 3 ? 1 : aDir == 1 ? -1 : aDir == 0 ? -2 : -2;
		int fDir = b2.getData();
		fDir = fDir == 5 ? 1 : fDir == 4 ? -1 : fDir == 3 ? -2 : -2;
		if (aDir == fDir)
		{
			if ((filter.equals(Material.AIR)) || (f.getInventory().getItem(1).getType().equals(filter)))
			{
				toReturn.add(f.getInventory().getItem(1));
				alsoToReturn.add(new Runnable() {
					public Inventory i;
					public void run()
					{
						this.i.setItem(1, null);
					}
					public Runnable seti(Furnace f2)
					{
						this.i = f2.getInventory();
						return this;
					}
				}.seti(f));
			}
		}
		else if (aDir == -fDir)
		{
			if ((filter.equals(Material.AIR)) || (f.getInventory().getItem(2).getType().equals(filter)))
			{
				toReturn.add(f.getInventory().getItem(2));
				alsoToReturn.add(new Runnable() {
					public Inventory i;
					public void run()
					{
						this.i.setItem(2, null);
					}
					public Runnable seti(Furnace f2)
					{
						this.i = f2.getInventory();
						return this;
					}
				}.seti(f));
			}

		}
		else if ((filter.equals(Material.AIR)) || (f.getInventory().getItem(0).getType().equals(filter)))
		{
			toReturn.add(f.getInventory().getItem(0));
			alsoToReturn.add(new Runnable() {
				public Inventory i;
				public void run()
				{
					this.i.setItem(0, null);
				}
				public Runnable seti(Furnace f2)
				{
					this.i = f2.getInventory();
					return this;
				}
			}.seti(f));
		}

		return new Object[] { toReturn, alsoToReturn };
	}

	Object[] getInputFromStorageMinecart(StorageMinecart sm, Material filter)
	{
		List<ItemStack> toReturn = new ArrayList<ItemStack>();
		List<Runnable> alsoToReturn = new ArrayList<Runnable>();
		Inventory i = sm.getInventory();
		int counter = 0;
		for (ItemStack i2 : i.getContents())
		{
			if (i2 != null)
			{
				if ((filter.equals(Material.AIR)) || (i2.getType().equals(filter)))
				{
					toReturn.add(i2);
					ChestRunnable r = new ChestRunnable();
					r.i = i;
					r.index = counter;
					alsoToReturn.add(r);
					return new Object[] { toReturn, alsoToReturn };
				}
			}
			counter++;
		}
		return new Object[] { toReturn, alsoToReturn };
	}

	Object[] getInputFromChest(Block b, int xAdd, int zAdd, Material filter)
	{
		List<ItemStack> toReturn = new ArrayList<ItemStack>();
		List<Runnable> alsoToReturn = new ArrayList<Runnable>();
		Inventory i = ((Chest)b.getWorld().getBlockAt(b.getX() + xAdd, b.getY(), b.getZ() + zAdd).getState()).getInventory();
		int counter = 0;
		for (ItemStack i2 : i.getContents())
		{
			if (i2 != null)
			{
				if ((filter.equals(Material.AIR)) || (i2.getType().equals(filter)))
				{
					toReturn.add(i2);
					ChestRunnable r = new ChestRunnable();
					r.i = i;
					r.index = counter;
					alsoToReturn.add(r);
					return new Object[] { toReturn, alsoToReturn };
				}
			}
			counter++;
		}
		return new Object[] { toReturn, alsoToReturn };
	}
}

class ChestRunnable implements Runnable
{
	public Inventory i;
	public int index;

	public void run()
	{
		this.i.setItem(this.index, null);
	}
}
package factorization.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract interface IActOnCraft
{
  public abstract void onCraft(ItemStack paramItemStack1, IInventory paramIInventory, int paramInt, ItemStack paramItemStack2, EntityPlayer paramEntityPlayer);
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.IActOnCraft
 * JD-Core Version:    0.6.2
 */
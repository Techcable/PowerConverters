package factorization.api;

import net.minecraft.item.ItemStack;

public abstract interface ISubInventory
{
  public abstract int getSizeInventory(Object paramObject);

  public abstract ItemStack getStackInSlot(Object paramObject, int paramInt);

  public abstract void setInventorySlotContents(Object paramObject, int paramInt, ItemStack paramItemStack);
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.ISubInventory
 * JD-Core Version:    0.6.2
 */
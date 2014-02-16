package factorization.api.datahelpers;

import net.minecraft.nbt.NBTTagCompound;

public abstract class DataHelperNBT extends DataHelper
{
  protected NBTTagCompound tag;

  public NBTTagCompound getTag()
  {
    return tag;
  }

  public boolean isNBT()
  {
    return true;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataHelperNBT
 * JD-Core Version:    0.6.2
 */
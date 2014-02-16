package factorization.api.datahelpers;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;

public class DataOutNBT extends DataHelperNBT
{
  public DataOutNBT(NBTTagCompound theTag)
  {
    tag = theTag;
  }

  public DataOutNBT() {
    this(new NBTTagCompound());
  }

  public NBTTagCompound getTag()
  {
    return tag;
  }

  protected boolean shouldStore(Share share)
  {
    return !share.is_transient;
  }

  public boolean isReader()
  {
    return false;
  }

  protected <E> Object putImplementation(E value) throws IOException
  {
    if ((value instanceof Boolean))
      tag.setBoolean(name, ((Boolean)value).booleanValue());
    else if ((value instanceof Byte))
      tag.setByte(name, ((Byte)value).byteValue());
    else if ((value instanceof Short))
      tag.setShort(name, ((Short)value).shortValue());
    else if ((value instanceof Integer))
      tag.setInteger(name, ((Integer)value).intValue());
    else if ((value instanceof Long))
      tag.setLong(name, ((Long)value).longValue());
    else if ((value instanceof Float))
      tag.setFloat(name, ((Float)value).floatValue());
    else if ((value instanceof Double))
      tag.setDouble(name, ((Double)value).doubleValue());
    else if ((value instanceof String))
      tag.setString(name, (String)value);
    else if ((value instanceof NBTTagCompound)) {
      tag.setCompoundTag(name, (NBTTagCompound)value);
    }
    return value;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataOutNBT
 * JD-Core Version:    0.6.2
 */
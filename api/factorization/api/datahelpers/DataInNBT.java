package factorization.api.datahelpers;

import factorization.shared.Core;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;

public class DataInNBT extends DataHelperNBT
{
  public DataInNBT(NBTTagCompound theTag)
  {
    tag = theTag;
  }

  protected boolean shouldStore(Share share)
  {
    return !share.is_transient;
  }

  public boolean isReader()
  {
    return true;
  }

  protected <E> Object putImplementation(E o) throws IOException
  {
    if (!tag.hasKey(name))
      return o;
    try
    {
      if ((o instanceof Boolean))
        return Boolean.valueOf(tag.getBoolean(name));
      if ((o instanceof Byte))
        return Byte.valueOf(tag.getByte(name));
      if ((o instanceof Short))
        return Short.valueOf(tag.getShort(name));
      if ((o instanceof Integer))
        return Integer.valueOf(tag.getInteger(name));
      if ((o instanceof Long))
        return Long.valueOf(tag.getLong(name));
      if ((o instanceof Float))
        return Float.valueOf(tag.getFloat(name));
      if ((o instanceof Double))
        return Double.valueOf(tag.getDouble(name));
      if ((o instanceof String))
        return tag.getString(name);
      if ((o instanceof NBTTagCompound))
        return tag.getCompoundTag(name);
    }
    catch (Throwable t) {
      Core.logWarning("Failed to load " + name + "; will use default value " + o, new Object[0]);
      Core.logWarning("The tag: " + tag, new Object[0]);
      t.printStackTrace();
    }
    return o;
  }

  public boolean hasLegacy(String name)
  {
    return tag.hasKey(name);
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataInNBT
 * JD-Core Version:    0.6.2
 */
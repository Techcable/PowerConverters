package factorization.api.datahelpers;

import factorization.api.FzOrientation;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class DataHelper
{
  protected String name;
  protected boolean valid;
  private static final Class[] validTypes = { Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, NBTTagCompound.class, String.class };

  public DataHelper as(Share share, String set_name)
  {
    name = set_name;
    valid = shouldStore(share);
    return this;
  }

  public DataHelper asSameShare(String set_name) {
    name = set_name;
    return this;
  }
  protected abstract boolean shouldStore(Share paramShare);

  public abstract boolean isReader();

  public boolean isWriter() { return !isReader(); }

  public NBTTagCompound getTag()
  {
    return null;
  }

  public boolean isNBT() {
    return false;
  }

  public <E> E put(E o)
    throws IOException
  {
    if (!valid) {
      return o;
    }
    if ((o instanceof IDataSerializable)) {
      return (E) ((IDataSerializable)o).serialize(name, this);
    }
    if ((o instanceof Enum)) {
      Enum value = (Enum)o;
      int i = value.ordinal();
      i = ((Integer)put(Integer.valueOf(i))).intValue();
      if (isWriter()) {
        return (E) value;
      }
      return (E) ((Enum[])value.getClass().getEnumConstants())[i];
    }
    if ((o instanceof ItemStack)) {
      ItemStack value = (ItemStack)o;
      NBTTagCompound writtenTag = value.writeToNBT(new NBTTagCompound());
      if (isReader()) {
        return (E) ItemStack.loadItemStackFromNBT((NBTTagCompound)put(writtenTag));
      }
      put(writtenTag);
      return o;
    }

    return (E) putImplementation(o);
  }

  protected abstract <E> Object putImplementation(E paramE)
    throws IOException;

  public final boolean putBoolean(boolean value)
    throws IOException
  {
    return ((Boolean)put(Boolean.valueOf(value))).booleanValue(); } 
  public final byte putByte(byte value) throws IOException { return ((Byte)put(Byte.valueOf(value))).byteValue(); } 
  public final short putShort(short value) throws IOException { return ((Short)put(Short.valueOf(value))).shortValue(); } 
  public final int putInt(int value) throws IOException { return ((Integer)put(Integer.valueOf(value))).intValue(); } 
  public final long putLong(long value) throws IOException { return ((Long)put(Long.valueOf(value))).longValue(); } 
  public final float putFloat(float value) throws IOException { return ((Float)put(Float.valueOf(value))).floatValue(); } 
  public final double putDouble(double value) throws IOException { return ((Double)put(Double.valueOf(value))).doubleValue(); } 
  public final String putString(String value) throws IOException {
    return (String)put(value); } 
  public final FzOrientation putFzOrientation(FzOrientation value) throws IOException { return (FzOrientation)put(value); } 
  public final ItemStack putItemStack(ItemStack value) throws IOException {
    if ((isReader()) && (value == null)) {
      value = new ItemStack(0, 0, 0);
    }
    return (ItemStack)put(value);
  }
  public final ArrayList<ItemStack> putItemArray(ArrayList<ItemStack> value) throws IOException {
    String prefix = name;
    int len = asSameShare(prefix + "_len").putInt(value.size());
    if (isReader()) {
      value.clear();
      value.ensureCapacity(len);
      for (int i = 0; i < len; i++)
        value.add(asSameShare(prefix + "_" + i).putItemStack(null));
    }
    else {
      for (int i = 0; i < len; i++) {
        asSameShare(prefix + "_" + i).putItemStack((ItemStack)value.get(i));
      }
    }
    return value;
  }
  public final NBTTagCompound putTag(NBTTagCompound value) throws IOException {
    return (NBTTagCompound)put(value);
  }
  public final <E extends Enum> E putEnum(E value) throws IOException {
    return (E) (Enum)put(value);
  }

  public final Object putUntypedOject(Object value)
    throws IOException
  {
    if (!valid) {
      return value;
    }
    String orig_name = name;
    if (isReader()) {
      byte typeIndex = asSameShare(orig_name + ".type").putByte((byte)-1);
      asSameShare(orig_name);
      if ((typeIndex < 0) || (typeIndex > validTypes.length)) {
        return value;
      }
      Class type = validTypes[typeIndex];
      if ((value != null) && (value.getClass() == type)) {
        return put(value);
      }

      if (type == Boolean.class)
        value = Boolean.valueOf(false);
      else if (type == Short.class)
        value = Short.valueOf((short)0);
      else if (type == Integer.class)
        value = Integer.valueOf(0);
      else if (type == Long.class)
        value = Long.valueOf(0L);
      else if (type == Float.class)
        value = Float.valueOf(0.0F);
      else if (type == Double.class)
        value = Double.valueOf(0.0D);
      else if (type == NBTTagCompound.class)
        value = new NBTTagCompound();
      else if (type == String.class)
        value = "";
      else {
        return null;
      }
      return put(value);
    }
    Class value_type = value.getClass();
    for (byte i = 0; i < validTypes.length; i = (byte)(i + 1)) {
      Class type = validTypes[i];
      if (value_type == type) {
        asSameShare(orig_name + ".type").putByte(i);
        asSameShare(orig_name);
        put(value);
        return value;
      }
    }
    throw new IllegalArgumentException("Don't know how to handle: " + value_type);
  }

  public void log(String message)
  {
  }

  public boolean hasLegacy(String name) {
    return false;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataHelper
 * JD-Core Version:    0.6.2
 */
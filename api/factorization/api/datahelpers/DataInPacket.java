package factorization.api.datahelpers;

import cpw.mods.fml.relauncher.Side;
import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class DataInPacket extends DataHelper
{
  private final DataInput dis;
  private final Side side;

  public DataInPacket(DataInput dis, Side side)
  {
    this.dis = dis;
    this.side = side;
  }

  protected boolean shouldStore(Share share)
  {
    return share.is_public;
  }

  public boolean isReader()
  {
    return true;
  }

  protected <E> Object putImplementation(E o) throws IOException
  {
    if ((o instanceof Boolean))
      return Boolean.valueOf(dis.readBoolean());
    if ((o instanceof Byte))
      return Byte.valueOf(dis.readByte());
    if ((o instanceof Short))
      return Short.valueOf(dis.readShort());
    if ((o instanceof Integer))
      return Integer.valueOf(dis.readInt());
    if ((o instanceof Long))
      return Long.valueOf(dis.readLong());
    if ((o instanceof Float))
      return Float.valueOf(dis.readFloat());
    if ((o instanceof Double))
      return Double.valueOf(dis.readDouble());
    if ((o instanceof String))
      return dis.readUTF();
    if ((o instanceof NBTTagCompound)) {
      return (NBTTagCompound)NBTBase.readNamedTag(dis);
    }
    return o;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataInPacket
 * JD-Core Version:    0.6.2
 */
package factorization.api.datahelpers;

import cpw.mods.fml.relauncher.Side;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class DataOutPacket extends DataHelper
{
  private final DataOutput dos;
  private final Side side;

  public DataOutPacket(DataOutput dos, Side side)
  {
    this.dos = dos;
    this.side = side;
  }

  protected boolean shouldStore(Share share)
  {
    return share.is_public;
  }

  public boolean isReader()
  {
    return false;
  }

  protected <E> Object putImplementation(E value) throws IOException
  {
    if ((value instanceof Boolean))
      dos.writeBoolean(((Boolean)value).booleanValue());
    else if ((value instanceof Byte))
      dos.writeByte(((Byte)value).byteValue());
    else if ((value instanceof Short))
      dos.writeShort(((Short)value).shortValue());
    else if ((value instanceof Integer))
      dos.writeInt(((Integer)value).intValue());
    else if ((value instanceof Long))
      dos.writeLong(((Long)value).longValue());
    else if ((value instanceof Float))
      dos.writeFloat(((Float)value).floatValue());
    else if ((value instanceof Double))
      dos.writeDouble(((Double)value).doubleValue());
    else if ((value instanceof String))
      dos.writeUTF((String)value);
    else if ((value instanceof NBTTagCompound)) {
      NBTBase.writeNamedTag((NBTBase)value, dos);
    }
    return value;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataOutPacket
 * JD-Core Version:    0.6.2
 */
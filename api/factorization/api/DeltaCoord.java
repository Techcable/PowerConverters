package factorization.api;

import com.google.common.base.Splitter;
import factorization.api.datahelpers.DataHelper;
import factorization.api.datahelpers.IDataSerializable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public class DeltaCoord
  implements IDataSerializable
{
  public int x;
  public int y;
  public int z;
  public static DeltaCoord[] directNeighbors = { d(1, 0, 0), d(-1, 0, 0), d(0, -1, 0), d(0, 1, 0), d(0, 0, -1), d(0, 0, 1) };

  public static DeltaCoord[] planeNeighbors = { d(1, 0, 0), d(-1, 0, 0), d(0, 0, -1), d(0, 0, 1) };

  private static Splitter COMMA_SPLITTER = Splitter.on(',');

  public DeltaCoord()
  {
    x = (this.y = this.z = 0);
  }

  public DeltaCoord(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public DeltaCoord add(DeltaCoord o) {
    return new DeltaCoord(x + o.x, y + o.y, z + o.z);
  }

  public DeltaCoord scale(double d) {
    return new DeltaCoord((int)(x * d), (int)(y * d), (int)(z * d));
  }

  public boolean isZero() {
    return (x == 0) && (y == 0) && (z == 0);
  }

  public String toString()
  {
    return "DeltaCoord(" + x + ", " + y + ", " + z + ")";
  }

  private static DeltaCoord d(int x, int y, int z) {
    return new DeltaCoord(x, y, z);
  }

  public double getAngleHorizontal()
  {
    return Math.atan2(z, -x);
  }

  public ForgeDirection getDirection() {
    ForgeDirection[] values = ForgeDirection.VALID_DIRECTIONS;
    for (int i = 0; i < values.length; i++) {
      ForgeDirection d = values[i];
      if ((d.offsetX == x) && (d.offsetY == y) && (d.offsetZ == z)) {
        return d;
      }
    }
    return ForgeDirection.UNKNOWN;
  }

  public int getFaceSide() {
    if ((x == 0) && (z == 0)) {
      if (y == -1)
        return 0;
      if (y == 1)
        return 1;
    }
    else if ((y == 0) && (x == 0)) {
      if (z == -1)
        return 2;
      if (z == 1)
        return 3;
    }
    else if ((y == 0) && (z == 0)) {
      if (x == -1)
        return 4;
      if (x == 1) {
        return 5;
      }
    }

    return -1;
  }

  public DeltaCoord reverse() {
    return new DeltaCoord(-x, -y, -z);
  }

  public boolean isSubmissive() {
    return (x < 0) || (y < 0) || (z < 0);
  }

  public boolean equals(DeltaCoord o) {
    return (x == o.x) && (y == o.y) && (z == o.z);
  }

  public void alignToAxis() {
    int ax = Math.abs(x);
    int ay = Math.abs(y);
    int az = Math.abs(z);
    if ((ax >= ay) && (ax >= az)) {
      x = ((int)Math.signum(x));
      return;
    }
    if ((ay >= ax) && (ay >= az)) {
      y = ((int)Math.signum(y));
      return;
    }
    if ((az >= ay) && (az >= ax)) {
      z = ((int)Math.signum(z));
      return;
    }
    x = (this.y = this.z = 0);
  }

  public int get(int id) {
    switch (id) { case 0:
      return x;
    case 1:
      return y;
    case 2:
      return z; }
    throw new RuntimeException("not an dimension index");
  }

  public void set(int id, int val)
  {
    switch (id) { case 0:
      x = val; break;
    case 1:
      y = val; break;
    case 2:
      z = val; break;
    default:
      throw new RuntimeException("not an dimension index"); }
  }

  public void init(int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void writeToTag(String prefix, NBTTagCompound tag) {
    tag.setInteger(prefix + "dx", x);
    tag.setInteger(prefix + "dy", y);
    tag.setInteger(prefix + "dz", z);
  }

  public static DeltaCoord readFromTag(String prefix, NBTTagCompound tag) {
    return new DeltaCoord(tag.getInteger(prefix + "dx"), tag.getInteger(prefix + "dy"), tag.getInteger(prefix + "dz"));
  }

  public static DeltaCoord read(DataInput di) throws IOException {
    return new DeltaCoord(di.readInt(), di.readInt(), di.readInt());
  }

  public void write(DataOutput out) throws IOException {
    for (int i = 0; i < 3; i++)
      out.writeInt(get(i));
  }

  public static DeltaCoord parse(String input)
  {
    DeltaCoord ret = new DeltaCoord();
    int i = 0;
    for (String s : COMMA_SPLITTER.split(input)) {
      ret.set(i, Integer.parseInt(s));
      i++;
    }
    return ret;
  }

  public IDataSerializable serialize(String prefix, DataHelper data) throws IOException
  {
    x = ((Integer)data.asSameShare(prefix + "dx").put(Integer.valueOf(x))).intValue();
    y = ((Integer)data.asSameShare(prefix + "dy").put(Integer.valueOf(y))).intValue();
    z = ((Integer)data.asSameShare(prefix + "dz").put(Integer.valueOf(z))).intValue();
    return this;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.DeltaCoord
 * JD-Core Version:    0.6.2
 */
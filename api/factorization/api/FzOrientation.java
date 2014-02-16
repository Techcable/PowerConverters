package factorization.api;

import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;

public enum FzOrientation
{
  FACE_DOWN_POINT_SOUTH(ForgeDirection.DOWN, ForgeDirection.SOUTH), 
  FACE_DOWN_POINT_NORTH(ForgeDirection.DOWN, ForgeDirection.NORTH), 
  FACE_DOWN_POINT_EAST(ForgeDirection.DOWN, ForgeDirection.EAST), 
  FACE_DOWN_POINT_WEST(ForgeDirection.DOWN, ForgeDirection.WEST), 

  FACE_UP_POINT_SOUTH(ForgeDirection.UP, ForgeDirection.SOUTH), 
  FACE_UP_POINT_NORTH(ForgeDirection.UP, ForgeDirection.NORTH), 
  FACE_UP_POINT_EAST(ForgeDirection.UP, ForgeDirection.EAST), 
  FACE_UP_POINT_WEST(ForgeDirection.UP, ForgeDirection.WEST), 

  FACE_NORTH_POINT_UP(ForgeDirection.NORTH, ForgeDirection.UP), 
  FACE_NORTH_POINT_DOWN(ForgeDirection.NORTH, ForgeDirection.DOWN), 
  FACE_NORTH_POINT_EAST(ForgeDirection.NORTH, ForgeDirection.EAST), 
  FACE_NORTH_POINT_WEST(ForgeDirection.NORTH, ForgeDirection.WEST), 

  FACE_SOUTH_POINT_UP(ForgeDirection.SOUTH, ForgeDirection.UP), 
  FACE_SOUTH_POINT_DOWN(ForgeDirection.SOUTH, ForgeDirection.DOWN), 
  FACE_SOUTH_POINT_EAST(ForgeDirection.SOUTH, ForgeDirection.EAST), 
  FACE_SOUTH_POINT_WEST(ForgeDirection.SOUTH, ForgeDirection.WEST), 

  FACE_WEST_POINT_UP(ForgeDirection.WEST, ForgeDirection.UP), 
  FACE_WEST_POINT_DOWN(ForgeDirection.WEST, ForgeDirection.DOWN), 
  FACE_WEST_POINT_SOUTH(ForgeDirection.WEST, ForgeDirection.SOUTH), 
  FACE_WEST_POINT_NORTH(ForgeDirection.WEST, ForgeDirection.NORTH), 

  FACE_EAST_POINT_UP(ForgeDirection.EAST, ForgeDirection.UP), 
  FACE_EAST_POINT_DOWN(ForgeDirection.EAST, ForgeDirection.DOWN), 
  FACE_EAST_POINT_SOUTH(ForgeDirection.EAST, ForgeDirection.SOUTH), 
  FACE_EAST_POINT_NORTH(ForgeDirection.EAST, ForgeDirection.NORTH), 

  FACE_UNKNOWN_POINT_UNKNOWN(ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN);

  public static final FzOrientation UNKNOWN;
  public final ForgeDirection facing;
  public final ForgeDirection top;
  private FzOrientation nextFaceRotation;
  private FzOrientation prevFaceRotation;
  private int rotation;
  private FzOrientation swapped;
  private static FzOrientation[] valuesCache;

  private FzOrientation(ForgeDirection facing, ForgeDirection top)
  {
    this.facing = facing;
    this.top = top;
  }

  private void setup()
  {
    if (this == UNKNOWN) {
      prevFaceRotation = this; nextFaceRotation = this;
    }
    nextFaceRotation = find(facing, top.getRotation(facing));
    prevFaceRotation = find(facing, top.getRotation(facing).getRotation(facing).getRotation(facing));
  }

  private void setupRotation() {
    if (this == UNKNOWN) {
      return;
    }
    int rcount = 0;
    FzOrientation head = fromDirection(facing);
    for (int i = 0; i < 5; i++) {
      if (head == this) {
        rotation = rcount;
      }
      rcount++;
      head = head.nextFaceRotation;
    }
  }

  private static FzOrientation find(ForgeDirection f, ForgeDirection t) {
    for (FzOrientation o : values()) {
      if ((o.facing == f) && (o.top == t)) {
        return o;
      }
    }
    return UNKNOWN;
  }

  public FzOrientation rotateOnFace(int count)
  {
    count %= 4;
    if (count > 0) {
      FzOrientation here = this;
      while (count > 0) {
        count--;
        here = here.nextFaceRotation;
      }
      return here;
    }if (count < 0) {
      FzOrientation here = this;
      while (count < 0) {
        count++;
        here = here.prevFaceRotation;
      }
      return here;
    }
    return this;
  }

  public FzOrientation getNextRotationOnFace()
  {
    return nextFaceRotation;
  }

  public FzOrientation getPrevRotationOnFace() {
    return prevFaceRotation;
  }

  public FzOrientation getNextRotationOnTop() {
    return getSwapped().getNextRotationOnFace().getSwapped();
  }

  public FzOrientation getPrevRotationOnTop() {
    return getSwapped().getPrevRotationOnFace().getSwapped();
  }

  public FzOrientation rotateOnTop(int count) {
    return getSwapped().rotateOnFace(count).getSwapped();
  }

  public static FzOrientation getOrientation(int index) {
    if ((index >= 0) && (index < valuesCache.length)) {
      return valuesCache[index];
    }
    return UNKNOWN;
  }

  public static FzOrientation fromDirection(ForgeDirection dir) {
    if (dir == ForgeDirection.UNKNOWN) {
      return UNKNOWN;
    }
    return valuesCache[(dir.ordinal() * 4)];
  }

  public FzOrientation pointTopTo(ForgeDirection newTop)
  {
    FzOrientation fzo = this;
    for (int i = 0; i < 4; i++) {
      if (fzo.top == newTop) {
        return fzo;
      }
      fzo = fzo.nextFaceRotation;
    }
    return UNKNOWN;
  }

  public int getRotation() {
    return rotation;
  }

  public void setDiagonalVector(Vec3 vec) {
    vec.xCoord = facing.offsetX;
    vec.yCoord = facing.offsetY;
    vec.zCoord = facing.offsetZ;
    vec.xCoord += top.offsetX;
    vec.yCoord += top.offsetY;
    vec.zCoord += top.offsetZ;
  }

  public Vec3 getDiagonalVector() {
    Vec3 ret = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
    setDiagonalVector(ret);
    return ret;
  }

  public FzOrientation getSwapped() {
    return swapped;
  }

  static
  {
    UNKNOWN = FACE_UNKNOWN_POINT_UNKNOWN;

    valuesCache = values();

    for (FzOrientation o : values()) {
      o.setup();
    }
    for (FzOrientation o : values()) {
      o.setupRotation();
    }
    for (FzOrientation o : values()) {
      for (FzOrientation t : values()) {
        if ((o.facing == t.top) && (o.top == t.facing)) {
          o.swapped = t;
          break;
        }
      }
    }
    if (valuesCache.length == 0)
      throw new RuntimeException("lolwut");
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.FzOrientation
 * JD-Core Version:    0.6.2
 */
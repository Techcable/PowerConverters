package factorization.api;

import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import factorization.api.datahelpers.DataHelper;
import factorization.api.datahelpers.IDataSerializable;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class Quaternion
  implements IDataSerializable
{
  public double w;
  public double x;
  public double y;
  public double z;
  private static ThreadLocal<double[]> localStaticArray = new ThreadLocal()
  {
    protected double[] initialValue() {
      return new double[4];
    }
  };

  private static Quaternion[] quat_cache = new Quaternion[FzOrientation.values().length];
  private static final double DOT_THRESHOLD = 0.9995000000000001D;
  private static Vec3 uvCache = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

  public Quaternion()
  {
    this(1.0D, 0.0D, 0.0D, 0.0D);
  }

  public Quaternion(double w, double x, double y, double z) {
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Quaternion(Quaternion orig) {
    w = orig.w;
    x = orig.x;
    y = orig.y;
    z = orig.z;
  }

  public Quaternion(double[] init) {
    this(init[0], init[1], init[2], init[3]);
    assert (init.length == 4);
  }

  public Quaternion(double w, Vec3 v) {
    this(w, v.xCoord, v.yCoord, v.zCoord);
  }

  public Quaternion(double w, ForgeDirection dir) {
    this(w, dir.offsetX, dir.offsetY, dir.offsetZ);
  }

  public void loadFrom(VectorUV v) {
    w = 1.0D;
    x = v.x;
    y = v.y;
    z = v.z;
  }

  public boolean isEqual(Quaternion other) {
    return (w == other.w) && (x == other.x) && (y == other.y) && (z == other.z);
  }

  public String toString()
  {
    return "Quaternion(" + w + ", " + x + ", " + y + ", " + z + ")";
  }

  public void writeToTag(NBTTagCompound tag, String prefix) {
    tag.setDouble(prefix + "w", w);
    tag.setDouble(prefix + "x", x);
    tag.setDouble(prefix + "y", y);
    tag.setDouble(prefix + "z", z);
  }

  public static Quaternion loadFromTag(NBTTagCompound tag, String prefix) {
    return new Quaternion(tag.getDouble(prefix + "w"), tag.getDouble(prefix + "x"), tag.getDouble(prefix + "y"), tag.getDouble(prefix + "z"));
  }

  public void write(ByteArrayDataOutput out) {
    double[] d = toStaticArray();
    for (int i = 0; i < d.length; i++)
      out.writeDouble(d[i]);
  }

  public void write(DataOutputStream out) throws IOException
  {
    double[] d = toStaticArray();
    for (int i = 0; i < d.length; i++)
      out.writeDouble(d[i]);
  }

  public static Quaternion read(DataInput in) throws IOException
  {
    double[] d = (double[])localStaticArray.get();
    for (int i = 0; i < d.length; i++) {
      d[i] = in.readDouble();
    }
    return new Quaternion(d);
  }

  public IDataSerializable serialize(String name_prefix, DataHelper data) throws IOException
  {
    w = ((Double)data.asSameShare(name_prefix + "w").put(Double.valueOf(w))).doubleValue();
    x = ((Double)data.asSameShare(name_prefix + "x").put(Double.valueOf(x))).doubleValue();
    y = ((Double)data.asSameShare(name_prefix + "y").put(Double.valueOf(y))).doubleValue();
    z = ((Double)data.asSameShare(name_prefix + "z").put(Double.valueOf(z))).doubleValue();
    return this;
  }

  public double[] fillArray(double[] out) {
    out[0] = w;
    out[1] = x;
    out[2] = y;
    out[3] = z;
    return out;
  }

  public double[] toArray() {
    return fillArray(new double[4]);
  }

  public double[] toStaticArray()
  {
    return fillArray((double[])localStaticArray.get());
  }

  public boolean isZero() {
    return (x == 0.0D) && (y == 0.0D) && (z == 0.0D);
  }

  public void update(double nw, double nx, double ny, double nz) {
    w = nw;
    x = nx;
    y = ny;
    z = nz;
  }

  public void update(Quaternion other) {
    update(other.w, other.x, other.y, other.z);
  }

  public void update(ForgeDirection dir) {
    update(w, dir.offsetX, dir.offsetY, dir.offsetZ);
  }

  public void updateVector(Vec3 v) {
    v.xCoord = x;
    v.yCoord = y;
    v.zCoord = z;
  }

  public Vec3 toVector() {
    return Vec3.createVectorHelper(x, y, z);
  }

  public void incrNormalize()
  {
    double normSquared = magnitudeSquared();
    if ((normSquared == 1.0D) || (normSquared == 0.0D)) {
      return;
    }
    double norm = Math.sqrt(normSquared);
    w /= norm;
    x /= norm;
    y /= norm;
    z /= norm;
  }
  //I think this is correct
  public static Quaternion getRotationQuaternion(FzOrientation orient) {
    if (orient.top == ForgeDirection.UP);
    return getRotationQuaternionRadians(Math.toRadians(orient.getRotation() * 90), orient.facing);
  }

  public static Quaternion getRotationQuaternionRadians(double angle, Vec3 axis) {
    double halfAngle = angle / 2.0D;
    double sin = Math.sin(halfAngle);
    return new Quaternion(Math.cos(halfAngle), axis.xCoord * sin, axis.yCoord * sin, axis.zCoord * sin);
  }

  public static Quaternion getRotationQuaternionRadians(double angle, ForgeDirection axis) {
    double halfAngle = angle / 2.0D;
    double sin = Math.sin(halfAngle);
    return new Quaternion(Math.cos(halfAngle), axis.offsetX * sin, axis.offsetY * sin, axis.offsetZ * sin);
  }

  public static Quaternion getRotationQuaternionRadians(double angle, double ax, double ay, double az) {
    double halfAngle = angle / 2.0D;
    double sin = Math.sin(halfAngle);
    return new Quaternion(Math.cos(halfAngle), ax * sin, ay * sin, az * sin);
  }

  public static Quaternion fromOrientation(FzOrientation orient)
  {
    int ord = orient.ordinal();
    if (quat_cache[ord] != null) {
      return quat_cache[ord];
    }
    if (orient == FzOrientation.UNKNOWN) {
      return quat_cache[ord] = new Quaternion();
    }
    Vec3 target = orient.getDiagonalVector();

    double quart = Math.toRadians(90.0D);
    int rotation = orient.getRotation();
    Quaternion q1;
    switch (orient.facing.ordinal()) {

    case 1:
      q1 = getRotationQuaternionRadians(0.0D * quart, ForgeDirection.WEST);
      rotation = 5 - rotation;
      break;
    case 2:
      q1 = getRotationQuaternionRadians(2.0D * quart, ForgeDirection.WEST);
      rotation = 3 - rotation;
      break;
    case 3:
      q1 = getRotationQuaternionRadians(1.0D * quart, ForgeDirection.WEST);
      rotation = 5 - rotation;
      break;
    case 4:
      q1 = getRotationQuaternionRadians(-1.0D * quart, ForgeDirection.WEST);
      rotation = 3 - rotation;
      break;
    case 5:
      q1 = getRotationQuaternionRadians(1.0D * quart, ForgeDirection.NORTH);

      rotation += Math.abs(orient.top.offsetZ) * 2;
      break;
    case 6:
      q1 = getRotationQuaternionRadians(-1.0D * quart, ForgeDirection.NORTH);
      rotation += Math.abs(orient.top.offsetY) * 2;
      break;
    default:
      return quat_cache[ord] =  new Quaternion();
    }
    Quaternion q2 = getRotationQuaternionRadians(rotation * quart, orient.facing);
    q2.incrMultiply(q1);
    return quat_cache[ord] =  q2;
  }

  public double setVector(Vec3 axis)
  {
    double halfAngle = Math.acos(w);
    double sin = Math.sin(halfAngle);
    axis.xCoord = (x / sin);
    axis.yCoord = (y / sin);
    axis.zCoord = (z / sin);
    return halfAngle * 2.0D;
  }

  @SideOnly(Side.CLIENT)
  public void glRotate() {
    double halfAngle = Math.acos(w);
    double sin = Math.sin(halfAngle);
    GL11.glRotatef((float)Math.toDegrees(halfAngle * 2.0D), (float)(x / sin), (float)(y / sin), (float)(z / sin));
  }

  public double dotProduct(Quaternion other) {
    return w * other.w + x * other.x + y * other.y + z * other.z;
  }

  public void incrLerp(Quaternion other, double t) {
    other.incrAdd(this, -1.0D);
    other.incrScale(t);
    incrAdd(other);
    incrNormalize();
  }

  public Quaternion slerp(Quaternion other, double t)
  {
    double dot = dotProduct(other);

    if (dot > 0.9995000000000001D)
    {
      Quaternion result = new Quaternion(this);
      Quaternion temp = other.add(this, -1.0D);
      temp.incrScale(t);
      result.incrAdd(temp);
      result.incrNormalize();
      return result;
    }
    dot = Math.min(-1.0D, Math.max(1.0D, dot));
    double theta_0 = Math.acos(dot);
    double theta = theta_0 * t;

    Quaternion v2 = other.add(this, -dot);
    v2.incrNormalize();

    Quaternion ret = scale(Math.cos(theta));
    v2.incrScale(Math.sin(theta));
    ret.incrAdd(v2);
    return ret;
  }

  public double getAngleBetween(Quaternion other) {
    double dot = dotProduct(other);
    dot = Math.min(-1.0D, Math.max(1.0D, dot));
    return Math.acos(dot);
  }

  public double magnitude()
  {
    return Math.sqrt(w * w + x * x + y * y + z * z);
  }

  public double magnitudeSquared() {
    return w * w + x * x + y * y + z * z;
  }

  public double incrDistance(Quaternion other) {
    incrAdd(other);
    return magnitude();
  }

  public void incrConjugate() {
    x *= -1.0D;
    y *= -1.0D;
    z *= -1.0D;
  }

  public void incrAdd(Quaternion other) {
    w += other.w;
    x += other.x;
    y += other.y;
    z += other.z;
  }

  public void incrAdd(Quaternion other, double scale) {
    w += other.w * scale;
    x += other.x * scale;
    y += other.y * scale;
    z += other.z * scale;
  }

  public void incrMultiply(Quaternion other)
  {
    double nw = w * other.w - x * other.x - y * other.y - z * other.z;
    double nx = w * other.x + x * other.w + y * other.z - z * other.y;
    double ny = w * other.y - x * other.z + y * other.w + z * other.x;
    double nz = w * other.z + x * other.y - y * other.x + z * other.w;
    update(nw, nx, ny, nz);
  }

  public void incrScale(double scaler) {
    w *= scaler;
    x *= scaler;
    y *= scaler;
    z *= scaler;
  }

  public void incrUnit() {
    incrScale(1.0D / magnitude());
  }

  public void incrReciprocal() {
    double m = magnitude();
    incrConjugate();
    incrScale(1.0D / (m * m));
  }

  public void incrCross(Quaternion other) {
    double X = y * other.z - z * other.y;
    double Y = z * other.x - x * other.z;
    double Z = x * other.y - y * other.x;
    x = X;
    y = Y;
    z = Z;
  }

  public void applyRotation(Vec3 p)
  {
    Quaternion point = new Quaternion(0.0D, p);
    Quaternion trans = multiply(point).multiply(conjugate());

    p.xCoord = trans.x;
    p.yCoord = trans.y;
    p.zCoord = trans.z;
  }

  public void applyRotation(VectorUV vec)
  {
    uvCache.xCoord = vec.x;
    uvCache.yCoord = vec.y;
    uvCache.zCoord = vec.z;
    applyRotation(uvCache);
    vec.x = uvCache.xCoord;
    vec.y = uvCache.yCoord;
    vec.z = uvCache.zCoord;
  }

  public double distance(Quaternion other)
  {
    return add(other).magnitude();
  }

  public Quaternion conjugate() {
    Quaternion ret = new Quaternion(this);
    ret.incrConjugate();
    return ret;
  }

  public Quaternion add(Quaternion other) {
    Quaternion ret = new Quaternion(this);
    ret.incrAdd(other);
    return ret;
  }

  public Quaternion add(Quaternion other, double scale) {
    Quaternion ret = new Quaternion(this);
    ret.incrAdd(other, scale);
    return ret;
  }

  public Quaternion multiply(Quaternion other) {
    Quaternion a = new Quaternion(this);
    a.incrMultiply(other);
    return a;
  }

  public Quaternion scale(double scaler) {
    Quaternion a = new Quaternion(this);
    a.incrScale(scaler);
    return a;
  }

  public Quaternion unit() {
    Quaternion r = new Quaternion(this);
    r.incrUnit();
    return r;
  }

  public Quaternion reciprocal() {
    Quaternion r = new Quaternion(this);
    r.incrReciprocal();
    return r;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.Quaternion
 * JD-Core Version:    0.6.2
 */
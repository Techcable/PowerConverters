package factorization.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class InvasiveCharge extends TileEntity
  implements IChargeConductor
{
  int id;
  int md;
  Charge charge = new Charge(this);

  public void initialize(int id, int md)
  {
    this.id = id;
    this.md = md;
  }

  public Coord getCoord() {
    return new Coord(this);
  }

  public String getInfo()
  {
    return null;
  }

  public Charge getCharge()
  {
    return charge;
  }

  public void writeToNBT(NBTTagCompound tag)
  {
    super.writeToNBT(tag);
    charge.writeToNBT(tag);
  }

  public void readFromNBT(NBTTagCompound tag)
  {
    super.readFromNBT(tag);
    charge.readFromNBT(tag);
  }

  public void updateEntity()
  {
    super.updateEntity();
    if ((worldObj.getBlockId(xCoord, yCoord, zCoord) != id) || (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != md)) {
      Coord me = getCoord();
      worldObj.removeBlockTileEntity(xCoord, yCoord, zCoord);
      return;
    }
    charge.update();
    if (!worldObj.isRemote);
  }

  public void invalidate()
  {
    super.invalidate();
    charge.invalidate();
  }

  public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z)
  {
    return false;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.InvasiveCharge
 * JD-Core Version:    0.6.2
 */
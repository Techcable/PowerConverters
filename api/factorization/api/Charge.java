package factorization.api;

import factorization.api.datahelpers.DataHelper;
import factorization.api.datahelpers.IDataSerializable;
import factorization.api.datahelpers.Share;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Charge
  implements IDataSerializable
{
  ConductorSet conductorSet = null;
  IChargeConductor conductor = null;
  boolean isConductorSetLeader = false;
  boolean justCreated = true;

  public Charge(IChargeConductor conductor) {
    this.conductor = conductor;
  }

  public int getValue() {
    if ((conductorSet == null) || (conductorSet.memberCount == 0)) {
      return 0;
    }
    int chargeShare = conductorSet.totalCharge / conductorSet.memberCount;
    if (isConductorSetLeader) {
      chargeShare += conductorSet.totalCharge % conductorSet.memberCount;
    }
    return chargeShare;
  }

  public void setValue(int newCharge) {
    createOrJoinConductorSet();
    conductorSet.totalCharge += newCharge - getValue();
  }

  public int addValue(int chargeToAdd) {
    createOrJoinConductorSet();
    return conductorSet.totalCharge += chargeToAdd;
  }

  public int deplete()
  {
    int ret = getValue();
    setValue(0);
    return ret;
  }

  public int deplete(int toTake) {
    int c = getValue();
    toTake = Math.min(toTake, c);
    setValue(c - toTake);
    return toTake;
  }

  public int tryTake(int toTake) {
    int c = getValue();
    if (c < toTake) {
      return 0;
    }
    setValue(c - toTake);
    return toTake;
  }

  public void writeToNBT(NBTTagCompound tag, String name) {
    tag.setInteger(name, getValue());
  }

  public void writeToNBT(NBTTagCompound tag) {
    writeToNBT(tag, "charge");
  }

  public void readFromNBT(NBTTagCompound tag, String name) {
    int val = tag.getInteger(name);
    setValue(val < 0 ? 0 : val);
  }

  public void readFromNBT(NBTTagCompound tag) {
    readFromNBT(tag, "charge");
  }

  void createOrJoinConductorSet() {
    if (conductorSet != null) {
      return;
    }
    Coord here = conductor.getCoord();
    if (here.w == null)
    {
      ConductorSet assignedConductorSet = new ConductorSet(conductor);
      return;
    }
    Iterable<Coord> neighbors = here.getNeighborsAdjacent();
    for (Coord n : neighbors) {
      ChargeMetalBlockConductance.taintBlock(n);
      IChargeConductor neighbor = (IChargeConductor)n.getTE(IChargeConductor.class);
      if (neighbor != null)
      {
        Charge neighbor_charge = neighbor.getCharge();
        if (neighbor_charge.conductorSet != null)
        {
          if (neighbor_charge.conductorSet.addConductor(conductor))
          {
            for (Coord coord_otherNeighbor : neighbors) {
              IChargeConductor otherNeighbor = (IChargeConductor)coord_otherNeighbor.getTE(IChargeConductor.class);
              if (otherNeighbor != null) {
                conductorSet.addNeighbor(otherNeighbor.getCharge().conductorSet);
              }
            }
            return;
          }
        }
      }
    }
    ConductorSet assignedConductorSet = new ConductorSet(conductor);
  }

  public void update()
  {
    TileEntity te = (TileEntity)conductor;
    World w = te.worldObj;
    if (w.isRemote) {
      return;
    }
    createOrJoinConductorSet();
    if (isConductorSetLeader) {
      conductorSet.update();
    }
    int seed = (te.xCoord << 4 + te.zCoord << 8) + te.yCoord;

    if ((justCreated) || ((w.getTotalWorldTime() + seed) % 600L == 0L)) {
      justCreated = false;
      if (conductorSet.leader == null) {
        conductorSet.leader = conductor;
      }
      Coord here = conductor.getCoord();
      for (IChargeConductor neighbor : here.getAdjacentTEs(IChargeConductor.class))
        justCreated |= conductorSet.addNeighbor(neighbor.getCharge().conductorSet);
    }
  }

  public void remove()
  {
    if (conductorSet == null) {
      return;
    }
    int memberCount = conductorSet.memberCount;
    if (memberCount <= 1) {
      return;
    }

    for (IChargeConductor hereConductor : conductorSet.getMembers(conductor))
      if (hereConductor != conductor)
      {
        Charge hereCharge = hereConductor.getCharge();
        int val = hereCharge.getValue();
        new ConductorSet(hereConductor).totalCharge = val;
        conductorSet.memberCount -= 1;
        conductorSet.totalCharge -= val;
      }
    conductorSet = null;
  }

  public static ChargeDensityReading getChargeDensity(IChargeConductor start)
  {
    ConductorSet cs = start.getCharge().conductorSet;
    if (cs == null) {
      return new ChargeDensityReading();
    }
    ChargeDensityReading ret = new ChargeDensityReading();
    ret.totalCharge = cs.totalCharge;
    ret.conductorCount = cs.memberCount;
    return ret;
  }

  public void invalidate()
  {
    if (conductorSet == null) {
      return;
    }
    remove();
  }

  public IDataSerializable serialize(String prefix, DataHelper data) throws IOException
  {
    int new_val = ((Integer)data.as(Share.PRIVATE, prefix + "charge").put(Integer.valueOf(getValue()))).intValue();
    if (data.isReader()) {
      setValue(new_val);
    }
    return this;
  }

  public static class ChargeDensityReading
  {
    public int totalCharge;
    public int conductorCount;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.Charge
 * JD-Core Version:    0.6.2
 */
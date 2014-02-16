package powercrystals.powerconverters.power.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.power.TileEntityEnergyProducer;

public class TileEntityIndustrialCraftProducer extends TileEntityEnergyProducer<IEnergyAcceptor> implements IEnergySource
{
	private boolean _isAddedToEnergyNet;
	private boolean _didFirstAddToNet;
	
	private int eu;
	
	private int _packetCount;
	
	public TileEntityIndustrialCraftProducer()
	{
		this(0);
	}
	
	public TileEntityIndustrialCraftProducer(int voltageIndex)
	{
		super(PowerConverterCore.powerSystemIndustrialCraft, voltageIndex, IEnergyAcceptor.class);
		if(voltageIndex == 0)
		{
			_packetCount = PowerConverterCore.throttleIC2LVProducer.getInt();
		}
		else if(voltageIndex == 1)
		{
			_packetCount = PowerConverterCore.throttleIC2MVProducer.getInt();
		}
		else if(voltageIndex == 2)
		{
			_packetCount = PowerConverterCore.throttleIC2HVProducer.getInt();
		}
		else if(voltageIndex == 3)
		{
			_packetCount = PowerConverterCore.throttleIC2EVProducer.getInt();
		}
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(!_didFirstAddToNet && !worldObj.isRemote)
		{
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			_didFirstAddToNet = true;
			_isAddedToEnergyNet = true;
		}
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if(!_isAddedToEnergyNet)
		{
			_didFirstAddToNet = false;
		}
	}
	
	@Override
	public void invalidate()
	{
		if(_isAddedToEnergyNet)
		{
			if(!worldObj.isRemote)
			{
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}
			_isAddedToEnergyNet = false;
		}
		super.invalidate();
	}
	
	@Override
	public int produceEnergy(int energy)
	{
		int eu = energy / PowerConverterCore.powerSystemIndustrialCraft.getInternalEnergyPerOutput();
		int usedEu = Math.min(eu, getMaxEnergyOutput() - this.eu);
		this.eu += usedEu;
		return (eu - usedEu) * PowerConverterCore.powerSystemIndustrialCraft.getInternalEnergyPerOutput();
	}


	public int getMaxEnergyOutput()
	{
		return getPowerSystem().getVoltageValues()[getVoltageIndex()];
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}

	@Override
	public double getOfferedEnergy() {
		return Math.min(eu, getMaxEnergyOutput());
	}

	@Override
	public void drawEnergy(double amount) {
		eu -= MathHelper.ceiling_double_int(amount);
	}
}
package powercrystals.powerconverters.power.buildcraft;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.power.TileEntityEnergyConsumer;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

public class TileEntityBuildCraftConsumer extends TileEntityEnergyConsumer<IPowerReceptor> implements IPowerReceptor
{
	private PowerHandler powerHandler;
	private int _mjLastTick = 0;
	private long _lastTickInjected;
	
	public TileEntityBuildCraftConsumer()
	{
		super(PowerConverterCore.powerSystemBuildCraft, 0, IPowerReceptor.class);
		powerHandler = new PowerHandler(this, Type.MACHINE);
		powerHandler.configure(2, 100, 1, 1000);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(worldObj.getWorldTime() - _lastTickInjected > 1)
		{
			_lastTickInjected = worldObj.getWorldTime();
			_mjLastTick = 0;
		}
		if (powerHandler.getEnergyStored() > 0) {
			float energy = powerHandler.useEnergy(0, powerRequest(), true);
			if(_lastTickInjected != worldObj.getWorldTime())
			{
				_lastTickInjected = worldObj.getWorldTime();
				_mjLastTick = 0;
			}
			
			_mjLastTick += MathHelper.floor_float(energy);
			storeEnergy((int)(energy * PowerConverterCore.powerSystemBuildCraft.getInternalEnergyPerInput()));
		}
	}
	
	public int powerRequest()
	{
		return getTotalEnergyDemand() / PowerConverterCore.powerSystemBuildCraft.getInternalEnergyPerInput();
	}

	@Override
	public int getInputRate()
	{
		return _mjLastTick;
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return powerHandler.getPowerReceiver();
	}

	@Override
	public void doWork(PowerHandler workProvider) {
	}

	@Override
	public World getWorld() {
		return worldObj;
	}
}

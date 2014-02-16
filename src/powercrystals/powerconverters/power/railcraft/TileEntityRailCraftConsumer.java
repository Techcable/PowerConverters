package powercrystals.powerconverters.power.railcraft;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.power.TileEntityEnergyConsumer;

public class TileEntityRailCraftConsumer extends TileEntityEnergyConsumer<IFluidHandler> implements IFluidHandler
{
	private FluidTank tank;
	private int _mBLastTick;
	
	public TileEntityRailCraftConsumer()
	{
		super(PowerConverterCore.powerSystemSteam, 0, IFluidHandler.class);
		tank = new FluidTank(1 * FluidContainerRegistry.BUCKET_VOLUME);
		
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if(tank != null && tank.getFluid() != null)
		{
			int amount = Math.min(tank.getFluidAmount(), PowerConverterCore.throttleSteamConsumer.getInt());
			int energy = amount * PowerConverterCore.powerSystemSteam.getInternalEnergyPerInput();
			energy = storeEnergy(energy);
			int toDrain = amount - (energy / PowerConverterCore.powerSystemSteam.getInternalEnergyPerInput());
			tank.drain(toDrain, true);
			_mBLastTick = toDrain;
		}
		else
		{
			_mBLastTick = 0;
		}
	}
	
	@Override
	public int getVoltageIndex()
	{
		return 0;
	}

	@Override
	public int getInputRate()
	{
		return _mBLastTick;
	}
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource == null || resource.fluidID != PowerConverterCore.steamId) return 0;
		if (doFill) {
			return tank.fill(resource, true);
		}
		int usedSteam = Math.min(resource.amount, tank.getCapacity() - tank.getFluidAmount());
		return usedSteam;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid != null && fluid.getID() == PowerConverterCore.steamId;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {tank.getInfo()};
	}
}

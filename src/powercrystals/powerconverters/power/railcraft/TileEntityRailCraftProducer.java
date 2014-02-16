package powercrystals.powerconverters.power.railcraft;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import powercrystals.core.position.BlockPosition;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.power.TileEntityEnergyProducer;

public class TileEntityRailCraftProducer extends TileEntityEnergyProducer<IFluidHandler> implements IFluidHandler
{
	private FluidTank tank;
	
	public TileEntityRailCraftProducer()
	{
		super(PowerConverterCore.powerSystemSteam, 0, IFluidHandler.class);
		tank = new FluidTank(1);
	}
	
	@Override
	public int produceEnergy(int energy)
	{
		int steam = Math.min(energy / PowerConverterCore.powerSystemSteam.getInternalEnergyPerOutput(), PowerConverterCore.throttleSteamProducer.getInt());
		for(int i = 0; i < 6; i++)
		{
			BlockPosition bp = new BlockPosition(this);
			bp.orientation = ForgeDirection.getOrientation(i);
			bp.moveForwards(1);
			TileEntity te = worldObj.getBlockTileEntity(bp.x, bp.y, bp.z);
			
			if(te != null && te instanceof IFluidHandler)
			{
				steam -= ((IFluidHandler)te).fill(bp.orientation.getOpposite(), new FluidStack(PowerConverterCore.steamId, steam), true);
			}
			if(steam <= 0)
			{
				return 0;
			}
		}

		return steam * PowerConverterCore.powerSystemSteam.getInternalEnergyPerOutput();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return  new FluidTankInfo[] {tank.getInfo()};
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}
}

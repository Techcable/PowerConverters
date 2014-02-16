package powercrystals.powerconverters.power.buildcraft;

import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import powercrystals.core.position.BlockPosition;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.power.TileEntityEnergyProducer;

public class TileEntityBuildCraftProducer extends TileEntityEnergyProducer<IPowerReceptor> implements IPowerEmitter
{
	int mj;
	public TileEntityBuildCraftProducer()
	{
		super(PowerConverterCore.powerSystemBuildCraft, 0, IPowerReceptor.class);
	}
	
	@Override
	public int produceEnergy(int energy)
	{
		int mj = energy / PowerConverterCore.powerSystemBuildCraft.getInternalEnergyPerOutput();
		
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = BlockPosition.getAdjacentTileEntity(this, dir);
			IPowerReceptor receptor;
			if (tile == null) continue;
			if (tile instanceof IPowerReceptor) {
				receptor = (IPowerReceptor) tile;
			} else continue;
			PowerReceiver receiver = receptor.getPowerReceiver(dir.getOpposite());
			int usedMJ = Math.min(MathHelper.floor_float(receiver.powerRequest()), mj);
			mj -= usedMJ;
			receiver.receiveEnergy(Type.ENGINE, usedMJ, dir.getOpposite());
		}
		return mj * PowerConverterCore.powerSystemBuildCraft.getInternalEnergyPerOutput();
	}

	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return true;
	}
}

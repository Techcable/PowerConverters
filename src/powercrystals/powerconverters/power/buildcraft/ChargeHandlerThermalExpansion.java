package powercrystals.powerconverters.power.buildcraft;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import powercrystals.powerconverters.PowerConverterCore;
import powercrystals.powerconverters.common.IChargeHandler;
import powercrystals.powerconverters.power.PowerSystem;

public class ChargeHandlerThermalExpansion implements IChargeHandler
{
	@Override
	public PowerSystem getPowerSystem()
	{
		return PowerConverterCore.powerSystemBuildCraft;
	}

	@Override
	public boolean canHandle(ItemStack stack)
	{
		return stack != null && stack.getItem() instanceof IEnergyContainerItem;
	}

	@Override
	public int charge(ItemStack stack, int energyInput)
	{
		int mj = energyInput / getPowerSystem().getInternalEnergyPerOutput();
		mj -= ((IEnergyContainerItem)stack.getItem()).receiveEnergy(stack, MathHelper.floor_double(mj / 10), false) * 10;
		return mj * getPowerSystem().getInternalEnergyPerOutput();
	}

	@Override
	public int discharge(ItemStack stack, int energyRequest)
	{
		int mj = energyRequest / getPowerSystem().getInternalEnergyPerInput();
		mj = (int)((IEnergyContainerItem)stack.getItem()).extractEnergy(stack, MathHelper.floor_double(mj / 10), false) * 10;
		return mj * getPowerSystem().getInternalEnergyPerInput();
	}
}

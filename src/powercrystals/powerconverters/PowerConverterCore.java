package powercrystals.powerconverters;

import cpw.mods.fml.common.IScheduledTickHandler;
import powercrystals.core.mod.BaseMod;
import powercrystals.core.updater.UpdateManager;
import powercrystals.powerconverters.common.BlockPowerConverterCommon;
import powercrystals.powerconverters.common.ItemBlockPowerConverterCommon;
import powercrystals.powerconverters.common.TileEntityCharger;
import powercrystals.powerconverters.common.TileEntityEnergyBridge;
import powercrystals.powerconverters.gui.PCGUIHandler;
import powercrystals.powerconverters.net.ClientPacketHandler;
import powercrystals.powerconverters.net.ConnectionHandler;
import powercrystals.powerconverters.net.IPCProxy;
import powercrystals.powerconverters.power.PowerSystem;
import powercrystals.powerconverters.power.buildcraft.BlockPowerConverterBuildCraft;
import powercrystals.powerconverters.power.buildcraft.ChargeHandlerThermalExpansion;
import powercrystals.powerconverters.power.buildcraft.ItemBlockPowerConverterBuildCraft;
import powercrystals.powerconverters.power.buildcraft.TileEntityBuildCraftConsumer;
import powercrystals.powerconverters.power.buildcraft.TileEntityBuildCraftProducer;
import powercrystals.powerconverters.power.ic2.BlockPowerConverterIndustrialCraft;
import powercrystals.powerconverters.power.ic2.ChargeHandlerIndustrialCraft;
import powercrystals.powerconverters.power.ic2.ItemBlockPowerConverterIndustrialCraft;
import powercrystals.powerconverters.power.ic2.TileEntityIndustrialCraftConsumer;
import powercrystals.powerconverters.power.ic2.TileEntityIndustrialCraftProducer;
import powercrystals.powerconverters.power.railcraft.BlockPowerConverterRailCraft;
import powercrystals.powerconverters.power.railcraft.ItemBlockPowerConverterRailCraft;
import powercrystals.powerconverters.power.railcraft.TileEntityRailCraftConsumer;
import powercrystals.powerconverters.power.railcraft.TileEntityRailCraftProducer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidRegistry.FluidRegisterEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import powercrystals.powerconverters.power.factorization.BlockPowerConverterFactorization;
import powercrystals.powerconverters.power.factorization.ItemBlockPowerConverterFactorization;
import powercrystals.powerconverters.power.factorization.TileEntityPowerConverterFactorizationConsumer;
import powercrystals.powerconverters.power.factorization.TileEntityPowerConverterFactorizationProducer;

@Mod(modid = PowerConverterCore.modId, name = PowerConverterCore.modName, version = PowerConverterCore.version,
dependencies = "required-after:PowerCrystalsCore;after:BasicComponents;after:BuildCraft|Energy;after:factorization;after:IC2;after:Railcraft;after:ThermalExpansion")
@NetworkMod(clientSideRequired = false, serverSideRequired = false,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { PowerConverterCore.modId }, packetHandler = ClientPacketHandler.class),
connectionHandler = ConnectionHandler.class)
public class PowerConverterCore extends BaseMod
{
	public static final String modId = "PowerConverters";
	public static final String modName = "Power Converters";
	public static final String version = "1.5.1R2.3.2B1";
	
	@SidedProxy(clientSide="powercrystals.powerconverters.net.ProxyClient", serverSide="powercrystals.powerconverters.net.ProxyServer")
	public static IPCProxy proxy;
	
	private static final String _texureFolder = "/powercrystals/powerconverters/textures/";
	public static final String guiFolder = _texureFolder + "gui/";

	public static Block converterBlockCommon;
	public static Block converterBlockBuildCraft;
	public static Block converterBlockIndustrialCraft;
	public static Block converterBlockSteam;
	public static Block converterBlockUniversalElectricity;
	public static Block converterBlockFactorization;
	
	public static PowerConverterCore instance;
	
	private static Property blockIdCommon;
	private static Property blockIdBuildCraft;
	private static Property blockIdIndustrialCraft;
	private static Property blockIdSteam;
	private static Property blockIdUniversalElectricty;
	private static Property blockIdFactorization;
	
	public static Property bridgeBufferSize;
	
	public static Property throttleSteamConsumer;
	public static Property throttleSteamProducer;
	public static Property throttleIC2LVProducer;
	public static Property throttleIC2MVProducer;
	public static Property throttleIC2HVProducer;
	public static Property throttleIC2EVProducer;
	
	public static PowerSystem powerSystemBuildCraft;
	public static PowerSystem powerSystemIndustrialCraft;
	public static PowerSystem powerSystemSteam;
	public static PowerSystem powerSystemFactorization;
	
	public static int steamId = -1;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt)
	{
		powerSystemBuildCraft = new PowerSystem("BuildCraft", "BC", 4375, 4375, null, null, "MJ/t");
		powerSystemIndustrialCraft = new PowerSystem("IndustrialCraft", "IC2", 1800, 1800, new String[] { "LV", "MV", "HV", "EV" }, new int[] { 32, 128, 512, 2048 }, "EU/t");
		powerSystemSteam = new PowerSystem("Steam", "STEAM", 875, 875, null, null, "mB/t");
		powerSystemFactorization = new PowerSystem("Factorization", "FZ", 175, 175, null, null, "CG/t");
				
		PowerSystem.registerPowerSystem(powerSystemBuildCraft);
		PowerSystem.registerPowerSystem(powerSystemIndustrialCraft);
		PowerSystem.registerPowerSystem(powerSystemSteam);
		PowerSystem.registerPowerSystem(powerSystemFactorization);
		
		setConfigFolderBase(evt.getModConfigurationDirectory());
		Configuration c = new Configuration(getCommonConfig());
		loadConfig(c);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) throws Exception
	{
		instance = this;
		
		converterBlockCommon = new BlockPowerConverterCommon(blockIdCommon.getInt());
		GameRegistry.registerBlock(converterBlockCommon, ItemBlockPowerConverterCommon.class, converterBlockCommon.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityEnergyBridge.class, "powerConverterEnergyBridge");
		GameRegistry.registerTileEntity(TileEntityCharger.class, "powerConverterUniversalCharger");
		LanguageRegistry.addName(new ItemStack(converterBlockCommon, 1, 0), "Energy Bridge");
		LanguageRegistry.addName(new ItemStack(converterBlockCommon, 1, 2), "Universal Charger");
		
		GameRegistry.addRecipe(new ItemStack(converterBlockCommon, 1, 0),
				"GRG", "LDL", "GRG",
				Character.valueOf('G'), Item.ingotGold,
				Character.valueOf('R'), Item.redstone,
				Character.valueOf('L'), Block.glass,
				Character.valueOf('D'), Item.diamond);
		
		GameRegistry.addRecipe(new ItemStack(converterBlockCommon, 1, 2),
				"GRG", "ICI", "GRG",
				Character.valueOf('G'), Item.ingotGold,
				Character.valueOf('R'), Item.redstone,
				Character.valueOf('I'), Item.ingotIron,
				Character.valueOf('C'), Block.chest);
		
		if(Loader.isModLoaded("BuildCraft|Energy") || Loader.isModLoaded("ThermalExpansion"))
		{
			converterBlockBuildCraft = new BlockPowerConverterBuildCraft(blockIdBuildCraft.getInt());
			GameRegistry.registerBlock(converterBlockBuildCraft, ItemBlockPowerConverterBuildCraft.class, converterBlockBuildCraft.getUnlocalizedName());
			GameRegistry.registerTileEntity(TileEntityBuildCraftConsumer.class, "powerConverterBCConsumer");
			GameRegistry.registerTileEntity(TileEntityBuildCraftProducer.class, "powerConverterBCProducer");
			LanguageRegistry.addName(new ItemStack(converterBlockBuildCraft, 1, 0), "BC Consumer");
			LanguageRegistry.addName(new ItemStack(converterBlockBuildCraft, 1, 1), "BC Producer");
			
			if(Loader.isModLoaded("BuildCraft|Energy"))
			{
				GameRegistry.addRecipe(new ItemStack(converterBlockBuildCraft, 1, 0),
						"G G", " E ", "G G",
						Character.valueOf('G'), Item.ingotGold,
						Character.valueOf('E'), new ItemStack((Block)(Class.forName("buildcraft.BuildCraftEnergy").getField("engineBlock").get(null)), 1, 1));
			}
			if(Loader.isModLoaded("ThermalExpansion"))
			{
				GameRegistry.addRecipe(new ItemStack(converterBlockBuildCraft, 1, 0),
						"G G", " E ", "G G",
						Character.valueOf('G'), Item.ingotGold,
						Character.valueOf('E'), new ItemStack((Block)(Class.forName("thermalexpansion.block.TEBlocks").getField("blockEngine").get(null)), 1, 1));
				TileEntityCharger.registerChargeHandler(new ChargeHandlerThermalExpansion());
			}
			
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockBuildCraft, 1, 1), new ItemStack(converterBlockBuildCraft, 1, 0));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockBuildCraft, 1, 0), new ItemStack(converterBlockBuildCraft, 1, 1));
		}
		
		if(Loader.isModLoaded("IC2"))
		{
			converterBlockIndustrialCraft = new BlockPowerConverterIndustrialCraft(blockIdIndustrialCraft.getInt());
			GameRegistry.registerBlock(converterBlockIndustrialCraft, ItemBlockPowerConverterIndustrialCraft.class, converterBlockIndustrialCraft.getUnlocalizedName());
			GameRegistry.registerTileEntity(TileEntityIndustrialCraftConsumer.class, "powerConverterIC2Consumer");
			GameRegistry.registerTileEntity(TileEntityIndustrialCraftProducer.class, "powerConverterIC2Producer");
			LanguageRegistry.addName(new ItemStack(converterBlockIndustrialCraft, 1, 0), "IC2 LV Consumer");
			LanguageRegistry.addName(new ItemStack(converterBlockIndustrialCraft, 1, 1), "IC2 LV Producer");
			LanguageRegistry.addName(new ItemStack(converterBlockIndustrialCraft, 1, 2), "IC2 MV Consumer");
			LanguageRegistry.addName(new ItemStack(converterBlockIndustrialCraft, 1, 3), "IC2 MV Producer");
			LanguageRegistry.addName(new ItemStack(converterBlockIndustrialCraft, 1, 4), "IC2 HV Consumer");
			LanguageRegistry.addName(new ItemStack(converterBlockIndustrialCraft, 1, 5), "IC2 HV Producer");
			LanguageRegistry.addName(new ItemStack(converterBlockIndustrialCraft, 1, 6), "IC2 EV Consumer");
			LanguageRegistry.addName(new ItemStack(converterBlockIndustrialCraft, 1, 7), "IC2 EV Producer");
			
			GameRegistry.addRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 0),
					"G G", " T ", "G G",
					Character.valueOf('G'), Item.ingotGold,
					Character.valueOf('T'), (ItemStack)(Class.forName("ic2.core.Ic2Items").getField("lvTransformer").get(null)));
			GameRegistry.addRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 2),
					"G G", " T ", "G G",
					Character.valueOf('G'), Item.ingotGold,
					Character.valueOf('T'), (ItemStack)(Class.forName("ic2.core.Ic2Items").getField("mvTransformer").get(null)));
			GameRegistry.addRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 4),
					"G G", " T ", "G G",
					Character.valueOf('G'), Item.ingotGold,
					Character.valueOf('T'), (ItemStack)(Class.forName("ic2.core.Ic2Items").getField("hvTransformer").get(null)));
			GameRegistry.addRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 6),
					"G G", " T ", "G G",
					Character.valueOf('G'), Item.ingotGold,
					Character.valueOf('T'), (ItemStack)(Class.forName("ic2.core.Ic2Items").getField("mfsUnit").get(null)));
			
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 1), new ItemStack(converterBlockIndustrialCraft, 1, 0));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 0), new ItemStack(converterBlockIndustrialCraft, 1, 1));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 3), new ItemStack(converterBlockIndustrialCraft, 1, 2));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 2), new ItemStack(converterBlockIndustrialCraft, 1, 3));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 5), new ItemStack(converterBlockIndustrialCraft, 1, 4));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 4), new ItemStack(converterBlockIndustrialCraft, 1, 5));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 7), new ItemStack(converterBlockIndustrialCraft, 1, 6));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockIndustrialCraft, 1, 6), new ItemStack(converterBlockIndustrialCraft, 1, 7));
			
			TileEntityCharger.registerChargeHandler(new ChargeHandlerIndustrialCraft());
		}
		
		if(Loader.isModLoaded("Railcraft") || Loader.isModLoaded("factorization"))
		{
			converterBlockSteam = new BlockPowerConverterRailCraft(blockIdSteam.getInt());
			GameRegistry.registerBlock(converterBlockSteam, ItemBlockPowerConverterRailCraft.class, converterBlockSteam.getUnlocalizedName());
			GameRegistry.registerTileEntity(TileEntityRailCraftConsumer.class, "powerConverterSteamConsumer");
			GameRegistry.registerTileEntity(TileEntityRailCraftProducer.class, "powerConverterSteamProducer");
			LanguageRegistry.addName(new ItemStack(converterBlockSteam, 1, 0), "Steam Consumer");
			LanguageRegistry.addName(new ItemStack(converterBlockSteam, 1, 1), "Steam Producer");
			
			if(Loader.isModLoaded("Railcraft"))
			{
				GameRegistry.addRecipe(new ItemStack(converterBlockSteam, 1, 0),
						"G G", " E ", "G G",
						Character.valueOf('G'), Item.ingotGold,
						Character.valueOf('E'), new ItemStack((Block)(Class.forName("mods.railcraft.common.blocks.RailcraftBlocks").getMethod("getBlockMachineBeta").invoke(null)), 1, 8));
			}
			else
			{
				Object fzRegistry = Class.forName("factorization.common.Core").getField("registry").get(null);
				GameRegistry.addRecipe(new ItemStack(converterBlockSteam, 1, 0),
						"G G", " E ", "G G",
						Character.valueOf('G'), Item.ingotGold,
						Character.valueOf('E'), (ItemStack)(Class.forName("factorization.common.Registry").getField("steamturbine_item").get(fzRegistry)));
			}
			
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockSteam, 1, 1), new ItemStack(converterBlockSteam, 1, 0));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockSteam, 1, 0), new ItemStack(converterBlockSteam, 1, 1));
		}

		if(Loader.isModLoaded("factorization"))
		{
			converterBlockFactorization = new BlockPowerConverterFactorization(blockIdFactorization.getInt());
			GameRegistry.registerBlock(converterBlockFactorization, ItemBlockPowerConverterFactorization.class,converterBlockFactorization.getUnlocalizedName());
			GameRegistry.registerTileEntity(TileEntityPowerConverterFactorizationConsumer.class, "powerConverterFZConsumer");
			LanguageRegistry.addName(new ItemStack(converterBlockFactorization, 1, 0), "Factorization Consumer");
			
			GameRegistry.registerTileEntity(TileEntityPowerConverterFactorizationProducer.class, "powerConverterFZProducer");
			LanguageRegistry.addName(new ItemStack(converterBlockFactorization, 1, 1), "Factorization Producer");
			
			Object fzRegistry = Class.forName("factorization.common.Core").getField("registry").get(null);
			
			GameRegistry.addRecipe(new ItemStack(converterBlockFactorization, 1, 0),
					"I I", " B ", "I I",
					Character.valueOf('I'), Item.ingotGold,
					Character.valueOf('B'), (ItemStack)(Class.forName("factorization.common.Registry").getField("solarboiler_item").get(fzRegistry)));
			
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockFactorization, 1, 1), new ItemStack(converterBlockFactorization, 1, 0));
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlockFactorization, 1, 0), new ItemStack(converterBlockFactorization, 1, 1));
		}
		
		NetworkRegistry.instance().registerGuiHandler(instance, new PCGUIHandler());
		
		if(FluidRegistry.isFluidRegistered("Steam"))
		{
			steamId = FluidRegistry.getFluidID("Steam");
		}
		
		MinecraftForge.EVENT_BUS.register(instance);
		
		proxy.load();
		
		TickRegistry.registerScheduledTickHandler((IScheduledTickHandler)new UpdateManager(this), Side.CLIENT);
	}
	
	@ForgeSubscribe
	public void forgeEvent(FluidRegisterEvent event)
	{
		if (event.fluidName.equals("Steam")) {
			steamId = event.fluidID;
		} else if(event.fluidName.equals("steam") && steamId <= 0)
		{
			steamId = event.fluidID;
		}
	}
	
	private static void loadConfig(Configuration c)
	{
		blockIdCommon = c.getBlock("ID.BlockCommon", 2850);
		blockIdBuildCraft = c.getBlock("ID.BlockBuildcraft", 2851);
		blockIdIndustrialCraft = c.getBlock("ID.BlockIndustrialCraft", 2852);
		blockIdSteam = c.getBlock("ID.BlockSteam", 2853);
		blockIdFactorization = c.getBlock("ID.BlockFactorization", 2855);
		
		bridgeBufferSize = c.get(Configuration.CATEGORY_GENERAL, "BridgeBufferSize", 160000000);
		
		throttleSteamConsumer = c.get("Throttles", "Steam.Consumer", 1000);
		throttleSteamConsumer.comment = "mB/t";
		throttleSteamProducer = c.get("Throttles", "Steam.Producer", 1000);
		throttleSteamProducer.comment = "mB/t";
		throttleIC2LVProducer = c.get("Throttles", "IC2.Consumer.LV", 1);
		throttleIC2LVProducer.comment = "Packets/t";
		throttleIC2MVProducer = c.get("Throttles", "IC2.Consumer.MV", 1);
		throttleIC2MVProducer.comment = "Packets/t";
		throttleIC2HVProducer = c.get("Throttles", "IC2.Consumer.HV", 1);
		throttleIC2HVProducer.comment = "Packets/t";
		throttleIC2EVProducer = c.get("Throttles", "IC2.Consumer.EV", 1);
		throttleIC2EVProducer.comment = "Packets/t";
		
		PowerSystem.loadConfig(c);
		
		c.save();
	}

	@Override
	public String getModId()
	{
		return modId;
	}

	@Override
	public String getModName()
	{
		return modName;
	}

	@Override
	public String getModVersion()
	{
		return version;
	}
}

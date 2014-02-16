package factorization.api;

import factorization.common.FzConfig;
import factorization.common.Registry;
import factorization.fzds.TransferLib;
import factorization.shared.Core;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ChargeMetalBlockConductance
{
  static boolean[][] validBlocks = new boolean[Block.blocksList.length][];
  public static ArrayList<ItemStack> excludeOres = new ArrayList();

  public static void setup() {
    if (!FzConfig.invasiveCharge) {
      return;
    }
    throw new IllegalArgumentException("Invasive charge disabled");
  }

  static void put(int id, int md)
  {
    boolean[] mds = validBlocks[id];
    if (mds == null)
    {
      boolean[] tmp18_16 = new boolean[16]; mds = tmp18_16; validBlocks[id] = tmp18_16;
    }
    mds[md] = true;
  }

  public static void taintBlock(Coord c) {
    int blockID = c.getId(); int md = c.getMd();
    if ((validBlocks[blockID] == null) || (validBlocks[blockID][md] == 0)) {
      return;
    }
    if (c.getTE() != null) {
      return;
    }
    InvasiveCharge te = new InvasiveCharge();
    te.validate();
    te.initialize(blockID, md);
    int orig_id = c.getId(); int orig_md = c.getMd();
    TransferLib.setRaw(c, Core.registry.factory_block.blockID, 0);
    c.setTE(te);
    TransferLib.setRaw(c, orig_id, orig_md, 0);
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.ChargeMetalBlockConductance
 * JD-Core Version:    0.6.2
 */
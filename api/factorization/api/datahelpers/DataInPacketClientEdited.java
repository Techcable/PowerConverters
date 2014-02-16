package factorization.api.datahelpers;

import cpw.mods.fml.relauncher.Side;
import java.io.DataInput;

public class DataInPacketClientEdited extends DataInPacket
{
  public DataInPacketClientEdited(DataInput dis)
  {
    super(dis, Side.CLIENT);
  }

  protected boolean shouldStore(Share share)
  {
    return (share.is_public) && (share.client_can_edit);
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataInPacketClientEdited
 * JD-Core Version:    0.6.2
 */
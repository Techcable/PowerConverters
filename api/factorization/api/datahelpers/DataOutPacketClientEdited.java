package factorization.api.datahelpers;

import cpw.mods.fml.relauncher.Side;
import java.io.DataOutput;

public class DataOutPacketClientEdited extends DataOutPacket
{
  public DataOutPacketClientEdited(DataOutput dos)
  {
    super(dos, Side.CLIENT);
  }

  protected boolean shouldStore(Share share)
  {
    return (share.is_public) && (share.client_can_edit);
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataOutPacketClientEdited
 * JD-Core Version:    0.6.2
 */
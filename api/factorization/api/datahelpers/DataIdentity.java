package factorization.api.datahelpers;

import java.io.IOException;

public class DataIdentity extends DataHelper
{
  final DataHelper parent;

  public DataIdentity(DataHelper parent)
  {
    this.parent = parent;
  }

  protected boolean shouldStore(Share share)
  {
    return false;
  }

  public boolean isReader()
  {
    return parent.isReader();
  }

  protected <E> Object putImplementation(E o) throws IOException
  {
    return o;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataIdentity
 * JD-Core Version:    0.6.2
 */
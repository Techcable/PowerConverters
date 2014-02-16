package factorization.api.datahelpers;

import java.io.IOException;

public abstract interface IDataSerializable
{
  public abstract IDataSerializable serialize(String paramString, DataHelper paramDataHelper)
    throws IOException;
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.IDataSerializable
 * JD-Core Version:    0.6.2
 */
package factorization.api;

import java.io.DataInputStream;
import java.io.IOException;

public abstract interface IEntityMessage
{
  public abstract boolean handleMessageFromServer(short paramShort, DataInputStream paramDataInputStream)
    throws IOException;

  public abstract boolean handleMessageFromClient(short paramShort, DataInputStream paramDataInputStream)
    throws IOException;
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.IEntityMessage
 * JD-Core Version:    0.6.2
 */
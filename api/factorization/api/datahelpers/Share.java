package factorization.api.datahelpers;

public enum Share
{
  PRIVATE(false, false, false), 
  VISIBLE(true, false, false), 
  MUTABLE(true, true, false), 
  MUTABLE_INDIRECT(false, true, false), 
  PRIVATE_TRANSIENT(false, false, true), 
  VISIBLE_TRANSIENT(true, false, true), 
  MUTABLE_TRANSIENT(true, true, true), 
  DESCRIPTION_PACKET(true, false, true);

  public final boolean is_public;
  public final boolean client_can_edit;
  public final boolean is_transient;

  private Share(boolean pub, boolean mut, boolean tran) { is_public = pub;
    client_can_edit = mut;
    is_transient = tran;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.Share
 * JD-Core Version:    0.6.2
 */
package factorization.api.datahelpers;

import java.io.IOException;
import java.util.Map;

public class DataValidator extends DataHelper
{
  Map<String, Object> fields;
  int fieldIndex = 0;

  boolean has_log = false;

  public DataValidator(Map<String, Object> fields)
  {
    this.fields = fields;
  }

  protected boolean shouldStore(Share share)
  {
    return (share.is_public) && (share.client_can_edit);
  }

  protected <E> Object putImplementation(E o) throws IOException
  {
    if (!fields.containsKey(name)) {
      log("Missing data");
      return o;
    }
    return fields.get(name);
  }

  public boolean isReader()
  {
    return true;
  }

  public void log(String message)
  {
    super.log(message);
    has_log = true;
  }

  public boolean isValid() {
    return !has_log;
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.datahelpers.DataValidator
 * JD-Core Version:    0.6.2
 */
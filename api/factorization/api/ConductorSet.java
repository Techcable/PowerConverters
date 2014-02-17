package factorization.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

class ConductorSet
  implements Comparable
{
  int totalCharge = 0;
  int memberCount = 0;
  static final int maxMemberCount = 24;
  IChargeConductor leader = null;
  TreeSet<ConductorSet> neighbors = null;
  Iterator<ConductorSet> neighborIterator = null;

  private static ArrayList<IChargeConductor> frontier = new ArrayList(100);
  private static HashSet<IChargeConductor> visited = new HashSet(125);

  ConductorSet(IChargeConductor leader)
  {
    this.leader = leader;
    memberCount = 1;
    Charge lc = leader.getCharge();
    lc.conductorSet = this;
    lc.isConductorSetLeader = true;
    lc.justCreated = true;
  }

  boolean addConductor(IChargeConductor other) {
    other.getCharge().conductorSet = this;
    memberCount += 1;
    return true;
  }

  void update() {
    if ((neighbors == null) || (neighbors.size() == 0)) {
      neighbors = null;
      return;
    }
    if ((neighborIterator == null) || (!neighborIterator.hasNext())) {
      neighborIterator = neighbors.iterator();
    }
    ConductorSet luckyNeighbor = (ConductorSet)neighborIterator.next();
    if (luckyNeighbor.memberCount <= 0) {
      neighborIterator.remove();
      return;
    }
    if ((luckyNeighbor.memberCount + memberCount < 24) && (luckyNeighbor.memberCount <= memberCount))
    {
      Iterable<IChargeConductor> noms = luckyNeighbor.getMembers(luckyNeighbor.leader);
      totalCharge += luckyNeighbor.totalCharge;
      for (IChargeConductor nom : noms)
      {
        addConductor(nom);
      }
      luckyNeighbor.totalCharge = 0;
      luckyNeighbor.memberCount = 0;
      luckyNeighbor.leader = null;
      neighborIterator.remove();
      return;
    }

    int ourCharge = totalCharge + luckyNeighbor.totalCharge;
    int ourMemberCount = memberCount + luckyNeighbor.memberCount;

    int hisNewCharge = ourCharge * luckyNeighbor.memberCount / ourMemberCount;
    luckyNeighbor.totalCharge = hisNewCharge;
    totalCharge = (ourCharge - hisNewCharge);
  }

  boolean addNeighbor(ConductorSet neighbor) {
    if ((neighbor == this) || (neighbor == null)) {
      return false;
    }
    if (neighbors == null) {
      neighbors = new TreeSet();
    }
    if (neighbors.add(neighbor)) {
      neighborIterator = null;
      neighbor.addNeighbor(this);
      return true;
    }
    return false;
  }

  Iterable<IChargeConductor> getMembers(IChargeConductor seed)
  {
    if (seed == null) {
      return new ArrayList(0);
    }
    frontier.clear();
    visited.clear();
    frontier.add(seed);
    visited.add(seed);
    ArrayList ret = new ArrayList(memberCount);
    while (frontier.size() > 0) {
      IChargeConductor here = (IChargeConductor)frontier.remove(0);
      Coord hereCoord = here.getCoord();
      Charge hereCharge = here.getCharge();
      if (hereCharge.conductorSet == this)
      {
        ret.add(here);
        for (IChargeConductor neighbor : hereCoord.getAdjacentTEs(IChargeConductor.class)) {
          if (visited.add(neighbor))
            frontier.add(neighbor);
        }
      }
    }
    return ret;
  }

  public int compareTo(Object arg0)
  {
    return hashCode() - arg0.hashCode();
  }
}

/* Location:           /Users/schlabachn1/decompile/Factorization.jar
 * Qualified Name:     factorization.api.ConductorSet
 * JD-Core Version:    0.6.2
 */
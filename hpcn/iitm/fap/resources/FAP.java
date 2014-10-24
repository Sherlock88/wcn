package hpcn.iitm.fap.resources;

import hpcn.iitm.fap.resources.UE;
import iitm.hpcn.fap.Main;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class FAP {

	private int id;
	private int fapVictimCount;
	private Point2D location;
	private double bias;
	public Set<UE> associatedUE = new HashSet<UE>();
	
	public FAP(int id, Point2D location) {
		super();
		this.id = id;
		fapVictimCount = 0;
		this.location = location;
		this.bias = Main.BIAS;
	}

	public double getBias()
	{
		return bias;
	}
	
	public void setBias(double bias)
	{
		this.bias = bias;
	}
	
	public int getId() {
		return id;
	}

	public Point2D getLocation() {
		return location;
	}

	public void addUE(UE ue) {
			associatedUE.add(ue);
	}

	public void removeAllUE() {
		associatedUE.clear();
	}

	public void printUE() {
		System.out.println(this + "\t: " + associatedUE.size() + "\t: " + associatedUE);
	}

	@Override
	public String toString() {
		return String.format("FAP%s", id);
	}

	public int getUECount() {
		return associatedUE.size();
	}
	
	public Set<UE> getAssociatedUE() {
		return associatedUE;
	}
	
	public void setVictimUeStatus()
	{
		int victimCount = 0;
		for(UE ue : associatedUE) {
			if(ue.getSINRILdb() < Params.MIN_SINR_TH_DB) {
				ue.setUeVictim(true);
				victimCount++;
			}
			else
				ue.setUeVictim(false);
		}
		setFapVictimCount(victimCount);
	}
	
//	public int getVictimUeCount()
//	{
//		int count = 0;
//		for(UE ue : associatedUE)
//			if(ue.isUeVictim() == true)
//				count++;
//		return count;
//	}
	
	public int getFapVictimCount() {
		return fapVictimCount;
	}

	public void setFapVictimCount(int fapVictimCount) {
		this.fapVictimCount = fapVictimCount;
	}
	
}

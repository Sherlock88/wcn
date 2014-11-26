package hpcn.iitm.fap.resources;

import hpcn.iitm.fap.resources.UE;
import iitm.hpcn.fap.Main;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class FAP {

	private int id;
	private int fapVictimCount;
	private int mapVictimCount;
	private Point2D location;
	private double bias;
	private int macroIndex;
	public Set<UE> associatedPicoUE = new HashSet<UE>();
	
	public FAP(int id, Point2D location) {
		super();
		this.id = id;
		fapVictimCount = 0;
		mapVictimCount = 0;
		this.location = location;
		this.bias = Main.BIAS;
	}
	
	public FAP(int id, Point2D location, int macroIndex) {
		super();
		this.id = id;
		fapVictimCount = 0;
		mapVictimCount = 0;
		this.location = location;
		this.bias = Main.BIAS;
		this.macroIndex = macroIndex;
	}

	public int getMapVictimCount() {
		return mapVictimCount;
	}
	
	public void setMapVictimCount(int mapVictimCount) {
		this.mapVictimCount = mapVictimCount;;
	}
	
	public int getMacroIndex() {
		return macroIndex;
	}
	
	public void setMacroIndex(int macroIndex) {
		this.macroIndex = macroIndex;
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
			associatedPicoUE.add(ue);
	}

	public void removeAllUE() {
		associatedPicoUE.clear();
	}

	public void printUE() {
		System.out.println(this + "\t: " + associatedPicoUE.size() + "\t: " + associatedPicoUE);
	}

	@Override
	public String toString() {
		return String.format("FAP%s", id);
	}

	public int getUECount() {
		return associatedPicoUE.size();
	}
	
	public Set<UE> getAssociatedUE() {
		return associatedPicoUE;
	}
	
	public void setVictimUeStatus()
	{
		int victimCount = 0;
		for(UE ue : associatedPicoUE) {
			/*if(ue.getSINRILdb() < Params.MIN_SINR_TH_DB) {
				ue.setUeVictim(true);
				victimCount++;
			}
			else
				ue.setUeVictim(false);*/
			double distance = location.distance(ue.getLocation());
			if((Params.PICO_RADIUS - distance) <= Params.B) {
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

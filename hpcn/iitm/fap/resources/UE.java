package hpcn.iitm.fap.resources;

import hpcn.iitm.fap.resources.FAP;
import hpcn.iitm.fap.resources.Params;
import hpcn.iitm.fap.resources.TargetBsDTO;
import iitm.hpcn.fap.PathLoss;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class UE {

	private int id;
	private Point2D location;
	private TargetBsDTO target;
	// ueType = MUE, FUE, out of range & can not communicate.
	private int ueType;
	private int dataRate;
	private int macroIndex;
	private boolean ueVictim;
	private boolean sorted;
	private double sinrMacroDB;
	private double sinrMacroDB_IF, sinrMacroDB_IL;

	private double rsrpMacroDB;
	private double bitRate;
	private double bitRateABS;

	private ArrayList<TargetBsDTO> fapList;
	private HashSet<Integer> fapSet;

	public UE(int id, Point2D location) {
		this.id = id;
		this.location = location;
		this.ueType = Params.OUT_OF_RANGE;
		this.ueVictim = false;
		this.sorted = false;

		fapSet = new HashSet<Integer>();
		fapList = new ArrayList<TargetBsDTO>();
	}
	
	public UE(int id, Point2D location, int dataRate, int macroIndex) {
		this.id = id;
		this.location = location;
		this.ueType = Params.OUT_OF_RANGE;
		this.ueVictim = false;
		this.sorted = false;
		this.dataRate = dataRate;
		this.macroIndex = macroIndex;

		fapSet = new HashSet<Integer>();
		fapList = new ArrayList<TargetBsDTO>();
	}

	public int getDataRate() {
		return dataRate;
	}
	
	public void setDataRate(int dataRate) {
		this.dataRate = dataRate;
	}
	
	public int getMacroIndex() {
		return macroIndex;
	}
	
	public void setMacroIndex(int macroIndex) {
		this.macroIndex = macroIndex;
	}
	
	public double getBitRate() {
		return bitRate;
	}

	public void setBitRate(double bitRate)
	{
		this.bitRate = bitRate;
	}
	public double getBitRateABS() {
		return bitRateABS;
	}

	public boolean isUeVictim() {
		return ueVictim;
	}

	public void setUeVictim(boolean ueVictim) {
		this.ueVictim = ueVictim;
	}

	public double getSinrMacroDB_IF() {
		return sinrMacroDB_IF;
	}

	public void setSinrMacroDB_IF(double sinrMacroDB_IF) {
		this.sinrMacroDB_IF = sinrMacroDB_IF;
	}

	public double getSinrMacroDB_IL() {
		return sinrMacroDB_IL;
	}

	public void setSinrMacroDB_IL(double sinrMacroDB_IL) {
		this.sinrMacroDB_IL = sinrMacroDB_IL;
	}

	public int getId() {
		return id;
	}

	public Point2D getLocation() {
		return location;
	}

	public int getUeType() {
		return ueType;
	}

	public double getRsrpMacroDB() {
		return rsrpMacroDB;
	}

	public void setRsrpMacroDB(double rsrpMacroDB) {
		this.rsrpMacroDB = rsrpMacroDB;
	}

	public void setOutOfRange() {
		this.ueType = Params.OUT_OF_RANGE;
	}

	public boolean isOutOfRange() {
		return (this.ueType == Params.OUT_OF_RANGE) ? true : false;
	}

	public void setSinrMacroDB(double sinrMacroDB) {
		this.sinrMacroDB = sinrMacroDB;
	}
	
	public void calcDataRateRSRP(int mUeCount, int mVictimCount)
	{
		double dataRate = 0.0;
		double subChannelPerUser = 0.0;
		double freeSubChannels = 0.0;
		
		if (ueType == Params.MUE) 
		{
			dataRate = 1.0 * Math.log10(1 + PathLoss.dB2watt(sinrMacroDB_IL)) / Math.log10(2.0);
			subChannelPerUser = (double) (Params.SCs_FOR_10_MHz - freeSubChannels)/ mUeCount;
			dataRate = dataRate * Params.SUBCHANNEL_BANDWIDTH * subChannelPerUser;
		}
		else if (ueType == Params.FUE)
		{
			dataRate = 1.0 * Math.log10(1 + PathLoss.dB2watt(getSINRILdb())) / Math.log10(2.0);
			subChannelPerUser = (double) Params.SCs_FOR_10_MHz / target.getFapBS().getUECount();
			dataRate = dataRate * Params.SUBCHANNEL_BANDWIDTH * subChannelPerUser;
		}
		else
		{
			System.err.println("ERROR :: UE is out of range SINR value unreliable.");
		}
		bitRate = dataRate;
	}
	
	// Data Rates are computed considering full bandwidth
	// Reduction by alpha factor of b/w is not done here
	public void calcDataRateABS2(int mUeCount, int mVictimCount) { 	//TODO WCN Project
		double dataRate = 0.0;
		if (ueType == Params.MUE) {
			if (ueVictim == true) {
				dataRate = 1.0 * Math.log10(1 + PathLoss.dB2watt(sinrMacroDB_IF))
						/ Math.log10(2.0); 
				dataRate = dataRate * (Params.BANDWIDTH / mVictimCount);
			} else {
				dataRate = 1.0 * Math.log10(1 + PathLoss.dB2watt(sinrMacroDB_IL))
						/ Math.log10(2.0);
				dataRate = dataRate * (Params.BANDWIDTH / (mUeCount - mVictimCount));
			}
		} else if (ueType == Params.FUE) {
			if (ueVictim == true) {
				dataRate = 1.0 * Math.log10(1 + PathLoss.dB2watt(getSINRIFdb()))
						/ Math.log10(2.0);
				dataRate = dataRate * (Params.BANDWIDTH / target.getFapBS().getFapVictimCount());
			} else {
				dataRate = 1.0 * Math.log10(1 + PathLoss.dB2watt(getSINRILdb()))
						/ Math.log10(2.0);
				dataRate = dataRate * (Params.BANDWIDTH / (target.getFapBS().getUECount() - target
						.getFapBS().getFapVictimCount()));
			}
		}
		else
		{
			System.err.println("ERROR :: UE is out of range SINR value unreliable.");
		}
		
		bitRate = dataRate;
	}
	
	public void calcDataRateABSFinal(int mUeCount, int mVictimCount, double alphaM, double alphaP) {
		double dataRate = 0.0;
		if (ueType == Params.MUE) {
			if (ueVictim == true) {
				dataRate = (alphaP) * Math.log10(1 + PathLoss.dB2watt(sinrMacroDB_IF))
						/ Math.log10(2.0); 
				dataRate = dataRate * (Params.BANDWIDTH / mVictimCount);
			} else {
				dataRate = (1.0 - alphaM - alphaP) * Math.log10(1 + PathLoss.dB2watt(sinrMacroDB_IL))
						/ Math.log10(2.0);
				//dataRate = (1.0 - alphaP) * Math.log10(1 + PathLoss.dB2watt(sinrMacroDB_IL))	//RPS is considered
						/// Math.log10(2.0);
				dataRate = dataRate * (Params.BANDWIDTH / (mUeCount - mVictimCount));
			}
		} else if (ueType == Params.FUE) {
			if (ueVictim == true) {
				dataRate = (alphaM) * Math.log10(1 + PathLoss.dB2watt(getSINRIFdb()))
						/ Math.log10(2.0);
				dataRate = dataRate * (Params.BANDWIDTH / target.getFapBS().getFapVictimCount());
			} else {
				dataRate = (1.0 - alphaM - alphaP) * Math.log10(1 + PathLoss.dB2watt(getSINRILdb()))
						/ Math.log10(2.0);
				//dataRate = (1.0 - alphaM) * Math.log10(1 + PathLoss.dB2watt(getSINRILdb()))		//RPS is considered
						/// Math.log10(2.0);
				dataRate = dataRate * (Params.BANDWIDTH / (target.getFapBS().getUECount() - target
						.getFapBS().getFapVictimCount()));
			}
		}
		else
		{
			System.err.println("ERROR :: UE is out of range SINR value unreliable.");
		}
		//bitRateABS = dataRate;
		bitRate = dataRate;
	}

	public double getSinrMacroDB() {
		if (ueType == Params.OUT_OF_RANGE)
			System.err
					.println("ERROR :: UE is out of range SINR value unreliable.");
		if (target == null)
			return sinrMacroDB;
		System.err
				.println("ERROR :: connected to femto. Thermal noise returned.");
		return Params.NOISE_DB;
	}

	public double getSINRILdb() {
		if (ueType == Params.OUT_OF_RANGE) {
			System.err
					.println("ERROR :: UE is out of range SINR value unreliable.");
			return Params.NOISE_DB;
		}
		if (target != null)
			return target.getSinrDBIL();
		System.err
				.println("ERROR :: connected to macro SINR returned[sinrMacroIL]");
		return sinrMacroDB_IL; // raj
	}

	public double getSINRIFdb() {
		if (ueType == Params.OUT_OF_RANGE)
			System.err
					.println("ERROR :: UE is out of range SINR  = Thermal noise.");
		if (target != null)
			return target.getSinrDBIF();
		System.err
				.println("ERROR :: connected to macro. SINR IF = Thermal noise.");
		return sinrMacroDB_IF; //raj
	}

	public void add(double femtoSignal, double sinrDBIL, double sinrDBIF,
			FAP pTarget) {
		int fapID = pTarget.getId();
		if (fapSet.contains(fapID)) {
			System.err.println("FAP(" + fapID + ") already present");
			return;
		}
		fapSet.add(fapID);
		sorted = false;
		// System.out.println(sinrDBIL+" :: "+sinrDBIF);
		TargetBsDTO bs = new TargetBsDTO(femtoSignal, sinrDBIL, sinrDBIF,
				pTarget);
		fapList.add(bs);
	}

	public FAP maxRsrp() {
		FAP fap = null;
		if (!sorted) {
			sortBSperRsrp();
			sorted = true;
		}
		TargetBsDTO tBs = fapList.get(0);
		if (rsrpMacroDB > tBs.getRsrpFemtoDB()) {
			target = null;
			ueType = Params.MUE;
		} else {
			ueType = Params.FUE;
			target = tBs;
			fap = target.getFapBS();
		}
		return fap;
	}


	@Override
	public String toString() {
		if (target == null)
			return String.format("UE%s(Macro)", id);
		else
			return String.format("UE%s(%s)", id, target.getFapBS());
	}

	// sort bs per max rsrp.
	private void sortBSperRsrp() {
		Collections.sort(fapList, new MaxRsrpIL());
	}

	private class MaxRsrpIL implements Comparator<TargetBsDTO> {
		// sort in decreasing order.
		//@Override
		public int compare(TargetBsDTO e1, TargetBsDTO e2) {
			return e2.getRsrpFemtoDB() > e1.getRsrpFemtoDB() ? 1 : (e2
					.getRsrpFemtoDB() < e1.getRsrpFemtoDB() ? -1 : 0);
		}
	}

}

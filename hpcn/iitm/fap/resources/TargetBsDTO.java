package hpcn.iitm.fap.resources;

import iitm.hpcn.fap.Main;
import iitm.hpcn.fap.PathLoss;
import hpcn.iitm.fap.resources.FAP;

public class TargetBsDTO {
	
	private double sinrDBIL, sinrDBIF;
	private double rsrpFemtoDB;
	private FAP fapBS;
	
	public TargetBsDTO(double rsrpFemto, double sinrDBIL, double sinrDBIF, FAP fapTarget) {
		super();
		this.sinrDBIL = sinrDBIL;
		this.sinrDBIF = sinrDBIF;
		this.fapBS = fapTarget;
		this.rsrpFemtoDB =rsrpFemto;
	}
	
	public double getBiasedRsrpDB() {
		return rsrpFemtoDB + fapBS.getBias();
	}
	
	public double getSinrDBIL() {
		return sinrDBIL;
	}

	public void setSinrDBIL(double sinrDBIL) {
		this.sinrDBIL = sinrDBIL;
	}

	public double getSinrDBIF() {
		return sinrDBIF;
	}

	public void setSinrDBIF(double sinrDBIF) {
		this.sinrDBIF = sinrDBIF;
	}

	public FAP getFapBS() {
		return fapBS;
	}

	public void setFapBS(FAP target) {
		this.fapBS = target;
	}

	public double getRsrpFemtoDB() {
		return rsrpFemtoDB;
	}

	public void setRsrpFemtoDB(double rsrpFemto) {
		this.rsrpFemtoDB = rsrpFemto;
	}
	@Override
	public String toString() {
		return " [" + fapBS + " sinrDBIF=" + sinrDBIF + ", sinrDBIL="
				+ sinrDBIL + "]";
	}
	
	public double getTotalBitRate() {
		double bitRate = 0.0;
		if(rsrpFemtoDB >= Params.MIN_RSRP_TH_DB)
			bitRate = ((1 - Main.ALPHA) * (Math.log10(1 + PathLoss.dB2watt(sinrDBIL)) / Math.log10(2.0))) + (Main.ALPHA * (Math.log10(1 + PathLoss.dB2watt(sinrDBIF)) / Math.log10(2.0)));
		return bitRate;
	}
	
	public double getResidualBitrate() {
		return getTotalBitRate() / (fapBS.getUECount() + 1);
	}

	// the real bit rate achieved with equal channel allocation.
//	public double getAllocatedBitRate() {
//		double bitRate = 0.0;
//		if (sinrDBIF >= Params.MIN_RSRP_TH_DB)
//			bitRate += Main.ALPHA * Math.log10(1 + PathLoss.dB2watt(sinrDBIF)) / Math.log10(2.0); //raj : Is log value correct
//		if (sinrDBIL >= Params.MIN_RSRP_TH_DB)
//			bitRate += (1 - Main.ALPHA)	* Math.log10(1 + PathLoss.dB2watt(sinrDBIL)) / Math.log10(2.0);//raj : Is log value correct
//		bitRate /= fapBS.getUECount();
//		return bitRate;
//	}
}

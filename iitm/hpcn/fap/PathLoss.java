package iitm.hpcn.fap;

import hpcn.iitm.fap.resources.FAP;
import hpcn.iitm.fap.resources.MAP;
import hpcn.iitm.fap.resources.Params;
import hpcn.iitm.fap.resources.UE;

//pathloss equations are from lte-sim

public class PathLoss {

	public static double PLmbs2UE(double distance) {
		double PLoss = 128.1 + (37.6 * Math.log10 (distance*0.001));	//standard pathloss eqn
//		double PLoss = 138 + (37.6 * Math.log10(distance * 0.001));		//provides 654m of coverage
//		double PLoss = 0 + (25.0 * Math.log10 (distance));
		return PLoss;
	}
	
	public static double PLfap2UE(double distance) {
		//rajkarn
		//double PLoss = 	30.6 + 36.7 * Math.log10(distance); //distance in Metres
		double PLoss = 140.7 + 36.7 * Math.log10(distance * 0.001); //distance in kilometres [STANDARD]
		//double PLoss = 138.0 + 36.7 * Math.log10(distance * 0.001); //distance in kilometres
		
//		double PLoss = 127 + 30 * Math.log10 (distance * 0.001);// + Params.EXT_WALL_LOSS_DB;
//		if(distance > 15)
//			PLoss += Params.EXT_WALL_LOSS_DB;
//		double PLoss = 0 + (30.0 * Math.log10 (distance));
		return PLoss;
	}
	
	//rajkarn
	public static double PLpico2UE(double distance)
	{
		double PLoss = 	30.6 + 36.7 * Math.log10(distance); //distance in Metres
		//double PLoss = 140.7 + 36.7 * Math.log10(distance * 0.001); //distance in kilometers
		//both pathloss models give same value
		return PLoss;
	}
	
	public static double rxPowerdB(UE ue, MAP map)
	{
		double rxSignal = 0;
		double distance = 0;
		distance = ue.getLocation().distance(map.getLocation());
		rxSignal = watt2dB(Params.MACRO_POWER) - PathLoss.PLmbs2UE(distance);
		return rxSignal;
	}
	
	public static double rxPowerdB(UE ue, FAP fap)
	{
		double rxSignal = 0;
		double distance = 0;
		distance = ue.getLocation().distance(fap.getLocation());
		rxSignal = watt2dB(Params.FAP_POWER) - PathLoss.PLfap2UE(distance);
		return rxSignal;
	}

	public static double rxPowerWatt(UE ue, MAP map) {
		return dB2watt(rxPowerdB(ue,map));
	}

	public static double rxPowerWatt(UE ue, FAP fap) {
		return dB2watt(rxPowerdB(ue,fap));
	}

	public static double watt2dB(double powerWatt) {
		double DB = 10 * Math.log10(powerWatt);
		return DB;
	}

	public static double dB2watt(double dB) {
		double watt = Math.pow(10, dB / 10);
		return watt;
	}

	public static double watt2dBm(double powerWatt) {
		double DBm = 10 * Math.log10(1000 * powerWatt);
		return DBm;
	}

	public static double dBm2watt(double dBm) {
		double watt = Math.pow(10, (dBm - 30) / 10);
		return watt;
	}

	public static double dB2dBm(double dB) {
		double DBm = watt2dBm(dB2watt(dB));
		return DBm;
	}

	public static double dBm2dB(double DBm) {
		double DB = watt2dB(dBm2watt(DBm));
		return DB;
	}
}

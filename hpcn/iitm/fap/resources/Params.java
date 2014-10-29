package hpcn.iitm.fap.resources;

public interface Params {
	//resource blocks 	
	public static final int NO_OF_CHANNELS = 256;	

	public static final int SCs_FOR_1_4_MHz = 6;
	public static final int SCs_FOR_3_MHz = 15;
	public static final int SCs_FOR_5_MHz = 25;
	public static final int SCs_FOR_10_MHz = 50;
	public static final int SCs_FOR_15_MHz = 75;
	public static final int SCs_FOR_20_MHz = 100;
	
	
	//Transmitted power should be 46dbm for 10MHz bandwidth
	public static final double MACRO_POWER = 39.8/SCs_FOR_10_MHz;	// 46 dbm for 10MHz, 43 dbm for <=5MHz
	public static final double FAP_POWER = 1.0/SCs_FOR_10_MHz;		// 30 dbm [PICO]

	public static final double MIN_SINR_TH_DB = -10.0;
	public static final double EXT_WALL_LOSS_DB = 20;  				
	public static final double PENETRATION_LOSS_DB = 10;
	public static final double ANTENNAGAIN_FEMTO_DBI = 0;// 8
	public static final double ANTENNAGAIN_MACRO_DBI = 0;// 15
	public static final double NOISE_DB = -144.0;// or -97.5;
	public static final double MIN_RSRP_TH_DB = -135.0; 
	public static final double BANDWIDTH = 10000;	//In kHz  // 10MHz
	public static final double SUBCHANNEL_BANDWIDTH = 200;	//iN kHz
	
	public static final double BSCHANNELWATT = 5;
	
	public static final int MAXRSRP = 1;
	public static final int MAXRSRPBIAS = 2;
	public static final int TOTALCAPACITY = 3;
	public static final int RESIDUALCAPACITY = 4;
	public static final int COMBINEDABS = 5;
	
	public static final int OUT_OF_RANGE = -1;
	public static final int MUE = 0;
	public static final int FUE = 1;
	
	public static final double PERCENTILE_5 = 0.05;
	public static final double PERCENTILE_10 = 0.1;
	
	public static final double BITRATE_MAX = 5.0;
	public static final double BITRATE_MIN = 0.0;
	
	public static final double SINR_MAX = 40.0;
	public static final double SINR_MIN = -40.0;
	public static final int CDF_STEP = 1000;
}
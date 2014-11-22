package iitm.hpcn.fap;

import hpcn.iitm.fap.resources.FAP;
import hpcn.iitm.fap.resources.UE;
import hpcn.iitm.fap.resources.Params;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Simulator {
	private Set<UE> setUE = new HashSet<UE>();
	private ArrayList<UE> listUE = new ArrayList<UE>();
	private ArrayList<UE> listMUE = new ArrayList<UE>();
	private ArrayList<UE> listFUE = new ArrayList<UE>();
	private Set<FAP> setFAP = new HashSet<FAP>();
	private Set<UE> associatedMacroUE = new HashSet<UE>();
	private int macroVictimCount = 0;
	private int associationType = 0;
	private int macroIndex = 0;
	private double ee = 0;
	private double capacity = 0;
	private double capacityMV = 0;
	private double capacityM = 0;
	private double capacityFV = 0;
	private double capacityF = 0;
	private double capacityAllVictim = 0;
	private double capacity5PerMV = 0;
	private double capacity5PerFV = 0;
	private double capacity5PerAllVictim = 0;
	
	// Station types
	public static final int STATIONMACRO = 0;
	public static final int STATIONPICO = 1;
	public static final int STATIONUE = 2;
		
	private SummaryStatistics bitRateMV = new SummaryStatistics();
	private SummaryStatistics bitRateFV = new SummaryStatistics();
	private SummaryStatistics bitRateMA = new SummaryStatistics();
	private SummaryStatistics bitRateFA = new SummaryStatistics();
	
	private ArrayList<Double> sinrListM;
	private ArrayList<Double> macroVictimSinr;
	//private ArrayList<Double> sinrListM_IF;
	//private ArrayList<Double> sinrListM_IL;
	private ArrayList<Double> sinrListF;
	private ArrayList<Double> femtoVictimSinr;
	//private ArrayList<Double> sinrListF_IF;
	//private ArrayList<Double> sinrListF_IL;
	
	private ArrayList<Double> bitRateListM;
	private ArrayList<Double> bitRateListMV;
	private ArrayList<Double> bitRateListF;
	private ArrayList<Double> bitRateListFV;
	private ArrayList<Double> bitRateListAll;
	private ArrayList<Double> bitRateListAllVictim;
	private ArrayList<Double> bitRateList5PerMV;
	private ArrayList<Double> bitRateList5PerFV;
	private ArrayList<Double> bitRateList5PerAllVictim;
	private ArrayList<Double> sinrAll;
	private ArrayList<Double> sinrAllVictim;
	private double fairnessIndexAll;
	private double fairnessIndexAllVictim;
	private double alphaM;
	private double alphaP;

	boolean ABSmode;

	public void init(String UEFile, String FAPFile, int associationType)
	{
		init(UEFile, FAPFile, associationType, macroIndex);
	}
	
	public void init(String UEFile, String FAPFile, int associationType, int macroIndex) {
		this.ABSmode = false;
		this.macroIndex = macroIndex;
		this.associationType = associationType;
		try {
			getInfo(UEFile, STATIONUE);
			getInfo(FAPFile, STATIONPICO);
			listUE.addAll(setUE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void runSim() {
		warmUpUe();
		associateUEs();
		setVictimStatus();
		//calculateSinrRange();	//to check the SINR ranges at various distances
		makeSINRList();
		calculateDataRates();
		makeBitrateList();
		//sortUEperBitRate();
		makePercentileBitrateList();
		makeSummaryStatistics();
		System.out.println("MUE#: " + associatedMacroUE.size());
	}
	
	public void init_2(String ABSFile)
	{
		this.ABSmode = true;
		try {
			Scanner fileScanner = new Scanner(new File(ABSFile));
			this.alphaM = fileScanner.nextDouble();
			this.alphaP = fileScanner.nextDouble();
			fileScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	public void runSim_2(int scnCount)
	{
		warmUpUe();
		associateUEs();
		setVictimStatus();
		makeSINRList();
		calculateDataRatesABSFinal();
		makeBitrateList();
		//sortUEperBitRate();
		makePercentileBitrateList();
		makeSummaryStatistics();
	}

	public void calculateSinrRange() {
		Point2D point = new Point2D.Double(450.0, 0.0);
		FAP fap = new FAP(10, point);
		
		double noiseWatt = PathLoss.dB2watt(Params.NOISE_DB);
		double mSignal = 0.0, fSignal = 0.0;
		double sinrMacro_IL = 0.0;
		double sinrFemto_IL = 0.0;
		for (int dist = 1; dist < 450; dist++) {
			double distance = dist;
			double femtoSignalSum = 0.0;
			UE ue = new UE(dist, new Point2D.Double(distance, 0.0));
			mSignal = PathLoss.rxPowerWatt(ue, null);

			fSignal = PathLoss.rxPowerWatt(ue, fap);

			for (FAP fbs : setFAP) {
				femtoSignalSum = femtoSignalSum + PathLoss.rxPowerWatt(ue, fbs);
			}

			sinrMacro_IL = PathLoss.watt2dB(mSignal / (femtoSignalSum + noiseWatt));
			sinrFemto_IL = PathLoss.watt2dB(fSignal / (femtoSignalSum - fSignal + mSignal + noiseWatt));
			double diff = sinrMacro_IL - sinrFemto_IL;
			//System.out.println(dist + " " + "SINR Diff: " + diff + " mSINR: " + sinrMacro_IL + " fSINR: "
				//	+ sinrFemto_IL + " mSignal: " + mSignal + " fSignal: " + fSignal);

			System.out.printf("%d Diff: %.4f mSINR %.4f fSINR %.4f mSignal %.10f fSignal %.10f\n", 
					dist, diff, sinrMacro_IL, sinrFemto_IL, PathLoss.watt2dB(mSignal), PathLoss.watt2dB(fSignal));
		}
	}

	public void warmUpUe() {
		for (UE ue : setUE) {
			double macroSignal = PathLoss.rxPowerWatt(ue, null);	//NULL represents Macrocell
			double noiseWatt = PathLoss.dB2watt(Params.NOISE_DB);
			double sinrMacro_IL = 0.0;
			double sinrMacro_IF = 0.0;
			double femtoSignalSum = 0.0;
			double femtoSinrIL = 0.0;
			double femtoSinrIF = 0.0;

			ue.setRsrpMacroDB(PathLoss.watt2dB(macroSignal));// +Params.ANTENNAGAIN_MACRO_DBI);

			// sum of all femtocell signals at the current UE location under consideration.
			for (FAP fap : setFAP)
				femtoSignalSum += PathLoss.rxPowerWatt(ue, fap);

			sinrMacro_IL = (PathLoss.watt2dB(macroSignal / (femtoSignalSum + noiseWatt)));

			sinrMacro_IF = (PathLoss.watt2dB(macroSignal / noiseWatt));
			if (sinrMacro_IL > 40)
				sinrMacro_IL = 40;

			if (sinrMacro_IF > 40)
				sinrMacro_IF = 40;

			ue.setSinrMacroDB_IL(sinrMacro_IL);
			ue.setSinrMacroDB_IF(sinrMacro_IF);

			// collect SINR_IF, SINR_IL of all femto BS
			for (FAP fap : setFAP) {
				double femtoSignal = PathLoss.rxPowerWatt(ue, fap);

				femtoSinrIF = femtoSignal
						/ (femtoSignalSum - femtoSignal + noiseWatt);

				femtoSinrIL = femtoSignal
						/ (femtoSignalSum - femtoSignal + macroSignal + noiseWatt);

				femtoSinrIF = PathLoss.watt2dB(femtoSinrIF);
				femtoSinrIL = PathLoss.watt2dB(femtoSinrIL);
				femtoSignal = PathLoss.watt2dB(femtoSignal);

				if (femtoSinrIF > 40)
					femtoSinrIF = 40;
				if (femtoSinrIL > 40)
					femtoSinrIL = 40;
				ue.add(femtoSignal, femtoSinrIL, femtoSinrIF, fap);
			}
		}
	}

	public void associateUEs() {
		for (UE ue : setUE) {
			FAP target = null;

			if (associationType == Params.MAXRSRP || associationType == Params.COMBINEDABS)
				target = ue.maxRsrp();	// For combined ABS calculation, association criteria is assumed to be RSRP based
			else
				System.err.println("ERROR :: unknown association tech all associated to macro bs");

			if (target != null)
				target.addUE(ue);
			else
				associatedMacroUE.add(ue);
		}
	}

	public void calculateDataRates() {
		if(associationType == 1)
			for (UE ue : setUE)
				ue.calcDataRateRSRP(getMacroUeCount(), getMacroVictimCount());
		if (associationType == 5)
			for (UE ue : setUE)
				ue.calcDataRateABS2(getMacroUeCount(), getMacroVictimCount());
		//else
			//System.err.println("ERROR: Unexpected Association Type");
	}
	
	private void sortUEperBitRate()
	{
		Collections.sort(listUE, new MaxBitRate());
		
		for(UE ue : listUE)
			if(ue.getUeType() == Params.MUE)
				listMUE.add(ue);
			else if(ue.getUeType() == Params.FUE)
				listFUE.add(ue);
			else
				System.err.println("Error: UE is out of range");
		
		Collections.sort(listMUE, new MaxBitRate());
		Collections.sort(listFUE, new MaxBitRate());
	}
	
	private class MaxBitRate implements Comparator<UE> {
		// sort in increasing order.
		public int compare(UE e1, UE e2) {
			return e2.getBitRate() > e1.getBitRate() ? -1 : (e2
					.getBitRate() < e1.getBitRate() ? 1 : 0);
		}
	}
	
	public void makePercentileBitrateList()
	{
		sortUEperBitRate();
		
		bitRateList5PerMV = new ArrayList<Double>();
		bitRateList5PerFV = new ArrayList<Double>();
		bitRateList5PerAllVictim = new ArrayList<Double>();
		int nUsers = 0;
		
		nUsers = (int) Math.floor(Params.PERCENTILE_10 * listUE.size());
		for(int i = 0; i < nUsers; i++)
		{
			UE ue = listUE.get(i);
			bitRateList5PerAllVictim.add(ue.getBitRate());
		}
		
		nUsers = (int) Math.floor(Params.PERCENTILE_10 * listMUE.size());
		for(int i = 0; i < nUsers; i++)
		{
			UE ue = listMUE.get(i);
			bitRateList5PerMV.add(ue.getBitRate());
		}
		
		nUsers = (int) Math.floor(Params.PERCENTILE_10 * listFUE.size());
		for(int i = 0; i < nUsers; i++)
		{
			UE ue = listFUE.get(i);
			bitRateList5PerFV.add(ue.getBitRate());
		}
		
		double sumMVThroughput = 0;
		double sumFVThroughput = 0;
		double sumAllVictimThroughput = 0;
		
		for(Double d : bitRateList5PerMV) {
			sumMVThroughput += d;
		}
		
		for(Double d : bitRateList5PerFV){
			sumFVThroughput += d;
		}
		
		for(Double d : bitRateList5PerAllVictim){
			sumAllVictimThroughput += d;
		}
		
		capacity5PerMV = sumMVThroughput / 1000;	//In Mbps
		capacity5PerFV = sumFVThroughput / 1000;
		capacity5PerAllVictim = sumAllVictimThroughput / 1000;
	}
	
	public void makeBitrateList()
	{
		bitRateListM = new ArrayList<Double>();
		bitRateListMV = new ArrayList<Double>();
		bitRateListF = new ArrayList<Double>();
		bitRateListFV = new ArrayList<Double>();
		bitRateListAll = new ArrayList<Double>();
		bitRateListAllVictim = new ArrayList<Double>();
		
		for(UE ue : setUE)
		{
			if(ue.getUeType() == Params.MUE)
			{
				bitRateListM.add(ue.getBitRate());
				bitRateListAll.add(ue.getBitRate());
				if(ue.isUeVictim() == true)
				{
					bitRateListMV.add(ue.getBitRate());
					bitRateListAllVictim.add(ue.getBitRate());
				}
			}
			else if(ue.getUeType() == Params.FUE)
			{
				bitRateListF.add(ue.getBitRate());
				bitRateListAll.add(ue.getBitRate());
				if(ue.isUeVictim() == true)
				{
					bitRateListFV.add(ue.getBitRate());
					bitRateListAllVictim.add(ue.getBitRate());
				}
			}
			else
			{
				System.err.println("ERROR :: UE is out of range SINR value unreliable.");
			}
		}
		
		double sumThroughput = 0;
		double sumSqThroughput = 0;
		double sumMThroughput = 0;
		double sumFThroughput = 0;
		double sumMVThroughput = 0;
		double sumFVThroughput = 0;
		double sumAllVictimThroughput = 0;
		//double sumSqAllVictimThroughput = 0;
		double sumEnergy = 0;
		
		for(Double d : bitRateListAll){
			sumThroughput += d;
		}
		
		for(Double d : bitRateListM){
			sumMThroughput += d;
		}
		
		for(Double d : bitRateListF){
			sumFThroughput += d;
		}
		
		for(Double d : bitRateListAll){
			sumSqThroughput += (d*d);
		}
		
		for(Double d : bitRateListMV) {
			sumMVThroughput += d;
		}
		
		for(Double d : bitRateListFV){
			sumFVThroughput += d;
		}
		
		for(Double d : bitRateListAllVictim){
			sumAllVictimThroughput += d;
		}
		
		//for(Double d : bitRateListAllVictim){
		//	sumSqAllVictimThroughput += (d*d);
		//}
		
		double bsEnergy = 250 + associatedMacroUE.size() * 5;	//raj - what does it mean?
		double fapEnergy = (setFAP.size() - idleFemtoCount()) * 10;
		
		sumEnergy = bsEnergy + fapEnergy;
		
		fairnessIndexAll = ((sumThroughput*sumThroughput) / sumSqThroughput ) / bitRateListAll.size();
		
		ee = (sumEnergy/sumThroughput) * 1000;	//Energy consumed perMb
		capacity = sumThroughput / 1000;	//In Mbps
		capacityM = sumMThroughput / 1000;
		capacityF = sumFThroughput / 1000;
		capacityMV = sumMVThroughput / 1000;
		capacityFV = sumFVThroughput / 1000;
		capacityAllVictim = sumAllVictimThroughput / 1000;
		
	}
	
	public void makeSummaryStatistics()
	{
		for(Double d : bitRateListMV) {
			bitRateMV.addValue(d);
		}
		
		for(Double d : bitRateListM) {
			bitRateMA.addValue(d);
		}
		
		for(Double d : bitRateListFV){
			bitRateFV.addValue(d);
		}
		
		for(Double d : bitRateListF) {
			bitRateFA.addValue(d);
		}
	}
	
	public void calculateDataRatesABSFinal()
	{
		for (UE ue : setUE)
			ue.calcDataRateABSFinal(getMacroUeCount(), getMacroVictimCount(), this.alphaM, this.alphaP);
	}
	
	public void writeUeCount(PrintWriter ueCountWriter)
	{
		ueCountWriter.printf("1 %d\n", setFAP.size());
		
		ueCountWriter.printf("%d %d\n",
				(getMacroUeCount() - getMacroVictimCount()),
				getMacroVictimCount());
		
		for (FAP fap : setFAP)
			ueCountWriter.printf("%d %d\n",
					(fap.getUECount() - fap.getFapVictimCount()),
					fap.getFapVictimCount());
	}

	public void writeDataRate(PrintWriter brWriter, boolean ABSflag) {
		if(ABSflag == false)
			for (UE ue : setUE)
				brWriter.printf("%.6f\n", ue.getBitRate());
		else
			for (UE ue : setUE)
				brWriter.printf("%.6f\n", ue.getBitRateABS());
	}
	
	public void writeMatlabScenario(PrintWriter matlabWriter)
	{
		matlabWriter.printf("rate = [...\n");
		for(UE ue : setUE)
			matlabWriter.printf("%.6f\n", ue.getBitRate());
		matlabWriter.printf("];\n\n");
		matlabWriter.printf("nvue_count = [...\n");
		matlabWriter.printf("%d\n", getMacroUeCount() - getMacroVictimCount());
		for (FAP fap : setFAP)
			matlabWriter.printf("%d\n", fap.getUECount() - fap.getFapVictimCount());
		matlabWriter.printf("];\n\n");
		matlabWriter.printf("vue_count = [...\n");
		matlabWriter.printf("%d\n", getMacroVictimCount());
		for (FAP fap : setFAP)
			matlabWriter.printf("%d\n", fap.getFapVictimCount());
		matlabWriter.printf("];\n\n");
	}

	public void makeSINRList() {
		sinrListM = new ArrayList<Double>();
		macroVictimSinr = new ArrayList<Double>();
		//sinrListM_IL = new ArrayList<Double>();
		//sinrListM_IF = new ArrayList<Double>();
		sinrListF = new ArrayList<Double>();
		femtoVictimSinr = new ArrayList<Double>();
		//sinrListF_IL = new ArrayList<Double>();
		//sinrListF_IF = new ArrayList<Double>();
		sinrAll = new ArrayList<Double>();
		sinrAllVictim = new ArrayList<Double>();

		for (UE ue : setUE) {
			if (ue.getUeType() == Params.MUE)
			{
				//if(ue.isUeVictim() == false)
					//sinrListM_IL.add(ue.getSinrMacroDB_IL());
				//else
					//sinrListM_IF.add(ue.getSinrMacroDB_IF());
				
				if(associationType == 1 || associationType == 2 || associationType == 3 || associationType == 4)
				{
					sinrListM.add(ue.getSinrMacroDB_IL());
					if(ue.isUeVictim() == true)
					{
						macroVictimSinr.add(ue.getSinrMacroDB_IL());
						sinrAllVictim.add(ue.getSinrMacroDB_IL());
					}
				}
				else	//associationType is CombinedABS
				{
					if(ue.isUeVictim() == true)
					{
						sinrListM.add(ue.getSinrMacroDB_IF());
						macroVictimSinr.add(ue.getSinrMacroDB_IF());
						sinrAllVictim.add(ue.getSinrMacroDB_IF());
					}
					else
						sinrListM.add(ue.getSinrMacroDB_IL());
				}
				sinrAll.add(ue.getSinrMacroDB_IL());
				sinrAll.add(ue.getSinrMacroDB_IF());
			}
			else if (ue.getUeType() == Params.FUE) 
			{
				//if(ue.isUeVictim() == false)
					//sinrListF_IL.add(ue.getSINRILdb());
				//else
					//sinrListF_IF.add(ue.getSINRIFdb());
			
				if(associationType == 1)
				{
					sinrListF.add(ue.getSINRILdb());
					if(ue.isUeVictim() == true)
					{
						femtoVictimSinr.add(ue.getSINRILdb());
						sinrAllVictim.add(ue.getSINRILdb());
					}
				}
				else
				{
					if(ue.isUeVictim() == true)
					{
						sinrListF.add(ue.getSINRIFdb());
						femtoVictimSinr.add(ue.getSINRIFdb());
						sinrAllVictim.add(ue.getSINRIFdb());
					}
					else
						sinrListF.add(ue.getSINRILdb());
				}
				sinrAll.add(ue.getSINRILdb());
				sinrAll.add(ue.getSINRIFdb());
			}
		}
	}

	public int getTotalFUECount() {
		int ueCount = 0;
		for (FAP fap : setFAP) {
			ueCount += fap.getUECount();
		}
		return ueCount;
	}

	public int idleFemtoCount() {
		int count = 0;
		for (FAP fap : setFAP)
			if (fap.getUECount() == 0)
				count++;
		return count;
	}

	public void setVictimStatus() {
		int victimCount = 0;
		for (UE ue : associatedMacroUE) {
			if (ue.getSinrMacroDB_IL() < Params.MIN_SINR_TH_DB) { //TODO WCN PROJECTs
				ue.setUeVictim(true);
				victimCount++;
			} else {
				ue.setUeVictim(false);
			}
		}
		setMacroVictimCount(victimCount);
		for (FAP fap : setFAP) {
			fap.setVictimUeStatus();
		}
	}

	public void setMacroVictimCount(int macroVictimCount) {
		this.macroVictimCount = macroVictimCount;
	}
	
	public int getMacroVictimCount() {
		return macroVictimCount;
	}

	public int getMacroUeCount() {
		return associatedMacroUE.size();
	}

	private void getInfo(String filename, int stationType) throws FileNotFoundException {
		Scanner fileScanner = new Scanner(new File(filename));
		double x, y;
		int macroIndex, dataRate, id = 0;
		
		while (fileScanner.hasNextLine()) {
			try
			{
				x = fileScanner.nextDouble();
				y = fileScanner.nextDouble();
				Point2D point = new Point2D.Double(x, y);
				if(stationType == STATIONUE)
				{
					dataRate = fileScanner.nextInt();
					macroIndex = fileScanner.nextInt();
					if(macroIndex == this.macroIndex)
						setUE.add(new UE(id++, point, dataRate, macroIndex));
				}
				else
					if(stationType == STATIONPICO)
					{
						macroIndex = fileScanner.nextInt();
						if(macroIndex == this.macroIndex)
							setFAP.add(new FAP(id++, point, macroIndex));
					}
			}
			catch (NoSuchElementException nse)
			{
			}
		}
		fileScanner.close();
		//return set;
	}

	public ArrayList<Double> getSinrListM() {
		return sinrListM;
	}
	
	public ArrayList<Double> getMacroVictimSinr() 	{
		return macroVictimSinr;
	}
	
	public ArrayList<Double> getFemtoVictimSinr() {
		return femtoVictimSinr;
	}
	
//	public ArrayList<Double> getSinrListM_IF() {
//		return sinrListM_IF;
//	}

//	public ArrayList<Double> getSinrListM_IL() {
//		return sinrListM_IL;
//	}

	public ArrayList<Double> getSinrListF() {
		return sinrListF;
	}
	
//	public ArrayList<Double> getSinrListF_IF() {
//		return sinrListF_IF;
//	}

//	public ArrayList<Double> getSinrListF_IL() {
//		return sinrListF_IL;
//	}

	public ArrayList<Double> getBitRateListM() {
		return bitRateListM;
	}

	public ArrayList<Double> getBitRateListF() {
		return bitRateListF;
	}

	public ArrayList<Double> getBitRateListAll() {
		return bitRateListAll;
	}
	
	public ArrayList<Double> getBitRateListMV() {
		return bitRateListMV;
	}

	public ArrayList<Double> getBitRateListFV() {
		return bitRateListFV;
	}
	
	public ArrayList<Double> getBitRateListAllVictim() {
		return bitRateListAllVictim;
	}

	public ArrayList<Double> getBitRateList5PerMV() {
		return bitRateList5PerMV;
	}
	public ArrayList<Double> getBitRateList5PerFV() {
		return bitRateList5PerFV;
	}
	public ArrayList<Double> getBitRateList5PerAllVictim() {
		return bitRateList5PerAllVictim;
	}
	public ArrayList<Double> getSinrAll() {
		return sinrAll;
	}

	public void setSinrAll(ArrayList<Double> sinrAll) {
		this.sinrAll = sinrAll;
	}
	
	public ArrayList<Double> getSinrAllVictim() {
		return sinrAllVictim;
	}

	public void setSinrAllVictim(ArrayList<Double> sinrAllVictim) {
		this.sinrAllVictim = sinrAllVictim;
	}

	public double getEE() {
		return ee;
	}

	public double getCAP() {
		return capacity;
	}
	
	public double getCAPMV() {
		return capacityMV;
	}
	
	public double getCAPM() {
		return capacityM;
	}
	
	public double getCAPFV() {
		return capacityFV;
	}
	
	public double getCAPF() {
		return capacityF;
	}
	
	public double getCAPAllVictim() {
		return capacityAllVictim;
	}
	
	public double getCapacity5PerMV() {
		return capacity5PerMV;
	}

	public double getCapacity5PerFV() {
		return capacity5PerFV;
	}

	public double getCapacity5PerAllVictim() {
		return capacity5PerAllVictim;
	}
	
	public double getFairnessIndexAll() {
		return fairnessIndexAll;
	}

	public double getFairnessIndexAllVictim() {
		return fairnessIndexAllVictim;
	}
	
	public double meanBitrateMV() {
		return bitRateMV.getMean();
	}
	
	public double meanBitrateFV() {
		return bitRateFV.getMean();
	}
	
	public double meanBitrateMA() {
		return bitRateMA.getMean();
	}
	
	public double meanBitrateFA() {
		return bitRateFA.getMean();
	}
	
	public double stdBitrateMV() {
		return bitRateMV.getStandardDeviation();
	}
	
	public double stdBitrateFV() {
		return bitRateFV.getStandardDeviation();
	}
	
	public double stdBitrateMA() {
		return bitRateMA.getStandardDeviation();
	}
	
	public double stdBitrateFA() {
		return bitRateFA.getStandardDeviation();
	}
}

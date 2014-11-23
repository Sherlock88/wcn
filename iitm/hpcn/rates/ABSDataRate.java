package iitm.hpcn.rates;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import hpcn.iitm.fap.resources.CDFHelper;
import hpcn.iitm.fap.resources.Params;
import iitm.hpcn.fap.Main;
import iitm.hpcn.fap.Simulator;

public class ABSDataRate {

	public static int RUNS;
	
	public static void main(String[] args) {
		
		args = "Data/ UE-Dist-600-2000 PICO-Dist-600-6 1000 0.1 6.0".split(" ");
		RUNS = Integer.parseInt(args[3]);
		RUNS = 2;

		String outPath1 = "./outPut/";
		
		new File(outPath1).mkdirs();

		PrintWriter eeWriter = null;
		PrintWriter capacityWriter = null;
		PrintWriter capacityMWriter = null;
		PrintWriter capacityFWriter = null;
		PrintWriter capacityMVWriter = null;
		PrintWriter capacityFVWriter = null;
		PrintWriter capacityAllVictimWriter = null;
		PrintWriter capacityPerMVWriter = null;
		PrintWriter capacityPerFVWriter = null;
		PrintWriter capacityPerAllVictimWriter = null;
		PrintWriter fairnessWriter = null;
		
		PrintWriter capacityMeanMV = null;
		PrintWriter capacityMeanFV = null;
		PrintWriter capacityMeanMA = null;
		PrintWriter capacityMeanFA = null;
		PrintWriter capacityStdMV = null;
		PrintWriter capacityStdFV = null;
		PrintWriter capacityStdMA = null;
		PrintWriter capacityStdFA = null;
		try {
			eeWriter = new PrintWriter(outPath1+"EE_ABS");
			capacityWriter = new PrintWriter(outPath1+"CAP_ABS");
			capacityMWriter = new PrintWriter(outPath1 + "capM_ABS");
			capacityFWriter = new PrintWriter(outPath1 + "capF_ABS");
			capacityMVWriter = new PrintWriter(outPath1 + "capMV_ABS");
			capacityFVWriter = new PrintWriter(outPath1 + "capFV_ABS");
			capacityAllVictimWriter = new PrintWriter(outPath1 + "capAllVictim_ABS");
			capacityPerMVWriter = new PrintWriter(outPath1 + "capMV_5Per_ABS");
			capacityPerFVWriter = new PrintWriter(outPath1 + "capFV_5Per_ABS");
			capacityPerAllVictimWriter = new PrintWriter(outPath1 + "capAllVictim_5Per_ABS");
			fairnessWriter = new PrintWriter(outPath1 + "fairnessAll_ABS");
			
			capacityMeanMV = new PrintWriter(outPath1 + "capacityMeanMV_ABS");
			capacityMeanFV = new PrintWriter(outPath1 + "capacityMeanFV_ABS");
			capacityMeanMA = new PrintWriter(outPath1 + "capacityMeanMA_ABS");
			capacityMeanFA = new PrintWriter(outPath1 + "capacityMeanFA_ABS");
			capacityStdMV = new PrintWriter(outPath1 + "capacityStdMV_ABS");
			capacityStdFV = new PrintWriter(outPath1 + "capacityStdFV_ABS");
			capacityStdMA = new PrintWriter(outPath1 + "capacityStdMA_ABS");
			capacityStdFA = new PrintWriter(outPath1 + "capacityStdFA_ABS");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		eeWriter.append(Main.ALPHA+"\t");
		capacityWriter.append(Main.ALPHA+"\t");
		capacityMWriter.append(Main.ALPHA+"\t");
		capacityFWriter.append(Main.ALPHA+"\t");
		capacityMVWriter.append(Main.ALPHA+"\t");
		capacityFVWriter.append(Main.ALPHA+"\t");
		capacityAllVictimWriter.append(Main.ALPHA+"\t");
		capacityPerMVWriter.append(Main.ALPHA+"\t");
		capacityPerFVWriter.append(Main.ALPHA+"\t");
		capacityPerAllVictimWriter.append(Main.ALPHA+"\t");
		fairnessWriter.append(Main.ALPHA+"\t");
		
		capacityMeanMV.append(Main.ALPHA+"\t");
		capacityMeanFV.append(Main.ALPHA+"\t");
		capacityMeanMA.append(Main.ALPHA+"\t");
		capacityMeanFA.append(Main.ALPHA+"\t");
		capacityStdMV.append(Main.ALPHA+"\t");
		capacityStdFV.append(Main.ALPHA+"\t");
		capacityStdMA.append(Main.ALPHA+"\t");
		capacityStdFA.append(Main.ALPHA+"\t");

		int runType = Params.COMBINEDABS;
		
		CDFHelper macroSinrCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"mSinr_"+Main.ALPHA+"_ABS_"+runType);
		CDFHelper macroVictimSinrCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"mVSinr_"+Main.ALPHA+"_ABS_"+runType);
		//CDFHelper macroSinrILCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"mSinrIL_"+Main.ALPHA+"_"+Main.BIAS+"_ABS_"+runType);
		//CDFHelper macroSinrIFCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"mSinrIF_"+Main.ALPHA+"_"+Main.BIAS+"_ABS_"+runType);
		CDFHelper femtoSinrCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"fSinr_"+Main.ALPHA+"_ABS_"+runType);
		CDFHelper femtoVictimSinrCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"fVSinr_"+Main.ALPHA+"_ABS_"+runType);
		//CDFHelper femtoSinrILCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"fSinrIL_"+Main.ALPHA+"_"+Main.BIAS+"_ABS_"+runType);
		//CDFHelper femtoSinrIFCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"fSinrIF_"+Main.ALPHA+"_"+Main.BIAS+"_ABS_"+runType);
		CDFHelper allSINRCDF = new CDFHelper(40, -40, Params.CDF_STEP, outPath1+"allSinr_"+Main.ALPHA+"_ABS_"+runType);
		CDFHelper allVictimSINRCDF = new CDFHelper(40, -40, Params.CDF_STEP, outPath1+"allVictimSinr_"+Main.ALPHA+"_ABS_"+runType);

		CDFHelper macroBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"mBitRate_"+Main.ALPHA+"_ABS_"+runType);	//raj
		CDFHelper femtoBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"fBitRate_"+Main.ALPHA+"_ABS_"+runType);
		CDFHelper allBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"allBitRate_"+Main.ALPHA+"_ABS_"+runType);
		
		CDFHelper macroVictimBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"mVBitRate_"+Main.ALPHA+"_ABS_"+runType);
		CDFHelper femtoVictimBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"fVBitRate_"+Main.ALPHA+"_ABS_"+runType);
		CDFHelper allVictimBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"allVBitRate_"+Main.ALPHA+"_ABS_"+runType);
		CDFHelper allVictimPercentileBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"allVPerBitRate_"+Main.ALPHA+"_ABS_"+runType);
		
		double energyEfficiency = 0;
		double systemCapacity = 0;
		double systemCapacityM = 0;
		double systemCapacityF = 0;
		double systemCapacityMV = 0;
		double systemCapacityFV = 0;
		double systemCapacityAllVictim = 0;
		double systemCapacityPerMV = 0;
		double systemCapacityPerFV = 0;
		double systemCapacityPerAllVictim = 0;
		double fairnessIndex = 0;
		
		double meanCapacityMV = 0;
		double meanCapacityFV = 0;
		double meanCapacityMA = 0;
		double meanCapacityFA = 0;
		double stdCapacityMV = 0;
		double stdCapacityFV = 0;
		double stdCapacityMA = 0;
		double stdCapacityFA = 0;
		
		for(int i = 1; i <= RUNS; i++)
		{
			System.out.println("Scenario: " + i);
			Simulator scenarioABS = new Simulator();
			scenarioABS.init(args[0] + args[1] + "-" + i, args[0] + args[2] + "-" + i, args[0] + args[3] + "-" + i, runType);
			scenarioABS.init_2("matlabData/Results/ABS_value_" + i);
			scenarioABS.runSim_2(RUNS);
			
/*			try {
				PrintWriter absBrWriter = new PrintWriter(outPath1 + outPath2 + "BRABS-" + runType + "-" + i);
				scenarioABS.writeDataRate(absBrWriter, true);
				absBrWriter.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}*/
			
			macroSinrCDF.addCDFList(scenarioABS.getSinrListM());
			macroVictimSinrCDF.addCDFList(scenarioABS.getMacroVictimSinr());
			//macroSinrILCDF.addCDFList(scenarioABS.getSinrListM_IL());
			//macroSinrIFCDF.addCDFList(scenarioABS.getSinrListM_IF());
			femtoSinrCDF.addCDFList(scenarioABS.getSinrListF());
			femtoVictimSinrCDF.addCDFList(scenarioABS.getFemtoVictimSinr());
			//femtoSinrILCDF.addCDFList(scenarioABS.getSinrListF_IL());
			//femtoSinrIFCDF.addCDFList(scenarioABS.getSinrListF_IF());
			
			macroBitRateCDF.addCDFList(scenarioABS.getBitRateListM());
			femtoBitRateCDF.addCDFList(scenarioABS.getBitRateListF());
			
			macroVictimBitRateCDF.addCDFList(scenarioABS.getBitRateListMV());
			femtoVictimBitRateCDF.addCDFList(scenarioABS.getBitRateListFV());
			allVictimBitRateCDF.addCDFList(scenarioABS.getBitRateListAllVictim());
			allVictimPercentileBitRateCDF.addCDFList(scenarioABS.getBitRateList5PerAllVictim());
			
			allBitRateCDF.addCDFList(scenarioABS.getBitRateListAll());
			allSINRCDF.addCDFList(scenarioABS.getSinrAll());
			allVictimSINRCDF.addCDFList(scenarioABS.getSinrAllVictim());
			
			energyEfficiency += scenarioABS.getEE();
			systemCapacity += scenarioABS.getCAP();
			systemCapacityM += scenarioABS.getCAPM();
			systemCapacityF += scenarioABS.getCAPF();
			systemCapacityMV += scenarioABS.getCAPMV();
			systemCapacityFV += scenarioABS.getCAPFV();
			systemCapacityAllVictim += scenarioABS.getCAPAllVictim();
			systemCapacityPerMV += scenarioABS.getCapacity5PerMV();
			systemCapacityPerFV += scenarioABS.getCapacity5PerFV();
			systemCapacityPerAllVictim += scenarioABS.getCapacity5PerAllVictim();
			fairnessIndex += scenarioABS.getFairnessIndexAll();
			
			meanCapacityMV += scenarioABS.meanBitrateMV();
			meanCapacityFV += scenarioABS.meanBitrateFV();
			meanCapacityMA += scenarioABS.meanBitrateMA();
			meanCapacityFA += scenarioABS.meanBitrateFA();
			stdCapacityMV += scenarioABS.stdBitrateMV();
			stdCapacityFV += scenarioABS.stdBitrateFV();
			stdCapacityMA += scenarioABS.stdBitrateMA();
			stdCapacityFA += scenarioABS.stdBitrateFA();
		}
		
		macroSinrCDF.saveToFile();
		macroVictimSinrCDF.saveToFile();
		//macroSinrILCDF.saveToFile();
		//macroSinrIFCDF.saveToFile();
		femtoSinrCDF.saveToFile();
		femtoVictimSinrCDF.saveToFile();
		//femtoSinrILCDF.saveToFile();
		//femtoSinrIFCDF.saveToFile();
		
		macroBitRateCDF.saveToFile();
		femtoBitRateCDF.saveToFile();
		
		macroVictimBitRateCDF.saveToFile();
		femtoVictimBitRateCDF.saveToFile();
		allVictimBitRateCDF.saveToFile();
		allVictimPercentileBitRateCDF.saveToFile();
		
		allBitRateCDF.saveToFile();
		allSINRCDF.saveToFile();
		allVictimSINRCDF.saveToFile();
		
		eeWriter.append(runType+"\t"+energyEfficiency/RUNS+"\n");
		capacityWriter.append(systemCapacity/RUNS+"\t");
		capacityMWriter.append(systemCapacityM/RUNS+"\t");
		capacityFWriter.append(systemCapacityF/RUNS+"\t");
		capacityMVWriter.append(systemCapacityMV/RUNS+"\t");
		capacityFVWriter.append(systemCapacityFV/RUNS+"\t");
		capacityAllVictimWriter.append(systemCapacityAllVictim/RUNS+"\t");
		capacityPerMVWriter.append(systemCapacityPerMV/RUNS+"\t");
		capacityPerFVWriter.append(systemCapacityPerFV/RUNS+"\t");
		capacityPerAllVictimWriter.append(systemCapacityPerAllVictim/RUNS+"\t");
		fairnessWriter.append(fairnessIndex/RUNS+"\t");
		
		capacityMeanMV.append(meanCapacityMV/RUNS + "\t");
		capacityMeanFV.append(meanCapacityFV/RUNS + "\t");
		capacityMeanMA.append(meanCapacityMA/RUNS + "\t");
		capacityMeanFA.append(meanCapacityFA/RUNS + "\t");
		capacityStdMV.append(stdCapacityMV/RUNS + "\t");
		capacityStdFV.append(stdCapacityFV/RUNS + "\t");
		capacityStdMA.append(stdCapacityMA/RUNS + "\t");
		capacityStdFA.append(stdCapacityFA/RUNS + "\t");
		
		
		eeWriter.close();
		capacityWriter.close();
		capacityMWriter.close();
		capacityFWriter.close();
		capacityMVWriter.close();
		capacityFVWriter.close();
		capacityAllVictimWriter.close();
		capacityPerMVWriter.close();
		capacityPerFVWriter.close();
		capacityPerAllVictimWriter.close();
		fairnessWriter.close();
		
		capacityMeanMV.close();
		capacityMeanFV.close();
		capacityMeanMA.close();
		capacityMeanFA.close();
		capacityStdMV.close();
		capacityStdFV.close();
		capacityStdMA.close();
		capacityStdFA.close();
		System.out.println("\nAll done.");	
	}
}

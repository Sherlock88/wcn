package iitm.hpcn.fap;

import hpcn.iitm.fap.resources.CDFHelper;
import hpcn.iitm.fap.resources.Params;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

	public static int RUNS;
	public static double ALPHA;
	public static double BIAS;
	public static boolean debug = false;
	public static boolean BIASED = true;
	public static double[] alphaVal = { 0.2, 0.3, 0.4, 0.5 };
	
	public static void main(String[] args) throws Exception {

		args = "Data/ UE-Dist-600-2000 PICO-Dist-600-6 MACRO-Dist-600-2000 2 0.1 6.0".split(" ");
		RUNS = Integer.parseInt(args[4]);
		//ALPHA = Double.parseDouble(args[4]);
		//BIAS = Double.parseDouble(args[5]);

		String outPath1 = "./outPut/";
		//String outPath2 = "RSRP/";
		//String outPath3 = "UECOUNT/";
		String outPath_mtl = "./matlabData/";
		String outPath_mtl1 = "./matlabData/Scenarios/";
		String outPath_mtl2 = "./matlabData/Code/";
		String outPath_mtl3 = "./matlabData/Results";

		new File(outPath1).mkdirs();
		//new File(outPath1 + outPath2).mkdirs();
		//new File(outPath1 + outPath3).mkdirs();
		new File(outPath_mtl).mkdirs();
		new File(outPath_mtl1).mkdirs();
		new File(outPath_mtl2).mkdirs();
		new File(outPath_mtl3).mkdirs();

		PrintWriter eeWriter = null;
		PrintWriter capacityWriter = null;
		PrintWriter fairnessWriter = null;
		PrintWriter capacityMWriter = null;
		PrintWriter capacityFWriter = null;
		PrintWriter capacityMVWriter = null;
		PrintWriter capacityFVWriter = null;
		PrintWriter capacityAllVictimWriter = null;
		PrintWriter capacityPerMVWriter = null;
		PrintWriter capacityPerFVWriter = null;
		PrintWriter capacityPerAllVictimWriter = null;
		PrintWriter capacityMeanMV = null;
		PrintWriter capacityMeanFV = null;
		PrintWriter capacityMeanMA = null;
		PrintWriter capacityMeanFA = null;
		PrintWriter capacityStdMV = null;
		PrintWriter capacityStdFV = null;
		PrintWriter capacityStdMA = null;
		PrintWriter capacityStdFA = null;
		PrintWriter mtlFileNameWriter = null;
		
		try {
			eeWriter = new PrintWriter(outPath1+"EE");
			capacityWriter = new PrintWriter(outPath1+"CAP_FINAL");
			capacityMWriter = new PrintWriter(outPath1 + "CAPM_FINAL");
			capacityFWriter = new PrintWriter(outPath1 + "CAPF_FINAL");
			capacityMVWriter = new PrintWriter(outPath1 + "CAPMV_FINAL");
			capacityFVWriter = new PrintWriter(outPath1 + "CAPFV_FINAL");
			capacityAllVictimWriter = new PrintWriter(outPath1 + "capAllVictim_FINAL");
			capacityPerMVWriter = new PrintWriter(outPath1 + "capMV_5Per_FINAL");
			capacityPerFVWriter = new PrintWriter(outPath1 + "capFV_5Per_FINAL");
			capacityPerAllVictimWriter = new PrintWriter(outPath1 + "capAllVictim_5Per_FINAL");
			fairnessWriter = new PrintWriter(outPath1 + "fairnessAll_FINAL");
			
			capacityMeanMV = new PrintWriter(outPath1 + "capacityMeanMV_FINAL");
			capacityMeanFV = new PrintWriter(outPath1 + "capacityMeanFV_FINAL");
			capacityMeanMA = new PrintWriter(outPath1 + "capacityMeanMA_FINAL");
			capacityMeanFA = new PrintWriter(outPath1 + "capacityMeanFA_FINAL");
			capacityStdMV = new PrintWriter(outPath1 + "capacityStdMV_FINAL");
			capacityStdFV = new PrintWriter(outPath1 + "capacityStdFV_FINAL");
			capacityStdMA = new PrintWriter(outPath1 + "capacityStdMA_FINAL");
			capacityStdFA = new PrintWriter(outPath1 + "capacityStdFA_FINAL");
			mtlFileNameWriter = new PrintWriter(outPath_mtl + "MATLAB_FILES_NAME.m");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		mtlFileNameWriter.write("addpath('E:\\Experiment\\Code\\Projects\\WCN\\matlabData\\Code');\n");
		
		eeWriter.append(ALPHA+"\t");
		capacityWriter.append(ALPHA+"\t");
		capacityMWriter.append(ALPHA+"\t");
		capacityFWriter.append(ALPHA+"\t");
		capacityMVWriter.append(ALPHA+"\t");
		capacityFVWriter.append(ALPHA+"\t");
		capacityAllVictimWriter.append(ALPHA+"\t");
		capacityPerMVWriter.append(ALPHA+"\t");
		capacityPerFVWriter.append(ALPHA+"\t");
		capacityPerAllVictimWriter.append(ALPHA+"\t");
		fairnessWriter.append(ALPHA+"\t");
		
		capacityMeanMV.append(ALPHA+"\t");
		capacityMeanFV.append(ALPHA+"\t");
		capacityMeanMA.append(ALPHA+"\t");
		capacityMeanFA.append(ALPHA+"\t");
		capacityStdMV.append(ALPHA+"\t");
		capacityStdFV.append(ALPHA+"\t");
		capacityStdMA.append(ALPHA+"\t");
		capacityStdFA.append(ALPHA+"\t");
		
		for(int runType = 1 ; runType <= 5; runType++)
		{
			if(runType == 2 || runType == 3 || runType == 4)
				continue;
			
			CDFHelper macroSinrCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"mSinr_"+ALPHA+"."+runType);
			CDFHelper macroVictimSinrCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"mVSinr_"+ALPHA+"."+runType);
			CDFHelper femtoSinrCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"fSinr_"+ALPHA+"."+runType);
			CDFHelper femtoVictimSinrCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"fVSinr_"+ALPHA+"_"+"."+runType);
			CDFHelper allSINRCDF = new CDFHelper(40, -40, Params.CDF_STEP, outPath1+"allSinr_"+ALPHA+"."+runType);
			CDFHelper allVictimSINRCDF = new CDFHelper(40, -40, Params.CDF_STEP, outPath1+"allVictimSinr_"+ALPHA+"."+runType);
			CDFHelper macroBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"mBitRate_"+ALPHA+"."+runType);
			CDFHelper femtoBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"fBitRate_"+ALPHA+"."+runType);
			CDFHelper allBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"allBitRate_"+ALPHA+"."+runType);
			CDFHelper macroVictimBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"mVBitRate_"+ALPHA+"."+runType);
			CDFHelper femtoVictimBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"fVBitRate_"+ALPHA+"."+runType);
			CDFHelper allVictimBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"allVBitRate_"+ALPHA+"."+runType);
			CDFHelper allVictimPercentileBitRateCDF = new CDFHelper(1000, 0, Params.CDF_STEP, outPath1+"allVPerBitRate_"+ALPHA+"."+runType);

			//CDFHelper macroSinrILCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"mSinrIL_"+ALPHA+"_"+BIAS+"_"+runType);
			//CDFHelper macroSinrIFCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"mSinrIF_"+ALPHA+"_"+BIAS+"_"+runType);
			//CDFHelper femtoSinrILCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"fSinrIL_"+ALPHA+"_"+BIAS+"_"+runType);
			//CDFHelper femtoSinrIFCDF = new CDFHelper(40,-40, Params.CDF_STEP, outPath1+"fSinrIF_"+ALPHA+"_"+BIAS+"_"+runType);
			
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

			for(int run = 1; run <= RUNS; run++)
			{
				System.out.println("\nScenario: " + run + "\n-------------");
				for(int macroIndex = 0; macroIndex <= 6; macroIndex++)
				{
					Simulator scenario = new Simulator();
					scenario.init(args[0] + args[1] + "-" + run, args[0] + args[2] + "-" + run, args[0] + args[3] + "-" + run, runType, macroIndex);
					scenario.runSim();

					if(runType == 5)
						generateMatlabData(outPath_mtl1, outPath_mtl2, run, scenario, mtlFileNameWriter, macroIndex);
					
					macroSinrCDF.addCDFList(scenario.getSinrListM());
					macroVictimSinrCDF.addCDFList(scenario.getMacroVictimSinr());
					femtoSinrCDF.addCDFList(scenario.getSinrListF());
					femtoVictimSinrCDF.addCDFList(scenario.getFemtoVictimSinr());
					macroBitRateCDF.addCDFList(scenario.getBitRateListM());
					femtoBitRateCDF.addCDFList(scenario.getBitRateListF());
					macroVictimBitRateCDF.addCDFList(scenario.getBitRateListMV());
					femtoVictimBitRateCDF.addCDFList(scenario.getBitRateListFV());
					allVictimBitRateCDF.addCDFList(scenario.getBitRateListAllVictim());
					allVictimPercentileBitRateCDF.addCDFList(scenario.getBitRateList5PerAllVictim());
					allBitRateCDF.addCDFList(scenario.getBitRateListAll());
					allSINRCDF.addCDFList(scenario.getSinrAll());
					allVictimSINRCDF.addCDFList(scenario.getSinrAllVictim());
					
					//macroSinrILCDF.addCDFList(scenario.getSinrListM_IL());
					//macroSinrIFCDF.addCDFList(scenario.getSinrListM_IF());
					//femtoSinrILCDF.addCDFList(scenario.getSinrListF_IL());
					//femtoSinrIFCDF.addCDFList(scenario.getSinrListF_IF());

					energyEfficiency += scenario.getEE();
					systemCapacity += scenario.getCAP();
					systemCapacityM += scenario.getCAPM();
					systemCapacityF += scenario.getCAPF();
					systemCapacityMV += scenario.getCAPMV();
					systemCapacityFV += scenario.getCAPFV();
					systemCapacityAllVictim += scenario.getCAPAllVictim();
					systemCapacityPerMV += scenario.getCapacity5PerMV();
					systemCapacityPerFV += scenario.getCapacity5PerFV();
					systemCapacityPerAllVictim += scenario.getCapacity5PerAllVictim();
					fairnessIndex += scenario.getFairnessIndexAll();
					
					meanCapacityMV += scenario.meanBitrateMV();
					meanCapacityFV += scenario.meanBitrateFV();
					meanCapacityMA += scenario.meanBitrateMA();
					meanCapacityFA += scenario.meanBitrateFA();
					stdCapacityMV += scenario.stdBitrateMV();
					stdCapacityFV += scenario.stdBitrateFV();
					stdCapacityMA += scenario.stdBitrateMA();
					stdCapacityFA += scenario.stdBitrateFA();
				}
			}

			
			macroSinrCDF.saveToFile();
			macroVictimSinrCDF.saveToFile();
			femtoSinrCDF.saveToFile();
			femtoVictimSinrCDF.saveToFile();
			macroBitRateCDF.saveToFile();
			femtoBitRateCDF.saveToFile();
			macroVictimBitRateCDF.saveToFile();
			femtoVictimBitRateCDF.saveToFile();
			allVictimBitRateCDF.saveToFile();
			allVictimPercentileBitRateCDF.saveToFile();
			allBitRateCDF.saveToFile();
			allSINRCDF.saveToFile();
			allVictimSINRCDF.saveToFile();
			
			//macroSinrILCDF.saveToFile();
			//macroSinrIFCDF.saveToFile();
			//femtoSinrILCDF.saveToFile();
			//femtoSinrIFCDF.saveToFile();

			eeWriter.append(runType+"\t"+energyEfficiency/RUNS+"\n");	//original format
			capacityWriter.append(systemCapacity/RUNS+"\n");
			capacityMWriter.append(systemCapacityM/RUNS+"\n");
			capacityFWriter.append(systemCapacityF/RUNS+"\n");
			capacityMVWriter.append(systemCapacityMV/RUNS+"\n");
			capacityFVWriter.append(systemCapacityFV/RUNS+"\n");
			capacityAllVictimWriter.append(systemCapacityAllVictim/RUNS+"\n");
			capacityPerMVWriter.append(systemCapacityPerMV/RUNS+"\n");
			capacityPerFVWriter.append(systemCapacityPerFV/RUNS+"\n");
			capacityPerAllVictimWriter.append(systemCapacityPerAllVictim/RUNS+"\n");
			fairnessWriter.append(fairnessIndex/RUNS+"\n");
			capacityMeanMV.append(meanCapacityMV/RUNS + "\n");
			capacityMeanFV.append(meanCapacityFV/RUNS + "\n");
			capacityMeanMA.append(meanCapacityMA/RUNS + "\n");
			capacityMeanFA.append(meanCapacityFA/RUNS + "\n");
			capacityStdMV.append(stdCapacityMV/RUNS + "\n");
			capacityStdFV.append(stdCapacityFV/RUNS + "\n");
			capacityStdMA.append(stdCapacityMA/RUNS + "\n");
			capacityStdFA.append(stdCapacityFA/RUNS + "\n");
		}

		mtlFileNameWriter.close();
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
		
		System.out.println("\nAll done...");
	}
	
	public static void generateMatlabData(String outPath_mtl1, String outPath_mtl2, int run, Simulator scenario, PrintWriter mtlFileNameWriter, int macroIndex)
	{
		try {
			//PrintWriter brWriter = new PrintWriter(outPath1 + outPath2
				//	+ "BITRATE" + "-" + runType + "-" + i);
			//PrintWriter ueCountWriter = new PrintWriter(outPath1
				//	+ outPath3 + "UECOUNT" + "-" + i);
			PrintWriter mtlScnWriter = new PrintWriter(outPath_mtl1
					+ "SCENARIO_" + run + "_MC_" + macroIndex + ".m");
			PrintWriter mtlCodeWriter = new PrintWriter(outPath_mtl2
					+ "CODE_" + run + "_MC_" + macroIndex + ".m");

			//scenario.writeDataRate(brWriter, false);
			//scenario.writeUeCount(ueCountWriter);
			scenario.writeMatlabScenario(mtlScnWriter);
			writeMatlabCode(mtlCodeWriter, run, macroIndex);
			mtlFileNameWriter.append("CODE_" + run + "_MC_" + macroIndex + ";\n");

			//brWriter.close();
			//ueCountWriter.close();
			mtlScnWriter.close();
			mtlCodeWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeMatlabCode(PrintWriter mtlCodeWriter, int run, int macroIndex)
	{
		mtlCodeWriter.write("clc;\n");
		mtlCodeWriter.write("addpath('E:\\Experiment\\Code\\Projects\\WCN\\matlabData\\Scenarios');\n");
		mtlCodeWriter.write("SCENARIO_" + run + "_MC_" + macroIndex + "\n");
		mtlCodeWriter.write("cvx_begin\n");
		mtlCodeWriter.write("variables a;\n");
		mtlCodeWriter.write("variables b;\n");
		mtlCodeWriter.write("c = sum(nvue_count) * log(1-a-b) + (sum(vue_count) - vue_count(1)) * log(a) + vue_count(1) * log (b) + sum(rate)\n");
		mtlCodeWriter.write("maximise c\nsubject to\n");
		mtlCodeWriter.write("a>=0\nb>=0\na<1\nb<1\na+b<=1\n");
		mtlCodeWriter.write("cvx_end\n");
		mtlCodeWriter.write("results = fopen('E:\\Experiment\\Code\\Projects\\WCN\\matlabData\\Results\\ABS_SCENARIO_" + run + "_MC_" + macroIndex + "', 'w');\n");
		mtlCodeWriter.write("fprintf(results, '%d %d\\n', a, b);\n");
		mtlCodeWriter.write("fclose(results);\n");
	}
	
}

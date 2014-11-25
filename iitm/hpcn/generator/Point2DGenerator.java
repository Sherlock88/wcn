
/*
 * Scenario constraints:
 * 1. One Macrocell at center (0,0) and six others uniformly surrounding it
 * 2. Two to five Picocells per Macrocell
 * 3. Each Picocell is located at a varying distance from the Macrocell it is attached to
 * 4. Distance between MBS & PBS is >= 75m
 * 5. Distance between PBS & PBS is >= 40m
 * 6. Distance between MBS & UE is >= 35m
 * 7. Distance between PBS & UE is >= 10m
 */

package iitm.hpcn.generator;

import hpcn.iitm.fap.resources.Params;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.io.File;

public class Point2DGenerator {
	public static int N_SCENARIOS, totalPicoCount, expectedResourceBlocksRequired = 0, ueCount;
	private int radiusMacro, radiusPico, picoCount, scenarioCount, resourceBlocksRequired;
	private double hotSpotProb = 2.0/3;			//Non-Uniform Distribution
	private static LinkedHashSet<Point2D> setMACRO, setFEMTO, setUE;
	private static Vector<Integer> picoPerMacro = new Vector<Integer>();
	private static Vector<Integer> ueDataDemands = new Vector<Integer>();
	private static Vector<Integer> ueToMC = new Vector<Integer>();
	private static Vector<Integer> pcToMC = new Vector<Integer>();
	Random random;
	
	// Station types
	public static final int STATIONMACRO = 0;
	public static final int STATIONPICO = 1;
	public static final int STATIONUE = 2;
	
	// Data rates
	public static final int VOICE = 0;
	public static final int DATA = 1;
	public static final int VIDEO = 2;
	public static final int VOICERATE = 16;
	public static final int DATARATE = 48;
	public static final int VIDEORATE = 128;
	public static final double VOICEPROB = 0.2;
	public static final double DATAPROB = 0.35;
	public static final double VIDEOPROB = 0.45;
	
	// Resource blocks
	public static final int RBCOUNT	= 250;
	public static final int SUBCHANNEL	= 20;	//KHz
	public static final int VOICERB = 1;		// Sub-channels
	public static final int DATARB = 2;			// Sub-channels
	public static final int VIDEORB = 7;		// Sub-channels
	
	
	public Point2DGenerator(int radiusM, int radiusP, int picoCount, double ueDensity) {
		this.radiusMacro = radiusM;
		this.radiusPico = radiusP;
		this.picoCount = picoCount;
		totalPicoCount = 0;
		random = new Random(System.currentTimeMillis());
		Point2DGenerator.ueCount = (int) Math.ceil(ueDensity *  Math.PI * (radiusM/1000.0) * (radiusM/1000.0));
		ueCount = Params.USERS_PER_MACRO;
	}
	
	public static void main(String[] args) {
		args = "Data/ UE-Dist-600-2000 PICO-Dist-600-6 MACRO-Dist-600-2000 2 400".split(" ");	//400 UE per km-sq
		N_SCENARIOS = Integer.parseInt(args[4]);
		int radiusM = Params.MACRO_RADIUS;
		int radiusP = Params.PICO_RADIUS;
		int n_pico = Params.MAX_PICO_COUNT;
		double density = Double.parseDouble(args[5]);
		femtoClusterGenerator(radiusM, radiusP, n_pico, density, args[0], args[1], args[2], args[3]);
	}
	
	public static void femtoClusterGenerator(int radiusM, int radiusP, int picoCount, double ueDensity, String path, String ueFileName, String femtoFileName, String macroFileName) {
		Point2DGenerator g = new Point2DGenerator(radiusM, radiusP, picoCount, ueDensity);
		for (int i = 1; i <= N_SCENARIOS; i++) {
			setMACRO = new LinkedHashSet<Point2D>();
			setFEMTO = new LinkedHashSet<Point2D>();
			setUE = new LinkedHashSet<Point2D>();
			
			// Re-initialize
			totalPicoCount = 0;
			picoPerMacro.clear();
			ueToMC.clear();
			ueDataDemands.clear();
			pcToMC.clear();
			
			g.generateClusterScenario();
			g.saveToFile(path, ueFileName + "-" + i, femtoFileName + "-" + i, macroFileName + "-" + i);
			
			System.out.println("PCToMC: " + i);
			Iterator<Integer> itrPCToMC = pcToMC.iterator();
			while(itrPCToMC.hasNext())
			{
				int macroIndex = ((Integer)itrPCToMC.next()).intValue();
				System.out.print(macroIndex + " ");
			}
			System.out.println();
			
		}
		
		System.out.println("\nFor " + ueCount + " UEs, expected RB requirement averaging over " + (7 * N_SCENARIOS) + " MCs in " 
				+ N_SCENARIOS + " scenarios is " + expectedResourceBlocksRequired / (7 * N_SCENARIOS));
	}
	
	public void generateClusterScenario() {
		double x, y;
		int i, r = 2 * radiusMacro;
		double theta = 0;
		++scenarioCount;
		System.out.println("---------------------------\nScenario: " + scenarioCount);

		// Generate a Macrocell at the center and six Macrocells surrounding it
		
		// Macrocell at the center
		setMACRO.add(new Point2D.Double(0, 0));
		picoFixedGenerator(0, 0, radiusMacro, picoCount, 0);
		ueGenerator(0, 0, 0);
		expectedResourceBlocksRequired += resourceBlocksRequired;
		
		// Surrounding Macrocells
		for(i = 1; i <= 6; i++)
		{
			theta = i * (2 * Math.PI / 6);
			x = r * Math.cos(theta);
			y = r * Math.sin(theta);
			setMACRO.add(new Point2D.Double(x, y));
			picoFixedGenerator(x, y, radiusMacro, picoCount, i);
			ueGenerator(x, y, i);
			expectedResourceBlocksRequired += resourceBlocksRequired;
		}
	}
	
	public void picoFixedGenerator(double x, double y, int radiusMacro, int picoCount, int macroIndex)
	{
		double xCo, yCo;
		double distance, angle;
		picoCount = 2 + random.nextInt(picoCount - 2);		// Randomizing Picocell#/Macrocell
		totalPicoCount += picoCount;
		picoPerMacro.addElement(new Integer(picoCount));
		
		System.out.println("MCID: " + macroIndex + ", picoPerMacro: " + picoCount);
		genPico:
		for(int i = 0; i < picoCount; i++)
		{
			// Generating Picocell at a distance >= MBSToPBS from the Macrocell it's attached to
			distance = Params.MBSToPBS + random.nextDouble() * (radiusMacro - Params.MBSToPBS);
			angle = (2 * Math.PI) * random.nextDouble();
			xCo = x + distance * Math.cos(angle);
			yCo = y + distance * Math.sin(angle);
			
			// Generating Picocell at a distance >= PBSToPBS from another Picocell
			Iterator<Point2D> itrPico = setFEMTO.iterator();
			while(itrPico.hasNext())
			{
				Point2D curPico = itrPico.next();
				if(Point2D.distance(xCo, yCo, curPico.getX(), curPico.getY()) < Params.PBSToPBS)
				{
					i--;
					continue genPico;
				}
			}
			setFEMTO.add(new Point2D.Double(xCo, yCo));
			pcToMC.add(macroIndex); System.out.println("MCID: " + macroIndex);
		}
	}
	
	public void ueGenerator(double xMacro, double yMacro, int macroIndex)
	{
		Integer intPico;
		int i, hotspotUECount = 0, uniformUECount = 0;
		double probDataRate, xPico, yPico;
		Point2D locMacro = new Point2D.Double(xMacro, yMacro), locPico;
		Point2D[] arrFEMTO = setFEMTO.toArray(new Point2D[setFEMTO.size()]); 
		resourceBlocksRequired = 0;
		
		for(i = 1; i <= Point2DGenerator.ueCount; i++)
		{
			if(random.nextDouble() < (this.hotSpotProb))
				hotspotUECount++;
			else
				uniformUECount++;
			
			probDataRate = random.nextDouble();
			if(probDataRate <= VOICEPROB)
			{
				ueDataDemands.add(VOICE);
				resourceBlocksRequired += VOICERB;
			}
			else
				if(probDataRate < (DATAPROB + VOICEPROB))
				{
					ueDataDemands.add(DATA);
					resourceBlocksRequired += DATARB;
				}
				else
				{
					ueDataDemands.add(VIDEO);
					resourceBlocksRequired += VIDEORB;
				}
			
			ueToMC.add(macroIndex);
		}
		
		getPoints(xMacro, yMacro, radiusMacro, uniformUECount, 0, 0, STATIONMACRO);
		intPico = (Integer) picoPerMacro.get(macroIndex);
		System.out.println("Macrocell " + macroIndex + ": Hotspot / Uniform : " + hotspotUECount + " / " + uniformUECount + ", Resource Blocks: " + resourceBlocksRequired + ", Pico/Macro: " + intPico);
		for(i = totalPicoCount - intPico.intValue(); i < totalPicoCount; i++)
		{
			xPico = arrFEMTO[i].getX();
			yPico = arrFEMTO[i].getY();
			locPico = new Point2D.Double(xPico,  yPico);
			double distMBSToPBS = locMacro.distance(locPico);
			distMBSToPBS -= radiusPico;
			double A = distMBSToPBS * 0.2;
			double hotspotMin = radiusPico - Params.B;
			double hotspotMax = radiusPico + A;
			double hotspotRadius = hotspotMin + (hotspotMax - hotspotMin) * random.nextDouble();
			//System.out.println("MC: " + locMacro + ", PC: " + locPico + ", HPRadius: " + hotspotRadius);
			getPoints(xPico, yPico, hotspotRadius, hotspotUECount / intPico, 0, 0, STATIONPICO);
		}
	}
	
	// Center is at (X,Y) random radius will belong [restrictedZoneInner,R - restrictedZoneOuter] number of points c
	private void getPoints(double X, double Y, double R, int c,
			double restrictedZoneInner, double restrictedZoneOuter, int stationType) {
		
		double d;
		
		for (int i = 0; i < c; i++) {
			
			if(stationType == STATIONMACRO)
				//d = Params.MBSToUE + restrictedZoneInner + (R - restrictedZoneInner - restrictedZoneOuter - Params.MBSToUE) * Math.sqrt(random.nextDouble());
				d = Params.MBSToUE + restrictedZoneInner + (R - restrictedZoneInner - restrictedZoneOuter - Params.MBSToUE) * random.nextDouble();
			else
				//d = Params.PBSToUE + restrictedZoneInner + (R - restrictedZoneInner - restrictedZoneOuter - Params.PBSToUE) * Math.sqrt(random.nextDouble());
				d = Params.PBSToUE + restrictedZoneInner + (R - restrictedZoneInner - restrictedZoneOuter - Params.PBSToUE) * random.nextDouble();

			double theta = 2 * Math.PI * random.nextDouble();
			double x = X + d * Math.cos(theta);
			double y = Y + d * Math.sin(theta);

			setUE.add(new Point2D.Double(x, y));
		}
	}

	public void saveToFile(String path, String ueFileName, String femtoFileName, String macroFileName) {
		try {
			new File(path).mkdirs();
			printPoints(setMACRO, new PrintWriter(path + macroFileName), STATIONMACRO);
			printPoints(setFEMTO, new PrintWriter(path + femtoFileName), STATIONPICO);
			printPoints(setUE, new PrintWriter(path + ueFileName), STATIONUE);
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void printPoints(LinkedHashSet<Point2D> points, PrintWriter out, int stationType) {
		if(stationType == STATIONMACRO)
		{
			Iterator<Point2D> itrMacro= points.iterator();
			Iterator<Integer> itrPicoPerMacro = picoPerMacro.iterator();
			
			while(itrMacro.hasNext() && itrPicoPerMacro.hasNext())
			{
				Point2D point = itrMacro.next();
				int picoPerMacro = ((Integer)itrPicoPerMacro.next()).intValue();
				out.printf("%6.4f %6.4f %d%n", point.getX(), point.getY(), picoPerMacro);
			}
		}
		else 
			if(stationType == STATIONPICO)
			{
				Iterator<Point2D> itrPico= points.iterator();
				Iterator<Integer> itrPCToMC = pcToMC.iterator();
				
				while(itrPico.hasNext() && itrPCToMC.hasNext())
				{
					Point2D point = itrPico.next();
					int macroIndex = ((Integer)itrPCToMC.next()).intValue();
					out.printf("%6.4f %6.4f %d%n", point.getX(), point.getY(), macroIndex);
				}
			}
			else
			{
				Iterator<Point2D> itrUE= points.iterator();
				Iterator<Integer> itrUEDemand = ueDataDemands.iterator();
				Iterator<Integer> itrUEToMC = ueToMC.iterator();
				
				while(itrUE.hasNext() && itrUEDemand.hasNext() && itrUEToMC.hasNext())
				{
					Point2D point = itrUE.next();
					int ueDataDemand = ((Integer)itrUEDemand.next()).intValue();
					int macroIndex = ((Integer)itrUEToMC.next()).intValue();
					out.printf("%6.4f %6.4f %d %d%n", point.getX(), point.getY(), ueDataDemand, macroIndex);
				}
			}
		out.close();
	}
}

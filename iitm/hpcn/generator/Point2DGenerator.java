
/*
 * Scenario constraints:
 * 1. One Macrocell at center (0,0) and six others uniformly surrounding it
 * 2. Two to five Picocells per Macrocell
 * 3. Each Picocell is located at a varying distance from the Macrocell it is attached to
 * 4. Distance between MBS & PBS is >= 75m
 * 5. Distance between PBS & PBS is >= 40m
 */

package iitm.hpcn.generator;

import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.Set;
import java.io.File;

public class Point2DGenerator {
	public static int N_SCENARIOS, totalPicoCount;
	private int radiusMacro, radiusPico, picoCount, ueCount, scenarioCount;
	private double hotSpotProb = 2.0/3;			//Non-Uniform Distribution
	private static Set<Point2D> setMACRO, setFEMTO, setUE;
	private static Vector<Integer> picoPerMacro = new Vector<Integer>();
	private static Vector<Integer> ueDataDemands = new Vector<Integer>();
	private final int MBSToPBS = 75, PBSToPBS = 40, MBSToUE = 35, PBSToUE = 10;
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
		
	public Point2DGenerator(int radiusM, int radiusP, int picoCount, int ueCount, double ueDensity) {
		this.radiusMacro = radiusM;
		this.radiusPico = radiusP;
		this.picoCount = picoCount;
		totalPicoCount = 0;
		random = new Random(System.currentTimeMillis());
		this.ueCount = (int) Math.ceil(ueDensity *  Math.PI * (radiusM/1000.0) * (radiusM/1000.0));
		System.out.println("UE count : " + this.ueCount);
	}
	
	public static void main(String[] args) {
		args = "Data/ UE-Dist-600-2000 PICO-Dist-600-6 MACRO-Dist-600-2000 2 400".split(" ");	//400 UE per km-sq
		N_SCENARIOS = Integer.parseInt(args[4]);
		int radiusM = 600;
		int radiusP = 200;
		int n_pico = 5;
		int n_ue = 2000;
		double density = Double.parseDouble(args[5]);
		femtoClusterGenerator(radiusM, radiusP, n_pico, n_ue, density, args[0], args[1], args[2], args[3]);
	}
	
	public static void femtoClusterGenerator(int radiusM, int radiusP, int picoCount, int ueCount, double ueDensity, String path, String ueFileName, String femtoFileName, String macroFileName) {
		Point2DGenerator g = new Point2DGenerator(radiusM, radiusP, picoCount, ueCount, ueDensity);
		for (int i = 1; i <= N_SCENARIOS; i++) {
			setMACRO = new LinkedHashSet<Point2D>();
			setFEMTO = new LinkedHashSet<Point2D>();
			setUE = new LinkedHashSet<Point2D>();
			totalPicoCount = 0;
			picoPerMacro.clear();
			g.generateClusterScenario();
			g.saveToFile(path, ueFileName + "-" + i, femtoFileName + "-" + i, macroFileName + "-" + i);
		}
	}
	
	public void generateClusterScenario() {
		double x, y;
		int i, r = 2 * radiusMacro;
		double theta = 0;
		++scenarioCount;
		System.out.println("---------------------------\nScenario instance : " + scenarioCount);

		// Generate a Macrocell at the center and six Macrocells surrounding it
		
		// Macrocell at the center
		setMACRO.add(new Point2D.Double(0, 0));
		picoFixedGenerator(0, 0, radiusMacro, picoCount);
		ueGenerator(0, 0, 0);
		
		// Surrounding Macrocells
		for(i = 0; i < 6; i++)
		{
			theta = i * (2 * Math.PI / 6);
			x = r * Math.cos(theta);
			y = r * Math.sin(theta);
			setMACRO.add(new Point2D.Double(x, y));
			picoFixedGenerator(x, y, radiusMacro, picoCount);
			ueGenerator(x, y, i);
		}
	}
	
	public void picoFixedGenerator(double x, double y, int radiusMacro, int picoCount)
	{
		double xCo, yCo;
		double distance, angle;
		picoCount = 2 + random.nextInt(picoCount - 2);		// Randomizing Picocell#/Macrocell
		totalPicoCount += picoCount;
		picoPerMacro.addElement(new Integer(picoCount));
		
		genPico:
		for(int i = 0; i < picoCount; i++)
		{
			// Generating Picocell at a distance >= MBSToPBS from the Macrocell it's attached to
			distance = MBSToPBS + random.nextDouble() * (radiusMacro - MBSToPBS);
			angle = (2 * Math.PI) * random.nextDouble();
			xCo = x + distance * Math.cos(angle);
			yCo = y + distance * Math.sin(angle);
			
			// Generating Picocell at a distance >= PBSToPBS from another Picocell
			Iterator<Point2D> itrPico = setFEMTO.iterator();
			while(itrPico.hasNext())
			{
				Point2D curPico = itrPico.next();
				if(Point2D.distance(xCo, yCo, curPico.getX(), curPico.getY()) < PBSToPBS)
				{
					i--;
					continue genPico;
				}
			}
			setFEMTO.add(new Point2D.Double(xCo, yCo));
		}
	}
	
	public void ueGenerator(double xMacro, double yMacro, int macroIndex)
	{
		Integer intPico;
		int i, hotspotUECount = 0, uniformUECount = 0;
		double probDataRate;
		Point2D[] arrFEMTO = setFEMTO.toArray(new Point2D[setFEMTO.size()]); 
		
		for(i = 1; i <= this.ueCount; i++)
		{
			if(random.nextDouble() < (this.hotSpotProb))
				hotspotUECount++;
			else
				uniformUECount++;
			
			probDataRate = random.nextDouble();
			if(0 <= probDataRate && probDataRate <= VOICEPROB)
				ueDataDemands.add(VOICE);
			else
				if(VOICEPROB < probDataRate && probDataRate < DATAPROB)
					ueDataDemands.add(DATA);
				else
					ueDataDemands.add(VIDEO);
		}
		
		System.out.println("Hotspot / Uniform : " + hotspotUECount + " / " + uniformUECount);
		getPoints(xMacro, yMacro, radiusMacro, uniformUECount, 0, 0);
		
		intPico = (Integer) picoPerMacro.get(macroIndex);
		System.out.println("Pico/Macro: " + intPico);
		for(i = totalPicoCount - intPico.intValue(); i < totalPicoCount; i++)
			getPoints(arrFEMTO[i].getX(), arrFEMTO[i].getY(), radiusPico, hotspotUECount / intPico, 0, 0);
	}
	
	// Center is at (X,Y) random radius will belong [restrictedZoneInner,R - restrictedZoneOuter] number of points c
	private void getPoints(double X, double Y, double R, int c,
			double restrictedZoneInner, double restrictedZoneOuter) {
		for (int i = 0; i < c; i++) {
			double d = restrictedZoneInner + (R - restrictedZoneInner - restrictedZoneOuter)
					* Math.sqrt(random.nextDouble());

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
	
	private void printPoints(Set<Point2D> points, PrintWriter out, int stationType) {
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
				for (Point2D point : points) {
					out.printf("%6.4f %6.4f%n", point.getX(), point.getY());
				}
			}
			else
			{
				Iterator<Point2D> itrMacro= points.iterator();
				Iterator<Integer> itrUEDemand = ueDataDemands.iterator();
				
				while(itrMacro.hasNext() && itrUEDemand.hasNext())
				{
					Point2D point = itrMacro.next();
					int ueDataDemand = ((Integer)itrUEDemand.next()).intValue();
					out.printf("%6.4f %6.4f %d%n", point.getX(), point.getY(), ueDataDemand);
				}
			}
		out.close();
	}
}

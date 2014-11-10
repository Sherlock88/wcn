package iitm.hpcn.generator;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class Point2DGenerator {
	public static int N_SCENARIOS;
	
	private int radiusMacro, radiusPico, femtoCount, ueCount;
	private double hotSpotProb = 2.0/3;			//Non-Uniform Distribution
	private static Set<Point2D> setMACRO;
	private static Set<Point2D> setFEMTO;
	private static Set<Point2D> setUE;
	private int scenarioCount = 0;
	
	public Point2DGenerator(int radiusM, int radiusP, int femtoCount, int ueCount, double ueDensity) {
		this.radiusMacro = radiusM;
		this.radiusPico = radiusP;
		this.femtoCount = femtoCount;
		this.ueCount = (int) Math.ceil(ueDensity *  Math.PI * (radiusM/1000.0) * (radiusM/1000.0));
		System.out.println("UE count : " + this.ueCount);
	}
	
	public static void main(String[] args) {
		args = "Data/ UE-Dist-600-2000 PICO-Dist-600-6 MACRO-Dist-600-2000 2 400".split(" ");	//400 UE per km-sq
		N_SCENARIOS = Integer.parseInt(args[4]);
		int radiusM = 600;
		int radiusP = 200;
		int n_pico = 6;
		int n_ue = 2000;
		double density = Double.parseDouble(args[5]);
		femtoClusterGenerator(radiusM, radiusP, n_pico, n_ue, density, args[0], args[1], args[2], args[3]);
	}
	
	public static void femtoClusterGenerator(int radiusM, int radiusP, int femtoCount, int ueCount, double ueDensity, String path, String ueFileName, String femtoFileName, String macroFileName) {
		Point2DGenerator g = new Point2DGenerator(radiusM, radiusP, femtoCount, ueCount, ueDensity);
		for (int i = 1; i <= N_SCENARIOS; i++) {
			setMACRO = new LinkedHashSet<Point2D>();
			setFEMTO = new LinkedHashSet<Point2D>();
			setUE = new LinkedHashSet<Point2D>();
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

		// Generate a MACROCell at the center and six MACROCells surrounding it
		
		// MACROCell at the center
		setMACRO.add(new Point2D.Double(0, 0));
		picoFixedGenerator(0, 0, radiusMacro, femtoCount);
		ueGenerator(0, 0, 0);
		
		// Surrounding MACROCells
		for(i = 0; i < 6; i++)
		{
			theta = i * (2 * Math.PI / 6);
			x = r * Math.cos(theta);
			y = r * Math.sin(theta);
			setMACRO.add(new Point2D.Double(x, y));
			picoFixedGenerator(x, y, radiusMacro, femtoCount);
			ueGenerator(x, y, i + 1);
		}
	}
	
	public void picoFixedGenerator(double x, double y, int radius, int picoCount)
	{
		double xCo, yCo;
		double distance = radius - (radius / 4);
		double angle = (2 * Math.PI) / picoCount;
		for(int i = 0; i < picoCount; i++)
		{
			xCo = x + distance * Math.cos(i * angle);
			yCo = y + distance * Math.sin(i * angle);
			setFEMTO.add(new Point2D.Double(xCo, yCo));
		}
	}
	
	public void saveToFile(String path, String ueFileName, String femtoFileName, String macroFileName) {
		try {
			new File(path).mkdirs();
			printPoints(setMACRO, new PrintWriter(path + macroFileName));
			printPoints(setFEMTO, new PrintWriter(path + femtoFileName));
			printPoints(setUE, new PrintWriter(path + ueFileName));
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void ueGenerator(double xMacro, double yMacro, int macroIndex)
	{
		Random prob = new Random((long)xMacro + (long)yMacro);
		int hotspotUECount = 0, uniformUECount = 0, i;
		Point2D[] arrFEMTO = setFEMTO.toArray(new Point2D[setFEMTO.size()]); 
		
		for(i = 1; i <= this.ueCount; i++)
		{
			if(prob.nextDouble() < (this.hotSpotProb))
				hotspotUECount++;
			else
				uniformUECount++;
		}
		
		System.out.println("Hotspot / Uniform : " + hotspotUECount + " / " + uniformUECount);
		getPoints(xMacro, yMacro, radiusMacro, uniformUECount, 0, 0);
		
		macroIndex *= 6;
		for(i = macroIndex; i < macroIndex + 6; i++)
			getPoints(arrFEMTO[i].getX(), arrFEMTO[i].getY(), radiusPico, hotspotUECount / femtoCount, 0, 0);
	}
	
	/*
	 * Centre is at (X,Y) random radius will belong [restrictedZoneInner,R -
	 * restrictedZoneOuter] number of points c
	 */
	private void getPoints(double X, double Y, double R, int c,	//raj added restrictedZoneOuter
			double restrictedZoneInner, double restrictedZoneOuter) {
		Random randDist = new Random();
		Random randAngle = new Random();
		
		//setUE = new LinkedHashSet<Point2D>();
		for (int i = 0; i < c; i++) {
			double d = restrictedZoneInner + (R - restrictedZoneInner - restrictedZoneOuter)
					* Math.sqrt(randDist.nextDouble());

			double theta = 2 * Math.PI * randAngle.nextDouble();	// [Correct]
			double x = X + d * Math.cos(theta);		//angle theta should be in radians
			double y = Y + d * Math.sin(theta);

			setUE.add(new Point2D.Double(x, y));
		}
	}
	
	private void printPoints(Set<Point2D> points, PrintWriter out) {
		for (Point2D point : points) {
			out.printf("%6.4f %6.4f%n", point.getX(), point.getY());
		}
		out.close();
	}
}

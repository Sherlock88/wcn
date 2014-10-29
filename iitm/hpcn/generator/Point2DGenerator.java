package iitm.hpcn.generator;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Point2DGenerator {
	public static int N_SCENARIOS;
	
	private int radiusMacro, radiusPico, femtoCount, ueCount;
	private double hotSpotProb = 2.0/3;	//Non-Uniform Distribution
	private Set<Point2D> setMACRO;
	private Set<Point2D> setFEMTO;
	private Set<Point2D> setUE;
	private int scenarioCount = 0;
	
	public Point2DGenerator(int radiusM, int radiusP, int femtoCount, int ueCount, double ueDensity) {
		this.radiusMacro = radiusM;
		this.radiusPico = radiusP;
		this.femtoCount = femtoCount;
		this.ueCount = (int) Math.ceil(ueDensity *  Math.PI * (radiusM/1000.0) * (radiusM/1000.0));
		System.out.println("UE count : " + this.ueCount);
	}
	
	public static void main(String[] args) {
		args = "Data/ UE-Dist-600-2000 PICO-Dist-600-6 2 400".split(" ");	//400 UE per km-sq
		N_SCENARIOS = Integer.parseInt(args[3]);
		int radiusM = 600;
		int radiusP = 200;
		int n_pico = 6;
		int n_ue = 2000;
		double density = Double.parseDouble(args[4]);
		femtoClusterGenerator(radiusM, radiusP, n_pico, n_ue, density, args[0], args[1], args[2]);
	}
	
	public static void femtoClusterGenerator(int radiusM, int radiusP, int femtoCount, int ueCount, double ueDensity, String path, String ueFileName, String femtoFileName) {
		Point2DGenerator g = new Point2DGenerator(radiusM, radiusP, femtoCount, ueCount, ueDensity);
		for (int i = 1; i <= N_SCENARIOS; i++) {
			g.generateClusterScenario();
			g.saveToFile(path, ueFileName + "-" + i, femtoFileName + "-" + i);
		}
	}
	
	public void generateClusterScenario() {
		double x, y;
		int i, r = 2 * radiusMacro;
		double theta = Math.random() * 2 * Math.PI;
		++scenarioCount;
		System.out.println("Scenario instance : " + scenarioCount);

		// Generate a MACROCell at the center and six MACROCells surrounding it
		setMACRO = new HashSet<Point2D>();
		setMACRO.add(new Point2D.Double(0, 0));
		for(i = 0; i < 6; i++)
		{
			theta += i * (Math.PI / 6);
			x = r * Math.cos(theta);
			y = r * Math.sin(theta);
			setMACRO.add(new Point2D.Double(x, y));
			picoFixedGenerator(x, y, radiusMacro, femtoCount);
			ueGenerator(x, y);
		}
	}
	
	public void picoFixedGenerator(double x, double y, int radius, int picoCount)
	{
		double xCo, yCo;
		double distance = radius - (radius / 4);
		double angle = (2 * Math.PI) / picoCount;
		setFEMTO = new HashSet<Point2D>();
		for(int i = 0; i < picoCount; i++)
		{
			xCo = distance * Math.cos(i * angle);
			yCo = distance * Math.sin(i * angle);
			setFEMTO.add(new Point2D.Double(xCo, yCo));
		}
	}
	
	public void saveToFile(String path, String ueFileName, String femtoFileName) {
		try {
			new File(path).mkdirs();
			printPoints(setFEMTO, new PrintWriter(path + femtoFileName));
			printPoints(setUE, new PrintWriter(path + ueFileName));
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void ueGenerator(double xMacro, double yMacro)
	{
		Random prob = new Random();
		int hotspotUECount = 0, uniformUECount = 0;
		
		for(int i = 1; i <= this.ueCount; i++)
		{
			if(prob.nextDouble() < (this.hotSpotProb))
				hotspotUECount++;
			else
				uniformUECount++;
		}
		
		System.out.println("Hotspot / Uniform : " + hotspotUECount + " / " + uniformUECount);
		getPoints(xMacro, yMacro, radiusMacro, uniformUECount, 0, 0);
		Set<Point2D> points = new HashSet<Point2D>();
		
		for(Point2D pico : setFEMTO)
			getPoints(xMacro, yMacro, radiusMacro, uniformUECount, 0, 0);
	}
	
	/*
	 * Centre is at (X,Y) random radius will belong [restrictedZoneInner,R -
	 * restrictedZoneOuter] number of points c
	 */
	private void getPoints(double X, double Y, double R, int c,	//raj added restrictedZoneOuter
			double restrictedZoneInner, double restrictedZoneOuter) {
		Random randDist = new Random();
		Random randAngle = new Random();
		
		setUE = new HashSet<Point2D>();
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

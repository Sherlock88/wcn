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
	//private double ueDensity;
	private double hotSpotProb = 2.0/3;	//Non-Uniform Distribution
	private Point2D MACRO;
	private Set<Point2D> setFEMTO;
	private Set<Point2D> setUE;
	private int scenarioCount = 0;
	
	public Point2DGenerator(int radiusM, int radiusP, int femtoCount, int ueCount, double ueDensity) {
		this.radiusMacro = radiusM;
		this.radiusPico = radiusP;
		this.femtoCount = femtoCount;
		//this.ueDensity = ueDensity;
		this.ueCount = (int) Math.ceil(ueDensity *  Math.PI * (radiusM/1000.0) * (radiusM/1000.0));
		System.out.println("UE count : " + this.ueCount);
	}
	
	public static void main(String[] args) {
		
		args = "Data/ UE-Dist-600-2000 PICO-Dist-600-6 1000 400".split(" ");	//400 UE per km-sq
		N_SCENARIOS = Integer.parseInt(args[3]);
		int radiusM = 600;
		int radiusP = 200;
		int n_pico = 6;
		int n_ue = 2000;
		double density = Double.parseDouble(args[4]);
		femtoClusterGenerator(radiusM, radiusP, n_pico, n_ue, density, args[0], args[1], args[2]);
	}

	public Set<Point2D> picoFixedGenerator(int x, int y, int radius, int picoCount)
	{
		double xCo, yCo;
		double distance = radius - (radius / 4);
		double angle = (2 * Math.PI) / picoCount;
		Set<Point2D> points = new HashSet<Point2D>();
		for(int i = 0; i < picoCount; i++)
		{
			xCo = distance * Math.cos(i * angle);
			yCo = distance * Math.sin(i * angle);
			points.add(new Point2D.Double(xCo, yCo));
		}
		return points;
	}
	
	public Set<Point2D> picoFixedGeneratorForGraph(int a, int b, int radius, int picoCount)
	{
		//double distance = radius - (radius / 4);
		//double angle = (2 * Math.PI) / picoCount;
		Set<Point2D> points = new HashSet<Point2D>();
		int[] xCo = {450, 50, -400, -200, 100};
		int[] yCo = {0, 300, 150, -150, -350};
		
		for(int i = 0; i < 5; i++)
		{
			double x = xCo[i];
			double y = yCo[i];
			points.add(new Point2D.Double(x, y));
		}
		return points;
	}
	
	public Set<Point2D> ueUniformGenerator(double x, double y, int radius, int ueCount)
	{
		Set<Point2D> points = new HashSet<Point2D>();
		points =  getPoints(x, y, radius, ueCount , 0, 0);
		return points;
	}
	
//	public Set<Point2D> ueHotspotGenerator(double x, double y, double radius, int ueCount)
//	{
//		Set<Point2D> points = new HashSet<Point2D>();
//		points = getPoints(x, y, radius, ueCount , 0, 0);
//		return points;
//	}
	
	public static void femtoClusterGenerator(int radiusM, int radiusP, int femtoCount, int ueCount, double ueDensity, String path, String ueFileName, String femtoFileName) {
		Point2DGenerator g = new Point2DGenerator(radiusM, radiusP, femtoCount, ueCount, ueDensity);
		for (int i = 1; i <= N_SCENARIOS; i++) {
			g.generateClusterScenario(0, 0);
			g.saveToFile(path, ueFileName + "-" + i, femtoFileName + "-" + i);
		}
	}
	
	public void generateClusterScenario(int x, int y) {
		++scenarioCount;
		System.out.println("Scenario instance : " + scenarioCount);
		MACRO = new Point2D.Double(x, y);
		setFEMTO = picoFixedGenerator(x, y, radiusMacro, femtoCount);
		//setFEMTO = picoFixedGeneratorForGraph(x, y, radiusMacro, femtoCount); //TODO raj
		
		ueGenerator();
	}
	
	public void ueGenerator()
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
		//RAJ: For now, uniformly generated UE's may be generated close to PICOs. This needs to be changed.
		System.out.println("Hotspot / Uniform : " + hotspotUECount + " / " + uniformUECount);
		setUE = ueUniformGenerator(MACRO.getX(), MACRO.getY(), radiusMacro, uniformUECount);
		Set<Point2D> points = new HashSet<Point2D>();
		for(Point2D pico : setFEMTO)
		{
			points = ueUniformGenerator(pico.getX(), pico.getY(), radiusPico, hotspotUECount / femtoCount);
			for(Point2D point : points)
				setUE.add(point);
		}
	}
	
	/*
	 * Centre is at (X,Y) random radius will belong [restrictedZoneInner,R -
	 * restrictedZoneOuter] number of points c
	 */
	private Set<Point2D> getPoints(double X, double Y, double R, int c,	//raj added restrictedZoneOuter
			double restrictedZoneInner, double restrictedZoneOuter) {
		Random randDist = new Random();
		Random randAngle = new Random();
		Set<Point2D> points = new HashSet<Point2D>();

		for (int i = 0; i < c; i++) {
			double d = restrictedZoneInner + (R - restrictedZoneInner - restrictedZoneOuter)
					* Math.sqrt(randDist.nextDouble());

			double theta = 2 * Math.PI * randAngle.nextDouble();	// [Correct]
			double x = X + d * Math.cos(theta);		//angle theta should be in radians
			double y = Y + d * Math.sin(theta);

			points.add(new Point2D.Double(x, y));
		}
		return points;
	}
	
	private void printPoints(Set<Point2D> points, PrintWriter out) {
		for (Point2D point : points) {
			out.printf("%6.4f %6.4f%n", point.getX(), point.getY());
		}
		out.close();
	}
	
	//rajkarn : Function to check the distances of nodes from origin
	private void printDistance(Set<Point2D> points, PrintWriter out)
	{
		for(Point2D point : points)
			out.printf("%f\n", point.distance(0,0));
		out.close();
	}
	
	public void saveToFile(String path, String ueFileName, String femtoFileName) {
		try {
			new File(path).mkdirs();
			printPoints(setFEMTO, new PrintWriter(path + femtoFileName));
			printPoints(setUE, new PrintWriter(path + ueFileName));
			
			//printDistance(setFEMTO, new PrintWriter(path + "DistanceFEMTO" + femtoFileName));	//raj
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

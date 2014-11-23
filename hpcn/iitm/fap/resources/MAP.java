package hpcn.iitm.fap.resources;

import java.awt.geom.Point2D;

public class MAP {
	
	private int id;
	private int picoCount;
	private Point2D location;
	
	public MAP(int id, int picoCount, Point2D location) {
		this.id = id;
		this.picoCount = picoCount;
		this.location = location;
	}
	
	public int getId() {
		return id;
	}
	
	public int picoCount() {
		return picoCount;
	}
	
	public Point2D getLocation() {
		return location;
	}
}

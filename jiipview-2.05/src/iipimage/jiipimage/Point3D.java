package iipimage.jiipimage;
/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
/*
 * CanvasImage.java
 * 
 */
/**
 * @author Denis Pitzalis 
 */
public class Point3D implements Cloneable {

    /**
     * x coordinate of the point.
     */
    public double x;
    
    /**
     * y coordinate of the point.
     */
    public double y;
    
    /**
     * z coordinate of the point.
     */
    public double z;
    
    /**
     * Construct a new point with the given coordinates.
     * 
     * @param	x x coordinate of the point.
     * @param	y y coordinate of the point.
     * @param	z z coordinate of the point.
     */
    public Point3D (double x, double y, double z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }
    
    /**
     * Construct a new point with coordinates all set to the origin.
     */
    public Point3D () {
	this( 0.0, 0.0, 0.0 );
    }
    
    /**
     * Set the coordinates for a point.
     */
    public void set (double x, double y, double z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }
    
    /**
     * Create a printable representation of the point.
     */
    public String toString() {
	return new String("( " + x + ", " + y + ", " + z + " )");
    }
}


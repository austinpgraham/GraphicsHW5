/**
 * Author: Austin Graham
 */
package edu.ou.cs.cg.homework;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.media.opengl.*;

/**
 * Represent a polygon as a series of vector sides 
 * and a list of points with a given center and radius
 *
 * Each polygon is drawn as a circle with a certain number 
 * of points for convexity
 */
class Polygon
{
    // Sides of the polygon
    private ArrayList<Vector> sides;

    // List of points connecting the sides
    private Point[] points;

    // Radius of the polygon
    private float radius;

    // Angle at which to start driving
    private float startAngle;

    // Color to fill if necessary
    private float[] fillColor = null;

    // The center of the polygon
    public Point center;

    /**
     * Construct polygon instance
     *
     * @param numPoints: Number of points in polygon
     * @param center: Center of the polygon
     * @param radius: Radius of the polygon
     * @param startAngle: Angle at which to start drawing
     */
    public Polygon(int numPoints, Point center, float radius, float startAngle)
    {
        this.center = center;
        this.radius = radius;
        this.startAngle = startAngle;

        // Generate the points in the polygon and
        // Update the side vectors
        this.points = this.generatePoints(numPoints, this.center, radius, startAngle);
        this.sides = this.updateVectors();

        // Set velocity of all points to that 
        // of the center
        for(Point p: this.points)
        {
            p.setVelocity(this.center.getVelocity());
        }
    }

    /**
     * Construct polygon instance
     *
     * @param numPoints: Number of points in polygon
     * @param center: Center of the polygon
     * @param radius: Radius of the polygon
     * @param startAngle: Angle at which to start drawing
     * @param fillColor: Color to fill when drawing
     */
    public Polygon(int numPoints, Point center, float radius, float startAngle, float[] fillColor)
    {
        this.center = center;
        this.radius = radius;
        this.startAngle = startAngle;

        // Generate the points in the polygon and
        // Update the side vectors
        this.points = this.generatePoints(numPoints, this.center, radius, startAngle);
        this.sides = this.updateVectors();

        // Set velocity of all points to that 
        // of the center
        for(Point p: this.points)
        {
            p.setVelocity(this.center.getVelocity());
        }

        // Set fill color
        this.fillColor = fillColor;
    }

    /**
     * Construct a polygon as a single point
     *
     * @param center: Center of the polygon
     * @param startRadius: Radius at which to start drawing
     */
    public Polygon(Point center, float startRadius)
    {
        this.center = center;
        this.radius = startRadius;

        // We only want the one point: the center
        this.points = new Point[]{ this.center };
        this.sides = this.updateVectors();
    }

    /**
     * Update side vectors in the polygon
     *
     * @return A list of vectors representing sides
     * of the polygon going counterclockwise
     */
    private ArrayList<Vector> updateVectors()
    {
        ArrayList<Vector> _s = new ArrayList<Vector>();
        for(int i = 0; i < points.length - 1; i++)
        {
            Vector v = new Vector(points[i], points[i+1]);
            _s.add(v);
        }
        _s.add(new Vector(points[points.length - 1], points[0]));
        return _s;
    }

    /**
     * Draw the polygon
     */
    public void draw(GL2 gl, float[] color)
    {
        // If there is only one side, we have
        // a point, so draw the center
        if(this.sides.size() <= 1)
        {
            this.center.draw(gl);
        }
        else 
        {
            if(this.fillColor == null)
            {
                // Otherwise, draw all sides
                for(Vector v: this.sides)
                {
                    Utils.drawLine(gl, v.getStartPoint(), v.getEndPoint(), color);
                }
            }
            else 
            {
                // Get skip amount
                float inc = 360 / (float)this.sides.size();

                // Draw filled polygon
                Utils.drawCircle(gl, this.center, this.radius, 0.0, 360.0, inc, color, this.fillColor, true, this.points);
            }
            
        }
    }

    /**
     * Draw the polygon
     */
    public void draw(GL2 gl)
    {
        // Set the color to white
        float[] color = {1.0f, 1.0f, 1.0f};

        // If there is only one side, we have
        // a point, so draw the center
        if(this.sides.size() <= 1)
        {
            this.center.draw(gl);
        }
        else 
        {
            if(this.fillColor == null)
            {
                // Otherwise, draw all sides
                for(Vector v: this.sides)
                {
                    Utils.drawLine(gl, v.getStartPoint(), v.getEndPoint(), color);
                }
            }
            else 
            {
                // Get skip amount
                float inc = 360 / (float)this.sides.size();
                
                // Draw filled polygon
                Utils.drawCircle(gl, this.center, this.radius, 0.0, 360.0, inc, color, this.fillColor, true, this.points);
            }
            
        }
    }

    /**
     * Set the velocity of the polygon
     *
     * @param c: New velocity vector
     */
    public void setVelocity(Vector c)
    {
        // Set the velocity of all points
        this.center.setVelocity(c);
        for(Point p: this.points)
        {
            p.setVelocity(c);
        }
    }

    /**
     * Set the center of the polygon
     *
     * @param center: New polygon center
     */
    public void setCenter(Point center)
    {
        this.center = center;
    }

    public void move(float dx, float dy)
    {
        this.center.translate(dx, dy);
        for(Point p: this.points)
        {
            p.translate(dx, dy);
        }
    }

    public void rotate(double theta)
    {
        for(Point p: this.points)
        {
            p.rotate(theta);
        }
        this.updateVectors();
    }

    public void scale(float amount)
    {
        this.radius *= amount;
        this.points = this.generatePoints(this.points.length, this.center, this.radius, this.startAngle);
    }

    /**
     * Increase the area of the polygon 
     * by some amount
     *
     * @param amount: Amount to increase the area
     */
    public void increaseArea(float amount)
    {
        // Reset the radius
        this.radius = this.radius * amount * amount;

        // Regenerate the points
        this.points = this.generatePoints(this.points.length, this.center, this.radius, this.startAngle);

        // Reset all velocities
        for(Point p: this.points)
        {
            p.setVelocity(this.center.getVelocity());
        }
    }

    /**
     * Copy the state of another polygon into this one
     *
     * @param p: Polygon whose state to copy
     */
    public void copyState(Polygon p)
    {
        // Copy center, radius, and regenerate points
        this.center = p.center;
        this.radius = p.getRadius();
        this.points = this.generatePoints(this.points.length, this.center, this.radius, this.startAngle);
        this.setVelocity(p.center.getVelocity());
    }

    /**
     * Get the radius of the polygon
     *
     * @return Radius of the polygon
     */
    public float getRadius()
    {
        return this.radius;
    }

    public boolean contains(Point m)
    {
        float dist = new Vector(this.center, m).getMagnitude();
        return dist <= this.radius;
    }

    /**
     * Update the internal state of the polygon
     */
    public void update()
    {
        // Update all points with their 
        // appropriate velocity vectors
        this.center.update();
        if(this.points.length > 1)
        {
            for(Point p: this.points)
            {
                p.update();
            }
        }
        // Update the sides
        this.sides = this.updateVectors();
    }

    /**
     * Get the start angle of the polygon
     *
     * @return The start drawing angle
     */
    public float getStartAngle()
    {
        return this.startAngle;
    }

    /**
     * Calculate any collisions between
     * this polygon and another vector
     *
     * @param v: Vector colliding
     *
     * @return The vector where the collision
     * was detected or null if none found
     */
    public Vector collision(Vector v)
    {
        // For all interal sides, check
        // for intersection
        for(Vector in_v: this.sides)
        {
            if(Line2D.linesIntersect(in_v.getStartPoint().getX(), 
                                     in_v.getStartPoint().getY(), 
                                     in_v.getEndPoint().getX(), 
                                     in_v.getEndPoint().getY(), 
                                     v.getStartPoint().getX(), 
                                     v.getStartPoint().getY(), 
                                     v.getEndPoint().getX(), 
                                     v.getEndPoint().getY()))
                                     {
                                         return in_v;
                                     }
        }
        // If none found, return null
        return null;
    }

    /**
     * Get the list of sides for this polygon
     *
     * @return The internal list of sides
     */
    public ArrayList<Vector> getSides()
    {
        return this.sides;
    }

    /**
     * Calculate collisions between this polygon 
     * and another.
     *
     * @param p: Polygon with which to calculate
     *
     * @return Vector where collision is found or null
     */
    public Vector collision(Polygon p)
    {
        // If only one side, calculate with 
        // single cvector
        if(p.getSides().size() <= 1)
        {
            return this.collision(p.center.getPointVector());
        }

        // Iterate through all sides internally
        // and through the other polygon to calculate 
        // intersections
        for(Vector in_v: this.sides)
        {
            for(Vector v: p.getSides())
            {
                if(Line2D.linesIntersect(in_v.getStartPoint().getX(), 
                                        in_v.getStartPoint().getY(), 
                                        in_v.getEndPoint().getX(), 
                                        in_v.getEndPoint().getY(), 
                                        v.getStartPoint().getX(), 
                                        v.getStartPoint().getY(), 
                                        v.getEndPoint().getX(), 
                                        v.getEndPoint().getY()))
                                        {
                                            return in_v;
                                        }
            }
        }

        // Null if none found
        return null;
    }

    /**
     * Generate points within the polygon
     *
     * @param numPoints: Number of points in the polygon
     * @param center: The center of the polygon
     * @param radius: Radius of the polygon
     * @param startAngle: Angle which to start drawing
     *
     * @return List of points
     */
    protected Point[] generatePoints(int numPoints, Point center, float radius, float startAngle)
    {
        // If there are only one point,
        // Return the center
        if(numPoints == 1)
        {
            return new Point[]{ center };
        }

        // Generate in a circular the points of the polygon
        final float FULL_CIRC = 360f;
		final float RADIUS = radius;
		float skipDegree = FULL_CIRC / numPoints;
		Point[] points = new Point[numPoints];
		int count = 0;
		for(float i = startAngle; i < FULL_CIRC + startAngle; i+= skipDegree)
		{
            if(count >= numPoints) break;
			double x =  center.getFloatX() + Math.cos(Math.toRadians(i))*RADIUS;
			double y = center.getFloatY() + Math.sin(Math.toRadians(i))*RADIUS;
			Point p = new Point((float)x, (float)y);
			points[count] = p;
			count ++;
		}
        return points;
    }
}
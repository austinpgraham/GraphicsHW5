/**
 * Author: Austin Graham
 * 
 * Offers simple extention of Point2D 
 * class to avoid having to cast the 
 * coordinates all the time
 */
package edu.ou.cs.cg.homework;

import java.util.Random;
import java.awt.geom.Point2D;
import javax.media.opengl.*;

/**
 * Defines a single 2D point
 */
public class Point extends Point2D.Float
{
    // A random object
    private Random rand = new Random();

    // Velocity of the point
    private Vector velocity;

    // Target magnitude of the point
    private final float TARGET_MAG = 0.01667f;

    /**
     * Construct a point object
     *
     * @param x: X coordinate
     * @param y: Y coordinate
     */
    public Point(float x, float y)
    {
        super(x, y);
        // Calculate a random x velocity and get the y off of that
        // and the target magnitude
        float xVel = (float)(rand.nextDouble()*2.0 - 1.0) * this.TARGET_MAG;
        float yVel = (float)Math.sqrt(Math.pow(this.TARGET_MAG, 2) - Math.pow(xVel, 2));
        // Randomly invert the y velocity
        yVel = (this.rand.nextInt(2) == 0) ? -yVel : yVel;
        this.velocity = new Vector(xVel, yVel);
    }

    /**
     * @return Y coordinate
     */
    public float getFloatY()
    {
        return (float)this.getY();
    }

    /**
     * @return X coordinate
     */
    public float getFloatX()
    {
        return (float)this.getX();
    }

    /**
     * Set x coordinate
     *
     * @param x: new x
     */
    public void setFloatX(float x)
    {
        this.x = x;
    }

    /**
     * Set y coordinate
     *
     * @param y: new y
     */
    public void setFloatY(float y)
    {
        this.y = y;
    }

    /**
     * Set the velocity of the point
     *
     * @param v: New velocity
     */
    public void setVelocity(Vector v)
    {
        this.velocity = v;
    }

    /**
     * @return the current velocity
     */
    public Vector getVelocity()
    {
        return this.velocity;
    }

    /**
     * Reset the point
     *
     * @param p: New point to copy
     */
    public void reset(Point p)
    {
        this.x = p.getFloatX();
        this.y = p.getFloatY();
    }

    /**
     * Increase the velocity by some amount
     *
     * @param amount: amount to adjust by
     */
    public void alterVelocity(float amount)
    {
        this.velocity.increaseMagnitude(amount);
    }

    /**
     * Draw the point
     * 
     * @param gl: The GL context
     */
    public void draw(GL2 gl)
    {
        Utils.drawPoint(gl, this, new float[]{1.0f, 1.0f, 1.0f});
    }

    /**
     * Update the point with its velocity
     */
    public void update()
    {
        this.setFloatX(this.x + this.velocity.x);
        this.setFloatY(this.y + this.velocity.y);
    }

    /**
     * Get the vector representing the point
     * and its end point following its velocity
     *
     * @return Calculated vector
     */
    public Vector getPointVector()
    {
        Point endPoint = new Point(this.x + this.velocity.x, this.y + this.velocity.y);
        Vector vel_vec = new Vector(this, endPoint);
        return vel_vec;
    }

    public void translate(float dx, float dy)
    {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Subtract one point from another
     *
     * @param one: A point
     * @param two: A point
     *
     * @return two - one
     */
    public static Vector subtract(Point one, Point two)
    {
        float x = two.getFloatX() - one.getFloatX();
        float y = two.getFloatY() - one.getFloatY();
        return new Vector(x, y);
    }
}
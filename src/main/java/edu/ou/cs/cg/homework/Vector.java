/**
 * Author: Austin Graham
 */
package edu.ou.cs.cg.homework;

/**
 * Represents a Vector object
 */
class Vector
{
    // X component
    public float x;

    // Y component
    public float y;

    // MAgnitude
    private float magnitude;

    // Start point
    private Point start;

    // End point
    private Point end;

    /**
     * @return The magnitude of the vector
     */
    private float calcMagnitude()
    {
        return (float)(Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2)));
    }

    /**
     * Construct a vector
     *
     * @param x: The x component
     * @param y: The y component
     */
    public Vector(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.magnitude = this.calcMagnitude();
        this.start = null;
        this.end = null;
    }

    /**
     * Construct a vector from a start and end point
     *
     * @param start: The start point
     * @param end: The end point
     */
    public Vector(Point start, Point end)
    {
        this.start = start;
        this.end = end;
        this.x = end.getFloatX() - start.getFloatX();
        this.y = end.getFloatY() - start.getFloatY();
        this.magnitude = this.calcMagnitude();
    }

    /**
     * @return the magnitude of the vector
     */
    public float getMagnitude()
    {
        return this.magnitude;
    }

    /**
     * Set the x component
     *
     * @param x: The new x component
     */
    public void setX(float x)
    {
        this.x = x;
        this.magnitude = this.calcMagnitude();
    }

    /**
     * Set the y component
     *
     * @param y: The new y component
     */
    public void setY(float y)
    {
        this.y = y;
        this.magnitude = this.calcMagnitude();
    }

    /**
     * Increases the magnitude of the vector
     * 
     * @param amount: The amount to increase
     */
    public void increaseMagnitude(float amount)
    {
        this.x *= amount;
        this.y *= amount;
        this.magnitude = this.calcMagnitude();
    }

    /**
     * @return The start point
     */
    public Point getStartPoint()
    {
        return this.start;
    }

    /**
     * @return The end point
     */
    public Point getEndPoint()
    {
        return this.end;
    }

    /**
     * Gets the reflection vector with a normal
     *
     * @param normal: Normal for reflection
     *
     * @return The reflected vector
     */
    public Vector reflected(Vector normal)
    {
        float _dot = Vector.dot(this, normal);
        Vector scaledNormal = Vector.scale(normal, _dot*2);
        return Vector.subtract(this, scaledNormal);
    }

    /**
     * @return the normal, given it is counterclockwise
     */
    public Vector getNormal()
    {
        return new Vector(-this.y / this.magnitude, this.x / this.magnitude);
    }

    /**
     * Dot two vectors
     *
     * @param v1: First vector
     * @param v2: Second vector
     *
     * @return The dot porduct
     */
    public static float dot(Vector v1, Vector v2)
    {
        return (v1.x*v2.x) + (v1.y*v2.y);
    }
    
    /**
     * Subtract two vectors
     *
     * @param v1: First vector
     * @param v2: Second vector
     *
     * @return v1 - v2
     */
    public static Vector subtract(Vector v1, Vector v2)
    {
        float x = v1.x - v2.x;
        float y = v1.y - v2.y;
        return new Vector(x, y);
    }

    /**
     * Scale a vector
     *
     * @param v: VEctor to scale
     * @param amount: Amount to scale
     *
     * @return amount(v)
     */
    public static Vector scale(Vector v, float amount)
    {
        return new Vector(v.x*amount, v.y*amount);
    }
}
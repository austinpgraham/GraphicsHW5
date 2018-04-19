/**
 * Author: Austin Graham
 */
package edu.ou.cs.cg.homework;

import java.util.ArrayList;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

/**
 * Keeps a collection of Polygon objects to 
 * manage the focus
 */
class PolygonCollection
{
    // List of polygons to keep
    private ArrayList<Polygon> polygons;

    // Hull container
    private ArrayList<Point> hull = new ArrayList<Point>();

    // Name associated with each polygon
    private ArrayList<String> name;

    // Index of the focused polygon
    private int focused = -1;

    /**
     * Construct an empty collection
     */
    public PolygonCollection()
    {
        this.polygons = new ArrayList<Polygon>();
        this.name = new ArrayList<String>();
    }

    /**
     * Add a polygon to the collection
     * 
     * @param p: A Polygon object
     */
    public void addPolygon(Polygon p, String name)
    {
        this.polygons.add(p);
        this.name.add(name);
        this.focused = this.polygons.size() - 1;
    }

    /**
     * Set the focused polygons
     *
     * @param index: The index to focus
     */
    public void setFocused(int index)
    {
        this.focused = index;
    }

    /**
     * Get the focused polygon
     *
     * @return the focused polygon object
     */
    public Polygon getFocusedPolygon()
    {
        return this.polygons.get(this.focused);
    }

    /**
     * Get the polygons in the collection,
     *
     * @return The internal list of polygons
     */
    public ArrayList<Polygon> getPolygons()
    {
        return this.polygons;
    }

    /**
     * Get the index of the focused polygon
     *
     * @return Focused index
     */
    public int getFocused()
    {
        return this.focused;
    }

    /*
     * Remove currently selected polygon
     *
     * @return Name associated with removed polygon
     */
    public String remove()
    {
        this.polygons.remove(this.focused);
        String s = this.name.remove(this.focused);
        if(this.focused == this.polygons.size())
        {
            this.focused --;
        }
        return s;
    }

    /*
     * Get number of polygons in collection
     */
    public int size()
    {
        return this.polygons.size();
    }

    /*
     * Draw this polygon collection
     * 
     * @param gl: the GL context
     */
    public void draw(GL2 gl)
    {
        final float[] WHITE = {1.0f, 1.0f, 1.0f};
        final float[] GRAY = {150f/255f,150f/255f,150f/255f};
        for(int i = 0; i < this.size(); i++)
        {
            if(i != this.focused)
            {
                this.polygons.get(i).draw(gl, GRAY);
            }
        }
        if(this.focused != -1)
        {
            this.polygons.get(this.focused).draw(gl, WHITE);
        }
    }

    /*
     * Ask if any polygons contain
     * this point
     * 
     * @param m: Point to query
     * 
     * @return Index of containing polygon if any
     */
    public int contains(Point m)
    {
        for(int i = this.polygons.size() - 1; i >= 0; i--)
        {
            if(this.polygons.get(i).contains(m))
            {
                return i;
            }
        }
        return -1;
    }

    /*
     * Calculate if the points are in a counter clockwise formation
     * 
     * @param p: Edge point
     * @param q: Next point
     * @param r: Collection point
     * 
     * @return if the points go counter clockwise
     */
    private boolean ccw(Point p, Point q, Point r)
    {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
      
        if(val >= 0)
        {
            return false;
        }
        return true;
    }

    /*
     * Get the convex hull for this polygon collection
     * 
     * @return the list of points on the null
     */
    public ArrayList<Point> getHull()
    {
        this.hull = new ArrayList<Point>();

        // If collection less than three, add 
        // known points and terminate
        if(this.polygons.size() < 3)
        {
            for(Polygon p: this.polygons)
            {
                this.hull.add(p.center);
            }
            return this.hull;
        }

        // Get the leftmost polygon
        int leftMost = -1;
        float leftX = 1;
        for(int i = 0; i < this.polygons.size(); i++)
        {
            if(this.polygons.get(i).center.getFloatX() < leftX)
            {
                leftMost = i;
            }
        }

        int p = leftMost;
        int q;
        do
        {
            // If trying to add polygon I've seen before, exit
            if(this.hull.contains(this.polygons.get(p).center)) break;

            // Add next polygon
            this.hull.add(this.polygons.get(p).center);

            // Get next polygon index
            q = (p+1) % this.polygons.size();

            // Find next polygon that is most ccw
            for(int i = 0; i < this.polygons.size(); i++)
            {
                Point pc = this.polygons.get(p).center;
                Point qc = this.polygons.get(i).center;
                Point rc = this.polygons.get(q).center;
                boolean meh = this.ccw(pc, qc, rc);
                if(meh)
                {
                    q = i;
                }
            }
            p=q;
        // Exit when we end up where we started
        }while(p != leftMost);

        // Return the hull
        return this.hull;
    }
}
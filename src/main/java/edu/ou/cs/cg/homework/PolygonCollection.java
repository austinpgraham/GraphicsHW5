/**
 * Author: Austin Graham
 */
package edu.ou.cs.cg.homework;

import java.util.ArrayList;

/**
 * Keeps a collection of Polygon objects to 
 * manage the focus
 */
class PolygonCollection
{
    // List of polygons to keep
    private ArrayList<Polygon> polygons;

    // Index of the focused polygon
    private int focused = 0;

    /**
     * Construct an empty collection
     */
    public PolygonCollection()
    {
        this.polygons = new ArrayList<Polygon>();
    }

    /**
     * Add a polygon to the collection
     * 
     * @param p: A Polygon object
     */
    public void addPolygon(Polygon p)
    {
        this.polygons.add(p);
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
}
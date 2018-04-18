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

    // Index of the focused polygon
    private int focused = -1;

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

    public int size()
    {
        return this.polygons.size();
    }

    public void draw(GL2 gl)
    {
        final float[] WHITE = {1.0f, 1.0f, 1.0f};
        final float[] GRAY = {169f/255f,169f/255f,169f/255f};
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
}
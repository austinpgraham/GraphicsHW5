/**
 * Author: Austin Graham
 * Defines a series of GL based functions to draw
 * more complex objects.
 */
package edu.ou.cs.cg.homework;

import java.awt.geom.*;
import javax.media.opengl.*;

public class Utils
{
    /*
	 * Draw a quadralateral given four vertices and a fill color
	 */
	public static void drawQuad(GL2 gl, Point2D one, Point2D two, Point2D three, Point2D four, float[] color)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(one.getX(), one.getY());
		gl.glVertex2d(two.getX(), two.getY());
		gl.glVertex2d(three.getX(), three.getY());
		gl.glVertex2d(four.getX(), four.getY());
		gl.glEnd();
	}

	/**
	 * Draw a single point
	 */
	public static void drawPoint(GL2 gl, Point p, float[] color)
	{
		gl.glBegin(GL.GL_POINTS);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2f(p.getFloatX(), p.getFloatY());
		gl.glEnd();
	}

	/*
	 * Draw a triangle given three vertices and a fill color
	 */
	public static void drawTriangle(GL2 gl, Point2D one, Point2D two, Point2D three, float[] color)
	{
		gl.glBegin(GL2.GL_TRIANGLES);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(one.getX(), one.getY());
		gl.glVertex2d(two.getX(), two.getY());
		gl.glVertex2d(three.getX(), three.getY());
		gl.glEnd();
	}

	/*
	 * Draw a quad given four vertices with two colors 
	 * forming a gradient
	 */
	public static void drawQuadGradient(GL2 gl, Point2D one, Point2D two, Point2D three, Point2D four, float[] start, float[] end)
	{
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(start[0], start[1], start[2]);
		gl.glVertex2d(one.getX(), one.getY());
		gl.glVertex2d(two.getX(), two.getY());
		gl.glColor3f(end[0], end[1], end[2]);
		gl.glVertex2d(three.getX(), three.getY());
		gl.glVertex2d(four.getX(), four.getY());
		gl.glEnd();
	}

	/*
	 * Draw a line given two vertices and a color
	 */
	public static void drawLine(GL2 gl, Point2D start, Point2D end, float[] color)
	{
		gl.glBegin(GL.GL_LINES);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(start.getX(), start.getY());
		gl.glVertex2d(end.getX(), end.getY());
		gl.glEnd();
	}

	/* 
	 * The same as above, but allowing for a width
	 */
	public static void drawLine(GL2 gl, Point2D start, Point2D end, float[] color, float width)
	{
		gl.glLineWidth(width);
		gl.glBegin(GL.GL_LINES);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(start.getX(), start.getY());
		gl.glVertex2d(end.getX(), end.getY());
		gl.glEnd();
		gl.glLineWidth(1.0f);
	}

	/* 
	 * The same as above, but allowing for an alpha as well
	 */
	public static void drawLine(GL2 gl, Point2D start, Point2D end, float[] color, float width, float alpha)
	{
		gl.glLineWidth(width);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4f(color[0], color[1], color[2], alpha);
		gl.glVertex2d(start.getX(), start.getY());
		gl.glVertex2d(end.getX(), end.getY());
		gl.glEnd();
		gl.glLineWidth(1.0f);
	}

	/*
	 * Draws a circle with a center, radius, start and end degree marker, 
	 * fill color, and designate an outline if needed
	 */
	public static void drawCircle(GL2 gl, Point2D center, float radius, double start, double end, float[] color, boolean outline)
	{
		final float[] BLACK = new float[]{0f, 0f, 0f};
		int len = 360;
		double[] vert = new double[(len+1)*2];
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(center.getX(), center.getY());
		double centx = center.getX();
		double centy = center.getY();
		for(double angle = start, i = 0; angle <= end; angle++, i+= 2)
		{
			double x = centx + Math.cos(Math.toRadians(angle))*radius;
			double y = centy + Math.sin(Math.toRadians(angle))*radius;
			gl.glVertex2d(x,y);
			vert[(int)i] = x;
			vert[(int)i+1] = y;
		}
		gl.glEnd();
		// If outline, do it
		if(outline)
		{
			for(int i = 0; i < vert.length - 2; i+=2)
			{
				final Point2D.Float from = new Point2D.Float((float)vert[i], (float)vert[i+1]);
				final Point2D.Float to = new Point2D.Float((float)vert[i+2], (float)vert[i+3]);
				Utils.drawLine(gl, from, to, BLACK);
			}
			final Point2D.Float from = new Point2D.Float((float)vert[vert.length - 2], (float)vert[vert.length - 1]);
			final Point2D.Float to = new Point2D.Float((float)vert[0], (float)vert[1]);
			Utils.drawLine(gl, from, to, BLACK);
		}
	}

	/*
	 * Overload of the above, allowing for an degree increment to draw polygons
	 */
	public static void drawCircle(GL2 gl, Point2D center, float radius, double start, double end, int inc, float[] color, boolean outline)
	{
		final float[] BLACK = new float[]{0f, 0f, 0f};
		int len = 360 / inc;
		double[] vert = new double[(len+1)*2];
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glColor3f(color[0], color[1], color[2]);
		gl.glVertex2d(center.getX(), center.getY());
		double centx = center.getX();
		double centy = center.getY();
		for(double angle = start, i = 0; angle <= end; angle += inc, i+=2)
		{
			double x = centx + Math.cos(Math.toRadians(angle))*radius;
			double y = centy + Math.sin(Math.toRadians(angle))*radius;
			vert[(int)i] = x;
			vert[(int)i+1] = y;
			gl.glVertex2d(x,y);
		}
		gl.glEnd();
		if(outline)
		{
			for(int i = 0; i < vert.length - 2; i+=2)
			{
				final Point2D.Float from = new Point2D.Float((float)vert[i], (float)vert[i+1]);
				final Point2D.Float to = new Point2D.Float((float)vert[i+2], (float)vert[i+3]);
				Utils.drawLine(gl, from, to, BLACK);
			}
			final Point2D.Float from = new Point2D.Float((float)vert[vert.length - 2], (float)vert[vert.length - 1]);
			final Point2D.Float to = new Point2D.Float((float)vert[0], (float)vert[1]);
			Utils.drawLine(gl, from, to, BLACK);
		}
	}
}
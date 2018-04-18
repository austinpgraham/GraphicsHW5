//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Mar  1 18:52:22 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160209 [weaver]:	Original file.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package edu.ou.cs.cg.homework;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.TextRenderer;

//******************************************************************************

/**
 * The <CODE>Interaction</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class View
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final int				DEFAULT_FRAMES_PER_SECOND = 60;
	private static final DecimalFormat	FORMAT = new DecimalFormat("0.000");

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final GLJPanel			canvas;
	private int						w;				// Canvas width
	private int						h;				// Canvas height

	private final KeyHandler		keyHandler;
	private final MouseHandler		mouseHandler;

	private final FPSAnimator		animator;
	private int						counter = 0;	// Frame display counter

	private TextRenderer			renderer;

	private Point2D.Double				origin;		// Current origin coordinates
	private Point2D.Double				cursor;		// Current cursor coordinates
	private ArrayList<Point2D.Double>	points;		// User's polyline points

	private String[] names = Network.getAllNames();
	private int focusString = 0;
	private int focusPoly = 0;
	private Stack<String> usedNames = new Stack<String>();
	private PolygonCollection nodes = new PolygonCollection();
	private float radius = 0.05f;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public View(GLJPanel canvas)
	{
		this.canvas = canvas;

		// Initialize model
		origin = new Point2D.Double(0.0, 0.0);
		cursor = null;
		points = new ArrayList<Point2D.Double>();

		// Initialize rendering
		canvas.addGLEventListener(this);
		animator = new FPSAnimator(canvas, DEFAULT_FRAMES_PER_SECOND);
		animator.start();

		// Initialize interaction
		keyHandler = new KeyHandler(this);
		mouseHandler = new MouseHandler(this);
	}

	//**********************************************************************
	// Getters and Setters
	//**********************************************************************

	public int	getWidth()
	{
		return w;
	}

	public int	getHeight()
	{
		return h;
	}

	public Point2D.Double	getOrigin()
	{
		return new Point2D.Double(origin.x, origin.y);
	}

	public void		setOrigin(Point2D.Double origin)
	{
		this.origin.x = origin.x;
		this.origin.y = origin.y;
		canvas.repaint();
	}

	public Point2D.Double	getCursor()
	{
		return cursor;
	}

	public void		setCursor(Point2D.Double cursor)
	{
		this.cursor = cursor;
		canvas.repaint();
	}

	public void		clear()
	{
		points.clear();
		canvas.repaint();
	}

	public void		add(Point2D.Double p)
	{
		points.add(p);
		canvas.repaint();
	}

	//**********************************************************************
	// Public Methods
	//**********************************************************************

	public Component	getComponent()
	{
		return (Component)canvas;
	}

	public void cycleString(boolean right)
	{
		if(right) this.focusString++;
		else this.focusString--;
		if(this.focusString >= this.names.length) this.focusString = 0;
		else if(this.focusString <= 0) this.focusString = this.names.length - 1;
	}

	public void cyclePolygon(boolean right)
	{
		if(right) this.focusPoly++;
		else this.focusPoly--;
		if(this.focusPoly >= this.nodes.size()) this.focusPoly = -1;
		else if(this.focusPoly < -1) this.focusPoly = this.nodes.size() - 1;
		this.nodes.setFocused(this.focusPoly);
	}

	public void removeNode()
	{
		if(this.focusPoly == -1)
		{
			return;
		}
		String removed = this.nodes.remove();
		if(this.nodes.size() == 0)
		{
			this.focusPoly = -1;
		}
		else if(this.focusPoly == this.nodes.size())
		{
			this.focusPoly--;
		}
		this.addName(removed);
	}

	public void translateUp()
	{
		int idx = this.nodes.getFocused();
		if(idx != -1)
		{
			Polygon p = this.nodes.getFocusedPolygon();
			float amount = p.getRadius() * 2f * 0.1f;
			p.move(0, amount);
		}
	}

	public void translateDown()
	{
		int idx = this.nodes.getFocused();
		if(idx != -1)
		{
			Polygon p = this.nodes.getFocusedPolygon();
			float amount = p.getRadius() * 2f * 0.1f;
			p.move(0, -amount);
		}
	}

	public void translateRight()
	{
		int idx = this.nodes.getFocused();
		if(idx != -1)
		{
			Polygon p = this.nodes.getFocusedPolygon();
			float amount = p.getRadius() * 2f * 0.1f;
			p.move(amount, 0);
		}
	}

	public void translateLeft()
	{
		int idx = this.nodes.getFocused();
		if(idx != -1)
		{
			Polygon p = this.nodes.getFocusedPolygon();
			float amount = p.getRadius() * 2f * 0.1f;
			p.move(-amount, 0);
		}
	}

	public void scaleUp()
	{
		int idx = this.nodes.getFocused();
		if(idx != -1)
		{
			Polygon p = this.nodes.getFocusedPolygon();
			float amount = 2f * 1.1f;
			p.scale(amount);
		}
	}

	public void scaleDown()
	{
		int idx = this.nodes.getFocused();
		if(idx != -1)
		{
			Polygon p = this.nodes.getFocusedPolygon();
			float amount = 1f / (2f * 1.1f);
			p.scale(amount);
		}
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void		init(GLAutoDrawable drawable)
	{
		w = drawable.getWidth();
		h = drawable.getHeight();

		renderer = new TextRenderer(new Font("Monospaced", Font.PLAIN, 12),
									true, true);
	}

	public void		dispose(GLAutoDrawable drawable)
	{
		renderer = null;
	}

	public void		display(GLAutoDrawable drawable)
	{
		updateProjection(drawable);

		update(drawable);
		render(drawable);
	}

	public void		reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	public void selectPolygon(Point m)
	{
		int f = this.nodes.contains(m);
		if(f != -1)
		{
			this.nodes.setFocused(f);
			this.focusPoly = f;
		}
	}

	public Polygon getSelected()
	{
		return this.nodes.getFocusedPolygon();
	}

	public boolean contains(Point m)
	{
		return this.nodes.contains(m) != -1;
	}

	public void placeName()
	{
		String name = this.names[this.focusString];
		Color color = Network.getColor(name);
		int sides = Network.getSides(name);
		float r = (float)color.getRed() / 255f;
		float g = (float)color.getGreen() / 255f;
		float b = (float)color.getBlue() / 255f;
		Random rand = new Random();
		float x = rand.nextFloat() - 0.5f;
		float y = rand.nextFloat() - 0.5f;
		Polygon p = new Polygon(sides, new Point(x, y), this.radius, 0f, new float[]{r, g, b});
		this.nodes.addPolygon(p, name);
		this.usedNames.push(name);
		this.removeName(name);
		if(this.focusString == this.names.length)
		{
			this.cycleString(true);
		}
	}

	//**********************************************************************
	// Private Methods (Viewport)
	//**********************************************************************

	private void	updateProjection(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();
		GLU		glu = new GLU();

		float	xmin = (float)(origin.x - 1.0);
		float	xmax = (float)(origin.x + 1.0);
		float	ymin = (float)(origin.y - 1.0);
		float	ymax = (float)(origin.y + 1.0);

		gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(xmin, xmax, ymin, ymax);		// 2D translate and scale
	}

	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************

	private void	update(GLAutoDrawable drawable)
	{
		counter++;								// Counters are useful, right?
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer
		drawAxes(gl);							// X and Y axes
		if(this.names.length > 0)
		{
			drawName(drawable);
		}

		this.nodes.draw(gl);
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	private void removeName(String name)
	{
		ArrayList<String> newArray = new ArrayList<String>();
		for(String n: this.names)
		{
			if(n != name)
			{
				newArray.add(n);
			}
		}
		this.names = new String[newArray.size()];
		this.names = newArray.toArray(this.names);
	}

	private void addName(String name)
	{
		ArrayList<String> newArray = new ArrayList<String>();
		for(String n: this.names)
		{
			newArray.add(n);
		}
		newArray.add(name);
		this.names = new String[newArray.size()];
		this.names = newArray.toArray(this.names);
	}

	private void	drawAxes(GL2 gl)
	{
		gl.glBegin(GL.GL_LINES);

		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glVertex2d(-10.0, 0.0);
		gl.glVertex2d(10.0, 0.0);

		gl.glVertex2d(0.0, -10.0);
		gl.glVertex2d(0.0, 10.0);

		gl.glEnd();
	}

	private void	drawName(GLAutoDrawable drawable)
	{
		renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		renderer.setColor(1.0f, 1.0f, 0, 1.0f);
		renderer.draw(this.names[this.focusString], 2, 2);
		renderer.endRendering();
	}
}

//******************************************************************************

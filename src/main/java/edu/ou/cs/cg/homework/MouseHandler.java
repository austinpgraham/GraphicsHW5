//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Thursday April 18 12:43:15 2016 by Austin Graham
//******************************************************************************
// Major Modification History:
//
// 20160225 [weaver]:	Original file.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package edu.ou.cs.cg.homework;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Stack;

//******************************************************************************

/**
 * The <CODE>MouseHandler</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class MouseHandler extends MouseAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View	view;

	// Keep track of transforms
	private Stack<Point> transforms;

	// Denote the action based on the 
	// click and movement patterns
	private int action = -1;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public MouseHandler(View view)
	{
		this.view = view;

		Component	component = view.getComponent();

		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
	}

	//**********************************************************************
	// Override Methods (MouseListener)
	//**********************************************************************

	public void		mouseClicked(MouseEvent e)
	{
		// Get the canvas coordinate where clicked 
		// transform to origin and select the correct 
		// polygon
		Point pointer = calcCoordinatesInView(e.getX(), e.getY());
		Point2D.Double o = view.getOrigin();
		pointer.setFloatX(pointer.getFloatX() + (float)o.getX());
		pointer.setFloatY(pointer.getFloatY() + (float)o.getY());
		this.view.selectPolygon(pointer);
	}

	public void		mouseEntered(MouseEvent e)
	{
	}

	public void		mouseExited(MouseEvent e)
	{
	}

	public void		mousePressed(MouseEvent e)
	{
		// When pressed, create new transform list
		transforms = new Stack<Point>();

		// Translate the click point
		Point current = calcCoordinatesInView(e.getX(), e.getY());
		Point2D.Double o = view.getOrigin();
		current.translate((float)o.getX(), (float)o.getY());

		// If clicked outside and shift is not down, pan
		// the entire view
		if(!view.contains(current) && !Utilities.isShiftDown(e))
		{
			current.translate((float)-o.getX(), (float)-o.getY());
			transforms.push(current);
			this.action = 0;
		}
		// Otherwise if shift isnt down and we click 
		// in a polygon, translate the polygon
		else if(!Utilities.isShiftDown(e))
		{
			this.action = 1;
		}
		// Otherwise we rotate
		else
		{
			transforms.push(current);
			this.action = 2;
		}
	}

	public void		mouseReleased(MouseEvent e)
	{
		// Reset the action
		this.action = -1;	
	}

	//**********************************************************************
	// Override Methods (MouseMotionListener)
	//**********************************************************************

	public void		mouseDragged(MouseEvent e)
	{
		// Translate the mouse position
		Point2D.Double origin = this.view.getOrigin();
		Point current = calcCoordinatesInView(e.getX(), e.getY());
		if(this.action == 0)
		{
			// Look at the last point and translate
			// The origin of the view using the vector
			// between current position and the previous
			Point last = transforms.peek();
			Vector transVect = Point.subtract(last, current);
			Point new_origin = new Point((float)origin.getX() - transVect.x, (float)origin.getY() - transVect.y);
			this.view.setOrigin(new Point2D.Double(new_origin.getX(), new_origin.getY()));
			transforms.push(current);
		}
		else if(this.action == 1)
		{
			// Do the same translation as above,
			// but only on a single polygon
			Point2D.Double o = view.getOrigin();
			current.setFloatX(current.getFloatX() + (float)o.getX());
			current.setFloatY(current.getFloatY() + (float)o.getY());
			Polygon focused = view.getSelected();
			Point center = focused.center;
			Vector movement = Point.subtract(center, current);
			focused.move(movement.x, movement.y);
		}
		else if(this.action == 2)
		{
			// Get the origin
			Point o = new Point((float)view.getOrigin().getX(), (float)view.getOrigin().getY());
			current.translate(-(float)o.getX(), -(float)o.getY());
			// Get the polygon and save the center
			Polygon focused = view.getSelected();
			Point center = new Point(focused.center.getFloatX(), focused.center.getFloatY());
			// Translate to the global origin
			focused.move(-center.getFloatX(), -center.getFloatY());
			// Use the vectors between current click point and the 
			// last to get the angle to rotate
			Point previous = transforms.peek();
			Vector v1 = Point.subtract(center, current);
			Vector v2 = Point.subtract(center, previous);
			float costheta = Vector.dot(v1, v2) / (v1.getMagnitude() * v2.getMagnitude());
			// If the cos is 1, we get NaN and bad
			// things happen. This means it is small therefore
			// there should be essentially no movement
			if(costheta != 1.0)
			{
				double arccos = Math.acos(costheta);
				focused.rotate(arccos);
			}
			// Move the polygon back
			focused.move(center.getFloatX(), center.getFloatY());
			transforms.push(current);
		}
	}

	public void		mouseMoved(MouseEvent e)
	{
	}

	//**********************************************************************
	// Override Methods (MouseWheelListener)
	//**********************************************************************

	public void		mouseWheelMoved(MouseWheelEvent e)
	{
	}

	//**********************************************************************
	// Private Methods
	//**********************************************************************

	private Point	calcCoordinatesInView(int sx, int sy)
	{
		int				w = view.getWidth();
		int				h = view.getHeight();
		double			vx = (sx * 2.0) / w - 1.0;
		double			vy = (sy * 2.0) / h - 1.0;

		return new Point((float)vx, (float)-vy);
	}
}

//******************************************************************************

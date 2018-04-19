//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Feb 29 23:46:15 2016 by Chris Weaver
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

//import java.lang.*;
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

	private Stack<Point> transforms;

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
		transforms = new Stack<Point>();
		Point current = calcCoordinatesInView(e.getX(), e.getY());
		Point2D.Double o = view.getOrigin();
		current.translate((float)o.getX(), (float)o.getY());
		if(!view.contains(current) && !Utilities.isShiftDown(e))
		{
			current.translate((float)-o.getX(), (float)-o.getY());
			transforms.push(current);
			this.action = 0;
		}
		else if(!Utilities.isShiftDown(e))
		{
			this.action = 1;
		}
		else
		{
			transforms.push(current);
			this.action = 2;
		}
	}

	public void		mouseReleased(MouseEvent e)
	{
		this.action = -1;	
	}

	//**********************************************************************
	// Override Methods (MouseMotionListener)
	//**********************************************************************

	public void		mouseDragged(MouseEvent e)
	{
		Point2D.Double origin = this.view.getOrigin();
		Point current = calcCoordinatesInView(e.getX(), e.getY());
		if(this.action == 0)
		{
			Point last = transforms.peek();
			Vector transVect = Point.subtract(last, current);
			Point new_origin = new Point((float)origin.getX() - transVect.x, (float)origin.getY() - transVect.y);
			this.view.setOrigin(new Point2D.Double(new_origin.getX(), new_origin.getY()));
			transforms.push(current);
		}
		else if(this.action == 1)
		{
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
			Point2D.Double o = view.getOrigin();
			current.translate((float)o.getX(), (float)o.getY());
			//if(transforms.size() % 3 == 0)
			//{
				Polygon focused = view.getSelected();
				Point center = focused.center;
				// transforms.pop();
				// transforms.pop();
				Point previous = transforms.peek();
				Vector v1 = Point.subtract(center, current);
				Vector v2 = Point.subtract(center, previous);
				float costheta = Vector.dot(v1, v2) / (v1.getMagnitude() * v2.getMagnitude());
				if(costheta != 1.0)
				{
					double arccos = Math.acos(costheta);
					focused.rotate(arccos);
				}
			//}
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
		Point2D.Double	p = view.getOrigin();
		double			vx = (sx * 2.0) / w - 1.0;
		double			vy = (sy * 2.0) / h - 1.0;

		return new Point((float)vx, (float)-vy);
	}
}

//******************************************************************************

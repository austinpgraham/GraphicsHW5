//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon April 19 12:36:04 2018 by Austin Graham
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

//******************************************************************************

/**
 * The <CODE>KeyHandler</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class KeyHandler extends KeyAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View	view;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public KeyHandler(View view)
	{
		this.view = view;

		Component	component = view.getComponent();

		component.addKeyListener(this);
	}

	//**********************************************************************
	// Override Methods (KeyListener)
	//**********************************************************************

	public void		keyPressed(KeyEvent e)
	{
		Point2D.Double	p = view.getOrigin();
		double			a = (Utilities.isShiftDown(e) ? 0.01 : 0.1);

		switch (e.getKeyCode())
		{
			case KeyEvent.VK_NUMPAD5:
				p.x = 0.0;	p.y = 0.0;	break;

			// For each arrow key, translate
			// the selected polygon in the proper
			// direction
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_LEFT:
				if(!Utilities.isShiftDown(e))
				{
					view.translateLeft();
				}
				else 
				{
					view.scaleDown();
				}
				break;

			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_RIGHT:
				if(!Utilities.isShiftDown(e))
				{
					view.translateRight();
				}
				else
				{
					view.scaleUp();
				}
				break;

			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_DOWN:
				if(!Utilities.isShiftDown(e))
				{
					view.translateDown();
				}
				else
				{
					view.scaleDown();
				}
				break;

			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_UP:
				if(!Utilities.isShiftDown(e))
				{
					view.translateUp();	
				}
				else 
				{
					view.scaleUp();
				}
				break;

			case KeyEvent.VK_NUMPAD1:
				p.x -= a;	p.y -= a;	break;

			case KeyEvent.VK_NUMPAD7:
				p.x -= a;	p.y += a;	break;

			case KeyEvent.VK_NUMPAD3:
				p.x += a;	p.y -= a;	break;

			case KeyEvent.VK_NUMPAD9:
				p.x += a;	p.y += a;	break;

			// Mac does not have delete key,
			// use back space to remove selected node
			case KeyEvent.VK_BACK_SPACE:
				view.removeNode();
				break;
			
			// Comma and period cycle objects, names 
			// if shift is down left and right respectively
			case KeyEvent.VK_COMMA:
				if(Utilities.isShiftDown(e))
				{
					view.cycleString(false);
				}
				else 
				{
					this.view.cyclePolygon(false);
				}
				break;
			case KeyEvent.VK_PERIOD:
				if(Utilities.isShiftDown(e))
				{
					view.cycleString(true);
				}
				else
				{
					this.view.cyclePolygon(true);
				}
				break;
			// Place name on enter
			case KeyEvent.VK_ENTER:
				this.view.placeName();
				break;
		}

		view.setOrigin(p);
	}
}

//******************************************************************************

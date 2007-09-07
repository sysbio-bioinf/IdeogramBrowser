package ideogram;

import java.awt.Color;
import java.util.ArrayList;

import junit.framework.Assert;


import util.MathUtility;

public class TableColorMapper implements IColorMapper 
{
	ArrayList<Color>	colorTable;
	
	public TableColorMapper()
	{
		colorTable = new ArrayList<Color>();
		
	}
	
	
	public Color map(double value) 
	{
		value = MathUtility.restrict(value, -1.0, 1.0 );
		
		if( colorTable == null || colorTable.size() == 0 )
			return null;
		
		int idx = (int)Math.round(0.5*(value+1.0)*(colorTable.size()-1));
		
		Assert.assertTrue( (idx>=0) && (idx<colorTable.size()) );
		
		return colorTable.get(idx);
	}


	public ArrayList<Color> getColorTable() {
		return colorTable;
	}


	public void setColorTable(ArrayList<Color> colorTable) {
		this.colorTable = colorTable;
	}
}

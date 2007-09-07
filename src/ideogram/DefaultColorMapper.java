package ideogram;

import java.awt.Color;

public class DefaultColorMapper implements IColorMapper 
{
	//ColorSpace		colorSpace;
	float          min_saturation,
					from,
					to;
	
	
	public DefaultColorMapper(float from, float to)
	{
		this.from = from;
		this.to = to;
		min_saturation = 0.05f;
	}
	
	public DefaultColorMapper()
	{
		this(1.0f/3.0f,0.0f);
		//colorSpace = ColorSpace.getInstance(ColorSpace.TYPE_HSV);
		
	}
	
	private Color lookup(double value)
	{
		float[] hsv = new float[3];
		
		hsv[0] = 0;
		hsv[1] = 1.0f;
		hsv[2] = 0.5f;
		
		/*value = MathUtility.restrict(value, -1.0, 1.0);
		
		if( value <= 0.0 )
		{
				hsv[0] = from;	// green
				hsv[2] = 0.8f;
		}
		else
		{
				hsv[0] = to;	// red
				hsv[2] = 1.0f;
		}
		
		if( value == 0.0 )
			hsv[1] = 0.0f;
		else 
			hsv[1] = (float)(Math.abs(value)*(1.0-min_saturation)+min_saturation);	// saturation
		*/
		/*
		if( value <= 0.0 )
			return new Color(0.0f,-(float)value,0.0f);
		else
			return new Color((float)value,0.0f,0.0f);
			*/
			
		return Color.getHSBColor( hsv[0], hsv[1], hsv[2] );
		
		//float rgb[] = colorSpace.toRGB(hsv);
		//return new Color(rgb[0],rgb[1],rgb[2]);
	}

	public Color map( double value ) 
	{
		if( Double.isNaN(value) )
			return null;
		else
			return lookup(value);
	}
}

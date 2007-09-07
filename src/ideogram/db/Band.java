/*
 * Created on 01.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ideogram.db;

/**
 * Encapsulates a chromosomal Band identifier (e.g. 1p36.33b).
 * 
 * @author muellera
 */
public class Band 
{
	public byte chromosome, arm;
	public short band, subband;
	public byte subsubband;
	public byte density;

	/**
	 * Arm
	 */
	public static final byte ARM_BOTH = 0, 
							 ARM_P = 1, 
							 ARM_Q = 2;
	
	/**
	 * Types
	 */
	public static final byte 	DENSITY_UNKNOWN = 0,
								DENSITY_GNEG = 1,		// "gneg"
								DENSITY_GPOS = 2,		// "apos"
								DENSITY_ACEN = 3,		// "acen"
								DENSITY_GVAR = 4,		// "gvar"
								DENSITY_STALK = 5;		// "stalk"
								
	public Band()
	{
		arm = ARM_BOTH;
		density = DENSITY_UNKNOWN; 		
	}
	
	/**
	 * The resolution of the Band specification.
	 * "1"      resolution = 0 // the whole chromosom
	 * "1p"     resolution = 1 // the whole arm
	 * "1p22" 	resolution = 2 // band
	 * "1p22.3" resolution = 3 // subband
	 * "Yq12a" "1p22.3e" resolution = 4 // subsubband
	 */
	public byte getResolution()
	{
		if( subsubband > 0 )
		{
			return 4;
		}
		if( subband > 0 )
		{
			return 3;
		}
		if( band > 0 )
		{
			return 2;
		}
		if( arm > 0 )
		{
			return 1;
		}
		return 0;
	}
	
	
	public void setChromosome( String chr )
		throws IllegalArgumentException
	{
		try {
			if( chr.equalsIgnoreCase("x") )
			{
				chromosome = 23;
				return;
			}
			if( chr.equalsIgnoreCase("y") )
			{
				chromosome = 24;
				return;
			}
			
			chromosome = Byte.parseByte(chr);
			if( (chromosome < 1) || (chromosome > 22 ) )
			{
				chromosome = 0;
				throw new IllegalArgumentException("Invalid chromsome number '"+chr+"'");
			}
		}
		catch( NumberFormatException e )
		{
			chromosome = 0;
			throw new IllegalArgumentException("Invalid chromsome string '"+chr+"'");
		}
	}
	
	public void setArm( String str )
		throws IllegalArgumentException
	{
		if( str.equalsIgnoreCase("p") )
		{
			arm = ARM_P;		
		}
		else
		{
			if( str.equalsIgnoreCase("q") )
			{
				arm = ARM_Q;		
			}			
			else
			{
				throw new IllegalArgumentException("Invalid chromosome arm '"+str+"'");
			}
		}
	}
	
	public void setBand( String str )
	{
		band = Short.parseShort(str);
	}
	
	public void setSubBand( String str )
	{
		subband = Short.parseShort(str);
	}
	
	public void setSubSubBand( char c )
		throws IllegalArgumentException
	{
		c = Character.toLowerCase(c);
		if( (c < 'a') || (c > 'z') )
		{
			throw new IllegalArgumentException("Subsubband out of range");
		}
		subsubband = (byte)(c - 'a' + 1);
	}
	
	public void setDensity( String str )
	{
		if( str.equalsIgnoreCase("gneg") )
		{
			density = DENSITY_GNEG;
			return;
		}
		if( str.equalsIgnoreCase("gpos") )
		{
			density = DENSITY_GPOS;
			return;
		}
		if( str.equalsIgnoreCase("acen") )
		{
			density = DENSITY_ACEN;
			return;
		}
		if( str.equalsIgnoreCase("gvar") )
		{
			density = DENSITY_GVAR;
			return;
		}
		if( str.equalsIgnoreCase("stalk") )
		{
			density = DENSITY_STALK;
			return;
		}
		density = DENSITY_UNKNOWN;
	}
	
	public String getChromosome()
	{
		if( (chromosome >= 1) && (chromosome <= 22) )
		{
			return new Byte(chromosome).toString(); 
		}
		else
		{
			switch( chromosome )
			{
				case 23:
					return "X";
					
				case 24:
					return "Y";
					
				default:
					return "?";
			}
		}
		
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(getChromosome());
		switch(arm)
		{
			case ARM_P:
				buf.append('p');
				break;
				
			case ARM_Q:
				buf.append('q');
				break;
				
			default:
				return buf.toString();			
		}
		if( band > 0 )
		{
			buf.append(band);
			if( subband > 0 )
			{	
				buf.append('.');	
				buf.append(subband);
			}
			if( subsubband > 0 )
			{
				buf.append((char)((subsubband - 1) + 'a'));
			}
		}

		return buf.toString();
	}
	
}

package ideogram;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class IdeogramViewPrintingHelper implements Printable
{
	private IdeogramView[] ideograms;
	private final  static int nrows = 2;
	private boolean printOnSinglePages;		// TODO: implement this feature
	
	public IdeogramViewPrintingHelper(IdeogramView[] ideograms)
	{
		printOnSinglePages = false;
		this.ideograms = ideograms;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException
	{
		if( pageIndex != 0 )
			return NO_SUCH_PAGE;
		
		Graphics2D g2 = (Graphics2D)graphics;
		
		
		/*
		double 	dpi = 72.0,
				resolution = 72.0; // [dpi]		 * 
		int 	res_x = (int) Math.round( (resolution*pageFormat.getImageableWidth())/dpi ),	// [px]
				res_y = (int) Math.round( (resolution*pageFormat.getImageableHeight())/dpi ); 	// [px]
		*/
		Dimension dim = getDim();
		int		res_x = dim.width,
				res_y = dim.height;
				
	
		double pWidth = pageFormat.getImageableWidth(),
				pHeight = pageFormat.getImageableHeight();
		
		double scale = Math.min(pWidth/res_x,
								pHeight/res_y);
		
		double w = res_x * scale,
				h = res_y * scale;
	
		g2.translate(pageFormat.getImageableX()+(pWidth-w)*0.5, 
					 pageFormat.getImageableY()+(pHeight-h)*0.5);
		g2.scale(scale, scale);
		try 
		{
			directPaintAll(g2);
		}
		catch( Exception e )
		{
			throw new PrinterException("Exception while printing\n"+e.getMessage());
		}
		return PAGE_EXISTS;	
	}
	
	
	public Dimension getDim()
	{
		int n = ideograms.length,
			ncols = (int)Math.round( (double)n / (double)nrows ),
			width = 0,
			height = 0,
			x = 0;
		
		for( int i=0; i<ideograms.length; ++i )
		{
			Dimension dim = ideograms[i].getDim();
			x += dim.width;
			height = Math.max(height,dim.height);
			if( (i+1) % ncols == 0 ) 
			{
				width = Math.max(width,x);
				x = 0;
			}
		}
		return new Dimension(width,2*height);
	}
	
	public void directPaintAll(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		Dimension dim = getDim();
		int ncols = (int)Math.round( (double)ideograms.length / (double)nrows );

		int x=0;
		for( int i=0; i<ideograms.length; ++i )
        {
        	if( i == ncols )
        	{
        		g2.translate(-x,dim.height/2);
        	}
        	int w = ideograms[i].getDim().width;
        	ideograms[i].directPaint(g2); // , ideograms[i].getWidth(), ideograms[i].getHeight());
        	g2.translate(w,0);
        	x += w;
        }		
	}

	public boolean isPrintOnSinglePages()
	{
		return printOnSinglePages;
	}

	public void setPrintOnSinglePages(boolean printOnSinglePages)
	{
		this.printOnSinglePages = printOnSinglePages;
	}
}

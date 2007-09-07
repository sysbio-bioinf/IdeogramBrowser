package ideogram;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.JTextPane;
import javax.swing.text.View;


// not yet used, because printed size is too small
public class TextPanePrinter implements Printable {

	JTextPane pane;

	boolean scaleWidthToFit = true;

	protected int currentPage = -1; 
	protected double pageEndY = 0; 
	protected double pageStartY = 0;
	
	public TextPanePrinter(JTextPane p) {
		this.pane = p;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {

		double scale = 1.0;
		Graphics2D graphics2D;
		View rootView;

		graphics2D = (Graphics2D) graphics;

		pane.setSize((int) pageFormat.getImageableWidth(), Integer.MAX_VALUE);
		pane.validate();

		rootView = pane.getUI().getRootView(pane);

		if ((scaleWidthToFit)
				&& (pane.getMinimumSize().getWidth() > pageFormat
						.getImageableWidth())) {
			scale = pageFormat.getImageableWidth()
					/ pane.getMinimumSize().getWidth();
			graphics2D.scale(scale, scale);
		}

		graphics2D.setClip((int) (pageFormat.getImageableX() / scale),
				(int) (pageFormat.getImageableY() / scale), (int) (pageFormat
						.getImageableWidth() / scale), (int) (pageFormat
						.getImageableHeight() / scale));

		if (pageIndex > currentPage) {
			currentPage = pageIndex;
			pageStartY += pageEndY;
			pageEndY = graphics2D.getClipBounds().getHeight();
		}

		graphics2D.translate(graphics2D.getClipBounds().getX(), graphics2D
				.getClipBounds().getY());

		java.awt.Rectangle allocation = new java.awt.Rectangle(0,
				(int) -pageStartY, (int) (pane.getMinimumSize().getWidth()),
				(int) (pane.getPreferredSize().getHeight()));

		if (printView(graphics2D, allocation, rootView)) {
			return Printable.PAGE_EXISTS;
		} else {
			pageStartY = 0;
			pageEndY = 0;
			currentPage = -1;
			return Printable.NO_SUCH_PAGE;
		}
	}

	protected boolean printView(Graphics2D graphics2D, Shape allocation,
			View view) {
		boolean pageExists = false;
		java.awt.Rectangle clipRectangle = graphics2D.getClipBounds();
		Shape childAllocation;
		View childView;

		if (view.getViewCount() > 0
				&& !view.getElement().getName().equalsIgnoreCase("td")) {
			for (int i = 0; i < view.getViewCount(); i++) {
				childAllocation = view.getChildAllocation(i, allocation);
				if (childAllocation != null) {
					childView = view.getView(i);
					if (printView(graphics2D, childAllocation, childView)) {
						pageExists = true;
					}
				}
			}
		} else {
			if (allocation.getBounds().getMaxY() >= clipRectangle.getY()) {
				pageExists = true;
				if ((allocation.getBounds().getHeight() > clipRectangle
						.getHeight())
						&& (allocation.intersects(clipRectangle))) {
					view.paint(graphics2D, allocation);
				} else {
					if (allocation.getBounds().getY() >= clipRectangle.getY()) {
						if (allocation.getBounds().getMaxY() <= clipRectangle
								.getMaxY()) {
							view.paint(graphics2D, allocation);
						} else {
							if (allocation.getBounds().getY() < pageEndY) {
								pageEndY = allocation.getBounds().getY();
							}
						}
					}
				}
			}
		}
		return pageExists;
	}
}

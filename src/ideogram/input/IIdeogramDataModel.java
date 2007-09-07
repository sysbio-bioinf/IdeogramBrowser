package ideogram.input;

import java.util.LinkedList;
import javax.swing.event.ChangeListener;

public interface IIdeogramDataModel
{
	void addChangeListener(ChangeListener listener);
	void removeChangeListener(ChangeListener listener);
	LinkedList<String> getFileName();
}
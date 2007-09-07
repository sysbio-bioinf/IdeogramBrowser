package ideogram.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class MemoryCopyNumberModel extends AbstractCopyNumberDataModel
{
	private ArrayList<CopyNumberRecord>		records;
	private String fileName;
	
	public MemoryCopyNumberModel()
	{
		fileName = new String();
		records = new ArrayList<CopyNumberRecord>();
	}
	

	public CopyNumberRecord get(int j)
	{
		return records.get(j);
	}

	public LinkedList<String> getFileName()
	{
		LinkedList<String> l = new LinkedList<String>();
		l.add(fileName);
		return l;
	}


	public int size()
	{
		return records.size();
	}


	public ArrayList<CopyNumberRecord> getRecords()
	{
		return records;
	}


	public void setRecords(ArrayList<CopyNumberRecord> records)
	{
		this.records = records;
	}


	public void setFileName(String info)
	{
		this.fileName = info;
	}


	public Collection<CopyNumberRecord> toCollection()
	{
		return records;
	}

	public LinkedList<String> getHeader() {
		LinkedList<String> l = new LinkedList<String>();
		l.add("MemoryCopyNumberModel doesn't contain any header");
		return l;
	}


	public LinkedList<String> getChipType() {
		LinkedList<String> l = new LinkedList<String>();
		l.add("MemoryCopyNumberModel doesn't contain any ChipType");
		return l;
	}

}

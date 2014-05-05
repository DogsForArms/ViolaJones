import ij.IJ;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Vector;


public class HaarFeatureChain implements Serializable
{
	Vector<HaarFeature> haarFeatures;
	
	static String nameOfTemporaryFolder = "/features/";
	File tmpDirectory;

	public HaarFeatureChain()
	{
		String path =  new File("").getAbsolutePath();
		path += nameOfTemporaryFolder;
		System.out.println("haarfeaturechain path = " + path);
		this.tmpDirectory = new File(path);
		if (this.tmpDirectory.exists())
		{
			tmpDirectory.delete();	
		} else
		{
			tmpDirectory.mkdir();
		}
	}
	private String location()
	{
		String location = tmpDirectory.getAbsolutePath()+ "\\" +"features.ser";
		IJ.log("directory = " + location);
		return location;
	}
	public void save(Vector<HaarFeature> features)
	{
		
		try
		{
			OutputStream file = new FileOutputStream(location());
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try
			{
				output.writeObject(features);
			} finally
			{
				output.close();
			}
		} catch (IOException ex)
		{
			System.out.println("Cannot perform output." + ex);
		}
	}
	
	public Vector<HaarFeature> load()
	{
		Vector<HaarFeature> recoveredQuarks = null;
		try
		{
			InputStream file = new FileInputStream(location());
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			
			try
			{
				recoveredQuarks = (Vector<HaarFeature>)input.readObject();
			} finally
			{
				input.close();
			}
			
		} catch (ClassNotFoundException ex)
		{
			System.out.println( "Cannot perform input. Class not found " + ex);
		} catch (IOException ex)
		{
	    	System.out.println( "Cannot perform input " + ex);
	    }
		return recoveredQuarks;
	}
}

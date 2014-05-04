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
import java.util.Vector;





public class FeatureValue 
{
	static String nameOfTemporaryFolder = "/tmp";
	File tmpDirectory;
	int i = 0;
	
	private boolean tmpDirectoryWasSetup = false;
	public FeatureValue()
	{
		String path =  new File("").getAbsolutePath();
		path += nameOfTemporaryFolder;
		System.out.println("path = " + path);
		this.tmpDirectory = new File(path);
		if (tmpDirectory.exists())
		{
			tmpDirectoryWasSetup = true;
//			System.out.println("deleting tmp directory '" + tmpDirectory + "'");
//			tmpDirectory.delete();
		} else
		{
			tmpDirectory.mkdir();
		}

//		System.out.println("Writing vector [" + i + "] to file");
//		tmpDirectory.mkdir();
		
	}
	
	public boolean tmpDirectoryAlreadyExists()
	{
		return tmpDirectoryWasSetup;
	}
	
	public void add(Vector<Triple> testList)
	{
		try
		{
			OutputStream file = new FileOutputStream(streamLocationForArrayList(i));
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try
			{
				output.writeObject(testList);
			} finally
			{
				output.close();
			}
		} catch (IOException ex)
		{
			System.out.println("Cannot perform output." + ex);
		}
		i++;
	}
	
	public Vector<Triple> get(int index)
	{
		Vector<Triple> recoveredQuarks = null;
		try
		{
			InputStream file = new FileInputStream(streamLocationForArrayList(index));
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			
			try
			{
				recoveredQuarks = (Vector<Triple>)input.readObject();
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

	public String streamLocationForArrayList(int index)
	{
		String path = tmpDirectory.getAbsolutePath()+ "\\" +index+".ser";
		return path;
	}
	
}

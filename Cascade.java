
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ethan
 */
public class Cascade implements Serializable
{
    Vector<Vector<HaarFeature>> cascade = new Vector<Vector<HaarFeature>>();
    Vector<Float> levelThreshold = new Vector<Float>();
    
    public static void saveCascade(File theFile, Cascade cascade)
    {
    	try
		{
			OutputStream file = new FileOutputStream(theFile);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try
			{
				output.writeObject(cascade);
			} finally
			{
				output.close();
			}
		} catch (IOException ex)
		{
			System.out.println("Cannot perform output." + ex);
		}
    	
    	System.out.println("Maybe saved cascade " + theFile);
    }
    
    public static Cascade loadCascade(File theFile)
    {
        System.out.println("loadCascade incomplete");
        Cascade cascade = null;
        try
		{
			InputStream file = new FileInputStream(theFile);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			
			try
			{
				cascade = (Cascade)input.readObject();
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
        
        
        if (cascade != null)
        {
        	System.out.println("Loaded cascade " + theFile);
        } else
        {
        	System.out.println("failed to load cascade " + theFile);
        }
        	
        return cascade;
    }
}

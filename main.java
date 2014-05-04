
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.List;
import java.awt.image.BufferedImage;
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
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.io.*;

import javax.imageio.ImageIO;

import java.util.*;
import java.util.logging.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ethan
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        testAdaBoost();
    }
    
    private static void loadGifsAtDirectoryIntoVector(Vector<IntegralImage> images, File directory)
    {
        for (File file : directory.listFiles())
        {
            String fileName = file.getName();
            String extension = "";
            
            String firstTwoChars = fileName.substring(0, 2);
            
            int i = fileName.lastIndexOf('.');
            if (i > 0) 
            {
                extension = fileName.substring(i+1);
            }
            if (!firstTwoChars.equals("._") && extension.equals("gif"))
            {
            	
                BufferedImage img = null;
                try 
                {
//                	System.out.println("file = " + file);
                    img = ImageIO.read(file);
                    ImageProcessor imageProcessor = new ByteProcessor(img);
                    IntegralImage ii = new IntegralImage(imageProcessor);
                    images.add(ii);
                } catch (IOException e) 
                {}

            }
        }
    }
 
    
    private static void testAdaBoost()
    {
        Vector<IntegralImage> falseSet = new Vector<IntegralImage>();
        Vector<IntegralImage> trueSet = new Vector<IntegralImage>();
        {
            File noDirectory =  new File("C:/Users/BOOBIES/Desktop/trainingData/no");
            File yesDirectory = new File("C:/Users/BOOBIES/Desktop/trainingData/yes");
            
            
            System.out.println("no directory = " + noDirectory);
            System.out.println("yes directory = " + yesDirectory);
            loadGifsAtDirectoryIntoVector(falseSet, noDirectory);
            loadGifsAtDirectoryIntoVector(trueSet, yesDirectory);
        }
        System.out.println("Gifs loaded!\t\t\ttrue:" + trueSet.size() + " false:" + falseSet.size());
        
        ViolaJones vj = new ViolaJones();
        Vector<HaarFeature> allFeatures = vj.getHaarFeatures();
        System.out.println("Generated all features!\t\tcount:" + allFeatures.size());
        
        AdaBoost boost = new AdaBoost();
        Vector<HaarFeature> bestFeatures = boost.startTraining(trueSet, falseSet, allFeatures, 3);
        System.out.println("done learnin!! Wow!");
    }
    


    		 
}

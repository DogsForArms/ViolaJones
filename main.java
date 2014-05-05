
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
	
	private static class SlimFeature
	{
		public int featureNumber;
		public float alpha;
		
		public SlimFeature(int featureNumber, float alpha)
		{
			this.featureNumber = featureNumber;
			this.alpha = alpha;
		}
	}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        testAdaBoost(10);
//    	
//    	ViolaJones vj = new ViolaJones();
//    	Vector<HaarFeature> hf = vj.getHaarFeatures();
//    	int i = 0;
//    	for (HaarFeature aHf : hf)
//    	{
//    		if (i == 97108 || i == 109917 || i == 6877 || i ==137874 || i == 111513 || i == 81013 || i == 15607 || i == 134897 || i == 101997)
//    		{
//    			/*
//    			 *         slimFeatures.add(new SlimFeature(90112, 0.72876203f));
//        slimFeatures.add(new SlimFeature(6877, 0.6615829f));
//        slimFeatures.add(new SlimFeature(137874,  0.6527973f));
//        slimFeatures.add(new SlimFeature(111513, 0.57696515f));
//        slimFeatures.add(new SlimFeature(81013, 0.5661316f));
//        slimFeatures.add(new SlimFeature(15607,  0.549178f));
//        slimFeatures.add(new SlimFeature(134897, 0.5285298f));
//        slimFeatures.add(new SlimFeature(101997,  0.5084175f))
//    			 */
//    			System.out.println("Hf " + i + " x,y = " + aHf.x + ", " + aHf.y);
//    		}
//    		i++;
//    	}

    	
        
//        testStaticFeatureList();
    }
    
    private static void testStaticFeatureList()
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
        
        Vector<SlimFeature> slimFeatures = new Vector<SlimFeature>();
        slimFeatures.add(new SlimFeature(97108, 1.5248452f));
        slimFeatures.add(new SlimFeature(109917,  1.1500407f));
        slimFeatures.add(new SlimFeature(90112, 0.72876203f));
        slimFeatures.add(new SlimFeature(6877, 0.6615829f));
        slimFeatures.add(new SlimFeature(137874,  0.6527973f));
        slimFeatures.add(new SlimFeature(111513, 0.57696515f));
        slimFeatures.add(new SlimFeature(81013, 0.5661316f));
        slimFeatures.add(new SlimFeature(15607,  0.549178f));
        slimFeatures.add(new SlimFeature(134897, 0.5285298f));
        slimFeatures.add(new SlimFeature(101997,  0.5084175f));
        
        System.out.println("\ntest "+trueSet.size()+" positives!");
    	testTargetFeaturesAgainstImages(slimFeatures, trueSet);
    	
    	
        System.out.println("\ntest "+falseSet.size()+" negatives!");
    	testTargetFeaturesAgainstImages(slimFeatures, falseSet);
    }

    private static void testTargetFeaturesAgainstImages(Vector<SlimFeature> slimFeatures, Vector<IntegralImage> images)
    {
    	ViolaJones vj = new ViolaJones();
    	Vector<HaarFeature> allFeatures = vj.getHaarFeatures();
    	
    	
    	for (IntegralImage ii : images)
    	{
    		float weightSum = 0.0f;
        	float sum = 0.0f;
    		for (SlimFeature slimFeature : slimFeatures)
    		{
    			HaarFeature hf = allFeatures.get(slimFeature.featureNumber);
    			
    			float v = slimFeature.alpha*ii.evaluate(hf);
    			sum += v;
    			weightSum += slimFeature.alpha;
    		}
    		
    		boolean success = (sum >= weightSum*0.5f);
    		System.out.println("image " + ii.name + " is a " +  success + " : " + sum + " > " + weightSum*0.5f);
    	}
    	
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
                    ii.name = fileName;
                    images.add(ii);
                } catch (IOException e) 
                {}

            }
        }
    }
 
    
    private static void testAdaBoost(int features)
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
        Vector<HaarFeature> bestFeatures = boost.startTraining(trueSet, falseSet, allFeatures, features);
        System.out.println("done learnin!! Wow!");
    }
    


    		 
}

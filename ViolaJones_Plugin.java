
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.IJ;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.imageio.ImageIO;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author ethan
 */
public class ViolaJones_Plugin implements PlugInFilter
{
	private class SlimFeature
	{
		public int featureNumber;
		public float alpha;
		public float threshold;
		
		public SlimFeature(int featureNumber, float threshold, float alpha)
		{
			this.featureNumber = featureNumber;
			this.threshold = threshold;
			this.alpha = alpha;
		}
	}
	
    ImagePlus ip;
    public int setup(String string, ImagePlus ip)
    {
        this.ip = ip;
        return DOES_8G + NO_CHANGES;
    }
    
    public void run(ImageProcessor imageProcessor)
    {
        
        IntegralImage ii = new IntegralImage(imageProcessor);

        Vector<HaarFeature> strongClassifier = (new HaarFeatureChain()).load();
        ImageStack imageStack = new ImageStack(imageProcessor.getWidth(), imageProcessor.getHeight());
        
        for (HaarFeature weakClassifier : strongClassifier)
        {
        	ByteProcessor bp = imageProcessorForFeature(weakClassifier, imageProcessor);
        	imageStack.addSlice("weakClassifier ", bp);
        	
        }
        
        ImagePlus imagePlus = new ImagePlus("best features", imageStack);
        imagePlus.show();
        
        int w = imageProcessor.getWidth();
        int h = imageProcessor.getHeight();
        
        int faces = 0;
        
        for (int x = 0; x <= w-24 ; x++)
        {
        	for (int y = 0; y <= h-24; y++)
        	{
        		boolean success = isFaceInRegion(x,y,ii,strongClassifier);
        		IJ.log("evaluating x,y " + x +", " + y + " : " + success);
        		if (success)
        		{
        			
        			faces++;
        		}
        	}
        }
    	

        IJ.log("I found " + faces + " face/faces");
        
//        ImagePlus out = new ImagePlus("aniamted tif", imageStack);
//        out.show();
    }
    

    private boolean isFaceInRegion(int x,int y,IntegralImage ii, Vector<HaarFeature> strongClassifier)
    {
    	int sum = 0;
    	int weightSum = 0;
    	for (HaarFeature weakClassifier : strongClassifier)
    	{
    		int d = ii.evaluateAsClassifier(weakClassifier,x,y);
    		
    		sum += d*weakClassifier.weight;
    		weightSum += weakClassifier.weight;
    	}
    	
    	return (sum*2 >= weightSum);
    }
    
    
    private ByteProcessor imageProcessorForFeature(HaarFeature hf, ImageProcessor I)
    {
    	int h = I.getHeight();
    	int w = I.getWidth();
    	
    	int hfWidth = hf.width;
    	int hfHeight = hf.height;
    	int hfTargetWidth = hf.targetWidth;
    	int hfTargetHeight = hf.targetHeight;
    	int[] vals = hf.pixels;
    	int hfx = hf.x;
    	int hfy = hf.y;
    	
    	int hfXFactor = hfTargetWidth/hfWidth;
    	int hfYFactor = hfTargetHeight/hfHeight;
    	
    	
    	ByteProcessor i2 = (ByteProcessor)I.duplicate();
    	
    	
    	for (int x = hfx; x < hfx+hfTargetWidth; x++)
    	{
    		for (int y = hfy; y < hfy+hfTargetHeight; y++)
    		{
    			int littleX = ((x-hfx)/hfXFactor);
    			int littleY = ((y-hfy)/hfYFactor);
    			int val = vals[littleY*hfWidth+littleX];
    			if (val == 1)
    			{
    				val = 255;
    			} else
    			{
    				val = 0;
    			}
    			
    			i2.set(x, y, val);
    			
    			
    		}
    	}
    	

    	return i2;
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
}


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
		
		public SlimFeature(int featureNumber, float alpha)
		{
			this.featureNumber = featureNumber;
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
        
        ImageStack imageStack = new ImageStack(imageProcessor.getWidth(), imageProcessor.getHeight());
        
        Vector<SlimFeature> slimFeatures = new Vector<SlimFeature>();
        slimFeatures.add(new SlimFeature(40058, 1.6034545f));
        slimFeatures.add(new SlimFeature(16168,  1.580527f));
        slimFeatures.add(new SlimFeature(6779, 1.4456567f));
        slimFeatures.add(new SlimFeature(11172, 1.3725158f));
        slimFeatures.add(new SlimFeature(2048,  1.2100765f));
        
        ViolaJones vj = new ViolaJones();
        Vector<HaarFeature> hfs = vj.getHaarFeatures();
        IntegralImage ii = new IntegralImage(imageProcessor);
        int j = 0;
        Vector<Triple> triples = new Vector<Triple>();
//        for (HaarFeature haarFeature : hfs)
//        {
////        	IJ.log("evaluating feature# " + j );
//        	int val = ii.evaluate(haarFeature);
//        	triples.add(new Triple(j, val, true));
//        	j++;
//        }
//        Collections.sort(triples);
//        
//        for (int ok = triples.size()-1; ok >= 0; ok--)
//        {
//        	Triple triple = triples.get(ok);
//        	HaarFeature hf = hfs.get(triple.index);
//        	ImageProcessor processor = imageProcessorForFeature(hf, imageProcessor);
//        	String sliceLabel = triple.index + " : " + triple.value;
//        	imageStack.addSlice(sliceLabel, processor);
//        }
        
	        for (int i = 0; i < slimFeatures.size(); i++)
	        {
	        	SlimFeature sf = slimFeatures.get(i);
	        	ImageProcessor processor = imageProcessorForFeature(hfs.get(sf.featureNumber), imageProcessor);
	        	String sliceLabel = sf.featureNumber + " : " + sf.alpha;
	        	imageStack.addSlice(sliceLabel, processor);
	        }
	       
    		float weightSum = 0.0f;
        	float sum = 0.0f;
    		for (SlimFeature slimFeature : slimFeatures)
    		{
    			HaarFeature hf = hfs.get(slimFeature.featureNumber);
    			
    			float v = slimFeature.alpha*ii.evaluate(hf);
    			sum += v;
    			weightSum += slimFeature.alpha;
    		}
    		
    		boolean success = (sum >= weightSum*0.5f);
    		IJ.log("image " + ii.name + " is a " +  success + " : " + sum + " > " + weightSum*0.5f);
    	
        
        ImagePlus out = new ImagePlus("aniamted tif", imageStack);
        out.show();
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
//    private void testStaticFeatureList()
//    {
//    	
//    	Vector<IntegralImage> falseSet = new Vector<IntegralImage>();
//        Vector<IntegralImage> trueSet = new Vector<IntegralImage>();
//        {
//            File noDirectory =  new File("C:/Users/BOOBIES/Desktop/trainingData/no");
//            File yesDirectory = new File("C:/Users/BOOBIES/Desktop/trainingData/yes");
//            
//            
//            System.out.println("no directory = " + noDirectory);
//            System.out.println("yes directory = " + yesDirectory);
//            loadGifsAtDirectoryIntoVector(falseSet, noDirectory);
//            loadGifsAtDirectoryIntoVector(trueSet, yesDirectory);
//        }
//        
//
//        
//        
//    }
    
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

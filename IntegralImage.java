/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ij.process.ImageProcessor;
import ij.IJ;
/**
 * @author ethan
 */
public class IntegralImage 
{
    int[][] ii;
    String name;
    
    public IntegralImage(ImageProcessor ip)
    {
        //initialize ii in one pass
        int h = ip.getHeight();
        int w = ip.getWidth();
        ii = new int[h][w];
    
        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                ii[y][x] = ii(x, y-1) + ii(x-1, y) + ip.get(x, y) - ii(x-1, y-1);
            }
        }
    }
    
    private int ii(int x, int y)
    {
        if (x < 0 || y < 0)
        {
            return 0;
        }
        
        if ( x == 24)
        {
        	x--;
        }
        if (y == 24)
        {
        	y--;
        }
        return ii[y][x];
    }
    
    //where x1 > x0 and y1 > y0
    private int sumOfRectangle(int x0, int y0, int x1, int y1)
    {   
    	
//    	IJ.log("sumOfRectangle("+x0+","+y0+","+x1+","+y1+")");
       /*
        *      x0,y0        x1,y0
        *           
        *               
        *      x0,y1        x1,y1
        *
        */
        return ii(x0,y0) + ii(x1,y1) - (ii(x1,y0) + ii(x0,y1));
    }
    
    public int evaluateAsClassifier(HaarFeature feature, int x, int y)
    {
    	int value = this.evaluate(feature,x,y);
    	
    	return (value >= feature.threshold ? 1 : 0);
    }
    
    public int evaluateAsClassifier(HaarFeature feature)
    {
    	return evaluateAsClassifier(feature,0,0);
    }
    /*
     * Evaluate a HaarFeature on this image.
     */
    public int evaluate(HaarFeature feature, int xTrans, int yTrans)
    {
    	int accum = 0;
        
        int xOffset = feature.x + xTrans;
        int yOffset = feature.y + yTrans;
        
        int width = feature.width;
        int height = feature.height;
        int targetWidth = feature.targetWidth;
        int targetHeight = feature.targetHeight;
        int[] pixels = feature.pixels;
        
        {
            int widthFactor = targetWidth/width;
            int heightFactor = targetHeight/height;

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    int regionX = xOffset+x*widthFactor;
                    int regionY = yOffset+y*heightFactor;
                    int regionWidth = widthFactor;
                    int regionHeight = heightFactor;
                    int regionPixel = pixels[y*width+x];
                    
                    accum += (regionPixel == 0 ? -1 : 1) * sumOfRectangle(regionX, regionY, regionX+regionWidth, regionY+regionHeight);
                  
                }
            }
        }
        return Math.abs(accum);
    }
    
    public int evaluate(HaarFeature feature)
    {
        return evaluate(feature,0,0);
    }
    
//    public HaarRectangle[] getRectangularRegionsForRegionWithTargetDimensions(int targetWidth, int targetHeight)
//    {
//        HaarRectangle[] haarRegions = new HaarRectangle[pixels.length];
//        
//        int widthFactor = targetWidth/width;
//        int heightFactor = targetHeight/height;
//        
//        int index = 0;
//        for (int y = 0; y < height; y++)
//        {
//            for (int x = 0; x < width; x++)
//            {
//                int regionX = x*widthFactor;
//                int regionY = y*heightFactor;
//                int regionWidth = widthFactor;
//                int regionHeight = heightFactor;
//                int regionPixel = get(x,y);
//                haarRegions[index] = new HaarRectangle(regionX, regionY, regionWidth, regionHeight, regionPixel);
//                index++;
//            }
//        }
//        
//        return haarRegions;
//    }
    
    
    
}

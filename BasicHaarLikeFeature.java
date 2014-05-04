
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * The basic pattern for the Haar-like feature.  This class can be used to retrieve 
 * the feature regions.
 * @author ethan
 */


public class BasicHaarLikeFeature 
{
    private int width, height;
    private int[] pixels;
    
    public BasicHaarLikeFeature(int width, int height, int[] pixels)
    {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
        
    }
    
    public int[] getPixels()
    {
        return pixels;
    }
            
    
    public int getSizeX()
    {
        return width;
    }
    
    public int getSizeY()
    {
        return height;
    }
    
    public int get(int x, int y)
    {
        return pixels[y*width+x];
    }
    
    /*
     *  important to note that targetWidth  divides getSizeX && 
     *                         targetHeight divides getSizeY
     */
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

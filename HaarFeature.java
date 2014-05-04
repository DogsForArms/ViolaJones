import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ethan
 */
public class HaarFeature implements Serializable
{
    int x, y, width, height, targetWidth, targetHeight;
    int[] pixels;
    
    
    //to be set at the learning stage
    float threshold;
    float weight;
    
    HaarFeature(int x, int y, int width, int height, int targetWidth, int targetHeight, int[] pixels)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.pixels = pixels;
    }   
}

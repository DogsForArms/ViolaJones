
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.IJ;
import java.awt.Rectangle;
import java.util.List;

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
    ImagePlus ip;
    public int setup(String string, ImagePlus ip)
    {
        this.ip = ip;
        return DOES_8G;
    }
    
    public void run(ImageProcessor imageProcessor)
    {
        IJ.log("hello world!");
        
        
        
//        ViolaJones vj = new ViolaJones();//standard detect faces cascade
//        
//        vj.detectObjects(imageProcessor);
        
//        List<Rectangle> rects= vj.detectObjects(ip);
        
//        for (int i = 0; i < rects.size(); i++)
//        {
//            IJ.log("rect " + i + " = " + rects.get(i));
//        }
    }
}

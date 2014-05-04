/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * A rectangular region origin is the upper left hand corner.
 * This is a binary region where 0 (white) is -1 and 1 (black) is +1
 * @author ethan
 */
public class HaarRectangle 
{
    int x;
    int y;
    int width;
    int height;
    
    int value;
    
    public HaarRectangle(int x, int y, int width, int height, int value)
    {
        this.x = x; 
        this.y = y;
        this.width = width;
        this.height = height;
        this.value = value;
    }
    
    public HaarRectangle()
    {}
}


import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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


public class ViolaJones 
{
    
	Vector<IntegralImage> positiveTrain;  // set (integralnih) slika za treniranje koje su pozitivne
	Vector<IntegralImage> negativeTrain;  // set (integralnih) slika za treniranje koje su negativne
	
	Vector<IntegralImage> positiveTest;  // set (integralnih) slika za testiranje koje su pozitivne
	Vector<IntegralImage> negativeTest;  // set (integralnih) slika za testiranje koje su negativne
    
    Cascade cascade = new Cascade();
    
    public ViolaJones()
    {}
    /* constructor */
    public ViolaJones(Vector<IntegralImage> ptr, Vector<IntegralImage>ntr, Vector<IntegralImage>pte, Vector<IntegralImage> nte)
    {
        positiveTrain = ptr;
        negativeTrain = ntr;
        
        positiveTest = pte;
        negativeTest = nte;
        
        
//        System.out.println("done with rediculous task positiveTrain.size = " + positiveTrain.size() + " negativetrain.size = " + negativeTrain.size());
//        
//        Vector<HaarFeature> featureVector = getHaarFeatures();
//        
//        System.out.println("featureVector.count = " + featureVector.size());
    }   
    /* learning phase */
    private void buildCascade(double f, double d, double targetF, Cascade cascade)
    {
        Vector<IntegralImage> N = (Vector<IntegralImage>)negativeTrain.clone();
        Vector<IntegralImage> P = (Vector<IntegralImage>)positiveTrain.clone();
        
        double tmpF=1.0;  // false positive rate
		double lastF=1.0;  // last false positive rate
		double tmpD=1.0;  // detection rate
		double lastD=1.0;  // last detection rate
		
		int i = 0;  // i - level caskade
		int n;  // broj featureova u trenutnom levelu kaskade
		
		Pair<Double,Double> tmpRet;  // pomocna varijabla
        
        AdaBoost adaBoost = new AdaBoost();
	
        while(tmpF>targetF) 
        {
			i++;
			n=0;
			tmpF=lastF;
			
			while(tmpF > f*lastF) 
            {
				n++;
                Vector<HaarFeature> cascadei = cascade.cascade.get(i);
				//adaBoostTrain(P,N,n,kaskada);  // trenira level kaskade s n featureova
				adaBoost.startTraining(P, N, cascadei, n);
				
				// Evaluate current cascaded classifier on validation set to determine tmpF and tmpD:
				tmpRet = evaluateOnTest(cascade);
				tmpF = tmpRet.first;
				tmpD = tmpRet.second;
				
				/*
				 * Decrease threshold for the i-th classifier until the current cascaded classifier
				 * has a detection rate of at least  (this also affects tmpF ):
				 */
				decreaseThreshold(i, d*lastD, cascade);
            }
			//by here we are done with N as being the negative train
			N.clear();
			
			/*
			 * If tempF > targetF then evaluate the current cascaded detector on the set of non-face images
			 * and put any false detections into the set N:
			 */
			if(tmpF > targetF)
                    evaluateOnTrainNegative(N, cascade);
        }   
    }
    
    /* Evaluate current cascaded classifier on validation set to determine tmpF and tmpD */
    private Pair<Double,Double> evaluateOnTest(Cascade cascade) 
    {
            int errP=0, errN=0;  // broj gresaka na P i na N

            for(int i=0;i<positiveTest.size();i++)
                    if(!evaluate(positiveTest.get(i),cascade)) 
                        errP++;

            for(int i = 0; i < negativeTest.size(); i++)
                    if(evaluate(negativeTest.get(i), cascade)) 
                        errN++;


            return new Pair<Double,Double>( errN/(double)positiveTest.size(), 
                                            errP/(double)negativeTest.size() );
    }
    
        
    public boolean evaluate(IntegralImage iim, Cascade cascade) 
    {
		double sum=0;
		for(int k, j, i = 0; i < cascade.cascade.size(); i++) 
        {
            sum=0;  
		
            for(j=0;j < cascade.cascade.get(i).size();j++) 
            {
                HaarFeature hf = cascade.cascade.get(i).get(j);
            	sum += iim.evaluate(hf) * hf.weight;
            }
		
            if(sum < cascade.levelThreshold.get(i))
                return false;  
        }
		return true;
    }
    
    
/*
 * evaluate the current cascaded detector on the set of non-face images
 * and put any false detections into the set N
 */
    private void evaluateOnTrainNegative(Vector<IntegralImage> N, Cascade cascade)
    {
        for (int i = 0; i < negativeTrain.size(); i++)
        {
            IntegralImage ii = negativeTrain.get(i);
            if (evaluate(ii, cascade))
            {
                N.add(ii);
            }
        }
    }
    
    /*
     * Decrease threshold for the ith classifier until the current cascaded classifier
     * has a detection rate of at least minD (this also affects tmpF )
     */
    private void decreaseThreshold(int ith, double minD, Cascade cascade) 
    {
		int errP;  
		double tmpD;  
		
		int down=0,mid,up=100100;  
		
		while(down <= up) 
        {
            //cascade.levelThreshold[ith]
            
            mid = (down+up)/2;
            cascade.levelThreshold.set(ith, (float)mid);
		
            errP = 0;
            for(int i = 0; i < positiveTest.size(); i++)
            {
            	if( !evaluate(positiveTest.get(i), cascade) ) 
                {
                    errP++;
                }
            }
            tmpD = errP/(double)negativeTest.size();
		
            if(tmpD < minD+1e-9) 
                up = mid-1;
            else 
                down = mid;
	}
    }
    

    
    /*
     * PRIVATE
     * returns a list of about 160k features of standard haar extractors 
     */
    public Vector<HaarFeature> getHaarFeatures()
    {
        int i, x, y, sizeX, sizeY, width, height;//, count, c;
        Vector<HaarFeature> featureVector = new Vector<HaarFeature>();
        int count = 0;
        
        
        BasicHaarLikeFeature[] feature = getBasicHaarFeatures();
        int features = feature.length;
        int frameSize = 24;
        
        BasicHaarLikeFeature currentFeature = null;
        /* Each shape */
        for (i = 0; i < features; i++) 
        {
            currentFeature = feature[i];
            
            sizeX = currentFeature.getSizeX();
            sizeY = currentFeature.getSizeY();

            /* each size (multiples of basic shapes) */
            for (width = sizeX; width < frameSize; width+=sizeX) 
            {
                for (height = sizeY; height < frameSize; height+=sizeY) 
                {
                    /* each possible position given size */
                	//UNDO THIS AFTER TESTS
                    for (x = 0; x+width < frameSize; x++) 
                    {
                        for (y = 0; y+height < frameSize; y++) 
                        {
                            //int result <- integralImage.evaluate(haarFeature) 
                            count++;
                            HaarFeature hr = new HaarFeature(x,y,sizeX,sizeY,width,height, currentFeature.getPixels());
                            featureVector.add(hr);
                        
                        
                        }
                    }
                }
            }
        }
        
        return featureVector;
    }
    
    /*
    * PRIVATE
    * helper method to return the basic types of haarFeaturs, later to be looped over in appropriat region
    */
    private BasicHaarLikeFeature[] getBasicHaarFeatures()
    {
        BasicHaarLikeFeature[] feature = new BasicHaarLikeFeature[5];
        int[] train = {0,1};
        feature[0] = new BasicHaarLikeFeature(2, 1, train);
        feature[1] = new BasicHaarLikeFeature(1,2, train);
        int[] train2 = {0,1,0};
        feature[2] = new BasicHaarLikeFeature(3, 1, train2);
        feature[3] = new BasicHaarLikeFeature(1, 3, train2);
        int[] train3 = {0,1,0,1};
        feature[4] = new BasicHaarLikeFeature(2, 2, train3);
        return feature;
    }



}

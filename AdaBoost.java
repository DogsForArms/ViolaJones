
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Learnin' class
 * @author ethan
 */
public class AdaBoost 
{ 
    
    
/* 
* @arguments: 
*		vector<Image> positive - a reference to the vector that contains all positive IntegralImages
*		vector<Image> negative - a reference to the vector that contains all negative IntegralImages
*		vector<BaseFeature> features - a reference to the vector that contains the feature-E method that tries to teach
*		int T - expected number of minimal error features
* @return:
*		returns T learned feature weights
*/
    public Vector<HaarFeature> startTraining(Vector<IntegralImage> P, Vector<IntegralImage> N, Vector<HaarFeature> features, int T)
    {
        Vector<Float> weightsPositive = new Vector<Float>(P.size());
        Vector<Float> weightsNegative = new Vector<Float>(N.size());
        Vector<HaarFeature> solution = new Vector<HaarFeature>(T);
        
        //initialize them at 1/(p.size*2) and  1/(n.size*2)
        for (int i = 0; i < P.size(); i++)
        {
            float v = 1/(P.size()*2.0f);
            weightsPositive.add(v);
        }
        for (int i = 0; i < N.size(); i++)
        {
            float v = 1/(N.size()*2.0f);
            weightsNegative.add(v);
        }
        
        FeatureValue featureValue = new FeatureValue(); // 160,000 | P.size+N.size | {int index, float threshold, boolean t/f}
        
        if (!featureValue.tmpDirectoryAlreadyExists())
        {
        	int lastHalfPercent = 0;
        	System.out.println("\n" + features.size()+ " features X " + (P.size()+N.size()) + " photo set");
        	System.out.print("\t|");
        	for (int lol = 0; lol < 50-3; lol++)
        	{
        		System.out.print(" ");
        	}
        	System.out.print("|\n\t");
            
        	//maybe I want to delete the last one in the case that this is a new dataset, remember to delete tmp
	        for (int i = 0; i < features.size(); i++)
	        {
	            Vector<Triple> tmp = new Vector<Triple>();
	            for (int j = 0; j < P.size(); j++)
	            {
	                int val = P.get(j).evaluate(features.get(i));
	                tmp.add(new Triple(j,val,true));
	            }
	
	            
	            for (int j = 0; j < N.size(); j++)
	            {
	                int val = N.get(j).evaluate(features.get(i));
	                tmp.add(new Triple(j,val,false));
	            }
	            List list = tmp;
	            //"sort( featureValue[i].begin(), featureValue[i].end() );"
	            Collections.sort(list);
	            featureValue.add(tmp);
	            
	            int halfPercent = 50*i/features.size();
	            while (halfPercent > lastHalfPercent)
	            {
	            	System.out.print("*");
	            	lastHalfPercent++;
	            }
	        }
        }
        
        System.out.println("\ndone with enormous setup");
        
        for (int i = 0; i < T; i++)
        {
            normalizeWeights(weightsPositive, weightsNegative);
            
            int bestFeature = -1, p = 0;
            float error = Float.MAX_VALUE;
            float threshold = 0;
            
            //select the best classifier!
            
            for (int j = 0; j < features.size(); j++)
            {
            	// (first, second) -> (+,-)
                Pair<Float, Float> total = new Pair<Float,Float>(sumWeight(weightsPositive), sumWeight(weightsNegative));
                Pair<Float, Float> curr = new Pair<Float,Float>(0.0f,0.0f); 
                
                Vector<Triple> listOfTriples = featureValue.get(j);
                for (int k = 0; k < listOfTriples.size(); k++)
                {
                    Triple triple = listOfTriples.get(k);
                    int index = triple.index;
                    float value = triple.value;
                    
                    

                    	     // 0 + (400 - 0)
                    float v1 = curr.first + (total.second - curr.second);
                    	     // 0 + (400 - 0)
                    float v2 = curr.second + (total.first - curr.first);
                    
                    float currError = Math.min(v1, v2);
                    
                    if (triple.positive)
                        curr.first += weightsPositive.get(index);
                    else
                        curr.second += weightsNegative.get(index);
                    
                    
                    //update best feature, has lowest error!
                    if (currError < error)
                    {
                        if (k == 0)
                            threshold = value;
                        else 
                            threshold = (listOfTriples.get(k-1).value + value)/2;
                        
                        
                        if (v1 < v2) 
                            p = -1;
                        else 
                            p = 1;

						error = (float)currError;
						bestFeature = j;
                    }
                } //end k loop, k < features[j].Size(), list of Triples
                //maybe resetJth vector?
                
            } //end j loop j < features.Size()
            
                    //update the weights
			
			float beta = error / (1 - error);	
			float alpha = (float) Math.log( 1 / beta );	
			System.out.println();
			System.out.println("Current error at this step: " + error );
			System.out.println("Current threshold at this step: " + threshold);
			System.out.println("Current alpha at this step: " + alpha);
			System.out.println("best feature = " + bestFeature);
			
            
				Vector<Triple> bestFeatureVector = featureValue.get(bestFeature);
				//boost the negatives, based on determined threshold
                for(int k=0; k < bestFeatureVector.size(); k++) 
                {
					Triple triple = bestFeatureVector.get(k);
					float val = triple.value;
					int index = triple.index;
					boolean positive = triple.positive;
			
					//if classified correctly, multiply by beta
					if (positive == ( p * val < p * threshold )) 
                    {
						if (positive == true) 
                        	weightsPositive.set(index, weightsPositive.get(index)*beta);
						else				 
                        	weightsNegative.set(index, weightsNegative.get(index)*beta);
                    }		
                }
                HaarFeature theBestFeature = features.get(bestFeature);
                theBestFeature.threshold = threshold;
                theBestFeature.weight = alpha;
                solution.add(theBestFeature);

        }//end i loop (i < T)
        

        return solution;
    }
    
    //normalize the weights 
    private void normalizeWeights(Vector<Float> positive, Vector<Float> negative)
    {
        float sum = 0;
        for (int i = 0 ; i < positive.size(); i++)
            sum += positive.get(i);
        
        for (int i = 0; i < negative.size(); i++)
            sum += negative.get(i);
    
        for (int i = 0; i < positive.size(); i++)
            positive.set(i, positive.get(i)/sum);
        
        for (int i = 0; i < negative.size(); i++)
            negative.set(i, negative.get(i)/sum);
    }
    
    //sum the weights of a vector of weights
    private float sumWeight( Vector< Float > weight) 
    {
		float rj = 0;
		for(int i=0; i<weight.size(); i++) 
	            rj += weight.get(i);
		return rj;
    }
    
    //prints a vector of floats.
    private void printVector(Vector<Float> v)
    {
    	for(int i=0; i<v.size(); i++) 
        {
            System.out.print(v.get(i));
        }
        System.out.print('\n');
    }
}

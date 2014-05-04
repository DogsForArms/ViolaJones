import java.io.Serializable;



public class Triple implements Comparable<Triple>, Serializable
    {
        public int index;
        public Integer value;
        public boolean positive;
        Triple(int index, int value, boolean positive)
        {
            this.index = index;
            this.value = value;
            this.positive = positive;
        }

        @Override
        public int compareTo(Triple o) 
        {
            return value.compareTo(o.value);
        }
        
        @Override
        public String toString()
        {
        	return "Triple: {" + index + ", " + value + ", " + positive + " }";
        }
        
        
    }
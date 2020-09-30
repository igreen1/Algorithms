package csp;

//mostly copied honestly
//super simple to hold two integers for arc in queue
public class Arc { 
    
    public final int l; 
    public final int r; 
    public final String op;
    
    public Arc(int left, int right, String op) { 
        this.l= left; 
        this.r = right; 
        this.op = op;
    }  
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null) return false;
        
        if (!Arc.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        
        Arc other = (Arc)obj;
        
        if(this.l == other.l && this.r == other.r && this.op == other.op)
        {
            return true;
        }
        return false;
    }
}

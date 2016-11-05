package apps;

import java.io.*;
import java.util.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    	 scalars= new ArrayList <ScalarSymbol> ();
    	 arrays= new ArrayList <ArraySymbol>();
    	for(int a=0; a<expr.length();a++){
    		int b =a;
    	   		if(Character.isLetter(expr.charAt(a))){
    	   			b=a+1;
    						for(;b<expr.length();b++){
    							if(expr.charAt(b)=='['){ //array
    								ArraySymbol add = new ArraySymbol(expr.substring(a,b));
    								if(arrays.contains(add)){
    									break;
    								}
    								//System.out.println(add);  test
    								arrays.add(add);
    								break;
    								}
    							else if(!Character.isLetter(expr.charAt(b))){ //scalars
    								ScalarSymbol add = new ScalarSymbol(expr.substring(a,b));
    								if(scalars.contains(add)){
    									break;
    								}
    								//System.out.println(add);  test
    								scalars.add(add);
    								break;
    							}
    							
    							}
    						if(b==expr.length()){ //scalars
    							ScalarSymbol add = new ScalarSymbol(expr.substring(a,b));
    							if(scalars.contains(add)){
									break;
								}
								//System.out.println(add); test
								scalars.add(add);
    						}
    						
    							}

    	   		a=b;
    	   		}
    	
    	
    	}

    
    
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    	
    	String s = this.eraseWhitespaces(expr);
    	// System.out.println(s); test
    	return this.calc(s);
	}
    
    
    
    private float calc(String s){
    	if(s.equalsIgnoreCase("")) return 0;
    	
    	if(s.charAt(0)=='(')
    	{
    		int i = this.findIndex(s, 0);
    		if(i == s.length()-1){
    			s = s.substring(1, s.length()-1);
    		}
    	}
    	
    	int m;
    	if((m= s.indexOf("["))>=0)
    	{
    		int n ;
    		n = this.findIndex(s, m);
    		
    		String before = s.substring(0,m);
    		String after = s.substring(n+1);

    		if(!this.containArith(before) && !this.containArith(after)){
    			float index = this.calc(s.substring(m+1,n));
				for(ArraySymbol a: this.arrays){
					if(a.name.equalsIgnoreCase(s.substring(0,m))){
						return a.values[(int)index];
					}
				}
    		}
    	}
    	
    	if(!this.containArith(s)){
    		if(Character.isDigit(s.charAt(0))){
    			return Integer.parseInt(s);
    		}else{
    			if(s.contains("[")){
    				int i = s.indexOf("[");
    				int j = s.indexOf("]"); 
    				float index = this.calc(s.substring(i+1,j));
    				for(ArraySymbol a: this.arrays){
    					if(a.name.equalsIgnoreCase(s.substring(0,i))) return a.values[(int)index];
    				}
    			}else{
    				for(ScalarSymbol sc: this.scalars){
    					if(sc.name.equalsIgnoreCase(s)) return sc.value;
    				}
    			}
    		}
    	}
    	
    	
    	for(int i =0;i<s.length();i++){
    		i = this.findIndex(s, i);
    		if(s.charAt(i)=='+')
    		{
    			float first = this.calc(s.substring(0,i));
    			float second = this.calc(s.substring(i+1));
    			return first+second;
    		}
    		
    	}
    	
    	for(int i =0;i<s.length();i++){
    		i = this.findIndex(s, i);
    		if(s.charAt(i)=='-'){

    			float first = this.calc(s.substring(0,i));
    			float second = this.calc(s.substring(i+1));
    			return first - second;
    		}
    	}
    	
    	for(int i =0;i<s.length();i++){
    		i = this.findIndex(s, i);
    		if(s.charAt(i)=='*')
    		{	
    			float first = this.calc(s.substring(0,i));
    			float second = this.calc(s.substring(i+1));
    			return first*second;
    		}
    		
    	}
    	
    	for(int i =0;i<s.length();i++){
    		i = this.findIndex(s, i);
    		
    		if(s.charAt(i)=='/'){
    			float first = this.calc(s.substring(0,i));
    			float second = this.calc(s.substring(i+1));
    			return first / second;
    		}
    	}
    	return -1000000000;
    }
    
    
    private boolean containArith(String s)
    {
    	if(s.contains("+")||s.contains("-")||s.contains("*")||s.contains("/")) return true;
    	return false;
    }
    
    private int findIndex(String s, int i){
    	if(s.charAt(i)=='(' )
		{
    		int ct = 0;
			int j=i;
			for(;j<s.length();j++)
			{
				if(s.charAt(j)=='('){
					ct++;
					continue;
				}
				if(s.charAt(j)==')'){
					ct--;
					if(ct == 0) break;
				}
			}
			return j;
		}
    	else if(s.charAt(i)=='['){
    		int ct = 0;
			int j=i;
			for(;j<s.length();j++)
			{
				
				if(s.charAt(j)=='['){
					ct++;
					continue;
				}
				if(s.charAt(j)==']'){
					ct--;
					if(ct == 0) break;
				}
			}

			return j;
    	}
    	return i;
    }
    
    
    
    private String eraseWhitespaces(String s){
    	s = s.trim();
    	for(int i = 0; i<s.length();i++){
    		if(s.charAt(i)==' ')
    		{
    			s = s.substring(0,i)+s.substring(i+1);
    			i--;
    		}
    	}
    	return s;
    }
    
    
    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class LargeNumberArithmetic {
	
	private static final HashMap<String, LargeInteger> symbolTable = new HashMap<>();
	
	/**
	 * Check whether tokens are alphabets or numbers
	 * @param c -  character to be validated
	 * @return - True if valid else false
	 */
	private static boolean validAtom(final char c){
		if(c >= 'a' && c <= 'z'){
			return true;
		}
		else if(c >= '0' && c <= '9'){
			return true;
		}
		return false;
	}
	
	/**
	 * Separate tokens from each command
	 * @param expression - expression to be parsed
	 * @return - List of tokens in the expression
	 */
	private static List<String> getTokens(final String expression){
		final List<String> tokens = new LinkedList<String>();
		StringBuffer token = new StringBuffer();
		for(int i=0;i< expression.length();i++){
			final char val = expression.charAt(i);
			if(validAtom(val)){ // Validate each character
				token.append(val);
			}
			else{
				if(token.length() > 0){ //Add token to the list of tokens
					tokens.add(token.toString());
					token = new StringBuffer();
				}
				tokens.add(""+val); // Add operators to the list of tokens
			}
		}
		if(token.length() > 0){
			tokens.add(token.toString());
		}
		return tokens;
	}
	
	/**
	 * Precedence level of the operators
	 * @param op - operator symbol
	 * @return - Returns Precedence level 
	 */
	private static int precedenceScore(String op){
		switch(op){
			case "+":
			case "-":
				return 0;
			case "*":
			case "/":
			case "%":
				return 1;
			case "^":
				return 2;
			case "!":
			case "~":
				return 3;
			default:
				return -1;
		}
	}
	
	/**
	 * Parse expression using Shunting-yard algorithm
	 * @param expression - expression to be parsed
	 * @return - Returns parsed expression
	 */
	private static String parseExpression(final String expression){
		final StringBuffer parsedExpression = new StringBuffer();
		final ArrayDeque<String> operators = new ArrayDeque<String>();  
		final List<String> tokens = getTokens(expression);
		for(final String token:tokens){
			if(token.matches("[a-z]") || token.matches("[\\d]+")){ // If variable or number
				parsedExpression.append(token+",");
			}
			else if(token.equals(")")){ // If closing parenthesis
				while(!"(".equals(operators.peek())){
					parsedExpression.append(operators.pop()+",");
				}
				operators.pop();
			}
			else{
				if(operators.isEmpty() || token.equals("(")){
					operators.push(token);
				}
				else{
					int op1Pred = precedenceScore(token);
					int op2Pred = precedenceScore(operators.peek());
					if(op1Pred > op2Pred){ // Lesser precedence operator on top
						operators.push(token);
					}
					else if((op1Pred == op2Pred) && "^".equals(token)){ // If power operation
						operators.push(token);
					}
					else{ // If Higher operator on top 
						parsedExpression.append(operators.pop()+",");
						operators.push(token);
					}
				}
			}
		}
		while(!operators.isEmpty()){
			parsedExpression.append(operators.pop()+",");
		}
		if(parsedExpression.length() > 1){
			parsedExpression.deleteCharAt(parsedExpression.length()-1);
		}
		return parsedExpression.toString();
	}
	
	/**
	 * Execute the parsed expression
	 * @param parsedExpression - parsed expression to be executed
	 * @return - Returns the result of the expression
	 */
	private static LargeInteger executeExpression(final String parsedExpression){
		String[] tokens = parsedExpression.split(",");
		final ArrayDeque<LargeInteger> variables = new ArrayDeque<>();
		for(int i =0; i< tokens.length; i++){
			if(tokens[i].matches("[\\d]+|[a-z]")){ // If number or variable
				if(symbolTable.containsKey(tokens[i])){
					variables.push(symbolTable.get(tokens[i]));
				}
				else{
					variables.push(new LargeInteger(tokens[i]));
				}				
			}
			else if(tokens[i].matches("[+-/*%\\^]")){ // If token is a binary operator
				final LargeInteger num2 = variables.pop();
				final LargeInteger num1 = variables.pop();				
				variables.push(LargeInteger.callBinaryOperator(tokens[i], num1, num2));
			}
			else{ // If token is a unary operator
				final LargeInteger num = variables.pop(); 
				variables.push(LargeInteger.callUnaryOperator(tokens[i], num));
			}
		}
		return variables.pop();
	}

	public static void main(String[] args) throws FileNotFoundException,IllegalArgumentException {		
		final String[] statements = new String[1002];
		int linenum = 0;
	    String cmd;
	    Scanner in;
	    int base = 10;
	    if(args.length > 1){
	    	in = new Scanner(new File(args[0]));
	    	base = Integer.parseInt(args[1]);
	    }	    
	    else if(args.length > 0){
	    	in = new Scanner(new File(args[0]));
	    }
	    else{
	    	throw new IllegalArgumentException("Not enough number of arguments passed"
					+ "\nUsage: LargeNumberArithmetic <input file> [optional:base 2-10000]\n"
					+ "Example: LargeNumberArithmetic input.txt 16");		
	    }
	    while(in.hasNext()) {
			linenum = in.nextInt();
			cmd = in.next();
			statements[linenum] = cmd;			
	    }
	    linenum++;
	    statements[linenum] = "END";
	    LargeInteger.base = base;
	    int curstatement = 1;
	    while(!"END".equals(statements[curstatement])){
	    	//If an assignment statement
	    	if(statements[curstatement].matches("^[a-z][=][\\d]+$")){
	    		String[] tokens = statements[curstatement].split("=");
	    		// Populate symbol table
	    		symbolTable.put(tokens[0], new LargeInteger(tokens[1]));
	    	}
	    	//If a goto statement
	    	else if(statements[curstatement].matches("^[a-z][?](.)*$")){
	    		String[] tokens = statements[curstatement].split("[?:]");
	    		if(symbolTable.containsKey(tokens[0])){
	    			int notZero = Integer.parseInt(tokens[1]);
	    			int zero = curstatement;
	    			if(tokens.length > 2){
	    				zero = Integer.parseInt(tokens[2]);
	    			}
	    			if(symbolTable.get(tokens[0]).checkZero()){
	    				curstatement = zero;	    				
	    			}
	    			else{
	    				curstatement = notZero-1;
	    			}
	    		}
	    	}
	    	// If a print list statement
	    	else if(statements[curstatement].matches("^[a-z][)]$")){
	    		if(symbolTable.containsKey(""+statements[curstatement].charAt(0))){
	    			symbolTable.get(""+statements[curstatement].charAt(0)).printList();
	    		}
	    	}
	    	// If a variable output statement
	    	else if(statements[curstatement].matches("^[a-z]$")){
	    		if(symbolTable.containsKey(statements[curstatement])){
	    			System.out.println(symbolTable.get(statements[curstatement]));
	    		}
	    		else{
	    			System.out.println(statements[curstatement]+" varibale not defined");
	    		}
	    	}
	    	else{ // If an expression statement
	    		String[] tokens = statements[curstatement].split("=");
	    		final String parsedExpression = parseExpression(tokens[1]);
	    		final LargeInteger result = executeExpression(parsedExpression);
	    		symbolTable.put(tokens[0], result);
	    	}
	    	curstatement++;
	    }
	    in.close();
	}

}

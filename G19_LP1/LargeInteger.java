import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class LargeInteger implements Comparator<LargeInteger>{

	public static int base = 21;
	public static final LargeInteger ZERO = new LargeInteger("0");
	public static final LargeInteger ONE = new LargeInteger("1");
	public static final LargeInteger TWO = new LargeInteger("2");
	private static final int ioBase = 10;	

	LinkedList<Integer> value;	
	boolean negative;		

	/**
	 * Default constructor to initialise the list 
	 */
	public LargeInteger() {
		value = new LinkedList<Integer>();
		negative = false;
	}

	/**
	 * Initialise the list by converting the decimal value in the string to 
	 * storage base value 
	 * @param s - Decimal value to be stored in the list
	 */
	public LargeInteger(final String s) {
		value  = new LinkedList<Integer>();
		if(s.charAt(0) == '-'){
			negative = true;
			convert(ioBase,base,s.substring(1));			
		}
		else{
			negative = false;
			convert(ioBase,base,s);
		}		
	}

	/**
	 * Initialise the list by converting the decimal value in the long format to 
	 * storage base value 
	 * @param num
	 */
	public LargeInteger(Long num) {				
		this(String.valueOf(num));
	}

	/**
	 * Convert number in String format from one base to another
	 * @param from - Source base
	 * @param to - Destination base
	 * @param s - Number value
	 */
	private void convert(int from, int to, String s)
	{
		//Return error if input is empty
		if (s == null || "".equals(s) || "0".equals(s))
		{
			value.add(0);
			return;
		}

		//Invariants: il length/digits in input base
		//ol number of digits in output base
		//ts accumulation array for the unit positions
		//cums the result array to store the actual converted digits
		
		int il = s.length();		
		int ol = il * (from / to+1);
		int[] ts = new int[ol+10]; 
		int[] cums = new int[ol+10];
		ts[0] = 1;

		for (int i = s.length()-1; i >=0 ; i--) //for each input digit
		{
			//add the input digit
			// times (base:to from^i) to the output accumulator
			for (int j = 0; j < ol; j++)  
			{
				cums[j] += ts[j] * (s.charAt(i) - '0');
				int temp = cums[j];	            
				int rem = 0;
				int ip = j;
				do // fix up any remainders in base:to
				{
					rem = temp / to;
					cums[ip] = temp-rem*to; 
					ip++;
					cums[ip] += rem;
					temp = cums[ip];
				}
				while (temp >=to);

			}

			//calculate the next power from^i in base:to format
			for (int j = 0; j < ol; j++)
			{
				ts[j] = ts[j] * from;
			} 
			for(int j=0;j<ol;j++) //check for any remainders
			{
				int temp = ts[j];
				int rem = 0;
				int ip = j;
				do
				{
					rem = temp / to;
					ts[ip] = temp - rem * to; 
					ip++;
					ts[ip] += rem;
					temp = ts[ip];
				}
				while (temp >= to);
			}
		}

		boolean leadingDigitFound = false; 
		for(int i = ol; i >=0; i--){	  
			if(cums[i] != 0){ //Skip the Leading zeros
				leadingDigitFound = true;
			}
			if(!leadingDigitFound){
				continue;
			}
			value.addFirst(cums[i]);
		}	    
	}

	/**
	 * Convert Large Integer from any base to decimal 
	 * @param digits - Digits in the Large Integer staring from LST
	 * @return - Decimal value of the Large Integer as a String
	 */
	private String convertToDecimal(final Integer[] digits){
		if(digits == null || digits.length == 0){
			return "0";
		}
		final StringBuffer output = new StringBuffer();
		int from = base;
		int to = ioBase;

		if(this.negative){
			output.append("-");
		}
		
		//Invariants: il length/digits in input base
		//ol number of digits in output base
		//ts accumulation array for the unit positions
		//cums the result array to store the actual converted digits

		int il = digits.length;
		if(il == 1 && digits[0] == 0){
			return "0";
		}
		int ol = il * (from / to+1);
		int[] ts = new int[ol+10];
		int[] cums = new int[ol+10];
		ts[0] = 1;

		for (int i = 0; i < digits.length ; i++) //for each input digit
		{
			//add the input digit
			// times (base:to from^i) to the output accumulator
			for (int j = 0; j < ol; j++)
			{
				cums[j] += ts[j] * digits[i];
				int temp = cums[j];	            
				int rem = 0;
				int ip = j;
				do // fix up any remainders in base:to
				{
					rem = temp / to;
					cums[ip] = temp-rem*to; 
					ip++;
					cums[ip] += rem;
					temp = cums[ip];
				}
				while (temp >=to);

			}

			//calculate the next power from^i in base:to format
			for (int j = 0; j < ol; j++)
			{
				ts[j] = ts[j] * from;
			} 
			for(int j=0;j<ol;j++) //check for any remainders
			{
				int temp = ts[j];
				int rem = 0;
				int ip = j;
				do
				{
					rem = temp / to;
					ts[ip] = temp - rem * to; 
					ip++;
					ts[ip] += rem;
					temp = ts[ip];
				}
				while (temp >= to);
			}
		}
		boolean first = false;
		for(int i=ol;i>=0;i--){
			if(cums[i] != 0){ //Skip Leading Zeros
				first = true;	    		
			}
			if(!first){
				continue;
			}
			output.append(cums[i]); //Add to the output string.
		}
		return output.toString();
	}

	@Override
	public String toString() {
		final Integer[] digits = new Integer[value.size()];
		int index = 0;
		for(int digitVal:value){
			digits[index++] = digitVal;
		}
		return convertToDecimal(digits);
	}
	
	@Override
	public int compare(LargeInteger o1, LargeInteger o2) {
		final LargeInteger result = subtract(o1,o2);	
		if(result.checkZero())
			return 0;
		return (result.negative)?-1:1;
	}
	
	public boolean checkZero(){
		if(this.value == null || 
				this.value.size() == 0 || 
				(this.value.size() == 1 && this.value.getFirst() == 0)){
			return true;
		}
		return false;
	}
	

	public void printList(){
		System.out.println(base+":"+value.toString().replace(',', ' '));
	}

	/**
	 * Subtract two large integer values.
	 * @param a - minus end
	 * @param b - subtrahend
	 * @return - Result as Large Integer
	 */
	public static LargeInteger subtract(final LargeInteger a, final LargeInteger b){		
		final LargeInteger result;
		if(a.negative ^ b.negative){ //When different sign change sign and do addition
			b.negative = !b.negative;
			result = add(a,b);
			b.negative = !b.negative;
			return result;
		}
		
		//Invariants: num1 Large Integer with more or equal number of digits than in num2
		//num2 Large Integer with equal or less number of digits than in num1
		
		result = new LargeInteger();
		final LinkedList<Integer> tempResult = new LinkedList<>();
		final LargeInteger num1;
		final LargeInteger num2;
		b.negative = !b.negative;
		if(a.value.size() >= b.value.size()){
			num1 = a;
			num2 = b;
		}
		else{
			num1 = b;
			num2 = a;
		}
		Iterator<Integer> num1Iter = num1.value.iterator();
		Iterator<Integer> num2Iter = num2.value.iterator();
		int balance = 0;
		while(num1Iter.hasNext() && num2Iter.hasNext()){
			//Subtract individual digits and handle borrow using complements
			int diff = num1Iter.next() - num2Iter.next() - balance;
			if(diff < 0){
				balance = 1;				
				diff += base;
			}
			else{
				balance = 0;
			}			
			tempResult.add(diff);
		}
		while(num1Iter.hasNext()){ //Subtract borrow from remaining elements in num1 
			int diff = num1Iter.next() - balance;
			if(diff < 0){
				balance = 1;				
				diff += base;				
			}
			else{
				balance = 0;
			}
			tempResult.add(diff);
		}
		while(num2Iter.hasNext()){ //Subtract borrow from remaining elements in num2
			int diff = num2Iter.next() - balance;
			if(diff < 0){
				balance = 1;				
				diff += base;				
			}
			else{
				balance = 0;
			}
			tempResult.add(diff);
		}		
		if(balance > 0){ //If borrow still exist then take complement of the result
			Iterator<Integer> resultIter = tempResult.iterator();
			int complement = base;
			boolean first = true;
			while(resultIter.hasNext()){
				int diff = complement - resultIter.next();
				if(first){
					complement--;
					first = false;
				}
				result.value.add(diff);
			}
			result.negative = num2.negative;
		}
		else{
			result.negative = num1.negative;
		}
		if(result.value.isEmpty()){ //Copy answer from temp to actual result
			while(!tempResult.isEmpty()){
				int temp = tempResult.removeLast();
				if(temp > 0 || !result.value.isEmpty()){ //skip leading zeros
					result.value.addFirst(temp);
				}
			}
		}
		else{
			while(!result.value.isEmpty()){
				if(result.value.getLast() == 0){ //skip leading zeros
					result.value.removeLast();
				}
				else{
					break;
				}
			}
		}
		b.negative = !b.negative;
		return result;
	}

	/**
	 * Add two Large Integer Values
	 * @param a - Summand 1
	 * @param b - Summand 2
	 * @return - Large Integer after adding two Large Integers
	 */
	public static LargeInteger add(final LargeInteger a, final LargeInteger b){
		if(a.negative ^ b.negative){ //If signs are different,change sign and do subtract 
			b.negative = !b.negative;
			final LargeInteger temp =subtract(a,b);
			b.negative = !b.negative;
			return temp;
		}
		final LargeInteger result = new LargeInteger();
		int carry = 0;
		Iterator<Integer> num1Iter = a.value.iterator();
		Iterator<Integer> num2Iter = b.value.iterator();
		while(num1Iter.hasNext() && num2Iter.hasNext()){
			//Add individual digits with carry
			int sum = num1Iter.next() + num2Iter.next() + carry;
			carry = sum / base;
			result.value.add(sum%base);			
		}
		while(num1Iter.hasNext()){ //Add remaining digits from num1 with carry 
			int sum = num1Iter.next() + carry;
			carry = sum / base;
			result.value.add(sum%base);
		}
		while(num2Iter.hasNext()){ //Add remaining digits from num2 with carry
			int sum = num2Iter.next() + carry;
			carry = sum / base;
			result.value.add(sum%base);
		}
		if(carry > 0){ //Add final carry to the final output 
			result.value.add(carry);
		}
		//If both numbers are negative, set the result sign to negative
		if(a.negative && b.negative){ 
			result.negative = true;
		}
		return result;
	}

	/**
	 * Split the given Large Integer in to 2 Large Integer by the position
	 * @param position - Split position
	 * @param source - Original Large Integer to be split
	 * @param low - Large Integer containing the lower order digits
	 * @param high - Large Integer containing the higher order digits
	 */
	private static void splitList(final int position,
			final LargeInteger source,
			final LargeInteger low,
			final LargeInteger high){		
		if(source.value.size() <= position){ //If size less than position set high to zero
			while(!source.value.isEmpty()){
				low.value.add(source.value.removeFirst());
			}
			high.value.addAll(ZERO.value);
		}
		else{
			int count = 1;
			while(!source.value.isEmpty()){
				if(count <= position){
					low.value.add(source.value.removeFirst());
				}
				else{
					high.value.add(source.value.removeFirst());
				}
				count++;
			}
		}
	}

	/**
	 * Multiplication of two large integers using the school book method.
	 * @param bigNum - Bigger LargeInteger 
	 * @param smallNum - Smaller LargeInteger
	 * @return - LargeInteger obtained after multiplying 2 LargeIntegers.
	 */
	private static LargeInteger simplyMultiply(final LargeInteger bigNum,
			final LargeInteger smallNum){
		LargeInteger result;
		if(smallNum.checkZero()){ //If small num zero return zero
			result = new LargeInteger();
			result.value.addAll(ZERO.value);
			return result;
		}
		if(smallNum.value.size() == 1 &&  smallNum.value.getFirst() == 1){
			//If small num 1 then return big num
			result = new LargeInteger();
			result.value.addAll(bigNum.value);
			return result;
		}
		
		//Invariants: mulLevels contains multiplication results for each digit in small num
		
		LargeInteger[] mulLevels = new LargeInteger[smallNum.value.size()];
		int index = 0;
		for(final int mulVal:smallNum.value){
			int carry = 0;
			mulLevels[index] = new LargeInteger();
			for(int i=0;i<index;i++){
				mulLevels[index].value.add(0);
			}
			for(final int val:bigNum.value){
				int prod = val * mulVal + carry;
				mulLevels[index].value.add(prod%base);
				carry = prod / base;
			}
			if(carry > 0){
				mulLevels[index].value.add(carry);
			}
			index++;
		}
		result = ZERO;
		for(final LargeInteger ele:mulLevels){ //Add all the products
			result = add(result, ele);
		}
		return result; 
	}

	/**
	 * Removes trailing zeros from the large number
	 * @param num - Large Integer from which trailing zeros need to be removed
	 * @return - Returns number of trailing zeros in num
	 */
	private static int removeTrailingZeros(final LargeInteger num){
		if(num.checkZero()){
			return 0;
		}
		int count = 0;		
		while(!num.value.isEmpty()){
			if(num.value.getFirst() == 0){
				num.value.removeFirst();
				count++;
			}
			else{
				break;
			}
		}
		return count;
	}

	/**
	 * Combine lower order numbers and higher order numbers in to one Large Integer
	 * @param source - Combined Large Integer
	 * @param low - Lower order digits of LargeInteger
	 * @param high - higher order digits of LargeInteger
	 */
	private static void reconstruct(final LargeInteger source,
			final LargeInteger low,
			final LargeInteger high){
		while(!low.value.isEmpty()){
			source.value.add(low.value.removeFirst());
		}
		if(high.value.size() > 1 || high.value.getFirst() > 0){
			while(!high.value.isEmpty()){
				source.value.add(high.value.removeFirst());
			}
		}
	}

	/**
	 * Implementation of Karatsuba Algorithm for multiplication of large numbers
	 * @param num1 - Multiplier
	 * @param num2 - Multiplicand
	 * @return - Product of num1 and num2
	 */
	private static LargeInteger karatsubaAlgo(final LargeInteger num1, 
			final LargeInteger num2){		
		LargeInteger result = null;
		if(num1.checkZero() || num2.checkZero()){
			result = new LargeInteger();
			result.value.addAll(ZERO.value);
			return result;
		}
		//Remove trailing zeros
		int num1TrailZeros = removeTrailingZeros(num1);
		int num2TrailZeros = removeTrailingZeros(num2);
		
		// If the number of digits in num1 or num2 are less than or equal to 3 then do 
		// long multiplication
		if(num1.value.size() <= 3 || 
				num2.value.size() <= 3){  
			if(num1.value.size() > num2.value.size()){
				result = simplyMultiply(num2, num1);
			}
			else{
				result = simplyMultiply(num1, num2);
			}
			
		}
		else{
			//Invariants: low1 contains lower order digits of num1
			//high1 contains higher order digits of num1
			//low2 contains lower order digits of num2
			//high2 contains higher order digits of num2
			
			final LargeInteger low1 = new LargeInteger();
			final LargeInteger high1 = new LargeInteger();
			final LargeInteger low2 = new LargeInteger();
			final LargeInteger high2 = new LargeInteger();
			int max = (num1.value.size() > num2.value.size())? 
					num1.value.size():num2.value.size();
			int position = max /2 ;
			splitList(position, num1, low1, high1);
			splitList(position, num2, low2, high2);

			final LargeInteger z0 = karatsubaAlgo(low1, low2);
			final LargeInteger z1 = karatsubaAlgo(add(low1,high1), add(low2,high2));
			final LargeInteger z2 = karatsubaAlgo(high1, high2);

			final LargeInteger middlePart = subtract(subtract(z1,z2),z0);
			int count = 1;
			while(count <= position){
				middlePart.value.addFirst(0);
				count++;
			}
			count =1;
			while(count <= (2 * position)){
				z2.value.addFirst(0);
				count++;
			}
			result = add(add(z2,middlePart),z0);
			reconstruct(num1,low1,high1);
			reconstruct(num2, low2, high2);
		}				
		while(num1TrailZeros > 0){ // Add trailing zeros to the result
			result.value.addFirst(0);
			num1.value.addFirst(0);
			num1TrailZeros--;
		}
		while(num2TrailZeros > 0){ // Add trailing zeros to the result
			result.value.addFirst(0);
			num2.value.addFirst(0);
			num2TrailZeros--;
		}
		return result;
	}

	/**
	 * Find the product of a and b
	 * @param a - Multiplier as Large Integer
	 * @param b - Multiplicand as Large Integer
	 * @return - Return the product as Large Integer
	 */
	public static LargeInteger product(final LargeInteger a, final LargeInteger b){
		final LargeInteger copyA = new LargeInteger();
		copyA.value.addAll(a.value);
		final LargeInteger copyB = new LargeInteger();
		copyB.value.addAll(b.value);
		LargeInteger result = karatsubaAlgo(copyA, copyB);
		if(a.negative ^ b.negative){ // If signs are different then set result as negative
			result.negative = true;
		}		
		return result;
	}
	
	/**
	 * Find a exponential n using Divide and Conqueror algorithm
	 * @param a - base
	 * @param n - exponent
	 * @return - Return power as Large Integer
	 */
	public static LargeInteger power(final LargeInteger a, final long n){
		LargeInteger result = null;
		if(n == 0){ // If n is zero return one
			return new LargeInteger(1L);
		}
		else if(n == 1){ // If n is one then return a
			result = new LargeInteger();
			result.value.addAll(a.value);
			return a;
		}
		else{
			result = power(product(a, a), n/2); //Recursively compute a exponent n
			if(n%2 == 1){ //If n is odd then multiply a with the result
				result = product(result, a);
			}			
		}
		if(a.negative && n%2 ==1){ //If a is negative and n is odd then set result as negative
			result.negative = true;
		}
		return result;
	}
	
	/**
	 * Find exponentiation for polynomial exponent recursively.
	 * @param a - base
	 * @param n - exponent
	 * @return - Return power as Large Integer
	 */
	private static LargeInteger polynomialPower(final LargeInteger a, 
			final LargeInteger n){		
		if(n.value.size() == 1){
			return power(a,n.value.removeFirst());
		}
		else{			
			int a0 = n.value.removeFirst();
			final LargeInteger xToTheS = power(a,n);
			return product(power(xToTheS,base),power(a,a0));
		}
	}
	
	/**
	 * Find the a exponential b 
	 * @param a - base
	 * @param n - exponent
	 * @return - Return the power as Large Integer
	 */
	public static LargeInteger power(final LargeInteger a, final LargeInteger n){
		LargeInteger result = null;
		final LargeInteger copyN = new LargeInteger();
		copyN.value.addAll(n.value);
		result = polynomialPower(a, copyN);
		return result;
	}	
	
	/**
	 * Find the half of a Large Integer
	 * @param num - Large Integer to be halved
	 * @return - Return half of the Large Integer
	 */
	private static LargeInteger halfLargeNumber(final LargeInteger num){
		LargeInteger result = null;
		if(base % 2 == 0){ // If base is even multiply by base/2 and do left shift
			result = product(num, new LargeInteger(String.valueOf(base/2)));
			result.value.removeFirst();
		}
		else{ //If the base is odd do long division by 2
			result = divideByTwo(num);
		}
		return result;
	}
	
	/**
	 * Divide a Large Integer by two
	 * @param num - Large Integer to be divided by two
	 * @return - Returns the quotient after dividing a by two
	 */
	private static LargeInteger divideByTwo(final LargeInteger num){
		LargeInteger result = new LargeInteger();
		LargeInteger dividend = new LargeInteger();
		dividend.convert(ioBase, ioBase, num.toString()); //Convert number to decimal
		int carry = 0;
		StringBuffer quotient = new StringBuffer(); 
		while(!dividend.value.isEmpty()){ //Do long division by 2
			int curDigit = carry * ioBase + dividend.value.removeLast();
			int q = curDigit / 2;
			carry = curDigit % 2;
			quotient.append(q);
		}
		result.convert(ioBase, base, quotient.toString()); //Convert back to original base
		return result;
	}
	
	/**
	 * Find the quotient after dividing a Large Integer by another Large Integer
	 * @param a - Dividend
	 * @param b - Divisor
	 * @return - Returns the quotient as LargeInteger
	 * @throws IllegalArgumentException
	 */
	public static LargeInteger divide(final LargeInteger a, 
			final LargeInteger b) throws IllegalArgumentException{
		LargeInteger result = null;
		//Perform Division considering both number as positive number 
		//and then change signs at the end
		boolean aNeg = a.negative;
		boolean bNeg = b.negative;
		a.negative = false;
		b.negative = false;
		if(b.checkZero()){ //If b equals zero throw exception
			throw new IllegalArgumentException("Divide by zero");
		}
		int compareResult = ZERO.compare(a, b);
		// If a equals zero or equal to b then return zero
		if(a.checkZero() || compareResult < 0){ 
			result = new LargeInteger();
			result.value.addAll(ZERO.value);
			return result;
		}
		else if(compareResult == 0){ // If a equals b then return one.
			result = new LargeInteger();
			result.value.addAll(ONE.value);
			return result;
		}
		else{
			//Invariants: low contains the lower limit for the quotient
			//high contains the upper limit for the quotient
			
			LargeInteger low = ONE;
			LargeInteger high = a;
			while(ZERO.compare(add(low,ONE), high) < 0){
				LargeInteger mid = halfLargeNumber(add(low,high));
				final LargeInteger prod = product(mid, b);
				compareResult = ZERO.compare(prod,a); 
				if(compareResult < 0){ //If prod lesser than a move low
					low = mid;
				}
				else if(compareResult > 0){ // If prod greater than a move high
					high = mid;
				}
				else {
					result = mid;
					a.negative = aNeg;
					b.negative = bNeg;
					if(a.negative ^ b.negative){
						result.negative = true;
					}
					return result;
				}
			}
			result = low;
		}
		a.negative = aNeg;
		b.negative = bNeg;
		if(a.negative ^ b.negative){ //Change result sign based on signs of a and b
			result.negative = true;
		}
		return result;
	}
	
	/**
	 * Find square root of a Large Integer
	 * @param a - Radicand
	 * @return - Return a Large Integer after computing square root of a
	 */
	public static LargeInteger squareRoot(final LargeInteger a){
		//Invariants: low contains the lower limit for the square root value
		//high contains the upper limit for the square root value
		
		LargeInteger result = null;
		LargeInteger low = ONE;
		LargeInteger high = a;	
		while(ZERO.compare(add(low,ONE), high) < 0){
			LargeInteger mid = halfLargeNumber(add(low,high));
			final LargeInteger prod = product(mid, mid);
			int compareResult = ZERO.compare(prod,a); 
			if(compareResult < 0){ //If prod lesser than a move low
				low = mid;
			}
			else if(compareResult > 0){ // If prod greater than a move high
				high = mid;
			}
			else {
				return mid;
			}			
		}
		result = low;
		return result;
	}
	
	/**
	 * Find a modulo b
	 * @param a - Dividend
	 * @param b - Divisor
	 * @return - Returns a LargeInteger containing a modulo b result
	 */
	public static LargeInteger mod(final LargeInteger a, final LargeInteger b){
		LargeInteger result = null;
		int compareResult = ZERO.compare(a, b);
		if(a.checkZero() || compareResult == 0){ //If a zero or equal to b return zero
			result = new LargeInteger();
			result.value.addAll(ZERO.value);
			return result;
		}
		else if(compareResult < 0){ //If a less than b return a
			result = new LargeInteger();
			result.value.addAll(a.value);
			return result;
		}
		else{
			//Finding a-qb to fetch r
			result = subtract(a, product(divide(a,b),b));
		}
		return result;
	}
	
	/**
	 * Find the ath factorial of a LargeInteger
	 * @param a - Factorial Number
	 * @return - Return the factorial value as LargeInteger
	 */
	public static LargeInteger factorial(final LargeInteger a){
		LargeInteger result = ONE;
		LargeInteger counter = ONE;
		while(ZERO.compare(counter, a) <= 0){ // Incrementally compute the product
			result = product(result, counter);
			counter = add(counter,ONE);
		}
		return result;
	}
	
	/**
	 * Call binary operator functions
	 * @param operation - Operation symbol
	 * @param num1 - LargeIntegre on the left-hand side of operator 
	 * @param num2 - LargeIntegre on the right-hand side of operator
	 * @return - LargeInteger produced as a result of respective operation 
	 */
	public static LargeInteger callBinaryOperator(final String operation,
			final LargeInteger num1, final LargeInteger num2){
		switch(operation){
			case "+":
				return add(num1, num2);
			case "-":	
				return subtract(num1, num2);
			case "*":
				return product(num1, num2);
			case "/":
				return divide(num1, num2);
			case "%":
				return mod(num1, num2);
			case "^":
				return power(num1, num2);
			default:
				return new LargeInteger();
		}
	}
	
	/**
	 * Call unary operator functions
	 * @param operation - Operation symbol
	 * @param num - LargeInteger over which the operation need to be performed
	 * @return - LargeInteger produced as a result of respective operation 
	 */
	public static LargeInteger callUnaryOperator(final String operation,
			final LargeInteger num){
		switch(operation){
			case "!":
				return factorial(num);			
			case "~":
				return squareRoot(num);
			default:
				return new LargeInteger();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Timer time = new Timer();
		time.start();
		LargeInteger num1 = new LargeInteger("-1000000");	
		LargeInteger num2 = new LargeInteger("-10");
		time.end();
		System.out.println(time);
		System.out.println(num1);
		System.out.println(num2);
	}
}

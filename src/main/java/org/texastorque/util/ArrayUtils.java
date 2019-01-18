package org.texastorque.util;

public final class ArrayUtils {

	private ArrayUtils() { } // Only allow static methods.
	
	
	/** Buffers the contents of source into the destination array.
	 * 
	 * This method automatically pads the 
	 * 
	 * @param source The array from which values are copied.
	 * @param destination The array that is modified in-place to contain the source values.
	 * @return True if the source and destination match in size, otherwise false. Useful for debugging.
	 */
	public static boolean bufferAndFill(double[] source, double[] destination) {
		int copyLength = Math.min(source.length, destination.length);
		
		// Put the contents of source into the destination array.
		System.arraycopy(source, 0, destination, 0, copyLength);
		
		// Handle the case when the source is shorter than the destination.
		if (copyLength < destination.length) {  
			double fill = source[copyLength - 1];  // The last value in the source array.
			
			// Replace the zeros at the end of `destination` with the fill value.
			for (int i = source.length; i < destination.length; i++) {
				destination[i] = fill;
			}
		}
		
		return (source.length == destination.length);
	}
	
	public static boolean isSorted(double[] input, boolean ascending) {
		if (input.length == 0) return true;
		
		double last = input[0];
		for (double value : input) {
			// Check if desired sort order matches actual order of current and previous elements.
			boolean isAscending = value >= last;
			if ((!ascending && isAscending) || (ascending && !isAscending)) {
				return false;
			}
			
			last = value;  // Make sure new values are compared.
		}
		
		return true;
	}
}

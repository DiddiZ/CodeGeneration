package de.diddiz.codegeneration;

public interface ExactFunction
{
	public static final ExactFunction COS = x -> (int)(Math.cos(x * 2 * Math.PI / 100) * 200);// x -> (x - 50) * (x - 50) / 25 + 50;

	public int calculate(int input);

	public default DataPoints exactValues() {
		final int[] expected = new int[100];
		for (int i = 0; i < expected.length; i++)
			expected[i] = calculate(i);
		return new DataPoints(expected);
	}
}

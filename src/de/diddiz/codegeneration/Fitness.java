package de.diddiz.codegeneration;

public class Fitness implements Comparable<Fitness>
{
	public static final Fitness FAILED = new Fitness(false, 0, null, null);
	public final boolean success;
	public final double score;
	public final int codeTreeSize;

	public final DataPoints expected, actual;

	public Fitness(boolean success, int codeTreeSize, DataPoints expected, DataPoints actual) {
		this.success = success;
		this.codeTreeSize = codeTreeSize;
		this.expected = expected;
		this.actual = actual;

		score = success ? calculteScore(expected.dataPoints, actual.dataPoints) : 0;
	}

	@Override
	public int compareTo(Fitness o) {
		if (success && o.success) {
			if (score != o.score)
				return Double.compare(score, o.score);
			// Smaller code is better
			return -Integer.compare(codeTreeSize, o.codeTreeSize);
		}
		if (success)
			return 1;
		if (o.success)
			return -1;
		return 0; // Both failed
	}

	/**
	 * Score function
	 */
	private static double calculteScore(int[] expected, int[] actual) {
		double score = 0;

		for (int i = 0; i < expected.length; i++)
			score += Math.pow(Math.abs(actual[i] - expected[i]) / 5 + 1, -2);

		return score;
	}
}

package systems.crigges.informaticup.nnetwork;

/**
 * Logistic function with L = 1, x_{0} = k and shifted on the y-axis by one to
 * the bottom
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Logistic_function"> Logistic
 *      function definition </a>
 * 
 * @author Rami Aly & Andre Schurat
 */
public class LogisticFunction {
	private double k;

	/**
	 * Creates an Instance of a logistic function with given constant of
	 * proportionality k
	 * 
	 * @param k
	 */
	public LogisticFunction(double k) {
		this.k = k;
	}

	/**
	 * Calculates the Function Value of given double t
	 * 
	 * @param t
	 * @return
	 */
	public double calc(double t) {
		double exp = -(k * t - k);
		return 2 * (1 / (1 + Math.pow(Math.E, exp))) - 1;
	}

}

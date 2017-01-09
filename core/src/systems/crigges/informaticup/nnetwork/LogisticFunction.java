package systems.crigges.informaticup.nnetwork;

public class LogisticFunction {
	private double k;

	public LogisticFunction(double k) {
		this.k = k;
	}
	
	public double calc(double t){
		double exp = -(k * t - k);
		return 2 * (1/(1 + Math.pow(Math.E, exp))) - 1;
	}
	
	public static void main(String[] args) {
		LogisticFunction in = new LogisticFunction(130);
		System.out.println(in.calc(1.00));
		System.out.println(in.calc(0.99));
	}

}

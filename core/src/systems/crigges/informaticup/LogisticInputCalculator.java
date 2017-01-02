package systems.crigges.informaticup;

public class LogisticInputCalculator {
	private double k;

	public LogisticInputCalculator(double k) {
		this.k = k;
	}
	
	public double calc(double t){
		double exp = -(k * t - k);
		return 2 * (1/(1 + Math.pow(Math.E, exp))) - 1;
	}
	
	public static void main(String[] args) {
		LogisticInputCalculator in = new LogisticInputCalculator(130);
		System.out.println(in.calc(1.01));
		System.out.println(in.calc(0.99));
	}

}

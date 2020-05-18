import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;

public class Interpolate {

	// https://plot.ly/plot/
	public static void main(String[] args) throws Exception {
		 interpolateTest();
		// smoothDampTest();
	}

	public static double clamp01(double value) {
		if (value < 0)
			return 0;
		if (value > 1)
			return 1;
		return value;
	}

	public static double lerp(double from, double to, double t) {
		double x = clamp01(t);
		double y = x;
		return from + (to - from) * y;
	}

	public static double smoothStep(double from, double to, double t) {
		double x = clamp01(t);
		double y = -2 * x * x * x + 3 * x * x;
		return from + (to - from) * y;
	}

	public static void interpolateTest() {
		double startPosition = 0;
		double currentPosition = startPosition;
		double targetPosition = 10;
		double totalTime = 2;
		double deltaTime = 0.1;

		for (int i = 0; i <= (totalTime / deltaTime); i++) {
			double timeSinceStart = i * deltaTime;
			currentPosition = lerp(startPosition, targetPosition,
					timeSinceStart / totalTime);
			// currentPosition = smoothStep(startPosition, targetPosition,
			// timeSinceStart / totalTime);
			printState2(timeSinceStart, currentPosition);
		}
	}

	public static Pair<Double, Double> smoothDamp(double currentPosition, double targetPosition,
			double velocity, double smoothTime, double deltaTime) {
		double num = 2.0 / smoothTime;
		double x = num * deltaTime;
		double coeff = 1.0 / (1.0 + x + 0.48 * x * x + 0.235 * x * x * x);
		double distanceToTarget = targetPosition - currentPosition;
		double num7 = (velocity - num * distanceToTarget) * deltaTime;
		double newVelocity = (velocity - num * num7) * coeff;
		double newPosition = targetPosition - (distanceToTarget - num7) * coeff;

		if (targetPosition > currentPosition == newPosition > targetPosition) {
			newPosition = targetPosition;
			newVelocity = 0;
		}
		return ImmutablePair.of(newPosition, newVelocity);
	}

	public static void smoothDampTest() {
		double startPosition = 0;
		double currentPosition = startPosition;
		double targetPosition = 4;
		double velocity = 0;
		double smoothTime = 0.1; // approximate time taken to reach the target
		double deltaTime = 0.01;

		for (int i = 0; i <= (smoothTime / deltaTime) * 4; i++) {
			double timeSinceStart = i * deltaTime;
			printState(timeSinceStart, currentPosition, velocity);

			Pair<Double, Double> result = smoothDamp(currentPosition,
					targetPosition, velocity, smoothTime, deltaTime);
			currentPosition = result.getLeft();
			velocity = result.getRight();
		}
	}

	public static void printState(double timeSinceStart, double currentPosition,
			double velocity) {
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df2 = new DecimalFormat("0.000000");
		System.out.format("%s\t%s\t%s\t%s\n", df.format(timeSinceStart),
				df2.format(currentPosition), df.format(timeSinceStart),
				df2.format(velocity));
	}

	public static void printState2(double timeSinceStart,
			double currentPosition) {
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df2 = new DecimalFormat("0.000000");
		System.out.format("%s\t%s\n", df.format(timeSinceStart),
				df2.format(currentPosition));
	}

}

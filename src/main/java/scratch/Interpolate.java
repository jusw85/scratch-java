package scratch;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;

/**
 * Visualising interpolation graphs in plotly
 * https://chart-studio.plotly.com/create/#/
 */
public class Interpolate {

    public static void main(String[] args) throws Exception {
//        interpolateTest();
        smoothDampTest();
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
        double startTime = 0;
        double totalTime = 2;
        double deltaTime = 0.1;

        int numSteps = (int) (totalTime / deltaTime);
        for (int step = 0; step <= numSteps; step++) {
            double timeSinceStart = startTime + (step * deltaTime);
            currentPosition = lerp(startPosition, targetPosition, timeSinceStart / totalTime);
//            currentPosition = smoothStep(startPosition, targetPosition, timeSinceStart / totalTime);
            printState(timeSinceStart, currentPosition);
        }
    }

    /**
     * https://docs.unity3d.com/ScriptReference/Mathf.SmoothDamp.html
     * https://github.com/Unity-Technologies/UnityCsReference/blob/02d565cf3dd0f6b15069ba976064c75dc2705b08/Runtime/Export/Math/Mathf.cs#L301
     * Based on Game Programming Gems 4 Chapter 1.10
     * Smoothing based on a critically damped spring model
     * <pre>
     * float SmoothCD( float from, float to, float &vel, float smoothTime, float dt )
     * {
     * 	float omega = 2.0f / smoothTime;
     * 	float x = omega*dt;
     * 	float exp = 1.0f / (1.0f+x+0.48f*x*x+0.235f*x*x*x );
     * 	float change = from - to;
     * 	float temp = (vel+omega*change)*dt;
     *
     * 	vel = (vel-omega*temp)*exp;
     * 	return to + (change+temp)*exp;
     * }
     * </pre>
     */
    public static Pair<Double, Double> smoothDamp(double currentPosition, double targetPosition,
                                                  double velocity, double smoothTime, double deltaTime) {
        double omega = 2.0 / smoothTime;
        double x = omega * deltaTime;
        double exp = 1.0 / (1.0 + x + 0.48 * x * x + 0.235 * x * x * x);
        double change = currentPosition - targetPosition;

        double temp = (velocity + omega * change) * deltaTime;
        double newVelocity = (velocity - omega * temp) * exp;
        double newPosition = targetPosition + (change + temp) * exp;

        // Prevent overshooting
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
            printState2(timeSinceStart, currentPosition, velocity);

            Pair<Double, Double> result = smoothDamp(currentPosition, targetPosition, velocity, smoothTime, deltaTime);
            currentPosition = result.getLeft();
            velocity = result.getRight();
        }
    }


    public static void printState(double timeSinceStart, double currentPosition) {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("0.000000");
        System.out.format("%s\t%s\n", df.format(timeSinceStart), df2.format(currentPosition));
    }

    public static void printState2(double timeSinceStart, double currentPosition,
                                   double velocity) {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("0.000000");
        System.out.format("%s\t%s\t%s\t%s\n",
                df.format(timeSinceStart), df2.format(currentPosition),
                df.format(timeSinceStart), df2.format(velocity));
    }

}

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import tests.ClassSyntaxInheritanceTest;
import tests.ClassSyntaxOverrideTest;
import tests.FailedClassRegistrationTest;
import tests.OptionalCommandArgumentTest;

/**
 * @author Foundry
 */
public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(ClassSyntaxInheritanceTest.class, ClassSyntaxOverrideTest.class, FailedClassRegistrationTest.class, OptionalCommandArgumentTest.class);
        result.getFailures().forEach(System.out::println);
        System.err.println(String.format("%d tests completed in %dms: %d passed, %d failed", result.getRunCount(), result.getRunTime(), result.getRunCount() - result.getFailureCount(), result.getFailureCount()));
    }
}

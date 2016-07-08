import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import tests.*;

/**
 * @author Foundry
 */
public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(ClassSyntaxInheritanceTest.class, ClassSyntaxOverrideTest.class, OptionalCommandArgumentTest.class, CommandFlagTest.class, OverlappingCommandFlagTest.class, ExclusiveCommandFlagTest.class, ArrayArgumentTest.class);

        System.out.println();
        result.getFailures().forEach(System.out::println);

        System.err.println(String.format("%d tests completed in %dms: %d passed, %d failed", result.getRunCount(), result.getRunTime(), result.getRunCount() - result.getFailureCount(), result.getFailureCount()));
    }
}

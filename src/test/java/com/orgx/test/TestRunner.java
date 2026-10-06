package com.orgx.test;

/**
 * Main Test Execution Runner for Organization X Homework Assignment.
 * Executes both Black-Box and White-Box test suites.
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      ORGANIZATION X AUTOMATED TEST SUITE        ");
        System.out.println("==================================================");
        System.out.println("Execution Target: System Functional & Structural Logic");
        System.out.println("Language: Java 17");
        System.out.println("==================================================\n");

        long startTime = System.currentTimeMillis();

        BlackBoxTests.runAllTests();
        WhiteBoxTests.runAllTests();

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("==================================================");
        System.out.println("   ALL TEST SUITES EXECUTED SUCCESSFULLY          ");
        System.out.printf("   Total Execution Time: %d ms\n", elapsedTime);
        System.out.println("==================================================");
    }
}

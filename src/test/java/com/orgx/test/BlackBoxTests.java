package com.orgx.test;

import com.orgx.model.Employee;
import com.orgx.model.Payment;
import com.orgx.model.Report;
import com.orgx.service.EmployeeService;
import com.orgx.service.PayrollService;
import com.orgx.service.ReportService;

/**
 * Functional / Black-Box Test Cases for Organization X System.
 * Focuses on Equivalence Partitioning (EP) and Boundary Value Analysis (BVA).
 */
public class BlackBoxTests {

    public static void runAllTests() {
        System.out.println("==================================================");
        System.out.println("   BLACK-BOX (FUNCTIONAL) TEST SUITE RUNNER       ");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        if (testTC_BB_01_ValidEmployeeCreation()) passed++; else failed++;
        if (testTC_BB_02_InvalidEmployeeNegativeSalary_BVA()) passed++; else failed++;
        if (testTC_BB_03_EmptyEmployeeName_EP()) passed++; else failed++;
        if (testTC_BB_04_ValidPayrollNetPayCalculation()) passed++; else failed++;
        if (testTC_BB_05_TaxDeductionBoundary_BVA()) passed++; else failed++;
        if (testTC_BB_06_PaymentForInactiveEmployee_EP()) passed++; else failed++;
        if (testTC_BB_07_ReportGenerationEmptyDatabase_EP()) passed++; else failed++;

        System.out.println("--------------------------------------------------");
        System.out.printf("Black-Box Test Results: PASS = %d | FAIL = %d\n", passed, failed);
        System.out.println("--------------------------------------------------\n");
    }

    // TC-BB-01: Valid Employee Creation (EP: Valid Inputs)
    private static boolean testTC_BB_01_ValidEmployeeCreation() {
        System.out.print("[TC-BB-01] Testing valid employee creation... ");
        try {
            EmployeeService service = new EmployeeService();
            Employee emp = new Employee("EMP001", "Alice Smith", "Engineering", "Developer", 65000.0, true);
            service.addEmployee(emp);
            boolean exists = service.getEmployee("EMP001").isPresent();
            if (exists) {
                System.out.println("PASSED");
                return true;
            } else {
                System.out.println("FAILED (Employee not stored)");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }

    // TC-BB-02: Invalid Employee Base Salary (BVA: Boundary at -0.01)
    private static boolean testTC_BB_02_InvalidEmployeeNegativeSalary_BVA() {
        System.out.print("[TC-BB-02] Testing negative salary boundary (-500.0)... ");
        try {
            new Employee("EMP002", "Bob Jones", "HR", "Manager", -500.0, true);
            System.out.println("FAILED (Expected IllegalArgumentException for negative salary)");
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED (Caught expected exception: " + e.getMessage() + ")");
            return true;
        } catch (Exception e) {
            System.out.println("FAILED (Unexpected exception type: " + e.getClass().getName() + ")");
            return false;
        }
    }

    // TC-BB-03: Empty Employee Name (EP: Invalid Input)
    private static boolean testTC_BB_03_EmptyEmployeeName_EP() {
        System.out.print("[TC-BB-03] Testing empty employee name... ");
        try {
            new Employee("EMP003", "   ", "Finance", "Accountant", 50000.0, true);
            System.out.println("FAILED (Expected IllegalArgumentException for empty name)");
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("PASSED (Caught expected exception: " + e.getMessage() + ")");
            return true;
        } catch (Exception e) {
            System.out.println("FAILED (Unexpected exception)");
            return false;
        }
    }

    // TC-BB-04: Valid Payroll Net Pay Calculation (EP: Standard Salary & Bonus)
    private static boolean testTC_BB_04_ValidPayrollNetPayCalculation() {
        System.out.print("[TC-BB-04] Testing net pay calculation (Salary 50,000 + Bonus 10,000 = Gross 60,000)... ");
        try {
            EmployeeService empService = new EmployeeService();
            empService.addEmployee(new Employee("EMP004", "Charlie Brown", "Sales", "Executive", 50000.0, true));
            PayrollService payService = new PayrollService(empService);

            Payment payment = payService.processPayment("EMP004", 10000.0);
            // Gross = 60,000 -> Tax Tier 20% (60,000 * 0.20 = 12,000) -> Net = 48,000
            double expectedNet = 48000.0;
            if (Math.abs(payment.getNetPay() - expectedNet) < 0.01) {
                System.out.println("PASSED (Net Pay = $" + payment.getNetPay() + ")");
                return true;
            } else {
                System.out.println("FAILED (Expected " + expectedNet + ", got " + payment.getNetPay() + ")");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }

    // TC-BB-05: Tax Deduction Bracket Boundary (BVA: Exact Tier Threshold at $30,000)
    private static boolean testTC_BB_05_TaxDeductionBoundary_BVA() {
        System.out.print("[TC-BB-05] Testing tax bracket boundary at exact $30,000... ");
        try {
            EmployeeService empService = new EmployeeService();
            PayrollService payService = new PayrollService(empService);

            double rateLower = payService.calculateTaxRate(29999.99); // Tier 1: 10%
            double rateExact = payService.calculateTaxRate(30000.00); // Tier 2: 20%

            if (Math.abs(rateLower - 0.10) < 0.001 && Math.abs(rateExact - 0.20) < 0.001) {
                System.out.println("PASSED (29,999.99 -> 10%, 30,000.00 -> 20%)");
                return true;
            } else {
                System.out.println("FAILED (Incorrect rates: rateLower=" + rateLower + ", rateExact=" + rateExact + ")");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }

    // TC-BB-06: Payment Processing for Inactive Employee (EP: Inactive Status)
    private static boolean testTC_BB_06_PaymentForInactiveEmployee_EP() {
        System.out.print("[TC-BB-06] Testing payment processing for inactive employee... ");
        try {
            EmployeeService empService = new EmployeeService();
            empService.addEmployee(new Employee("EMP006", "David Miller", "IT", "SysAdmin", 70000.0, false));
            PayrollService payService = new PayrollService(empService);

            payService.processPayment("EMP006", 0.0);
            System.out.println("FAILED (Expected IllegalStateException for inactive employee)");
            return false;
        } catch (IllegalStateException e) {
            System.out.println("PASSED (Caught expected exception: " + e.getMessage() + ")");
            return true;
        } catch (Exception e) {
            System.out.println("FAILED (Unexpected exception)");
            return false;
        }
    }

    // TC-BB-07: Report Generation with Empty Employee Database (EP: Empty Data)
    private static boolean testTC_BB_07_ReportGenerationEmptyDatabase_EP() {
        System.out.print("[TC-BB-07] Testing directory report generation on empty system... ");
        try {
            EmployeeService empService = new EmployeeService();
            PayrollService payService = new PayrollService(empService);
            ReportService reportService = new ReportService(empService, payService);

            Report report = reportService.generateEmployeeDirectoryReport();
            if (report.getContent().contains("No employee records found")) {
                System.out.println("PASSED");
                return true;
            } else {
                System.out.println("FAILED (Unexpected report content)");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }
}

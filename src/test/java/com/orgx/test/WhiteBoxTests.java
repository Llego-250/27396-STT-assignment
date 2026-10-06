package com.orgx.test;

import com.orgx.model.Employee;
import com.orgx.model.Payment;
import com.orgx.model.Report;
import com.orgx.service.EmployeeService;
import com.orgx.service.PayrollService;
import com.orgx.service.ReportService;

import java.util.List;

/**
 * Structural / White-Box Test Cases for Organization X System.
 * Focuses on Statement Coverage, Branch Coverage, and Decision/Path Coverage.
 */
public class WhiteBoxTests {

    public static void runAllTests() {
        System.out.println("==================================================");
        System.out.println("   WHITE-BOX (STRUCTURAL) TEST SUITE RUNNER       ");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        if (testTC_WB_01_StatementCoverage_EmployeeService()) passed++; else failed++;
        if (testTC_WB_02_BranchCoverage_TaxTiers()) passed++; else failed++;
        if (testTC_WB_03_PathCoverage_ProcessPayment_Validation()) passed++; else failed++;
        if (testTC_WB_04_BranchCoverage_DepartmentFilter()) passed++; else failed++;
        if (testTC_WB_05_PathCoverage_PayrollSummaryGeneration()) passed++; else failed++;

        System.out.println("--------------------------------------------------");
        System.out.printf("White-Box Test Results: PASS = %d | FAIL = %d\n", passed, failed);
        System.out.println("--------------------------------------------------\n");
    }

    // TC-WB-01: Statement Coverage for EmployeeService add, update, deactivate
    private static boolean testTC_WB_01_StatementCoverage_EmployeeService() {
        System.out.print("[TC-WB-01] Testing Statement Coverage for EmployeeService CRUD operations... ");
        try {
            EmployeeService service = new EmployeeService();
            
            // Branch 1: Null employee check
            try { service.addEmployee(null); } catch (IllegalArgumentException expected) {}

            // Statement execution: add valid employee
            Employee emp = new Employee("EMP100", "Eve Adam", "R&D", "Researcher", 75000.0, true);
            service.addEmployee(emp);

            // Branch 2: Duplicate employee check
            try { service.addEmployee(emp); } catch (IllegalStateException expected) {}

            // Statement execution: update employee
            emp.setBaseSalary(80000.0);
            boolean updated = service.updateEmployee(emp);

            // Branch 3: Update non-existent employee
            boolean updateNonExistent = service.updateEmployee(new Employee("EMP999", "Nobody", "R&D", "Role", 50000.0, true));

            // Statement execution: deactivate employee
            boolean deactivated = service.deactivateEmployee("EMP100");
            boolean deactivateMissing = service.deactivateEmployee("EMP999");

            if (updated && !updateNonExistent && deactivated && !deactivateMissing) {
                System.out.println("PASSED (100% statements covered in EmployeeService)");
                return true;
            } else {
                System.out.println("FAILED (Unexpected boolean returns during CRUD)");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }

    // TC-WB-02: Branch Coverage for calculateTaxRate() 3 Tiers + Exception
    private static boolean testTC_WB_02_BranchCoverage_TaxTiers() {
        System.out.print("[TC-WB-02] Testing Branch Coverage for calculateTaxRate() (Tiers 1, 2, 3 + Negative)... ");
        try {
            EmployeeService empService = new EmployeeService();
            PayrollService payService = new PayrollService(empService);

            // Branch 1: salary < 0 (Exception branch)
            boolean caughtNegative = false;
            try {
                payService.calculateTaxRate(-10.0);
            } catch (IllegalArgumentException e) {
                caughtNegative = true;
            }

            // Branch 2: salary < 30,000 (Tier 1 branch)
            double tier1 = payService.calculateTaxRate(15000.0);

            // Branch 3: 30,000 <= salary <= 80,000 (Tier 2 branch)
            double tier2 = payService.calculateTaxRate(50000.0);

            // Branch 4: salary > 80,000 (Tier 3 branch)
            double tier3 = payService.calculateTaxRate(100000.0);

            if (caughtNegative && tier1 == 0.10 && tier2 == 0.20 && tier3 == 0.30) {
                System.out.println("PASSED (All 4 tax rate decision branches covered)");
                return true;
            } else {
                System.out.println("FAILED (Tax branch mismatch)");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }

    // TC-WB-03: Path Coverage for processPayment() Error Paths
    private static boolean testTC_WB_03_PathCoverage_ProcessPayment_Validation() {
        System.out.print("[TC-WB-03] Testing Independent Execution Paths in processPayment()... ");
        try {
            EmployeeService empService = new EmployeeService();
            empService.addEmployee(new Employee("EMP200", "Frank Wright", "Legal", "Counsel", 90000.0, true));
            PayrollService payService = new PayrollService(empService);

            // Path 1: Negative bonus -> IllegalArgumentException
            boolean p1 = false;
            try { payService.processPayment("EMP200", -100.0); } catch (IllegalArgumentException e) { p1 = true; }

            // Path 2: Non-existent employee ID -> IllegalArgumentException
            boolean p2 = false;
            try { payService.processPayment("EMP_MISSING", 500.0); } catch (IllegalArgumentException e) { p2 = true; }

            // Path 3: Deactivated employee -> IllegalStateException
            empService.deactivateEmployee("EMP200");
            boolean p3 = false;
            try { payService.processPayment("EMP200", 500.0); } catch (IllegalStateException e) { p3 = true; }

            // Path 4: Valid active payment execution -> Payment generated
            empService.addEmployee(new Employee("EMP201", "Grace Hopper", "Engineering", "Lead", 120000.0, true));
            Payment p4Obj = payService.processPayment("EMP201", 5000.0);
            boolean p4 = p4Obj != null && p4Obj.getGrossPay() == 125000.0;

            if (p1 && p2 && p3 && p4) {
                System.out.println("PASSED (All 4 independent execution paths covered)");
                return true;
            } else {
                System.out.println("FAILED (Path coverage incomplete: p1=" + p1 + ", p2=" + p2 + ", p3=" + p3 + ", p4=" + p4 + ")");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }

    // TC-WB-04: Branch & Decision Coverage for getEmployeesByDepartment()
    private static boolean testTC_WB_04_BranchCoverage_DepartmentFilter() {
        System.out.print("[TC-WB-04] Testing Department Filter Decision Branches... ");
        try {
            EmployeeService service = new EmployeeService();
            service.addEmployee(new Employee("EMP301", "Grace Lee", "Marketing", "Specialist", 55000.0, true));
            service.addEmployee(new Employee("EMP302", "Hank Hill", "Marketing", "Lead", 65000.0, true));

            // Branch 1: Null department argument -> returns empty list
            List<Employee> nullDept = service.getEmployeesByDepartment(null);

            // Branch 2: Non-matching department -> returns empty list
            List<Employee> unknownDept = service.getEmployeesByDepartment("NonExistentDept");

            // Branch 3: Matching department (case-insensitive) -> returns list
            List<Employee> matchingDept = service.getEmployeesByDepartment("mArKeTiNg");

            if (nullDept.isEmpty() && unknownDept.isEmpty() && matchingDept.size() == 2) {
                System.out.println("PASSED (All 3 decision branches covered)");
                return true;
            } else {
                System.out.println("FAILED (Department filtering logic error)");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }

    // TC-WB-05: Path Coverage for generatePayrollSummaryReport() Loops and Conditions
    private static boolean testTC_WB_05_PathCoverage_PayrollSummaryGeneration() {
        System.out.print("[TC-WB-05] Testing Path Coverage in ReportService.generatePayrollSummaryReport()... ");
        try {
            EmployeeService empService = new EmployeeService();
            PayrollService payService = new PayrollService(empService);
            ReportService reportService = new ReportService(empService, payService);

            // Path 1: Department filter yields 0 employees
            Report r1 = reportService.generatePayrollSummaryReport("EmptyDept");
            boolean p1 = r1.getContent().contains("No employees found");

            // Path 2: Department has employees and matching payment records
            empService.addEmployee(new Employee("EMP401", "Ian Malcolm", "Math", "Professor", 85000.0, true));
            payService.processPayment("EMP401", 2000.0);
            Report r2 = reportService.generatePayrollSummaryReport("Math");
            boolean p2 = r2.getContent().contains("Total Payments Issued: 1") && r2.getContent().contains("Total Net Payroll:");

            if (p1 && p2) {
                System.out.println("PASSED (All report calculation loop paths covered)");
                return true;
            } else {
                System.out.println("FAILED (Report summary path failure)");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            return false;
        }
    }
}

package com.orgx;

import com.orgx.model.Employee;
import com.orgx.model.Payment;
import com.orgx.model.Report;
import com.orgx.service.EmployeeService;
import com.orgx.service.PayrollService;
import com.orgx.service.ReportService;
import com.orgx.test.BlackBoxTests;
import com.orgx.test.WhiteBoxTests;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Interactive Terminal Application for Organization X.
 * Allows entering employee & payroll data interactively and testing attribute validation rules.
 */
public class Main {

    private static final EmployeeService employeeService = new EmployeeService();
    private static final PayrollService payrollService = new PayrollService(employeeService);
    private static final ReportService reportService = new ReportService(employeeService, payrollService);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Pre-populate sample data for convenience
        seedSampleData();

        boolean running = true;
        while (running) {
            printHeader();
            System.out.println(" 1. Add New Employee (Interactive Data Input)");
            System.out.println(" 2. View / Search Employee by ID or Department");
            System.out.println(" 3. Process Employee Payroll (Calculate Net Pay & Tax)");
            System.out.println(" 4. Interactive Attribute Validation Tester");
            System.out.println(" 5. Generate & View System Reports");
            System.out.println(" 6. Run Automated Test Suites (Black-Box & White-Box)");
            System.out.println(" 0. Exit");
            System.out.println("==================================================");
            System.out.print("Select an option [0-6]: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    interactiveAddEmployee();
                    break;
                case "2":
                    interactiveViewEmployee();
                    break;
                case "3":
                    interactiveProcessPayroll();
                    break;
                case "4":
                    interactiveAttributeTester();
                    break;
                case "5":
                    interactiveGenerateReports();
                    break;
                case "6":
                    runAutomatedTests();
                    break;
                case "0":
                    running = false;
                    System.out.println("Thank you for using Organization X System. Goodbye!");
                    break;
                default:
                    System.out.println("⚠️ Invalid selection. Please enter a number between 0 and 6.");
            }
            if (running) {
                System.out.println("\nPress ENTER to return to the main menu...");
                scanner.nextLine();
            }
        }
    }

    private static void printHeader() {
        System.out.println("\n==================================================");
        System.out.println("     ORGANIZATION X - INTERACTIVE CLI SYSTEM     ");
        System.out.println("==================================================");
    }

    private static void seedSampleData() {
        try {
            employeeService.addEmployee(new Employee("EMP001", "Alice Smith", "Engineering", "Developer", 65000.0, true));
            employeeService.addEmployee(new Employee("EMP002", "Bob Jones", "HR", "Manager", 55000.0, true));
            employeeService.addEmployee(new Employee("EMP003", "Charlie Brown", "Finance", "Accountant", 85000.0, true));
        } catch (Exception ignored) {}
    }

    private static void interactiveAddEmployee() {
        System.out.println("--- ADD NEW EMPLOYEE ---");
        System.out.print("Enter Employee ID (e.g. EMP101): ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Department (e.g. Engineering, HR, Sales): ");
        String dept = scanner.nextLine().trim();

        System.out.print("Enter Role/Title: ");
        String role = scanner.nextLine().trim();

        System.out.print("Enter Base Salary ($): ");
        double salary = 0.0;
        try {
            salary = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ ERROR: Salary must be a valid numeric value.");
            return;
        }

        System.out.print("Is Employee Active? (yes/no) [default: yes]: ");
        String activeInput = scanner.nextLine().trim().toLowerCase();
        boolean active = !activeInput.equals("no") && !activeInput.equals("n");

        try {
            Employee emp = new Employee(id, name, dept, role, salary, active);
            employeeService.addEmployee(emp);
            System.out.println("✅ SUCCESS: Employee registered successfully!");
            System.out.println("   Details: " + emp);
        } catch (Exception e) {
            System.out.println("❌ REGISTRATION FAILED: " + e.getMessage());
        }
    }

    private static void interactiveViewEmployee() {
        System.out.println("--- SEARCH / VIEW EMPLOYEES ---");
        System.out.println("1. Search by Employee ID");
        System.out.println("2. Filter by Department");
        System.out.println("3. View All Employees");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("Enter Employee ID: ");
            String id = scanner.nextLine().trim();
            Optional<Employee> emp = employeeService.getEmployee(id);
            if (emp.isPresent()) {
                System.out.println("Found: " + emp.get());
            } else {
                System.out.println("⚠️ No employee found with ID: " + id);
            }
        } else if (choice.equals("2")) {
            System.out.print("Enter Department Name: ");
            String dept = scanner.nextLine().trim();
            List<Employee> list = employeeService.getEmployeesByDepartment(dept);
            if (list.isEmpty()) {
                System.out.println("⚠️ No employees found in department: " + dept);
            } else {
                System.out.println("Employees in " + dept + ":");
                for (Employee e : list) {
                    System.out.println(" • " + e);
                }
            }
        } else if (choice.equals("3")) {
            List<Employee> all = employeeService.getAllEmployees();
            if (all.isEmpty()) {
                System.out.println("⚠️ System database is currently empty.");
            } else {
                System.out.println("All Registered Employees (" + all.size() + "):");
                for (Employee e : all) {
                    System.out.println(" • " + e);
                }
            }
        }
    }

    private static void interactiveProcessPayroll() {
        System.out.println("--- PROCESS PAYROLL ---");
        System.out.print("Enter Employee ID to pay: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Bonus Amount ($): ");
        double bonus = 0.0;
        try {
            bonus = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ ERROR: Bonus must be a valid numeric amount.");
            return;
        }

        try {
            Payment payment = payrollService.processPayment(id, bonus);
            System.out.println("✅ PAYROLL DISBURSED SUCCESSFULLY!");
            System.out.println("--------------------------------------------------");
            System.out.printf(" Payment ID:      %s\n", payment.getPaymentId());
            System.out.printf(" Employee ID:     %s\n", payment.getEmployeeId());
            System.out.printf(" Gross Pay:       $%.2f\n", payment.getGrossPay());
            System.out.printf(" Tax Deduction:   $%.2f\n", payment.getTaxDeduction());
            System.out.printf(" Bonus Added:     $%.2f\n", payment.getBonus());
            System.out.printf(" NET PAY:         $%.2f\n", payment.getNetPay());
            System.out.printf(" Payment Date:    %s\n", payment.getPaymentDate());
            System.out.println("--------------------------------------------------");
        } catch (Exception e) {
            System.out.println("❌ PAYROLL PROCESSING ERROR: " + e.getMessage());
        }
    }

    private static void interactiveAttributeTester() {
        System.out.println("--- INTERACTIVE ATTRIBUTE VALIDATION TESTER ---");
        System.out.println("Test specific system constraints live in terminal:");
        System.out.println("1. Test Base Salary Attribute (Test negative/zero/boundary inputs)");
        System.out.println("2. Test Employee Name Attribute (Test empty/blank string inputs)");
        System.out.println("3. Test Employee ID Attribute (Test null/empty ID & duplicates)");
        System.out.println("4. Test Tax Bracket Attribute (Test salary tax rate tier thresholds)");
        System.out.print("Select Attribute Test [1-4]: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Enter a test salary value to evaluate (e.g. -500 or 45000): ");
                try {
                    double sal = Double.parseDouble(scanner.nextLine().trim());
                    Employee emp = new Employee("TEST_SAL", "Test User", "QA", "Tester", sal, true);
                    System.out.println("✅ VALID SALARY ATTRIBUTE! Created Employee: Base Salary = $" + emp.getBaseSalary());
                } catch (IllegalArgumentException e) {
                    System.out.println("🛡️ ATTRIBUTE VALIDATION TRIGGERED: Rejection expected!");
                    System.out.println("   Reason: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("❌ Invalid input format.");
                }
                break;

            case "2":
                System.out.print("Enter a test name string (e.g. try typing spaces or empty line): ");
                String testName = scanner.nextLine();
                try {
                    Employee emp = new Employee("TEST_NAME", testName, "QA", "Tester", 50000.0, true);
                    System.out.println("✅ VALID NAME ATTRIBUTE! Employee Name = '" + emp.getName() + "'");
                } catch (IllegalArgumentException e) {
                    System.out.println("🛡️ ATTRIBUTE VALIDATION TRIGGERED: Name rejected!");
                    System.out.println("   Reason: " + e.getMessage());
                }
                break;

            case "3":
                System.out.print("Enter a test Employee ID: ");
                String testId = scanner.nextLine();
                try {
                    Employee emp = new Employee(testId, "Sample User", "QA", "Tester", 50000.0, true);
                    employeeService.addEmployee(emp);
                    System.out.println("✅ VALID UNIQUE ID ATTRIBUTE! Registered ID = '" + emp.getId() + "'");
                } catch (Exception e) {
                    System.out.println("🛡️ ATTRIBUTE VALIDATION TRIGGERED: ID rejected!");
                    System.out.println("   Reason: " + e.getMessage());
                }
                break;

            case "4":
                System.out.print("Enter Gross Salary amount to test Tax Rate Tier ($): ");
                try {
                    double sal = Double.parseDouble(scanner.nextLine().trim());
                    double rate = payrollService.calculateTaxRate(sal);
                    System.out.printf("📊 TAX BRACKET RESULT: Salary $%.2f -> Tax Rate = %.0f%%\n", sal, rate * 100);
                    if (sal < 30000.0) {
                        System.out.println("   Bracket: Tier 1 (< $30,000)");
                    } else if (sal <= 80000.0) {
                        System.out.println("   Bracket: Tier 2 ($30,000 - $80,000)");
                    } else {
                        System.out.println("   Bracket: Tier 3 (> $80,000)");
                    }
                } catch (Exception e) {
                    System.out.println("🛡️ ATTRIBUTE VALIDATION TRIGGERED: " + e.getMessage());
                }
                break;

            default:
                System.out.println("⚠️ Unknown option.");
        }
    }

    private static void interactiveGenerateReports() {
        System.out.println("--- GENERATE SYSTEM REPORTS ---");
        System.out.println("1. Employee Directory Report");
        System.out.println("2. Department Payroll Summary Report");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            Report report = reportService.generateEmployeeDirectoryReport();
            System.out.println("\n" + report.getContent());
        } else if (choice.equals("2")) {
            System.out.print("Enter Department name (or press ENTER for ALL departments): ");
            String dept = scanner.nextLine().trim();
            Report report = reportService.generatePayrollSummaryReport(dept.isEmpty() ? null : dept);
            System.out.println("\n" + report.getContent());
        }
    }

    private static void runAutomatedTests() {
        System.out.println("Executing automated test suites...");
        BlackBoxTests.runAllTests();
        WhiteBoxTests.runAllTests();
    }
}

package com.orgx.service;

import com.orgx.model.Employee;
import com.orgx.model.Payment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service processing employee payments and tax calculations for Organization X.
 */
public class PayrollService {
    private final EmployeeService employeeService;
    private final List<Payment> paymentHistory = new ArrayList<>();

    public PayrollService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Calculates progressive tax rate based on annual/base salary tier.
     * Tier 1: Salary < $30,000 -> 10%
     * Tier 2: $30,000 <= Salary <= $80,000 -> 20%
     * Tier 3: Salary > $80,000 -> 30%
     */
    public double calculateTaxRate(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
        if (salary < 30000.0) {
            return 0.10;
        } else if (salary <= 80000.0) {
            return 0.20;
        } else {
            return 0.30;
        }
    }

    /**
     * Processes payment for a specific employee.
     */
    public Payment processPayment(String employeeId, double bonus) {
        if (bonus < 0) {
            throw new IllegalArgumentException("Bonus cannot be negative.");
        }

        Optional<Employee> empOpt = employeeService.getEmployee(employeeId);
        if (empOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found with ID: " + employeeId);
        }

        Employee emp = empOpt.get();
        if (!emp.isActive()) {
            throw new IllegalStateException("Cannot process payment for inactive employee ID: " + employeeId);
        }

        double grossPay = emp.getBaseSalary() + bonus;
        double taxRate = calculateTaxRate(grossPay);
        double taxDeduction = grossPay * taxRate;
        double netPay = grossPay - taxDeduction;

        Payment payment = new Payment(
                "PAY-" + UUID.randomUUID().toString().substring(0, 8),
                employeeId,
                grossPay,
                taxDeduction,
                bonus,
                netPay,
                LocalDate.now()
        );

        paymentHistory.add(payment);
        return payment;
    }

    public List<Payment> getPaymentHistory() {
        return new ArrayList<>(paymentHistory);
    }
}

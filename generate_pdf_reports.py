import os
from reportlab.lib.pagesizes import letter, landscape
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.platypus import (BaseDocTemplate, PageTemplate, Frame, NextPageTemplate,
                                Paragraph, Spacer, Table, TableStyle, PageBreak,
                                KeepTogether, Image)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

# ------------------------------------------------------------------
# YOUR DETAILS (edit here)
# ------------------------------------------------------------------
STUDENT_NAME = "Solide AZE"
STUDENT_ID = "27396"
COURSE_NAME = "STT Group-E"
LANGUAGE = "Java 17"
TEST_FRAMEWORK = "JUnit 5"
GITHUB_LINK = "https://github.com/your-username/your-repo"  # TODO: replace with your repo URL

# Logo file must be in the same folder as this script
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
LOGO_PATH = os.path.join(SCRIPT_DIR, "/home/Llego/Documents/course/ST/homework/auca_logo.png")

BLUE = colors.HexColor('#1F497D')
LIGHT_BLUE = colors.HexColor('#DCE6F2')


# ------------------------------------------------------------------
# PAGE HEADER (all pages except the cover)
# ------------------------------------------------------------------
def draw_header(canvas, doc):
    canvas.saveState()
    page_w, page_h = canvas._pagesize
    margin = 0.5 * inch
    y = page_h - 0.4 * inch

    canvas.setFont('Times-Bold', 10)
    canvas.setFillColor(BLUE)
    canvas.drawString(margin, y, f"Name: {STUDENT_NAME}")
    canvas.drawCentredString(page_w / 2, y, f"ID: {STUDENT_ID}")
    canvas.drawRightString(page_w - margin, y, f"Course: {COURSE_NAME}")

    canvas.setStrokeColor(BLUE)
    canvas.setLineWidth(0.75)
    canvas.line(margin, y - 6, page_w - margin, y - 6)

    # Page number at the bottom
    canvas.setFont('Times-Roman', 9)
    canvas.setFillColor(colors.HexColor('#595959'))
    canvas.drawCentredString(page_w / 2, 0.3 * inch, f"Page {doc.page}")
    canvas.restoreState()


def create_blackbox_pdf(output_path):
    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    # ---------------- Page templates: portrait cover/intro + landscape tables
    portrait_size = letter
    landscape_size = landscape(letter)

    doc = BaseDocTemplate(output_path, pagesize=portrait_size,
                          title="Software Testing Assignment - Black-Box Test Report",
                          author=STUDENT_NAME)

    cover_frame = Frame(1 * inch, 0.8 * inch,
                        portrait_size[0] - 2 * inch, portrait_size[1] - 1.6 * inch, id='cover')
    portrait_frame = Frame(1 * inch, 0.7 * inch,
                           portrait_size[0] - 2 * inch, portrait_size[1] - 1.5 * inch, id='portrait')
    landscape_frame = Frame(0.5 * inch, 0.6 * inch,
                            landscape_size[0] - 1 * inch, landscape_size[1] - 1.35 * inch, id='landscape')

    doc.addPageTemplates([
        PageTemplate(id='Cover', frames=[cover_frame], pagesize=portrait_size),
        PageTemplate(id='Portrait', frames=[portrait_frame], pagesize=portrait_size, onPage=draw_header),
        PageTemplate(id='Landscape', frames=[landscape_frame], pagesize=landscape_size, onPage=draw_header),
    ])

    # ---------------- Styles (Times, like the sample)
    styles = getSampleStyleSheet()

    cover_title = ParagraphStyle('CoverTitle', parent=styles['Normal'], fontName='Times-Bold',
                                 fontSize=14, leading=18, alignment=1)
    cover_text = ParagraphStyle('CoverText', parent=styles['Normal'], fontName='Times-Roman',
                                fontSize=12, leading=16, alignment=1)
    body = ParagraphStyle('Body', parent=styles['Normal'], fontName='Times-Roman',
                          fontSize=11, leading=14, alignment=4, spaceAfter=6)
    section_heading = ParagraphStyle('SecHeading', parent=styles['Normal'], fontName='Times-Bold',
                                     fontSize=12, leading=15, spaceBefore=14, spaceAfter=4)
    case_heading = ParagraphStyle('CaseHeading', parent=section_heading, textColor=BLUE)

    cell = ParagraphStyle('Cell', parent=styles['Normal'], fontName='Times-Roman', fontSize=10, leading=12)
    cell_bold = ParagraphStyle('CellBold', parent=cell, fontName='Times-Bold')
    th_style = ParagraphStyle('TH', parent=cell, fontName='Times-Bold', textColor=colors.white, alignment=1)
    tb_style = ParagraphStyle('TB', parent=cell, fontSize=9, leading=11)
    tb_bold = ParagraphStyle('TBB', parent=tb_style, fontName='Times-Bold')
    pass_style = ParagraphStyle('Pass', parent=tb_style, fontName='Times-Bold',
                                textColor=colors.HexColor('#2E7D32'), alignment=1)

    simple_grid = [
        ('GRID', (0, 0), (-1, -1), 0.5, colors.black),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
    ]

    # ---------------- Test data
    cases_data = [
        ("TC-BB-01", "FR-1", "Valid Employee Creation", "Equivalence Partitioning (Valid)",
         "ID='EMP001', Name='Alice Smith', Dept='Engineering', Salary=65000.0, Active=true",
         "Employee record stored; retrievable by ID without errors.",
         "Employee record stored & retrieved matching name 'Alice Smith' and $65,000.00 base salary.", "PASS"),

        ("TC-BB-02", "FR-1", "Invalid Base Salary Validation", "Boundary Value Analysis (Boundary: -$500.00)",
         "ID='EMP002', Name='Bob Jones', BaseSalary=-500.0",
         "System throws IllegalArgumentException ('Base salary cannot be negative.')",
         "IllegalArgumentException caught successfully. Record creation blocked.", "PASS"),

        ("TC-BB-03", "FR-1", "Empty Employee Name Validation", "Equivalence Partitioning (Invalid)",
         "ID='EMP003', Name='   ' (spaces), BaseSalary=50000.0",
         "System throws IllegalArgumentException ('Employee name cannot be null or empty.')",
         "IllegalArgumentException caught successfully. Record creation blocked.", "PASS"),

        ("TC-BB-04", "FR-2", "Valid Payroll Net Pay Calculation", "Equivalence Partitioning (Standard Payroll)",
         "Salary=50000.0, Bonus=10000.0 (Gross=60000.0 -> Tax Tier 20%)",
         "Gross=$60,000.00, Tax=$12,000.00, Net Pay=$48,000.00",
         "Payment generated with Gross=$60,000.00, Tax=$12,000.00, Net Pay=$48,000.00.", "PASS"),

        ("TC-BB-05", "FR-2", "Tax Bracket Threshold Boundary", "Boundary Value Analysis ($30,000 Boundary)",
         "Salary 1 = $29,999.99; Salary 2 = $30,000.00",
         "Salary 1 yields 10% tax rate; Salary 2 yields 20% tax rate.",
         "Rate for $29,999.99 = 0.10 (10%); Rate for $30,000.00 = 0.20 (20%). Boundary verified.", "PASS"),

        ("TC-BB-06", "FR-2", "Payment for Inactive Employee", "Equivalence Partitioning (Inactive State)",
         "Employee ID='EMP006', Active=false, Bonus=0.0",
         "System throws IllegalStateException ('Cannot process payment for inactive employee')",
         "IllegalStateException caught cleanly. Payment disbursement blocked.", "PASS"),

        ("TC-BB-07", "FR-3", "Report Generation on Empty Database", "Equivalence Partitioning (Empty Dataset)",
         "Employee Database with 0 records.",
         "Report generated containing 'No employee records found in system.'",
         "Report generated clean with zero errors, outputting graceful empty status message.", "PASS")
    ]
    passed = sum(1 for c in cases_data if c[7] == "PASS")
    total = len(cases_data)

    story = [NextPageTemplate('Portrait')]   # page after the cover uses the header

    # ==================================================================
    # COVER PAGE
    # ==================================================================
    story.append(Spacer(1, 0.6 * inch))
    if os.path.exists(LOGO_PATH):
        story.append(Image(LOGO_PATH, width=4.9 * inch, height=4.9 * inch * 108 / 468))
    else:
        story.append(Paragraph("ADVENTIST UNIVERSITY OF CENTRAL AFRICA", cover_title))
    story.append(Spacer(1, 0.7 * inch))

    story.append(Paragraph("SOFTWARE TESTING ASSIGNMENT", cover_title))
    story.append(Paragraph("Employee Management System", cover_title))
    story.append(Paragraph("Black-Box Test Cases Report", cover_title))
    story.append(Spacer(1, 0.5 * inch))

    story.append(Paragraph(f"Prepared by: {STUDENT_NAME}", cover_text))
    story.append(Paragraph(f"ID: {STUDENT_ID}", cover_text))
    story.append(Paragraph(f"Course: {COURSE_NAME}", cover_text))
    story.append(Spacer(1, 0.4 * inch))

    info_rows = [
        [Paragraph("Item", cell_bold), Paragraph("Link / detail", cell_bold)],
        [Paragraph("GitHub repository", cell), Paragraph(GITHUB_LINK, cell)],
        [Paragraph("Language", cell), Paragraph(LANGUAGE, cell)],
        [Paragraph("Testing framework", cell), Paragraph(TEST_FRAMEWORK, cell)],
        [Paragraph("Test result", cell), Paragraph(f"{passed} / {total} test cases passed", cell)],
    ]
    info_table = Table(info_rows, colWidths=[1.6 * inch, 4.5 * inch])
    info_table.setStyle(TableStyle(simple_grid))
    story.append(info_table)

    story.append(PageBreak())

    # ==================================================================
    # 1. INTRODUCTION
    # ==================================================================
    story.append(Paragraph("1. Introduction", section_heading))
    story.append(Paragraph(
        "This report documents the black-box testing of an Employee Management System built for "
        "Organization X. The system registers employees, calculates their payroll (including tax), "
        "and produces employee reports. Test cases were designed from the functional requirements "
        "only, without looking at the source code, using Equivalence Partitioning and Boundary "
        "Value Analysis.", body))

    # ==================================================================
    # 2. FUNCTIONAL REQUIREMENTS
    # ==================================================================
    story.append(Paragraph("2. Functional Requirements", section_heading))
    story.append(Paragraph("The test cases in this report are traced to the following requirements:", body))

    fr_rows = [
        [Paragraph("ID", cell_bold), Paragraph("Functional requirement", cell_bold),
         Paragraph("Simple explanation", cell_bold)],
        [Paragraph("FR-1", cell), Paragraph("Manage employee records", cell),
         Paragraph("The system shall register a valid employee and retrieve it by ID. The employee "
                   "name must not be empty and the base salary cannot be negative.", cell)],
        [Paragraph("FR-2", cell), Paragraph("Process payroll", cell),
         Paragraph("The system shall compute gross pay (salary + bonus), apply a 10% tax below "
                   "$30,000 and 20% from $30,000 upward, and return the net pay. Inactive "
                   "employees cannot be paid.", cell)],
        [Paragraph("FR-3", cell), Paragraph("Generate report", cell),
         Paragraph("The system shall produce a report of employees, and clearly state when no "
                   "employee records exist.", cell)],
    ]
    fr_table = Table(fr_rows, colWidths=[0.7 * inch, 1.9 * inch, 3.9 * inch])
    fr_table.setStyle(TableStyle(simple_grid + [
        ('BACKGROUND', (0, 0), (-1, 0), LIGHT_BLUE),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
    ]))
    story.append(fr_table)

    # ==================================================================
    # 3. IMPLEMENTATION & TEST ENVIRONMENT
    # ==================================================================
    story.append(Paragraph("3. Implementation & Test Environment", section_heading))
    env_rows = [
        [Paragraph("Language", cell_bold), Paragraph(LANGUAGE, cell)],
        [Paragraph("Testing framework", cell_bold), Paragraph(TEST_FRAMEWORK, cell)],
        [Paragraph("Test type", cell_bold), Paragraph("Black-box (functional) testing", cell)],
        [Paragraph("Techniques", cell_bold),
         Paragraph("Equivalence Partitioning, Boundary Value Analysis", cell)],
    ]
    env_table = Table(env_rows, colWidths=[1.9 * inch, 4.6 * inch])
    env_table.setStyle(TableStyle(simple_grid))
    story.append(env_table)

    # Switch to landscape for the wide tables
    story.append(NextPageTemplate('Landscape'))
    story.append(PageBreak())

    # ==================================================================
    # 4. BLACK-BOX TEST CASES SUMMARY
    # ==================================================================
    story.append(Paragraph("4. Black-Box Testing (Functional) - Summary Table", section_heading))

    summary_headers = [Paragraph(h, th_style) for h in (
        "Test Case ID", "Req ID", "Test Description & Scope", "Technique",
        "Input Specifications", "Expected Output", "Actual Output", "Status")]

    table_rows = [summary_headers]
    for cid, req, desc, tech, inputs, exp, act, status in cases_data:
        table_rows.append([
            Paragraph(cid, tb_bold), Paragraph(req, tb_bold), Paragraph(desc, tb_style),
            Paragraph(tech, tb_style), Paragraph(inputs, tb_style), Paragraph(exp, tb_style),
            Paragraph(act, tb_style), Paragraph(status, pass_style)
        ])

    col_widths = [0.8*inch, 0.6*inch, 1.4*inch, 1.3*inch, 2.0*inch, 1.7*inch, 1.6*inch, 0.6*inch]
    summary_table = Table(table_rows, colWidths=col_widths, repeatRows=1)
    summary_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), BLUE),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#A0A0A0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor('#F5F8FC')]),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
    ]))
    story.append(summary_table)
    story.append(PageBreak())

    # ==================================================================
    # 5. DETAILED TEST CASE REPORTS
    # ==================================================================
    story.append(Paragraph("5. Detailed Black-Box Test Case Reports", section_heading))
    story.append(Paragraph("Each report follows the structure of the test-case-report template.", body))

    for cid, req, desc, tech, inputs, exp, act, status in cases_data:
        rows = [
            [Paragraph("GENERAL INFORMATION", th_style), ""],
            [Paragraph("Test Stage", tb_bold), Paragraph("[X] Functionality   [ ] Unit   [ ] Integration", tb_style)],
            [Paragraph("Test Date / System Date", tb_bold), Paragraph("09/22/2026", tb_style)],
            [Paragraph("Tester Name", tb_bold), Paragraph(STUDENT_NAME, tb_style)],
            [Paragraph("Test Case Number", tb_bold), Paragraph(cid, tb_bold)],
            [Paragraph("Test Case Description", tb_bold), Paragraph(desc, tb_style)],
            [Paragraph("Test Technique", tb_bold), Paragraph(tech, tb_style)],
            [Paragraph("Result Status", tb_bold),
             Paragraph(f"[{'X' if status == 'PASS' else ' '}] Pass   [{'X' if status == 'FAIL' else ' '}] Fail", pass_style)],
            [Paragraph("Incident Number", tb_bold), Paragraph("N/A", tb_style)],

            [Paragraph("INTRODUCTION & REQUIREMENTS", th_style), ""],
            [Paragraph("Requirement(s) Tested", tb_bold), Paragraph(f"{req}: Functional Specification Requirement", tb_style)],
            [Paragraph("Roles & Responsibilities", tb_bold),
             Paragraph("Test Engineer: Execute functional cases.<br/>Developer: Fix identified defects.", tb_style)],
            [Paragraph("Setup & Stop Procedures", tb_bold),
             Paragraph("Setup: Initialize JDK 17 & Service Repositories.<br/>Stop: Tear down test context.", tb_style)],

            [Paragraph("TEST SPECIFICATIONS & RESULTS", th_style), ""],
            [Paragraph("Input Specifications", tb_bold), Paragraph(inputs, tb_style)],
            [Paragraph("Expected Results", tb_bold), Paragraph(exp, tb_style)],
            [Paragraph("Actual Results", tb_bold), Paragraph(act, tb_style)],
            [Paragraph("Final Disposition", tb_bold),
             Paragraph("PASS - All functional assertion criteria satisfied with 0 defects.", pass_style)],
        ]

        detail_table = Table(rows, colWidths=[2.2 * inch, 7.8 * inch])
        detail_table.setStyle(TableStyle([
            ('SPAN', (0, 0), (1, 0)), ('BACKGROUND', (0, 0), (1, 0), BLUE),
            ('SPAN', (0, 9), (1, 9)), ('BACKGROUND', (0, 9), (1, 9), BLUE),
            ('SPAN', (0, 13), (1, 13)), ('BACKGROUND', (0, 13), (1, 13), BLUE),
            ('BACKGROUND', (0, 1), (0, 8), colors.HexColor('#F2F2F2')),
            ('BACKGROUND', (0, 10), (0, 12), colors.HexColor('#F2F2F2')),
            ('BACKGROUND', (0, 14), (0, 17), colors.HexColor('#F2F2F2')),
            ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#A0A0A0')),
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
            ('TOPPADDING', (0, 0), (-1, -1), 4),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ]))

        story.append(KeepTogether([
            Paragraph(f"TEST CASE REPORT: {cid} - {desc}", case_heading),
            detail_table,
            Spacer(1, 12),
        ]))

    doc.build(story)
    print(f"PDF Black-Box report generated successfully: {output_path}")


create_blackbox_pdf("/home/Llego/Documents/course/ST/homework/reports/27396-Solide-AZE.pdf")
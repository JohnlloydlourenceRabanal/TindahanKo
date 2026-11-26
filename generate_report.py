import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch

def create_report(output_filename):
    doc = SimpleDocTemplate(
        output_filename,
        pagesize=letter,
        leftMargin=36,
        rightMargin=36,
        topMargin=32,
        bottomMargin=32
    )

    styles = getSampleStyleSheet()
    
    # Custom Palette: TindahanKo Brand Colors
    PRIMARY = colors.HexColor('#163C63')   # Deep Navy
    SECONDARY = colors.HexColor('#205A88') # Slate Blue
    ACCENT = colors.HexColor('#0D9488')    # Teal
    DARK_TEXT = colors.HexColor('#0F172A')
    MUTED_TEXT = colors.HexColor('#475569')
    LIGHT_BG = colors.HexColor('#F8FAFC')
    BORDER_COLOR = colors.HexColor('#CBD5E1')
    WHITE = colors.HexColor('#FFFFFF')

    # Typography Styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Heading1'],
        fontName='Helvetica-Bold',
        fontSize=14,
        leading=18,
        textColor=PRIMARY,
        alignment=0,
        spaceAfter=2
    )

    subtitle_style = ParagraphStyle(
        'DocSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=MUTED_TEXT,
        alignment=0,
        spaceAfter=6
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        fontName='Helvetica-Bold',
        fontSize=10.5,
        leading=14,
        textColor=PRIMARY,
        spaceBefore=7,
        spaceAfter=3,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2_Custom',
        fontName='Helvetica-Bold',
        fontSize=9,
        leading=12.5,
        textColor=SECONDARY,
        spaceBefore=5,
        spaceAfter=2.5,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        fontName='Helvetica',
        fontSize=7.8,
        leading=11,
        textColor=DARK_TEXT,
        spaceAfter=3
    )

    body_bold = ParagraphStyle(
        'Body_Bold',
        parent=body_style,
        fontName='Helvetica-Bold'
    )

    th_style = ParagraphStyle(
        'TH_Custom',
        fontName='Helvetica-Bold',
        fontSize=7.2,
        leading=9.5,
        textColor=WHITE
    )

    td_style = ParagraphStyle(
        'TD_Custom',
        fontName='Helvetica',
        fontSize=7.2,
        leading=9.5,
        textColor=DARK_TEXT
    )

    td_bold = ParagraphStyle(
        'TD_Bold',
        parent=td_style,
        fontName='Helvetica-Bold'
    )

    td_code = ParagraphStyle(
        'TD_Code',
        parent=td_style,
        fontName='Courier',
        fontSize=6.8,
        leading=9,
        textColor=colors.HexColor('#0F766E')
    )

    elements = []

    # Title & Header
    elements.append(Paragraph("IT342 – Systems Integration and Architecture 1", title_style))
    elements.append(Paragraph("Project Development 2: Implementation / Completion Report<br/><b>Core Feature (Vertical Slice)</b> – User Profile Management & Multi-Channel Password Recovery", subtitle_style))
    elements.append(HRFlowable(width="100%", thickness=1.5, color=PRIMARY, spaceBefore=0, spaceAfter=6))

    # Project Information Table
    info_data = [
        [Paragraph("System Name", td_bold), Paragraph("TindahanKo (Sari-Sari Store Inventory & Credit Management)", td_style),
         Paragraph("Student Name", td_bold), Paragraph("Rabanal, John Lloyd Lourence C.", td_style)],
        [Paragraph("Section", td_bold), Paragraph("IT342 - BSIT 3-A", td_style),
         Paragraph("Increment", td_bold), Paragraph("Project Development 2 – Core Feature (Vertical Slice)", td_style)],
        [Paragraph("GitHub Repository", td_bold), Paragraph("https://github.com/JohnlloydlourenceRabanal/TindahanKo", td_code),
         Paragraph("Date Submitted", td_bold), Paragraph("11/26/2026", td_style)]
    ]
    t_info = Table(info_data, colWidths=[100, 170, 90, 180])
    t_info.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), LIGHT_BG),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
    ]))
    elements.append(t_info)
    elements.append(Spacer(1, 5))

    # 1. Project Overview
    elements.append(Paragraph("1. Project Overview", h1_style))
    elements.append(Paragraph(
        "<b>TindahanKo</b> is a modern, responsive web application designed for community sari-sari stores in the Philippines to replace vulnerable paper notebooks (<i>kuwaderno</i>) with an automated, cloud-backed inventory, point-of-sale, and customer credit ledger system. The platform serves two primary actors: <b>Store Owners</b> who operate the storefront, maintain inventory, and record cash/credit purchases; and <b>Store Customers</b> who monitor running debt balances, credit limits, and payment schedules. By modernizing these manual practices, TindahanKo eliminates credit ledger disputes, mitigates bad debts, and enhances store liquidity.",
        body_style
    ))
    elements.append(Paragraph("<b>Core Feature for this Increment:</b> User Profile Management, Multi-Channel Password Recovery (Email & SMS), and Account Security Management.", body_style))
    elements.append(Paragraph("<b>Justification:</b> User Profile and Password Recovery constitutes the essential security pillar of TindahanKo. It guarantees that both Store Owners and Customer borrowers maintain complete control over their registered credentials, legal identities, and verified contact channels, while enabling frictionless, self-service account recovery via secure single-use 15-minute tokens delivered over Gmail SMTP and Semaphore SMS without requiring store admin intervention.", body_style))
    elements.append(Spacer(1, 5))

    # 2. Features Implemented in This Increment
    elements.append(Paragraph("2. Features Implemented in This Increment", h1_style))
    elements.append(Paragraph("2.1 Standard Features (from Project Development 1)", h2_style))
    
    std_data = [
        [Paragraph("Feature", th_style), Paragraph("Status", th_style), Paragraph("Implementation & Operational Notes", th_style)],
        [Paragraph("Account Registration", td_bold), Paragraph("Complete", td_bold), Paragraph("Public self-registration with duplicate validation for usernames and Philippine mobile numbers (<code>^(09|\\+639)\\d{9}$</code>), optional email registration, and BCrypt work factor 10 hashing.", td_style)],
        [Paragraph("Login / Logout", td_bold), Paragraph("Complete", td_bold), Paragraph("Stateless authentication using JJWT (HS512), client React Context session persistence, clean logout token purging, and secure HTTP 401/403 handlers.", td_style)],
        [Paragraph("Role-Based User Dashboard", td_bold), Paragraph("Complete", td_bold), Paragraph("Dedicated command hubs for <b>STORE_OWNER</b> (management metrics) and <b>CUSTOMER</b> (credit/utang overview), guarded by frontend route guards and backend <code>@PreAuthorize</code>.", td_style)],
        [Paragraph("User Profile (View & Edit)", td_bold), Paragraph("Complete", td_bold), Paragraph("Authenticated endpoints allowing users to review personal details and update Full Name, Philippine mobile number, and email address with strict uniqueness validation.", td_style)]
    ]
    t_std = Table(std_data, colWidths=[120, 60, 360])
    t_std.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), PRIMARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
    ]))
    elements.append(t_std)
    elements.append(Spacer(1, 5))

    # 2.2 Core Feature
    elements.append(Paragraph("2.2 Core Feature: User Profile Management & Multi-Factor Password Recovery", h2_style))
    elements.append(Paragraph(
        "<b>Description:</b> This module delivers comprehensive profile self-management, in-app authenticated password changes, and an unauthenticated multi-channel identity verification and password recovery engine. Users can flexibly choose between receiving single-use 15-minute recovery tokens (<code>TK-XXXXXXXX</code>) via <b>Google Gmail SMTP</b> or <b>Semaphore SMS API</b>, followed by automated security alert dispatch upon successful password updates.",
        body_style
    ))

    # User Roles and Access
    elements.append(Paragraph("User Roles and Access", body_bold))
    roles_data = [
        [Paragraph("Role", th_style), Paragraph("What this role can do in the feature", th_style)],
        [Paragraph("Store Owner (Admin)", td_bold), Paragraph("View and update personal store profile (full name, phone, email); update password with current-password verification; initiate self-service password recovery using registered Email or Philippine Mobile Number; receive security notices upon credential changes.", td_style)],
        [Paragraph("Store Customer (Debtor)", td_bold), Paragraph("View and update customer profile; edit registered mobile number and email; change account password; perform self-service password recovery via Email or SMS to regain access to personal credit ledger portal.", td_style)]
    ]
    t_roles = Table(roles_data, colWidths=[120, 420])
    t_roles.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), SECONDARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
    ]))
    elements.append(t_roles)
    elements.append(Spacer(1, 5))

    # User Flow
    elements.append(Paragraph("User Flow (Step-by-Step)", body_bold))
    elements.append(Paragraph("<b>Step 1: Choose Recovery Method:</b> Unauthenticated user navigates to <code>/forgot-password</code> and toggles between <b>Via Email (Gmail)</b> or <b>Via Mobile Number (SMS)</b>.", body_style))
    elements.append(Paragraph("<b>Step 2: Multi-Factor Identity Verification:</b> User inputs their Account Username, Registered Full Name, and corresponding registered Email Address or Philippine Mobile Number.", body_style))
    elements.append(Paragraph("<b>Step 3: Verification Code Dispatch:</b> Backend verifies matching credentials against database; invalidates prior tokens; generates an 8-character token (<code>TK-XXXXXXXX</code>) valid for 15 minutes; and dispatches the code via Google Gmail SMTP (or Semaphore SMS).", body_style))
    elements.append(Paragraph("<b>Step 4: Token Submission & New Password:</b> User enters the received verification code, sets their new password (min 8 characters), and confirms password.", body_style))
    elements.append(Paragraph("<b>Step 5: Cryptographic Update & Security Alert:</b> Backend validates token, hashes new password with BCrypt (factor 10), consumes token (<code>used=true</code>), and sends a live security notification to user's Gmail confirming the change.", body_style))
    elements.append(Spacer(1, 5))

    # CRUD Operations
    elements.append(Paragraph("CRUD Operations Summary", h2_style))
    crud_data = [
        [Paragraph("Operation", th_style), Paragraph("Implemented?", th_style), Paragraph("Where in the UI", th_style), Paragraph("Who can do it", th_style)],
        [Paragraph("Create (C)", td_bold), Paragraph("Yes", td_style), Paragraph("Forgot Password screen (<code>/forgot-password</code>) – issues single-use token in <code>password_reset_tokens</code>", td_style), Paragraph("Public / Any Registered User", td_style)],
        [Paragraph("Read (R)", td_bold), Paragraph("Yes", td_style), Paragraph("Profile Information Card (<code>/profile</code>) & Auth Me (<code>/api/auth/me</code>)", td_style), Paragraph("Store Owner / Customer", td_style)],
        [Paragraph("Update (U)", td_bold), Paragraph("Yes", td_style), Paragraph("Profile Edit Form (<code>/profile</code>) and Reset Password (<code>/reset-password</code>)", td_style), Paragraph("Store Owner / Customer", td_style)],
        [Paragraph("Delete (D)", td_bold), Paragraph("Yes", td_style), Paragraph("Automated single-use token invalidation & overwrite upon reset request", td_style), Paragraph("System Automated", td_style)]
    ]
    t_crud = Table(crud_data, colWidths=[80, 70, 260, 130])
    t_crud.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), PRIMARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
    ]))
    elements.append(t_crud)
    elements.append(Spacer(1, 5))

    # Database Models
    elements.append(Paragraph("Database Models & Schema Integration", h2_style))
    db_data = [
        [Paragraph("Model / Table", th_style), Paragraph("Key Fields", th_style), Paragraph("Relationships & Constraints", th_style)],
        [Paragraph("User<br/>(<code>users</code>)", td_bold), Paragraph("<code>user_id</code> (BIGSERIAL PK), <code>username</code> (VARCHAR 50 UK), <code>password_hash</code> (VARCHAR 255), <code>full_name</code> (VARCHAR 100), <code>mobile_number</code> (VARCHAR 15), <code>email</code> (VARCHAR 100), <code>role</code> (VARCHAR 20), <code>created_at</code> (TIMESTAMP)", td_code), Paragraph("1:N with <code>password_reset_tokens</code>. Role restricted to <code>STORE_OWNER</code> or <code>CUSTOMER</code>. Mobile number and username uniqueness strictly enforced.", td_style)],
        [Paragraph("PasswordResetToken<br/>(<code>password_reset_tokens</code>)", td_bold), Paragraph("<code>token_id</code> (BIGSERIAL PK), <code>user_id</code> (BIGINT FK), <code>token</code> (VARCHAR 100 UK), <code>expiry_date</code> (TIMESTAMP), <code>used</code> (BOOLEAN), <code>created_at</code> (TIMESTAMP)", td_code), Paragraph("Foreign Key &rarr; <code>users.user_id</code> with <code>ON DELETE CASCADE</code>. Single-use enforcement (<code>used=true</code>). Expiring lifetime: 15 minutes.", td_style)]
    ]
    t_db = Table(db_data, colWidths=[110, 250, 180])
    t_db.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), SECONDARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
    ]))
    elements.append(t_db)
    elements.append(Spacer(1, 5))

    # Key Files
    elements.append(Paragraph("Key Files Implemented / Modified", h2_style))
    files_data = [
        [Paragraph("Layer", th_style), Paragraph("File Path(s)", th_style), Paragraph("Purpose & Responsibilities", th_style)],
        [Paragraph("Entities & Repos", td_bold), Paragraph("<code>backend/.../entity/User.java</code><br/><code>backend/.../entity/PasswordResetToken.java</code><br/><code>backend/.../repository/UserRepository.java</code><br/><code>backend/.../repository/PasswordResetTokenRepository.java</code>", td_code), Paragraph("Defines user and token persistence models with JPA annotations, relationships, and queries.", td_style)],
        [Paragraph("DTOs", td_bold), Paragraph("<code>ForgotPasswordRequest.java</code>, <code>ResetPasswordRequest.java</code>,<br/><code>ChangePasswordRequest.java</code>, <code>ProfileUpdateRequest.java</code>,<br/><code>ProfileResponse.java</code>, <code>AuthResponse.java</code>, <code>UserDTO.java</code>", td_code), Paragraph("Encapsulates input payloads with Jakarta Validation annotations (format, regex, required fields).", td_style)],
        [Paragraph("Services", td_bold), Paragraph("<code>backend/.../service/AuthService.java</code><br/><code>backend/.../service/UserService.java</code><br/><code>backend/.../service/EmailService.java</code><br/><code>backend/.../service/SemaphoreService.java</code>", td_code), Paragraph("Multi-factor verification logic, token hashing, Gmail SMTP email dispatch, and Semaphore SMS API integration.", td_style)],
        [Paragraph("Controllers", td_bold), Paragraph("<code>backend/.../controller/AuthController.java</code><br/><code>backend/.../controller/UserController.java</code>", td_code), Paragraph("Exposes REST endpoints: <code>/api/auth/forgot-password</code>, <code>/api/auth/reset-password</code>, <code>/api/user/profile</code>.", td_style)],
        [Paragraph("Frontend UI", td_bold), Paragraph("<code>frontend/src/pages/ForgotPassword.jsx</code><br/><code>frontend/src/pages/Profile.jsx</code>, <code>Register.jsx</code><br/><code>frontend/src/api/auth.js</code>, <code>styles/index.css</code>", td_code), Paragraph("Interactive React views with recovery option switcher, input validation, and real-time alerts.", td_style)]
    ]
    t_files = Table(files_data, colWidths=[90, 250, 200])
    t_files.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), PRIMARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
    ]))
    elements.append(t_files)
    elements.append(Spacer(1, 5))

    # Validation and Business Rules
    elements.append(Paragraph("Validation and Business Rules", h2_style))
    rules_data = [
        [Paragraph("Rule", th_style), Paragraph("How it is enforced", th_style)],
        [Paragraph("Philippine Mobile Number Format", td_bold), Paragraph("Validated with regex <code>^(09|\\+639)\\d{9}$</code> on frontend and backend.", td_style)],
        [Paragraph("Mobile & Email Uniqueness", td_bold), Paragraph("Backend verifies <code>existsByMobileNumberAndUserIdNot</code> to prevent duplicate mobile claiming.", td_style)],
        [Paragraph("Multi-Factor Identity Matching", td_bold), Paragraph("Requires Username + Full Name + matching registered Email OR Phone Number before issuing tokens.", td_style)],
        [Paragraph("Single-Use Token Lifecycle", td_bold), Paragraph("Tokens expire after 15 minutes. Upon reset, <code>used=true</code> flag is committed; replay attacks return HTTP 400.", td_style)],
        [Paragraph("Password Policy & Differ Check", td_bold), Paragraph("Minimum 8 characters; matching confirmation; rejected if identical to current BCrypt hash.", td_style)],
        [Paragraph("Channel-Isolated Security Notices", td_bold), Paragraph("When Email is selected, only Gmail dispatch occurs and phone details are suppressed; security alert is sent on change.", td_style)]
    ]
    t_rules = Table(rules_data, colWidths=[150, 390])
    t_rules.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), SECONDARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
    ]))
    elements.append(t_rules)
    elements.append(Spacer(1, 5))

    # Branches and Pull Requests
    elements.append(Paragraph("Branches and Pull Requests", h2_style))
    branch_data = [
        [Paragraph("Branch", th_style), Paragraph("Merged Into", th_style), Paragraph("Pull Request Link", th_style), Paragraph("What it contains", th_style)],
        [Paragraph("feature/profile-password-mgmt", td_code), Paragraph("main", td_style), Paragraph("Merged into main (commit 5449361)", td_style), Paragraph("User Profile view/edit, Change Password, and single-use token password reset.", td_style)],
        [Paragraph("feature/sms-email-notification", td_code), Paragraph("main", td_style), Paragraph("Merged into main (commit d362950)", td_style), Paragraph("Integration of Semaphore SMS API and Google Gmail SMTP / Resend API dispatchers.", td_style)],
        [Paragraph("feature/email-mobile-options", td_code), Paragraph("main", td_style), Paragraph("Merged into main (commit c8087b8)", td_style), Paragraph("Flexible Email/SMS recovery selection, email registration, and security alert emails.", td_style)],
        [Paragraph("main", td_code), Paragraph("origin/main", td_style), Paragraph("https://github.com/JohnlloydlourenceRabanal/TindahanKo", td_code), Paragraph("Consolidated release for Increment 1 & 2 operational features.", td_style)]
    ]
    t_branch = Table(branch_data, colWidths=[120, 60, 160, 200])
    t_branch.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), PRIMARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
    ]))
    elements.append(t_branch)
    elements.append(Spacer(1, 5))

    # Screenshots & UI Verification Flow
    elements.append(Paragraph("Screenshots & User Flow Verification", h2_style))
    flow_steps = [
        [Paragraph("Flow Stage", th_style), Paragraph("Interface & Operational Details", th_style), Paragraph("Verification Output", th_style)],
        [Paragraph("1. Recovery Option Selection", td_bold), Paragraph("User visits <code>/forgot-password</code>. Interactive toggle switches between <b>Via Email (Gmail)</b> and <b>Via Mobile Number (SMS)</b> with reactive input fields.", td_style), Paragraph("Verified UI reactive state (Email and Phone toggles).", td_style)],
        [Paragraph("2. Identity Verification Input", td_bold), Paragraph("User inputs Account Username (<code>admin</code>), Registered Email (<code>akosirabanal@gmail.com</code>), and Full Name (<code>John Lloyd Lourence Rabanal</code>).", td_style), Paragraph("Mismatched data triggers inline error 400 Bad Request.", td_style)],
        [Paragraph("3. Single-Use Code Dispatch", td_bold), Paragraph("Backend generates <code>TK-XXXXXXXX</code> (15 min expiry) and dispatches real email via Gmail SMTP (<code>smtp.gmail.com:587</code>) with zero token leakage in HTTP response.", td_style), Paragraph("HTTP 200 OK: <code>liveEmailSent=true</code>, masked email displayed.", td_style)],
        [Paragraph("4. New Password Submission", td_bold), Paragraph("User provides received code and enters new password (min 8 chars) with matching confirmation. Validation rejects identical current password.", td_style), Paragraph("BCrypt work factor 10 hash saved to DB; token marked <code>used=true</code>.", td_style)],
        [Paragraph("5. Security Confirmation Alert", td_bold), Paragraph("Automated security confirmation email dispatched to recipient's Gmail inbox confirming credential update.", td_style), Paragraph("Verified delivery: <code>TindahanKo Security Alert - Password Changed</code>.", td_style)]
    ]
    t_flow = Table(flow_steps, colWidths=[120, 250, 170])
    t_flow.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), SECONDARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
    ]))
    elements.append(t_flow)
    elements.append(Spacer(1, 5))

    # 3. Remaining Tasks
    elements.append(Paragraph("3. Remaining Tasks", h1_style))
    tasks_data = [
        [Paragraph("No.", th_style), Paragraph("Task", th_style), Paragraph("Description", th_style), Paragraph("Priority", th_style), Paragraph("Target Increment", th_style)],
        [Paragraph("1", td_bold), Paragraph("Product Catalog & Inventory", td_bold), Paragraph("Full CRUD product catalog with pricing, reorder stock alerts, and category filtering.", td_style), Paragraph("High", td_bold), Paragraph("Project Development 3", td_style)],
        [Paragraph("2", td_bold), Paragraph("Point-of-Sale (POS) & Utang Ledger", td_bold), Paragraph("Dual-mode sales terminal recording cash transactions and customer credit lines with balance tracking.", td_style), Paragraph("High", td_bold), Paragraph("Project Development 3", td_style)],
        [Paragraph("3", td_bold), Paragraph("Automated Semaphore SMS Reminders", td_bold), Paragraph("Triggered SMS notifications dispatched when credit transactions occur or payment due dates arrive.", td_style), Paragraph("Medium", td_style), Paragraph("Project Development 3", td_style)],
        [Paragraph("4", td_bold), Paragraph("PayMongo Digital Payment Integration", td_bold), Paragraph("Online credit settlement via GCash and Maya e-wallets with automated webhook balance reconciliation.", td_style), Paragraph("High", td_bold), Paragraph("Project Development 4", td_style)],
        [Paragraph("5", td_bold), Paragraph("Sales & Receivables Analytics", td_bold), Paragraph("Daily sales charts, top customer debtors overview, and 1-click ledger export (PDF/Excel).", td_style), Paragraph("Medium", td_style), Paragraph("Project Development 4", td_style)]
    ]
    t_tasks = Table(tasks_data, colWidths=[24, 116, 230, 60, 110])
    t_tasks.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), PRIMARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
    ]))
    elements.append(t_tasks)
    elements.append(Spacer(1, 5))

    # 4. Outstanding Issues
    elements.append(Paragraph("4. Outstanding Issues", h1_style))
    elements.append(Paragraph(
        "<b>No known issues.</b> All unit and integration test suites are passing with zero errors. Multi-factor identity validation, single-use token lifecycle, Gmail SMTP email delivery, Semaphore SMS dispatch, and PostgreSQL/H2 database operations are fully operational and verified under live conditions.",
        body_style
    ))
    elements.append(Spacer(1, 5))

    # 5. Test Accounts
    elements.append(Paragraph("5. Test Accounts", h1_style))
    test_data = [
        [Paragraph("Role", th_style), Paragraph("Username", th_style), Paragraph("Password", th_style), Paragraph("Registered Contact", th_style), Paragraph("Purpose / Test Scope", th_style)],
        [Paragraph("Store Owner (Admin)", td_bold), Paragraph("admin", td_code), Paragraph("Password123", td_code), Paragraph("09914731143<br/>akosirabanal@gmail.com", td_style), Paragraph("Store owner command dashboard, profile management, and live Gmail recovery verification.", td_style)],
        [Paragraph("Store Customer", td_bold), Paragraph("customer_ana", td_code), Paragraph("CustPassword123", td_code), Paragraph("09205556677<br/>ana.reyes@gmail.com", td_style), Paragraph("Customer credit ledger portal, customer profile update, and SMS recovery testing.", td_style)],
        [Paragraph("Store Customer (Test)", td_bold), Paragraph("customer_juan", td_code), Paragraph("JuanPassword123", td_code), Paragraph("09170000000<br/>juan.delacruz@gmail.com", td_style), Paragraph("Self-registration and identity verification testing.", td_style)]
    ]
    t_test = Table(test_data, colWidths=[90, 80, 90, 130, 150])
    t_test.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), SECONDARY),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, LIGHT_BG]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
    ]))
    elements.append(t_test)
    elements.append(Spacer(1, 6))

    # 6. Declaration
    elements.append(KeepTogether([
        Paragraph("6. Declaration", h1_style),
        Paragraph("I declare that the work described in this report is my own, that it is accurately reflected in my GitHub repository, and that all remaining tasks and known issues are disclosed above.", body_style),
        Spacer(1, 3),
        Paragraph("<b>Name:</b> John Lloyd Lourence C. Rabanal", body_style),
        Paragraph("<b>Section:</b> IT342 - BSIT 3-A", body_style),
        Paragraph("<b>Date:</b> November 26, 2026", body_style),
        Paragraph("<b>Digital Signature:</b> <i>John Lloyd Lourence C. Rabanal (Digitally Signed)</i>", body_style)
    ]))

    doc.build(elements)
    print(f"Report generated successfully: {output_filename}")

if __name__ == '__main__':
    target_pdf = "docs/TindahanKo-Rabanal-JohnLloydLourence-BSIT3A-PD2.pdf"
    copy_pdf = "docs/Increment-2-Completion-Report.pdf"
    create_report(target_pdf)
    create_report(copy_pdf)

# QA Automation Practical Assessment: Provider Onboarding Workflow

This repository contains an industry-ready, highly robust QA automation framework designed to automate the Provider Onboarding Workflow. Built with Java, Selenium WebDriver, and TestNG, it adheres to strict Page Object Model (POM) standards, clean coding guidelines, and handles complex single-page application (SPA) synchronization challenges.

---

## 🎯 Assessment Objective & Tasks Status

**Objective**: Evaluate framework design, coding standards, and the ability to automate a real-world multi-step provider onboarding wizard.

| Assessment Task | Status | Implementation Details |
| :--- | :---: | :--- |
| **1. Login & Verification** | **Passed** | Automates authentication and verifies landing on the Dashboard via title/header validations. |
| **2. Navigate to Provider Management** | **Passed** | Navigates dynamically to Staff -> Providers page. |
| **3. Start Onboarding Workflow** | **Passed** | Handles location selection, searches/clears existing provider record with target NPI to ensure clean execution, and triggers the `Complete_Test_Onboarding_Workflow` wizard. |
| **4. Complete Onboarding & Submit** | **Passed** | Automates the complete 10+ page onboarding wizard (Personal Info, License, DEA, Affiliations, Portal Logins, Hospital Affiliations, Privileges, Professional Reference, Upload Documents, Attestations, shadow DOM signing, and final **Submit Application** click). |
| **5. Implement Negative Test Scenario** | **Passed** | Verifies login failure, capturing proper inline validation errors and reporting them. |

---

## 🚀 Technology Stack
- **Language**: Java 21 (JDK 21)
- **Automation tool**: Selenium WebDriver 4.18.1
- **Testing Framework**: TestNG 7.9.0
- **Build tool**: Maven 3.x
- **Reporting**: ExtentReports 5.1.1 (Detailed HTML Dashboard Reports with Embedded Fail-Safe Screenshots)
- **Logging**: Log4j2 (Dual console and file logger outputs saved to `logs/app.log`)

---

## ⚙️ Configuration & Environment Parameters

The framework is fully configurable. Environment parameters, locators timeouts, and target test cases are isolated in [config.properties](src/main/resources/config.properties):
- `browser`: Select target execution browser (e.g., `chrome`, `firefox`, `edge`).
- `url`: Application base staging URL.
- `email` & `password`: Login credentials.
- `npi`: The specific Provider NPI target (`[NPI_NUMBER]`).
- `workflowName`: Target onboarding workflow (`Complete_Test_Onboarding_Workflow`).
- `locationName`: Target onboarding location (`Dark Knight Oso Inc`).
- `timeout`: Explicit wait timeout configuration (default is `15` seconds).

---

## 📁 Framework Architecture

Following Page Object Model (POM) structure:
```text
Automation-Testing-Assignment/
├── logs/
│   └── app.log                          # Comprehensive runtime application logs
├── target/                              # Maven compilation build outputs
├── reports/
│   └── ExtentReport.html                # Rich HTML execution report dashboard
├── screenshots/                         # Automated screenshots captured on failure
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── automation/
│   │   │           ├── base/
│   │   │           │   └── BasePage.java       # Shared element and driver context
│   │   │           ├── pages/                  # Page Objects (POM pattern)
│   │   │           │   ├── LoginPage.java      # Login authentication
│   │   │           │   ├── DashboardPage.java  # Staff navigation
│   │   │           │   ├── ProvidersPage.java  # Custom dropdown and location selections
│   │   │           │   ├── AddProviderModal.java # Workflow & details step forms
│   │   │           │   ├── ProviderFormPage.java # New tab basic info form automation
│   │   │           │   └── onboarding/         # Onboarding wizard pages
│   │   │           └── utils/                  # Helper utilities
│   │   │               ├── ConfigReader.java   # Properties reader
│   │   │               ├── ElementUtils.java   # Explicit waits, retry actions, and JS operations
│   │   │               └── ReportUtils.java    # Extent Reports engine
│   │   └── resources/
│   │       ├── config.properties        # Project execution parameters
│   │       └── log4j2.xml               # Log4j2 layout configurations
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── automation/
│       │           ├── base/
│       │           │   └── BaseTest.java       # Driver setup, teardown, and screenshot listener hooks
│       │           └── tests/
│       │               └── OnboardingTest.java # Happy path and negative scenario test cases
│       └── resources/
│           └── testng.xml               # Suite file mapping test runs
└── pom.xml                              # Maven dependencies, source compilation & compiler plugin
```

---

## 💡 Key Design Patterns & Technical Solutions

During development, several complex Single Page Application (SPA) timing, rendering, and hydration issues were resolved:

1. **Hydration & State-Change Swallow Clicks**:
   - **Problem**: When changing locations, React takes a few hundred milliseconds to update the page state. Clicking the `+ Add Provider` button immediately resulted in the click being swallowed by React state transitions without any errors, causing execution to hang.
   - **Solution**: Implemented a state-synchronization click loop inside `ProvidersPage.clickAddProvider()`. The script verifies the location text is present, attempts to click `+ Add Provider`, and checks if the modal appears. It retries in a controlled loop (up to 3 times) to ensure the click is registered post-hydration.

2. **React Portal Dropdown Interceptions**:
   - **Problem**: Custom React-Select elements and Portal overlays placed options inside `<li class="pure-checkbox">` and appended options lists directly to `<body>`. Standard locators matching `<li class="breadcrumb-item">` in the background got click-intercepted.
   - **Solution**: Developed a portal-proof option locator `//li[not(contains(@class, 'breadcrumb'))]` that isolates the options by explicitly filtering out background breadcrumb items.

3. **Stale Element Reference Exceptions on Open Transition**:
   - **Problem**: Dynamic rendering of custom React options in the DOM causes element references to become stale as React updates the DOM nodes.
   - **Solution**: Implemented stale-element-proof selection loops in `AddProviderModal.selectPrivilegeByName(privilegeName)`. Stale element exceptions trigger a swift re-locating retry to dynamically locate the fresh elements.

4. **Hidden Duplicate Input Elements (DOM Intercepts)**:
   - **Problem**: React keeps hidden form fields in the DOM with `display: none`. Standard locators returned these hidden inputs first, throwing `InvalidElementStateException`.
   - **Solution**: Created a `sendKeysToVisible` helper in `AddProviderModal` that queries all matching inputs and filters only for the visible, enabled element.

5. **Popup Blocker and User-Initiated Interactions**:
   - **Problem**: Modern browsers block new tab generation unless triggered by a real, user-initiated click. Bypassing modal backdrop overlays using a JavaScript click results in Chrome blocking the tab.
   - **Solution**: Configured `--disable-popup-blocking` in ChromeOptions and implemented a hybrid click utility using Actions click (real pointer event) to cleanly trigger tabs.

6. **React State Sync for Auto-Populated Inputs**:
   - **Problem**: Asynchronous NPPES database lookups auto-populate fields, bypassing React state listeners and leaving forms invalid.
   - **Solution**: Implemented a synchronization wait loop that checks for the completion of the lookup, then triggers Javascript validation events (`input`, `change`, `blur`) on the fields to force React form state updates.

7. **Scroll-Clipped Viewport Inputs**:
   - **Problem**: Form fields inside scrollable container elements are clipped by CSS overflows, failing visibility checks.
   - **Solution**: Implemented vertical page scrolling and center alignment routines prior to interaction, complemented by a JavaScript click fallback.

8. **Redundant Questionnaire Filling and Sign Dialog Reset**:
   - **Problem**: Calling `fillAttestationsAndOpenSignature()` from multiple locations caused redundant form-filling, leading to explicit wait timeouts on the signature dialog.
   - **Solution**: Decoupled the form-filling/dialog-triggering phase from the signature execution phase.

9. **Disabled Application Submission & Final Flow Confirmation**:
   - **Problem**: The final "Submit Application" button on the Attestations page (`By.id("complete")`) remains disabled until the Docuseal document signature is completely typed, signed, and saved. Additionally, post-submission shows an asynchronous success modal popup which can cause timing issues if closed too early.
   - **Solution**: Updated `AttestationsPage` to wait dynamically for the "Submit Application" button to become clickable after the signature flow completes, execute the click robustly, and safely catch/handle the success dialog modal validation.

---

## 📋 Assumptions
1. **Target Provider Cleanup**: If the target NPI (`[NPI_NUMBER]`) already exists in the "In Process" list from a previous test run, the test checks for it and performs a deletion to ensure a clean slate before launching the onboarding workflow.
2. **Static Document Signature Name**: In the Docuseal step, the system uses the auto-filled first and last names generated during NPI lookup to construct the signature input name, ensuring consistent state values.
3. **Implicit Waits Avoided**: All page interactions exclusively use Explicit WebDriver Waits, dynamically polling the DOM for visibility, clickability, or state changes rather than relying on arbitrary thread sleeps (except for safety pacing intervals during shadow-root transitions).

---

## 🏃 Execution Instructions

### Prerequisites
1. Installed **Java Development Kit (JDK 21)**.
2. Installed **Apache Maven**.
3. Chrome browser installed.

### How to Run Tests
From your terminal, navigate to the root directory and run:
```bash
mvn clean test
```

### Reviewing Reports and Logs
- **Extent Report Dashboard**: Open `reports/ExtentReport.html` in any web browser to review the visual test execution graphs, passed test steps, fail logs, and screenshots.
- **Log Files**: Open `logs/app.log` to review complete detailed timestamps, class references, locator waits, and flow verification records.
- **Screenshots**: Any runtime failures automatically capture chrome screenshots and save them inside the `screenshots/` directory, logging them directly in the Extent HTML report.

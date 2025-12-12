# Bread Artisans Connector
**Bread Artisans Connector** is a lightweight, cross-platform middleware designed to bridge the gap between **Bread Artisans ERP** and local hardware peripherals (like Clover, Fiserv/Posnet POS Integration, ESC/POS printers and Fiscal Printers).

Since web browsers cannot directly access local USB or Network devices due to security sandboxing, this connector runs as a local background service. It exposes a secure `localhost` HTTP API that **Bread Artisans** consumes to trigger actions such as printing receipts on ESC/POS thermal printers or interacting with fiscal devices.

## 🚀 Key Features
-   **Cross-Platform:** Built with Java 17, runs natively on Linux, Windows, and macOS.
-   **Lightweight API:** Powered by **Javalin**, providing a fast and low-overhead HTTP server.
-   **ESC/POS Support:** Native integration with thermal printers via `escpos-coffee`.
-    **Posnet/Fisver Integration:** Native integration with Fiserv/Posnet pinpads integration (only devices for Argentina).
-    **Clover Integration:** Native integration with Clover devices (with *Network Pay Display*).
- **EPSON & Hasar fiscal printers integration:** Native integration with EPSON and Hasar 2nd generation fiscal printers.
-   **Minimalist GUI:** A simple Swing-based status window indicating server health and port activity (*against my professors' recommendation to use Swing, sorry professors Alejandro Peña & Adrian Narducci 😅*).
-   **Single-File Deployment:** Compiles into a single "Fat JAR" with all dependencies included for easy distribution.

## 📂 Project Architecture
The project follows a standard **Layered Architecture** to ensure scalability, maintainability, and separation of concerns. This structure is designed to easily support new hardware types (e.g., Fiscal Printers, Label Makers) by implementing new Service interfaces.

    src/main/java/com/breadartisans/connector/
    │
    ├── 🟢 Main.java                 # Entry point. Bootstraps the UI and starts the Javalin Server background thread.
    │
    ├── ⚙️ config/                   # Global configuration.
    │   └── AppConfig.java           # Handles reading properties, CORS, Port settings, and Environment variables.
    │
    ├── 🏷️ enums/                    # Enumerations for type safety.
    │   ├── Environment.java         # Defines DEVELOPMENT vs PRODUCTION modes.
    │   └── PrinterProtocol.java     # Supported protocols (ESCPOS, EPSON_FISCAL, HASAR_FISCAL).
    │
    ├── 🎨 ui/                       # User Interface Layer (Java Swing).
    │   ├── MainFrame.java           # The main dashboard window displaying server status and current config.
    │   └── SettingsDialog.java      # Modal form for modifying the `connector.properties` file.
    │
    ├── 🎮 controllers/              # HTTP Request Handlers (The "Gatekeepers").
    │   │                            # Responsible for validating JSON input and delegating to Services.
    │   ├── SystemController.java    # Health checks (/status) and system info.
    │   └── PrintController.java     # Print endpoints (/print).
    │
    ├── 📦 dto/                      # Data Transfer Objects.
    │   │                            # Defines the shape of JSON data entering and leaving the API.
    │   ├── request/
    │   │   └── PrintJobRequest.java # Java Record representing the print job payload.
    │   └── response/
    │       └── ApiResponse.java     # Standardized JSON response structure.
    │
    ├── 🧠 services/                 # Business Logic Layer.
    │   │                            # Defines "WHAT" needs to be done, not "HOW".
    │   ├── PrinterService.java      # Interface defining the contract for printing operations.
    │   ├── PrinterServiceFactory.java # Factory pattern to instantiate the correct service based on config.
    │   └── impl/                    # Concrete implementations of the services.
    │       └── EscPosService.java   # Implementation for standard Thermal Printers.
    │
    └── 🛠️ utils/                    # Helper classes and static utilities.
        └── PrinterUtils.java        # String formatting, hex conversion, etc. (Feature)

## ⚡ Getting Started
### Prerequisites
-   Java Development Kit (JDK) 17 or higher.
-   Maven.

### Installation & Build
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/marcoss2009/Bread-Artisans-Connector.git
    cd Bread-Artisans-Connector
    ```
2. **Build the project:** This command will run tests and generate the executable Fat JAR in the `target/` directory.
   ```bash
   mvn clean package
   ```
3. **Run the Connector:**
   ```bash
   java -jar target/connector-1.0-SNAPSHOT.jar
   ```
Once running, the connector will listen on **port 27323** (default).

## 🔌 API Usage Example
To print a ticket from the web client, send a `POST` request to the local connector:

**Endpoint:** `POST http://localhost:27323/print`

**Payload:**

    {
      "content": "Bread Artisans\nOrder #1024\n----------------\nTotal: $ 50.00\n\nThank you!",
      "cutPaper": true,
      "openCashDrawer": false
    }

**Response:**

    {
      "status": "success",
      "message": "Print job sent to queue"
    }

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](https://github.com/marcoss2009/Bread-Artisans-Connector/LICENSE) file for details.
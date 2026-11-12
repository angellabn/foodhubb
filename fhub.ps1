# FoodHub Startup & Public Tunnel Script
$ErrorActionPreference = "Continue"

$jarPath = "C:\Users\john6\Downloads\foodhub\foodhub\target\foodhub-0.0.1-SNAPSHOT.jar"
$javaExe = "C:\Program Files\Java\jdk-21.0.10\bin\java.exe"
$ltPath = "C:\Users\john6\Downloads\foodhub\node_modules\.bin\lt.cmd"

# --- Build JAR if missing ---
if (-not (Test-Path $jarPath)) {
    Write-Host "JAR not found. Building..."
    & "C:\Program Files\Apache\maven\bin\mvn.cmd" -f "C:\Users\john6\Downloads\foodhub\foodhub\pom.xml" package -DskipTests
}

# --- Kill anything already on port 8088 ---
try {
    $existing = Get-NetTCPConnection -LocalPort 8088 -ErrorAction SilentlyContinue
    if ($existing) {
        Write-Host "Killing process on port 8088 (PID $($existing[0].OwningProcess))..."
        Stop-Process -Id $existing[0].OwningProcess -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
    }
}
catch {}

# --- Start Spring Boot ---
Write-Host "Starting FoodHub on port 8088..."
$javaProcess = Start-Process -FilePath $javaExe `
    -ArgumentList "-jar", $jarPath, "--server.port=8088" `
    -PassThru -WindowStyle Hidden

# --- Wait for health check (up to 60 s) ---
$ready = $false
for ($i = 1; $i -le 30; $i++) {
    Start-Sleep -Seconds 2
    try {
        $r = Invoke-RestMethod -Uri "http://localhost:8088/api/health" -TimeoutSec 3
        if ($r.status -eq "UP") {
            $ready = $true
            Write-Host "FoodHub is UP at http://localhost:8088"
            break
        }
    }
    catch {
        Write-Host "Waiting for server... ($i/30)"
    }
}

if (-not $ready) {
    Write-Host "ERROR: FoodHub failed to start within 60 seconds."
    if ($javaProcess -and -not $javaProcess.HasExited) {
        $javaProcess | Stop-Process -Force
    }
    exit 1
}

# --- Start localtunnel (auto-reconnect) ---
Write-Host "Opening public tunnel..."
while ($true) {
    & $ltPath --port 8088
    Write-Host "Tunnel closed. Reconnecting in 3 seconds..."
    Start-Sleep -Seconds 3
}

$GITHUB_REPO = "Ibrahim-Haroon/git-autocommit-cli"
$JAR_NAME = "git-auto-commit-cli.jar"
$INSTALL_DIR = "$HOME\bin"
$SHELL_CONFIG = $PROFILE

function Detect-OS {
    if (-not $IsWindows) {
        Write-Error "Unsupported OS: This script is intended for Windows. Run `install.sh` for UNIX-based machines."
        exit 1
    }

    if (-not (Test-Path -Path $INSTALL_DIR)) {
        New-Item -ItemType Directory -Path $INSTALL_DIR -Force | Out-Null
    }
}

function Get-Latest-Release {
    Write-Output "Fetching latest release from GitHub..."

    try {
        $latestRelease = Invoke-RestMethod -Uri "https://api.github.com/repos/$GITHUB_REPO/releases/latest"
        $latestVersion = $latestRelease.tag_name
        $downloadUrl = $latestRelease.assets[0].browser_download_url

        if (-not $downloadUrl) {
            throw "No download URL found"
        }

        Write-Output "Latest version: $latestVersion"
        Write-Output "Downloading $downloadUrl..."
        Invoke-WebRequest -Uri $downloadUrl -OutFile "$INSTALL_DIR\$JAR_NAME"
    }
    catch {
        Write-Error "Failed to fetch or download the latest release: $_"
        exit 1
    }
}

function Create-Wrapper {
    $wrapperContent = @"
@echo off
powershell.exe -ExecutionPolicy Bypass -File "%~dp0autocommit_wrapper.ps1" %*
"@
    Set-Content -Path "$INSTALL_DIR\autocommit.bat" -Value $wrapperContent

    $wrapperScriptContent = @"
`$jarPath = Join-Path `$PSScriptRoot "$JAR_NAME"
`$repoUrl = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"

try {
    `$latestRelease = Invoke-RestMethod -Uri `$repoUrl
    `$latestVersion = `$latestRelease.tag_name
    `$currentVersion = & java -jar "`$jarPath" --version 2>&1 | Select-String -Pattern "(\d+\.\d+\.\d+)" | ForEach-Object { `$_.Matches.Groups[1].Value }

    if (`$latestVersion -ne `$currentVersion) {
        Write-Host "A new version is available. Updating..."
        `$downloadUrl = `$latestRelease.assets[0].browser_download_url
        Invoke-WebRequest -Uri `$downloadUrl -OutFile "`$jarPath"
        Write-Host "Updated to version `$latestVersion"
    }
}
catch {
    Write-Warning "Failed to check for updates: `$_"
}

& java -jar "`$jarPath" `$args
"@
    Set-Content -Path "$INSTALL_DIR\autocommit_wrapper.ps1" -Value $wrapperScriptContent
}

function Add-Installation-Directory-To-Path {
    $userPath = [System.Environment]::GetEnvironmentVariable("Path", [System.EnvironmentVariableTarget]::User)
    if ($userPath -notlike "*$INSTALL_DIR*") {
        $newPath = $userPath + ";$INSTALL_DIR"
        [System.Environment]::SetEnvironmentVariable("Path", $newPath, [System.EnvironmentVariableTarget]::User)
        $env:Path += ";$INSTALL_DIR"
        Write-Output "Added $INSTALL_DIR to your PATH."
    }
}

function Main {
    Detect-OS
    Get-Latest-Release
    Create-Wrapper
    Add-Installation-Directory-To-Path
    Write-Output "Installation complete. You can now use 'autocommit' from anywhere."
    Write-Output "Please restart your terminal for the PATH changes to take effect."
}

Main
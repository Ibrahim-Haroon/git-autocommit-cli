$GITHUB_REPO = "Ibrahim-Haroon/git-autocommit-cli"
$JAR_NAME = "autocommit.jar"

function Detect-OS {
    if ($IsWindows) {
        $Global:INSTALL_DIR = "$HOME\bin"
        $Global:SHELL_CONFIG = "$PROFILE"
    } else {
        Write-Error "Unsupported OS: This script is intended for Windows. Run `install.sh` for UNIX based machines"
        exit 1
    }

    if (!(Test-Path -Path $INSTALL_DIR)) {
        New-Item -ItemType Directory -Path $INSTALL_DIR | Out-Null
    }
}

function Get-Latest-Release {
    Write-Output "Fetching latest release from GitHub..."
    
    $latestRelease = Invoke-RestMethod -Uri "https://api.github.com/repos/$GITHUB_REPO/releases/latest"
    $latestVersion = $latestRelease.tag_name
    $downloadUrl = $latestRelease.assets | Where-Object { $_.name -eq $JAR_NAME } | Select-Object -ExpandProperty browser_download_url

    if (-not $downloadUrl) {
        Write-Error "Failed to fetch the latest release. Please check the repository name and release asset name."
        exit 1
    }

    Write-Output "Latest version: $latestVersion"
    Write-Output "Downloading $downloadUrl..."
    Invoke-WebRequest -Uri $downloadUrl -OutFile "$INSTALL_DIR\$JAR_NAME"
}

function Create-Wrapper {
    $wrapperContent = @"
@echo off
powershell.exe -ExecutionPolicy Bypass -File "%~dp0autocommit_wrapper.ps1" %*
"@
    Set-Content -Path "$INSTALL_DIR\autocommit.bat" -Value $wrapperContent

    $wrapperScriptContent = @"
`$jarPath = "`$PSScriptRoot\$JAR_NAME"
`$latestVersion = (Invoke-RestMethod -Uri "https://api.github.com/repos/$GITHUB_REPO/releases/latest").tag_name
`$currentVersion = & java -jar "`$jarPath" --version 2>&1 | Select-String -Pattern "(\d+\.\d+\.\d+)" | ForEach-Object { `$_.Matches.Groups[1].Value }

if (`$latestVersion -ne `$currentVersion) {
    Write-Host "A new version is available. Updating..."
    `$downloadUrl = (Invoke-RestMethod -Uri "https://api.github.com/repos/$GITHUB_REPO/releases/latest").assets | Where-Object { `$_.name -eq "$JAR_NAME" } | Select-Object -ExpandProperty browser_download_url
    Invoke-WebRequest -Uri `$downloadUrl -OutFile "`$jarPath"
    Write-Host "Updated to version `$latestVersion"
}

& java -jar "`$jarPath" `$args
"@
    Set-Content -Path "$INSTALL_DIR\autocommit_wrapper.ps1" -Value $wrapperScriptContent
}

function Add-Installation-Directory-To-Path {
    if (-Not ($env:Path -like "*$INSTALL_DIR*")) {
        [System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";$INSTALL_DIR", [System.EnvironmentVariableTarget]::User)
        Write-Output "Please restart your terminal or run 'refreshenv' to update your PATH."
    }
}

function Main {
    Detect-OS
    Get-Latest-Release
    Create-Wrapper
    Add-Installation-Directory-To-Path
    Write-Output "Installation complete. You can now use 'autocommit' from anywhere."
}

Main
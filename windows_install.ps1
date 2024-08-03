function Detect-OS {
    if ($IsWindows) {
        $Global:INSTALL_DIR = "$HOME\bin"
        $Global:SHELL_CONFIG = "$PROFILE"
    } else {
        Write-Error "Unsupported OS: This script is intended for Windows. Run `install.sh` for UNIX based machines"
        exit 1
    }

    if (!(Test-Path -Path $INSTALL_DIR)) {
        New-Item -ItemType Directory -Path $INSTALL_DIR
    }
}

function Get-Release {
    if (-Not (Test-Path -Path "releases\git-autocommit-cli*all.jar")) {
        Write-Error "Could not find latest release. Please make sure you're up to date by doing 'git pull'"
        exit 1
    }
}

function Place-Jar-And-Create-Wrapper {
    Copy-Item -Path (Get-Item -Path "releases\git-autocommit-cli*all.jar").FullName -Destination "$INSTALL_DIR\autocommit.jar"

    $wrapper = @"
@echo off
java -jar `"$INSTALL_DIR\autocommit.jar`" %*
"@
    Set-Content -Path "$INSTALL_DIR\autocommit.bat" -Value $wrapper
}

function Add-Installation-Directory-To-Path {
    if (-Not ($env:Path -like "*$INSTALL_DIR*")) {
        [System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";$INSTALL_DIR", [System.EnvironmentVariableTarget]::User)
        Write-Output "Please restart your terminal or run 'refreshenv' to update your PATH."
    }
}

function Main {
    Detect-OS
    Get-Release
    Place-Jar-And-Create-Wrapper
    Add-Installation-Directory-To-Path
    Write-Output "Installation complete. You can now use 'autocommit' from anywhere."
}

Main

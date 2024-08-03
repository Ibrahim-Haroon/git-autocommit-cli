#!/bin/bash


main() {
  detect_os
  get_latest_release
  place_jar_at_root_and_create_wrapper
  add_installation_directory_to_path

  echo "Installation complete. You can now use 'autocommit' from anywhere."
}

detect_os() {
  if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    INSTALL_DIR="$HOME/.local/bin"
  elif [[ "$OSTYPE" == "darwin"* ]]; then
    INSTALL_DIR="/usr/local/bin"
  else
    echo "Unsupported OS: $OSTYPE. Use 'windows_install.ps1' for Windows machines"
    exit 1
  fi

  mkdir -p "$INSTALL_DIR"
}

get_latest_release() {
    echo "Fetching latest release from GitHub..."

    local REPO="Ibrahim-Haroon/git-autocommit-cli"
    local API_URL="https://api.github.com/repos/$REPO/releases/latest"
    local DOWNLOAD_URL=$(curl -s "$API_URL" | jq -r '.assets[0].browser_download_url')
    local VERSION=$(curl -s "$API_URL" | jq -r '.tag_name')

    if [[ -z "$DOWNLOAD_URL" ]]; then
        echo "Failed to fetch the latest release. Please check the repository name and release asset name."
        return 1
    fi

    echo "Latest version: $VERSION"
    echo "Downloading from $DOWNLOAD_URL..."

    if curl -L "$DOWNLOAD_URL" -o "$INSTALL_DIR/git-auto-commit-cli.jar"; then
        echo "Successfully downloaded git-auto-commit-cli.jar to $INSTALL_DIR"
    else
        echo "Failed to download the JAR file."
        return 1
    fi
}

place_jar_at_root_and_create_wrapper() {
  echo '#!/bin/bash
JAR_PATH="'"$INSTALL_DIR/$JAR_NAME"'"
LATEST_VERSION=$(curl -s https://api.github.com/repos/'"$GITHUB_REPO"'/releases/latest | grep "tag_name" | cut -d '"'"' -f 4)
CURRENT_VERSION=$(java -jar "$JAR_PATH" --version 2>&1 | cut -d " " -f 2)

if [[ "$LATEST_VERSION" != "$CURRENT_VERSION" ]]; then
  echo "A new version is available. Updating..."
  curl -L $(curl -s https://api.github.com/repos/'"$GITHUB_REPO"'/releases/latest | grep "browser_download_url.*'"$JAR_NAME"'" | cut -d '"'"' -f 4) -o "$JAR_PATH"
  echo "Updated to version $LATEST_VERSION"
fi

java -jar "$JAR_PATH" "$@"' > "$INSTALL_DIR/autocommit"

  chmod +x "$INSTALL_DIR/autocommit"
}

add_installation_directory_to_path() {
  if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
      SHELL_CONFIG="$HOME/.bashrc"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
      SHELL_CONFIG="$HOME/.bash_profile"
    fi
    echo "export PATH=\"$INSTALL_DIR:\$PATH\"" >> "$SHELL_CONFIG"
    echo "Please restart your terminal or run 'source $SHELL_CONFIG' to update your PATH."
  fi
}

main
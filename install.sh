#!/bin/bash

GITHUB_REPO="Ibrahim-Haroon/git-autocommit-cli"
JAR_NAME="git-autocommit-cli.jar"

main() {
  detect_os
  download_jar
  create_wrapper
  add_installation_directory_to_path

  echo "Installation complete. You can now use 'autocommit' from anywhere."
}

detect_os() {
  if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    INSTALL_DIR="$HOME/.local/bin"
    SHELL_CONFIG="$HOME/.bashrc"
  elif [[ "$OSTYPE" == "darwin"* ]]; then
    INSTALL_DIR="/usr/local/bin"
    SHELL_CONFIG="$HOME/.bash_profile"
  else
    echo "Unsupported OS: $OSTYPE"
    exit 1
  fi

  mkdir -p "$INSTALL_DIR"
}

download_jar() {
  echo "Downloading latest release from GitHub..."

  RELEASE_INFO=$(curl -s "https://api.github.com/repos/$GITHUB_REPO/releases/latest")
  DOWNLOAD_URL=$(echo "$RELEASE_INFO" | grep -o '"browser_download_url": "[^"]*' | cut -d'"' -f4)

  if [[ -z "$DOWNLOAD_URL" ]]; then
    echo "Failed to fetch the download URL. Please check the repository name and release asset name."
    exit 1
  fi

  echo "Downloading from $DOWNLOAD_URL"
  if curl -L "$DOWNLOAD_URL" -o "$INSTALL_DIR/$JAR_NAME"; then
    echo "Successfully downloaded $JAR_NAME to $INSTALL_DIR"
  else
    echo "Failed to download the JAR file."
    exit 1
  fi
}

create_wrapper() {
  echo '#!/bin/bash
java -jar "'"$INSTALL_DIR/$JAR_NAME"'" "$@"' > "$INSTALL_DIR/autocommit"

  chmod +x "$INSTALL_DIR/autocommit"
}

add_installation_directory_to_path() {
  if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
    echo "export PATH=\"$INSTALL_DIR:\$PATH\"" >> "$SHELL_CONFIG"
    echo "Please restart your terminal or run 'source $SHELL_CONFIG' to update your PATH."
  fi
}

main
#!/bin/bash

main() {
  detect_os
  build_gradle
  place_jar_at_root_and_create_wrapper
  add_installation_directory_to_path

  echo "Installation complete. You can now use 'autocommit' from anywhere."
}

detect_os() {
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        INSTALL_DIR="$HOME/.local/bin"
    # check if macOS
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        INSTALL_DIR="/usr/local/bin"
    else
        echo "Unsupported OS: $OSTYPE"
        exit 1
    fi

    mkdir -p "$INSTALL_DIR"
}

build_gradle() {
  if ! ./gradlew shadowJar; then
      echo "Build failed. Please check the build logs for details."
      exit 1
  fi
}

place_jar_at_root_and_create_wrapper() {
  cp build/libs/git-auto-commit-cli-1.0-SNAPSHOT*all.jar "$INSTALL_DIR/autocommit.jar"

  echo '#!/bin/bash
  java -jar '"$INSTALL_DIR"'/autocommit.jar "$@"' > "$INSTALL_DIR/autocommit"

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


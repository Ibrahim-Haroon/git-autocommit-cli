# 🚀 AutoCommit: Your AI-Powered Commit Message Generator 
![Java Version](https://img.shields.io/badge/java-11-yellow)

AutoCommit is a powerful CLI tool that leverages AI to generate meaningful commit and PR messages to save you time!

## 🌟 Features

- Generate commit messages using local LLM or OpenAI models
- Easy-to-use CLI interface
- Customizable default settings
- Docker support for local LLM model

## 📦 Installation

1. Clone the repository
    ```shell
    git clone https://github.com/Ibrahim-Haroon/git-autocommit-cli.git
    ```
2. Create JAR file locally
    ```shell
    ./install.sh   # prefix with sudo for mac
    ```
3. Source the configuration file based on your shell (bashrc or zshrc):
    - For bash:
      ```shell
      source ~/.bashrc
      ```
    - For zsh:
      ```shell
      source ~/.zshrc
      ```
4. Export the path to the JAR file:
    - Find the location of the `autocommit` executable:
      ```shell
      which autocommit
      ```
    - Append the export path to your shell configuration file (`~/.bashrc` or `~/.zshrc`):
      ```shell
      echo 'export PATH=$PATH:/path/to/autocommit' >> ~/.bashrc   # or ~/.zshrc for zsh users
      ```
5. Verify installation
    ```shell
    autocommit --test
    ```

## 🛠️ Usage

Generate a commit message using:

- Local LLM model:
    ```shell
    autocommit --local
    ```
- OpenAI model:
    ```shell
    autocommit --openai
    ```
  You will need to provide an OpenAI API key (get it from [here](https://platform.openai.com/api-keys))
    ```shell
  autocommit --set-openai-key <api-key>
    ```
- Google Vertex AI model:
  - You must first set the Project Id and Location
    ```shell
    autocommit --set-google-vertex-project-id YOUR_PROJECT_ID
    autocommit --set-google-vertex-location YOUR_LOCATION
    ```
  ```shell
    autocommit --google
  ```

#### Set default settings using:
```shell
autocommit --set-default <google/local/openai>
```

## 📝 See all available commands
```shell
autocommit --help
```
```
Options: 
    --set-default, -d -> Set the default LLM response service { Value should be one of [local, openai, google] }
    --set-openai-key -> Set OpenAI API key { String }
    --local, -l [false] -> Use Local LLM response service 
    --openai, -o [false] -> Use OpenAI LLM response service 
    --google, -g [false] -> Use Google LLM response service 
    --make-pr-summary, -pr [false] -> Create a summary based off git log for PR message 
    --test, -t [false] -> Test CLI tool was installed correctly 
    --help, -h -> Usage info 
```

## 🖥️ Local LLM Setup

1. Download the model from [Hugging Face](https://huggingface.co/TheBloke/Llama-2-13B-chat-GGUF/blob/main/llama-2-13b-chat.Q4_K_M.gguf)
2. Place it in the `localLLM` directory
3. Run the model

## 🤝 Contributing

We welcome contributions to this project! Here's how you can help:

1. Create an issue ticket
2. Fork the repository
3. Make your changes
4. Submit a pull request

Please ensure your code adheres to our style guide.

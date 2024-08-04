package com.ibrahimharoon.gitautocommit.llm.templates

/**
 * Internal class providing template strings and generation functions for LLM prompts.
 *
 * This class encapsulates the various prompts and instructions used when interacting
 * with Language Models (LLMs) in the git-autocommit system. It includes templates
 * for generating commit messages, PR summaries, and defining the LLM's role.
 *
 * The templates and functions in this class are designed to create consistent and
 * effective prompts across different LLM providers, ensuring that the generated
 * content (commit messages and PR summaries) follows the desired format and includes
 * all necessary information.
 *
 * Usage of this class is internal to the git-autocommit system and its contents
 * should not be directly exposed to external components or users.
 */
internal class LlmTemplates {
    companion object {
        const val ROLE =
            """
            You are a Git Commit and PR Summary Assistant. Your primary task is to generate clear, concise, and
            informative commit messages and PR summaries based on git diffs and git logs. Your outputs should help
            developers understand the changes made without being overly verbose or too brief. Follow best practices
            for both commit messages and PR summaries, ensuring clarity and relevance.
            """

        fun commitPrompt(codeChanges: String): String =
            """
            You are provided with a git diff representing the changes made in the code. This will be pasted 
            directly into the commit message, so don't add weird characters. Use direct names for variables,
            functions, classes, etc. Pay careful attention for any accidental secret messages, such as API keys
            and notify in the commit message so the user can see before commiting. 
            
            You will be provided the following information:
                <codeChanges>
                {{$codeChanges}}
                </codeChanges>
                
            1. For minor changes like linting or small logic updates, generate a concise message under
               50 characters.
            2. For more complex changes involving multiple files or significant updates, generate a 
              detailed message with the following sections:
                    - A short summary of the changes.
                    - Bullet points describing each sub change.
            """

        fun prSummaryPrompt(gitLog: String): String =
            """
            You will provided the following information:
                <gitLog>
                {{$gitLog}}
                </gitLog>
                
            Generate a PR summary that follows best practices, providing a clear and comprehensive overview of 
            the changes introduced. This will be pasted directly into PR conversation tab in Github. Use direct names
            for variables, functions, classes, etc. The format should follow the structure below, ensuring each point
            is addressed clearly:

            1. Title: A short, descriptive title of the PR (max 50 characters).
            2. Description: A detailed description of the changes made, including:
               - The purpose of the PR.
               - A summary of the key changes.
               - Any relevant background information or context.
            3. Changes: A bullet-point list of specific changes made in the code (e.g., updated files, added functions).
        """
    }
}

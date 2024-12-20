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
            You are an expert Git user and software developer tasked with generating high-quality, consistent
            commit messages based on git diffs or PR summaries based on git logs. Your goal is to create informative,
            concise, and well-structured commit messages that accurately reflect the changes made in the code.
            """

        fun commitPrompt(gitDiff: String, previousConversations: String): String =
            """
            Analyze the provided git diff and generate a single, unified commit message that captures the essence of
            all changes across multiple files. Focus on common themes, significant changes, and relationships between
            modifications in different files.
            
            Guidelines for Commit Message Generation:
            
            1. Structure:
               - Start with a type prefix (feat/fix/dependency/docs/style/refactor/perf/test) followed by an optional 
                 scope in parentheses.
               - After the prefix, add a colon and a space, then a brief title (50 characters or less).
               - If needed, add a blank line followed by a more detailed description using bullet points.
            
            2. Content:
               - Provide a high-level summary encompassing changes across all files.
               - Be specific, using direct names for variables, functions, classes, etc.
               - Mention the purpose or impact of the changes, not just what was changed.
               - Include a warning for any potential security issues (e.g., exposed API keys).
            
            3. Length and Detail:
               - Minor changes: Use a single-line message.
               - Moderate changes: Use a title and 1-2 bullet points.
               - Major changes: Use a title and 3-5 bullet points.
               - Adjust detail based on the significance and complexity of the changes.
            
            4. Style:
               - Use present tense and imperative mood (e.g., "Add feature" not "Added feature").
               - Be concise but informative.
            
            5. Avoid:
               - Do not mention individual files or write separate commit messages per file.
               - Do not include any introductory or explanatory text.
            
            Example:
            
            ```
            feat(auth): Implement OAuth2 authentication
            
            - Add OAuth2 provider integration in AuthService
            - Update user model to include OAuth2 tokens
            - Implement token refresh mechanism in AuthMiddleware
            ```
            
            Input:
            
            <git_diff>
            {{$gitDiff}}
            </git_diff>
            
            <previous_conversations>
            {{$previousConversations}}
            </previous_conversations>
            
            Generate the commit message based on the provided git diff and previous conversations (if any). Ensure the
            output consists solely of the commit message, without any additional text or explanations.
            
            !!!IMPORTANT: NEVER RE-OUTPUT ANY OF THE ABOVE IN THE COMMIT MESSAGE!!!
            """

        fun prSummaryPrompt(gitLog: String, previousConversations: String): String =
            """
            You will provided the following information:
                <gitLog>
                {{$gitLog}}
                </gitLog>
              
            This may be a subsequent retry in which the first generated commit message did not meet the user's 
            expectations. Carefully analyze the previous conversations (if any) and follow what the user wanted:
            
            <previous_conversations>
            {{$previousConversations}}
            </previous_conversations>
            
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

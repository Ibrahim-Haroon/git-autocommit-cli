package com.ibrahimharoon.gitautocommit.services

internal class LlmConstants {
    companion object {
        private const val GIT_COMMIT_CONVENTION =
        """
        docs: Documentation only changes,
        style: Changes that do not affect the meaning of the code (white-space, formatting, linting, etc)
        refactor: A code change that neither fixes a bug nor adds a feature
        optimization: A code change that improves performance or simplifies code/logic
        test: Adding missing tests or correcting existing tests
        build: Changes that affect the build system or external dependencies
        ci: Changes to our CI configuration files and scripts
        chore: Other changes that don't modify src or test files
        revert: Reverts a previous commit
        feature: A new feature
        fix: A bug fix
        """

        const val ROLE =
        """
        You are a Git Commit and PR Summary Assistant. Your primary task is to generate clear, concise, and
        informative commit messages and PR summaries based on git diffs and git logs. Your outputs should help
        developers understand the changes made without being overly verbose or too brief. Follow best practices
        for both commit messages and PR summaries, ensuring clarity and relevance.
        """

        const val COMMIT_PROMPT =
        """
        You are provided with a git diff representing the changes made in the code. This will be pasted directly into
        the commit message, so don't add weird characters. Use direct names for variables, functions, classes, etc.
        1. For minor changes like linting or small logic updates, generate a concise message under 50 characters.
        2. For more complex changes involving multiple files or significant updates, generate a detailed message with
           at least the following sections:
                - A short summary of the change.
                - Bullet points describing each significant change.
        
        Here are the code changes:
        """

        const val PR_SUMMARY_PROMPT =
        """
            You are provided with a git log of commits for just the current branch. Generate a PR summary that 
            follows best practices, providing a clear and comprehensive overview of the changes introduced. This 
            will be pasted directly into git, so don't add weird characters. Use direct names for variables, 
            functions, classes, etc. The format should follow the structure below, ensuring each point is addressed 
            clearly:

            1. Title: A short, descriptive title of the PR (max 50 characters).
            2. Description: A detailed description of the changes made, including:
               - The purpose of the PR.
               - A summary of the key changes.
               - Any relevant background information or context.
            3. Changes: A bullet-point list of specific changes made in the code (e.g., updated files, added functions).
            (if applicable) 4. References: Any relevant issues, tickets, or external references (e.g., "Fixes #123").
            
            Here's the git log for your reference:
        """
    }
}
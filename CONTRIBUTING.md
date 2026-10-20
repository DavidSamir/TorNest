# Contributing to TorNest

Thanks for helping improve TorNest. You can contribute bug reports, documentation, compatibility findings, or code.

## Report a bug or suggest a feature

- Search [existing issues](https://github.com/DavidSamir/TorNest/issues) before opening a new one.
- For bugs, include steps to reproduce, expected and actual behavior, your device model, OS version, and TorNest version.
- For playback problems, include the affected title or episode and relevant logs. Remove personal information, IP addresses, and credentials before posting.
- For feature requests, explain the problem and how the proposed change would help. Discuss substantial changes in an issue before starting implementation.

## Submit a pull request

1. Fork the repository and create a branch for your change.
2. Keep the pull request focused on one fix or improvement. Follow the existing Kotlin style and module structure; avoid unrelated formatting changes.
3. Explain what changed, why it helps, and how you verified it. Link the related issue when there is one.
4. For code changes, build the affected app or modules and run relevant tests. Add or update tests when behavior changes need coverage. Documentation-only changes should have working links and accurate instructions.
5. For UI or playback changes, include screenshots or device-test results where useful. State the device and OS you tested, and distinguish tested behavior from expected compatibility.

See the [README](README.md#build-from-source) for build commands and the [developer guide](docs/DEVELOPMENT.md) for the project layout. On Windows, use `.\gradlew.bat` instead of `./gradlew`.

## Contribution rules

- Be respectful and keep feedback constructive. Discuss the work without personal attacks or harassment.
- Submit work you have permission to contribute. Credit third-party code and assets, and preserve applicable notices.
- Do not commit signing keys, publisher seeds, passwords, access tokens, personal logs, or private device data.
- Catalogue sources must be content you have permission to distribute. Do not add unauthorized sources or assume a catalogue listing establishes redistribution rights.
- Preserve sharing consent, local watch data, and existing installation compatibility. Explain any proposed changes to these behaviors explicitly.
- Keep documentation and screenshots consistent with the actual app. Do not claim a device or feature was tested unless it was.

Maintainers review contributions and may request changes before merging. Opening an issue or pull request does not guarantee acceptance.

# Development Scripts

## dev-reload.sh

A helper script to run the Spring Boot application and automatically restart it whenever code or resource files change (similar to `npm run dev` in JavaScript projects).

### Prerequisites

- `entr` (install via `brew install entr` on macOS or `apt install entr` on Linux)
- `./gradlew` (included in this project)

### Usage

```sh
chmod +x scripts/dev-reload.sh
./scripts/dev-reload.sh
```

This will watch for changes in `src/main/**/*.kt`, `*.java`, `*.yml`, and `*.properties` files and rerun the application automatically.

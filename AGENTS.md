# Repository Guidelines

## Scope
DynamisContent is the runtime asset system for Dynamis. It resolves and caches runtime content keyed by stable asset identifiers.

## Hard Boundaries
- No math types in this repository; Vectrix owns math concerns.
- No renderer dependencies; LightEngine/GPU integration belongs elsewhere.
- Asset IDs are runtime-stable keys.
- Content is runtime-only; AssetPipeline is build-time and lives in a separate repository.
- Session save data must persist `AssetId` values, not runtime handles.

## Project Structure
- `content-api/`: contracts for IDs, manifests, loaders, cache, manager.
- `content-core/`: default runtime implementations.
- `content-runtime/`: convenience facade and wiring.

## Build and Test
- `mvn validate`: validate module graph and build configuration.
- `mvn test`: run all module tests.
- Java version: `25` with preview enabled.

## Coding Conventions
- Java, 4-space indentation, UTF-8 source.
- `PascalCase` for types, `camelCase` for methods/fields.
- Keep APIs explicit and runtime-focused.

## Contribution Flow
- Keep changes module-scoped and small.
- Add tests with new behavior.
- Commit per phase with clear imperative messages.
- Do not push unless explicitly requested.

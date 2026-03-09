# DynamisContent Architecture Boundary Ratification Review

Date: 2026-03-09

## Intent and Scope

This is a boundary-ratification review for DynamisContent based on current repository code and module structure.

This pass does not refactor code. It defines strict ownership and dependency boundaries for runtime content authority versus asset preparation, session persistence, and world/runtime consumption.

## 1) Repo Overview (Grounded)

Repository shape:

- Multi-module Maven project:
  - `content-api`
  - `content-core`
  - `content-runtime`

Implemented responsibilities by module:

- `content-api`
  - runtime asset identity/types (`AssetId`, `AssetType`, `AssetTypes`)
  - runtime contracts (`AssetManager`, `AssetCache`, `AssetLoader`, `AssetResolver`)
  - manifest/runtime payload contracts (`AssetManifest`, `ManifestEntry`, `DmeshBlob`)
- `content-core`
  - manager/cache implementations (`DefaultAssetManager`, `DefaultAssetCache`)
  - runtime manifest utilities (`AssetManifestBuilder`, `JsonAssetManifestReader`)
  - runtime loader implementation (`DmeshBlobLoader`)
- `content-runtime`
  - composition/wiring facade (`ContentRuntime` builder)

Dependency shape:

- Depends only on `dynamis-core`, Jackson (manifest JSON), and internal modules.
- No dependencies on Session, WorldEngine, SceneGraph, LightEngine, MeshForge, AssetPipeline, Scripting, Localization, or Event.

## 2) Strict Ownership Statement

### 2.1 What DynamisContent should exclusively own

DynamisContent should own **runtime content identity/resolution authority**, including:

- stable runtime content identifiers and type identities
- runtime manifest/catalog contracts
- runtime loader registration and resolution boundaries
- runtime cache and content invalidation semantics

### 2.2 What is appropriate for Content

Appropriate concerns:

- runtime lookup of prepared assets by stable IDs
- loader interfaces and dependency-aware resolution at runtime
- runtime package/manifest metadata interpretation
- runtime cache lifecycle for loaded assets

### 2.3 What DynamisContent must never own

DynamisContent must not own:

- build-time asset preparation, import, baking, or pipeline orchestration (AssetPipeline)
- world/session authority (WorldEngine/Session)
- scene hierarchy/render planning/GPU execution
- scripting/runtime-policy orchestration
- feature-subsystem-specific orchestration logic

## 3) Dependency Rules

### 3.1 Allowed dependencies for DynamisContent

- `DynamisCore` for common exception/foundation primitives
- serialization/parsing libraries required for runtime manifest decoding
- internal loader/cache/runtime wiring modules

### 3.2 Forbidden dependencies for DynamisContent

- AssetPipeline build/import ownership layers
- WorldEngine/session orchestration layers
- render/GPU/scene orchestration layers
- scripting/localization policy layers

### 3.3 Who may depend on DynamisContent

- WorldEngine/session/runtime composition layers that require runtime asset resolution
- feature/runtime systems needing runtime content retrieval by `AssetId`

Dependency direction intent:

- AssetPipeline prepares artifacts -> DynamisContent resolves/loads them at runtime.

## 4) Public vs Internal Boundary Assessment

### 4.1 Canonical public boundary

Public boundary should primarily be:

- `content-api` contracts (`AssetManager`, IDs/types, manifest and loader interfaces)

### 4.2 Internal/implementation areas

Internal by intent:

- `content-core` concrete manager/cache/loaders/parsers
- `content-runtime` builder/wiring convenience layer

### 4.3 Current boundary pressure points

1. `DmeshBlobLoader` implements concrete `.dmesh` binary parsing in runtime core. This is acceptable for runtime decode, but it is a seam that can drift into pipeline-style format evolution logic if expanded carelessly.

2. `content-core` includes both mutable manifest builder and JSON manifest reader. This is practical, but builder usage should remain runtime/testing convenience, not become build-pipeline ownership.

3. `AssetManager` exposure is intentionally broad (`get/tryGet/invalidate/clear`); keep policy out of this layer.

## 5) Policy Leakage / Overlap Findings

### 5.1 Major clean boundaries confirmed

- No session save-state authority is implemented here.
- No world lifecycle/tick authority is implemented here.
- No render/scene/GPU policy or execution code is present.
- No direct overlap with MeshForge or AssetPipeline code in dependencies/imports.

### 5.2 Notable overlap/drift risks

1. **Content <-> AssetPipeline boundary (watch item)**  
Runtime `.dmesh` parsing is correct at consumption time, but future schema/migration logic must stay pipeline-owned and avoid shifting build concerns into runtime content core.

2. **Content <-> Session boundary (currently clean, watch item)**  
Session should persist `AssetId` references, not runtime loaded objects/handles. Keep this contract explicit in integration docs and tests.

3. **Content <-> feature-system policy (currently clean)**  
No feature orchestration exists today; maintain content as neutral runtime data authority only.

## 6) Relationship Clarification

### 6.1 Content vs Session

- Content owns runtime asset resolution/loading.
- Session owns save/load persistence and runtime-state restoration.
- Session should reference content identities (`AssetId`), while Content resolves those IDs at runtime.

### 6.2 Content vs AssetPipeline

- AssetPipeline owns build-time preparation/import/baking.
- Content owns runtime consumption of prepared outputs.
- Content should not absorb pipeline orchestration, transform, or authoring responsibilities.

### 6.3 Content vs WorldEngine

- WorldEngine orchestrates runtime systems and uses Content as a dependency for asset resolution.
- Content should remain orchestration-agnostic.

## 7) Ratification Result

**Ratified with constraints.**

Why:

- Current implementation is tightly focused on runtime content identity, manifests, loaders, and cache.
- Dependency direction is clean and narrow.
- Constraints are needed to prevent runtime loader/manifest code from drifting into build-time asset-pipeline ownership.

## 8) Strict Boundary Rules to Carry Forward

1. Keep `content-api` as the canonical runtime content contract surface.
2. Keep runtime content loading/resolution separate from build-time preparation.
3. Keep Session ownership limited to persistence of references/state, not content runtime object ownership.
4. Keep Content free of world/render/scripting orchestration policy.
5. Keep feature-specific logic outside content core modules.

## 9) Recommended Next Step

Next deep review should be **DynamisAssetPipeline**.

Reason:

- AssetPipeline is the nearest high-risk overlap boundary with Content on preparation-vs-runtime ownership.
- Ratifying AssetPipeline next will lock the build/runtime split and reduce future drift.

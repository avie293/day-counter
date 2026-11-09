# Changelog

## Unreleased

## 2.0.0 - 2026-11-09

|      | Fabric | NeoForge |
|------|--------|----------|
| 26.3 | Yes    | Yes      |

### Added
- Added a Discord link to the Mod Menu entry

### Changed
- Updated to TabbyLib 2, TabbyLib 2.0.1 or newer is now required
- Fabric Loader 0.20.1 or newer and NeoForge 26.3.0.102 or newer are now required
- Changed the license to the [Avie Terms & Conditions 1.0](https://raw.githubusercontent.com/avie293/.github/refs/heads/main/LICENSE)

### Performance
- Reduced per-frame allocations by building the HUD text once per tick instead of every frame

### Removed
- Removed the font option, the counter always uses the regular Minecraft font
- Removed the option to show the day of the Nether and End instead of the overworld day
- Removed the German translation
- Dropped support for Forge and Minecraft 26.2

# GPT Client — Minecraft Java 26.3 / Fabric

A client-side utility mod project named **GPT Client**. It is intended for Minecraft Java 26.3 on Fabric, including Android launchers such as MJLauncher that can load ordinary Fabric mods.

## Included features

- **Fullbright** — press `G` to switch gamma to a high value, then restore the previous gamma.
- **Zoom** — hold `C` to reduce FOV, release to restore it.
- **FPS counter** — shown in the HUD.
- **Custom HUD** — press `O` to toggle the HUD.
- **Keystrokes** — W/A/S/D state display and keyboard hints.
- **CPS counter** — counts left mouse clicks over the last second.
- **Custom main-menu branding** — adds a GPT Client label to the title screen.
- **Performance mode** — press `P` to disable entity shadows; toggling it off restores the previous setting. This is a small setting tweak, not a guaranteed FPS boost.

## Build compatibility

Target versions configured in this project:

- Minecraft Java: `26.3`
- Fabric Loader: `0.19.5`
- Fabric API: `0.161.0+26.3`
- Fabric Loom: `1.18-SNAPSHOT`
- Gradle: `9.7.1`
- Java toolchain: `25`

Fabric's 26.3 announcement recommends Loom 1.17 and Gradle 9.6.0 at its publication date; newer community build notes indicate Loom 1.18-SNAPSHOT / Gradle 9.7.1 for current 26.3 development. If Gradle reports a plugin/API mismatch, update the Loom/Gradle pair to the current compatible versions from Fabric's official development page.

## Build the JAR

This environment did not have Gradle or Java 25 installed and could not reach Maven repositories, so **a compiled JAR could not be built or tested here**. The ZIP contains the real source project, not a fake/empty JAR.

On a computer or Android development environment with JDK 25 and Gradle 9.7.1 installed:

```sh
gradle build
```

The installable mod JAR should be produced under `build/libs/` (use the non-`-sources` JAR). Copy it into the `mods` folder for the Fabric 26.3 instance, alongside Fabric API.

## Notes

- This is a first-pass source project and has **not been compiled or runtime-tested** against Minecraft 26.3 in this environment. Minecraft's client APIs can change between game drops; treat the first build as a compatibility check and use the compiler error output to correct any API differences.
- The custom menu feature is branding text, not a full replacement title-screen layout.
- The zoom uses the current FOV option and restores its previous value when released.

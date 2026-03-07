# Level Access Test

## HTML direct-level launch

You can launch any level directly in HTML by appending `?level=N`:

- `https://cosmonaut.marcvidal.ca/?level=1`
- `https://cosmonaut.marcvidal.ca/?level=2`
- ...
- `https://cosmonaut.marcvidal.ca/?level=24`

This bypasses menu flow and loads `GameScreen` directly for smoke testing.

## Desktop direct-level launch

Run desktop with:

```bash
./gradlew :desktop:run --args="--level=12"
```

## Automated desktop smoke

Run:

```bash
./qa/smoke_levels_desktop.sh
```

The script launches levels `1..24` with `--level=N` and marks pass if each level starts without immediate crash.

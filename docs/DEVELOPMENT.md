# TorNest developer guide

This guide covers catalogue authoring, project modules, and native torrent builds. Start with the [TorNest README](../README.md) for installation and build commands.

## Website and GitHub Pages

The public website source is in `website/`. It uses Node.js 22 and has no package dependencies. To build, check, and preview it locally:

```bash
node website/build.mjs
node website/check.mjs
node website/serve.mjs
```

Open `http://127.0.0.1:4173/TorNest/`. Stop the preview with Ctrl+C. Edit page content in `website/build.mjs`, styling in `website/assets/style.css`, and the copy-command interaction in `website/assets/site.js`. Rebuild to see changes. `website/dist/` is generated and stays out of Git.

GitHub Pages must use **GitHub Actions** as its source under repository **Settings → Pages**. The `Deploy TorNest website` workflow validates pull requests and publishes website changes pushed to `main`; it can also be run manually from Actions. It uploads only `website/dist/`.

The default public address is `https://davidsamir.github.io/TorNest/`. If the address changes, set `SITE_URL` when building and update links to the website. The build uses that URL for canonicals, social metadata, the sitemap, and the 404 page. For search reporting, verify the website's URL prefix with Google Search Console and Bing Webmaster Tools, then submit `sitemap.xml`. No analytics or tracking scripts are included by default.

## TV interface

The app uses a cinematic browsing design: bundled Inter typography, neutral dark surfaces, rounded cards and controls, pill navigation, and artwork-led hero and detail screens. Shared tokens live in `core/ui/.../theme`; features should use those roles rather than introduce their own fonts, colors, or focus treatment.

Focus animation scales only the drawn card or button, leaving its navigation bounds in place. Read animation state inside drawing/layer lambdas, honor Reduce motion, and keep the lazy layouts' stable keys and focus restoration. Artwork decoding is capped at 1280 by 720 pixels, including on 4K displays. The hero advances manually; loading placeholders are static. Avoid live blur, background video previews, and continuous decorative animations on the legacy Fire TV path.

Run the UI interaction tests with `./gradlew :core:ui:testDebugUnitTest`. `TvDesignTest` uses Robolectric native graphics at a TV-sized viewport to check D-pad activation, selected versus focused navigation, card focus geometry, keyboard input, and modal focus containment. It renders component previews into `core/ui/build/ui-previews/` with illustrative test titles. These are component checks, not evidence of real-device playback or frame-rate performance.

Before shipping a visual redesign, test cold startup, fast row/grid navigation, return focus after Details, long title/episode lists, seek controls, and playback with overlays on both an older Fire TV and a modern device. Compare frame timing and memory against the previous release while streaming; a desktop test pass does not establish a device performance result.

## Catalogue format


A new install starts from `core/data/src/main/assets/catalog.json`, described by
`catalog-manifest.json` beside it. From then on the app keeps the catalogue current from the peer
network: the maintainer publishes signed releases with `tools/catalog-publisher`, and every release is
verified against the publisher key built into the APK before it replaces anything. Catalogue release
tooling lives in `tools/catalog-publisher`.

Each entry looks like this. `id` is the key watch progress and My List are stored under; the
publisher pins it once, so renaming a title never loses anyone's progress:

```json
[
  {
    "id": "catalog-the-kid-1921",
    "title": "The Kid",
    "year": "1921",
    "image_url": "https://…/poster.jpg",
    "overview": "optional",
    "genres": ["Comedy"],
    "runtimeMinutes": 68,
    "magnets": [{ "quality": "720p", "magnet": "magnet:?xt=urn:btih:…" }]
  }
]
```

A show is an entry with `"type": "show"` and seasons of episodes in place of magnets. Every episode
has its own id, because that is what the viewer's progress on it is stored under:

```json
{
  "id": "show-the-twilight-zone-1959",
  "type": "show",
  "title": "The Twilight Zone",
  "year": "1959",
  "image_url": "https://…/poster.jpg",
  "backdrop_url": "https://…/backdrop.jpg",
  "genres": ["Sci-Fi"],
  "seasons": [
    {
      "number": 1,
      "episodes": [
        {
          "id": "show-the-twilight-zone-1959-s01e01",
          "number": 1,
          "name": "Where Is Everybody?",
          "runtimeMinutes": 25,
          "magnets": [{ "quality": "720p", "magnet": "magnet:?xt=urn:btih:…" }]
        }
      ]
    }
  ]
}
```

An episode's name is `"name"`, never `"title"`: the release format counts `"title"` keys to check
that nothing was lost, and it counts one per top-level entry. Season `0` is Specials.

For a single-season torrent, put its magnet in that season's `"packs"` list. For a multi-season or
full-show torrent, put it in a `"packs"` list on the show itself; the app shares that source across
all listed seasons. Episode-specific `"magnets"`, season packs, and show packs can coexist. Seasons
and episodes still need catalogue entries; adding a magnet does not discover or create those entries.

Episode selection understands `S01E02`, `1x02`, `Season 1/E02`, `S02/02 - Name`, and
`Season 2 Episode 3` names, including Windows paths, numbered partial packs, and Specials folders.
Combined `S01E01E03` files identify episodes 1 and 3; `S01E01-E03` identifies the full range.
Combined files play from the beginning; separate episode start times require chapter metadata.
Samples and extras are excluded. Show packs require season evidence for episode-only names.
Unlabelled collections are not assigned episodes by alphabetical position or file size. An
episode-specific source can contain one unlabelled video, but conflicting episode labels or
unmatched multi-video sources are refused and the player can try another source. Archives and
unrecognizable names require correction; container detection does not guarantee codec support.

Magnets are validated when the file loads: an entry whose `xt=urn:btih:` is not a 40-character hex
(or 32-character base32) info hash is dropped with a log line rather than failing later in the
player. Only content that may be freely redistributed belongs here — seeding *is* redistribution.

## Module layout

```
:app                 single activity, navigation, Hilt entry points
:core:model          domain types + pure rules (resume thresholds, source selection)
:core:common         typed errors, dispatchers, clock, logging (with export buffer)
:core:ui             theme, focus behaviour, cards/rows/hero/keyboard/consent dialog
:core:data           catalogue in use + updater, Room (progress, My List, search history), settings
:core:catalogue      pure JVM: catalogue release format, ids, Ed25519 verification, fetch sequence
:core:catalogue-swarm pure JVM: DHT pointer, release torrents, DHT state (libtorrent, shared with the tool)
:core:torrent        libtorrent4j engine, sequential streaming, loopback HTTP bridge, budget
:tools:catalog-publisher  desktop CLI: keygen, pin, build, verify, publish, fetch
:core:player         ExoPlayer factory, MediaSession service, playback state machine
:feature:*           home, library, details, search, player, settings
:core:testing        fixtures and fakes
```

## Notes for a real Fire TV

- Display frame-rate matching (24/50/60 Hz) is on by default; turn it off in Settings if your TV
  handles mode switches badly.
- Tunneled playback is enabled where the device reports support (Amazon's recommendation for 4K/HDR)
  and can be disabled in Settings if an AV receiver glitches.
- Media keys and Alexa transport commands work through `MediaSession`.

## Native torrent compatibility build

The compatibility build is reproducible under Linux/WSL. Install CMake, Ninja, Git, Python 3, Perl,
curl and unzip, then run `scripts/native-torrent/prepare.sh` followed by
`scripts/native-torrent/build_android.sh <abi>`. Sources and downloaded dependencies are pinned;
the source patch lives in `scripts/native-torrent/patch_android.py`. Copy the resulting library from
`$HOME/torfilx-native/build/<abi>/libtorrent4j-android11.so` into
`core/torrent/src/main/jniLibs/<abi>/libtorrent4j-android11.so` (or use `TORFILX_NATIVE_WORK` for a different
work directory). Build all four ABIs for the device tests; releases package the two ARM ABIs.


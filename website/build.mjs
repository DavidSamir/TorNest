import { mkdir, writeFile, copyFile, rm } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

const root = path.dirname(fileURLToPath(import.meta.url));
const out = path.join(root, 'dist');
const base = new URL(process.env.SITE_URL || 'https://davidsamir.github.io/TorNest/');
if (!base.pathname.endsWith('/')) base.pathname += '/';
const repo = 'https://github.com/DavidSamir/TorNest';
const releases = `${repo}/releases`;
const escape = text => text.replaceAll('&', '&amp;').replaceAll('"', '&quot;').replaceAll('<', '&lt;');
const arrow = '<span aria-hidden="true">↗</span>';
const button = (text, href, secondary = false) => `<a class="button${secondary ? ' secondary' : ''}" href="${href}">${text} ${arrow}</a>`;
const sectionLabel = (number, text) => `<p class="eyebrow"><span>${number}</span> ${text}</p>`;
const faq = (question, answer) => `<details><summary>${question}<span aria-hidden="true">+</span></summary><div class="answer">${answer}</div></details>`;

const pages = [
  {
    file: 'index.html', label: 'Overview', title: 'TorNest — BitTorrent streaming for Fire TV & Android TV',
    description: 'Meet TorNest: a remote-friendly BitTorrent streaming app for Android-based Fire TV, Android TV, and Google TV. Explore features, installation, and compatibility.',
    body: `<section class="hero wrap">
      <div class="hero-copy"><p class="eyebrow"><span class="live-dot"></span> MADE FOR THE BIG SCREEN</p>
        <h1>Your library.<br>Your <em>living room.</em></h1>
        <p class="intro">Meet TorNest. Browse movies and shows, stream over BitTorrent, and pick up where you left off. All from your TV remote.</p>
        <div class="actions">${button('Get TorNest', 'install.html')}${button('Explore on GitHub', repo, true)}</div>
        <p class="hero-note">For Android-based Fire TV, Android TV &amp; Google TV.</p>
      </div>
      <figure class="hero-preview"><img src="assets/ui-home.png" width="1920" height="1080" alt="TorNest interface preview with rounded navigation, featured title and play controls" fetchpriority="high"><figcaption>New interface preview &middot; Sample title and generated artwork.</figcaption></figure>
    </section>
    <div class="platform-strip"><div class="wrap"><span>YOUR REMOTE. YOUR SCREEN.</span><span>Fire TV</span><span>Android TV</span><span>Google TV</span><a href="compatibility.html">Check your device ${arrow}</a></div></div>
    <section class="section wrap" id="features">
      <div class="section-heading">${sectionLabel('01', 'THE EXPERIENCE')}<h2>Less setup.<br><em>More watching.</em></h2><p>A TV interface built around browsing, playing, and finding your place again.</p></div>
      <div class="feature-grid">
        <article><span class="feature-number">01 / BROWSE</span><h3>Comfortably remote.</h3><p>Explore Movies and Shows, search with the on-screen keyboard, and move through your library with a D-pad.</p></article>
        <article><span class="feature-number">02 / PLAY</span><h3>Start before it finishes.</h3><p>TorNest prioritizes video pieces for playback while the download continues. No separate media server required.</p></article>
        <article><span class="feature-number">03 / RETURN</span><h3>Keep your place.</h3><p>Continue Watching, episode progress, and My List stay on your device. Choose whether to autoplay the next episode.</p></article>
      </div>
    </section>
    <section class="interface-section wrap" aria-labelledby="interface-title">
      <div class="section-heading"><p class="eyebrow">A FRESH VIEW</p><h2 id="interface-title">Made to feel<br><em>at home on TV.</em></h2><p>Clean typography, rounded artwork, and clear remote focus bring your library into view.</p></div>
      <div class="preview-grid">
        <figure><img src="assets/ui-cards.png" width="1920" height="1080" loading="lazy" decoding="async" alt="TorNest poster cards showing a bright rounded focus outline"><figcaption>Find your focus. A clear outline keeps remote navigation easy to follow.</figcaption></figure>
        <figure><img src="assets/ui-search.png" width="1920" height="1080" loading="lazy" decoding="async" alt="TorNest search preview with rounded on-screen keyboard keys"><figcaption>Keep searching simple. Comfortable controls, built around the D-pad.</figcaption></figure>
      </div>
      <p class="preview-note">Component previews from the current source, with illustrative titles and generated artwork. Published APKs may use an earlier interface. See the <a href="${releases}">release notes</a> for the version you install.</p>
    </section>
    <section class="how-section"><div class="wrap how-layout"><div>${sectionLabel('02', 'BEHIND THE PLAY BUTTON')}<h2>A library on screen.<br><em>A network behind it.</em></h2><p class="muted">TorNest includes a catalogue of metadata and source references. Video comes from reachable BitTorrent peers, not bundled media files.</p><a class="text-link" href="troubleshooting.html">Understand playback ${arrow}</a></div><ol class="steps"><li><span>01</span><div><h3>Find something to watch</h3><p>Browse the built-in catalogue of films, seasons, and episodes.</p></div></li><li><span>02</span><div><h3>Connect to peers</h3><p>After you accept sharing, the app finds peers for the selected source.</p></div></li><li><span>03</span><div><h3>Stream as pieces arrive</h3><p>Playback begins as video downloads. Speed and buffering depend on peers and your connection.</p></div></li></ol></div></section>
    <section class="section wrap faq-layout"><div>${sectionLabel('03', 'A FEW GOOD QUESTIONS')}<h2>Before you<br><em>press play.</em></h2></div><div>
      ${faq('Do I need a media server?', '<p>No. TorNest runs on your TV device and streams from BitTorrent peers. You do not need a separate computer or NAS running a media server.</p>')}
      ${faq('Which TVs can run TorNest?', '<p>The release APK targets Android 5.1 / API 22 or newer on ARM devices. Android-based Fire TV, Android TV, and Google TV are intended targets. Check the <a href="compatibility.html">compatibility guide</a> for limits.</p>')}
      ${faq('Does streaming also upload data?', '<p>Yes. Participating in a BitTorrent swarm can upload pieces to other peers, who can see your network IP address. TorNest asks for sharing consent before peer-to-peer operation.</p>')}
      ${faq('Can I browse offline?', '<p>The bundled catalogue supports browsing without fetching an update. Streaming video and receiving catalogue updates require network access.</p>')}
    </div></section>
    <section class="cta wrap"><p class="eyebrow">MAKE YOURSELF AT HOME</p><h2>Meet your next<br><em>TV companion.</em></h2>${button('Install TorNest', 'install.html')}<p>Check your device. Choose a release. Settle in.</p></section>`
  },
  {
    file: 'install.html', label: 'Install', title: 'Install TorNest on Fire TV, Android TV & Google TV',
    description: 'Download a TorNest release APK and install it on an Android-based TV device. Follow sideloading, ADB installation, and update instructions.',
    body: `<section class="page-hero wrap">${sectionLabel('GET STARTED', 'INSTALLATION')}<h1>A few steps<br>to <em>your screen.</em></h1><p class="intro">Install the Android APK on a compatible TV device. You do not need to build the app or set up a media server.</p>${button('Open GitHub releases', releases)}<p class="hero-note">Release files currently use the earlier name: <code>torfilx-release.apk</code>.</p></section>
      <section class="guide wrap"><aside class="guide-aside"><p class="eyebrow">BEFORE YOU BEGIN</p><p>An Android-based TV, a compatible ARM processor, and permission to install an APK.</p><a class="text-link" href="compatibility.html">Check compatibility ${arrow}</a></aside><div class="guide-content">
      <article class="guide-step"><span class="step-index">01</span><div><h2>Choose a release</h2><p>Visit <a href="${releases}">GitHub releases</a>, read its notes, and download the attached <code>torfilx-release.apk</code>. The source-code ZIP is not the installable app.</p><p>Check the notes for the version's upgrade requirements and device limitations. Existing published builds may still show the earlier Torfilx branding.</p></div></article>
      <article class="guide-step"><span class="step-index">02</span><div><h2>Transfer and install</h2><p>Send the APK to your TV using your preferred file-transfer or sideloading tool. When the device asks, allow that installer to install apps, then open the APK and finish installation.</p><p>Menu names and sideloading options differ by device. Some managed TV boxes do not allow installation from outside their store.</p></div></article>
      <article class="guide-step"><span class="step-index">03</span><div><h2>Open TorNest</h2><p>Launch the app from your TV's apps screen. Review the sharing prompt before accepting peer-to-peer playback and catalogue updates.</p><p>Streaming needs reachable peers and can also upload pieces. You can review sharing and cache controls in Settings.</p></div></article>
      <section class="code-panel"><p class="eyebrow">OPTIONAL / INSTALL FROM YOUR COMPUTER</p><h2>Prefer ADB?</h2><p>With Android Platform Tools installed and device debugging enabled, connect using the address provided by your TV.</p><div class="code-toolbar"><span>TERMINAL</span><button type="button" class="copy-button" data-copy="adb-command">Copy commands</button></div><pre><code id="adb-command">adb connect &lt;tv-ip-address&gt;:5555
adb install -r torfilx-release.apk</code></pre><p class="small">Some devices require wireless pairing or a different port. Follow the debugging instructions shown on the device.</p><p class="copy-status small" role="status" aria-live="polite"></p></section>
      <section class="prose-section"><h2>Updating an existing installation</h2><p>Install a compatible APK signed with the same key over the existing app. Read the release notes and back up watch data in Settings before changing installations.</p><p>If an update is rejected, check the signing and migration notes first. Uninstalling removes local app data. Catalogue updates change the library metadata; they do not update the APK.</p><a class="text-link" href="troubleshooting.html">Need a hand? ${arrow}</a></section>
      </div></section>`
  },
  {
    file: 'compatibility.html', label: 'Compatibility', title: 'TorNest device compatibility — Fire TV, Android TV & Google TV',
    description: 'Check TorNest Android version, ARM processor, release APK, and TV platform requirements. Understand device support and playback limitations.',
    body: `<section class="page-hero wrap">${sectionLabel('FIND YOUR FIT', 'DEVICE COMPATIBILITY')}<h1>A big screen.<br>The <em>right platform.</em></h1><p class="intro">TorNest is built for TVs and streaming devices that can run Android APKs. Check the operating system, not just the name on the box.</p></section>
      <section class="wrap requirements"><div><p class="eyebrow">ANDROID VERSION</p><strong>5.1+</strong><span>Android API 22 or newer</span></div><div><p class="eyebrow">RELEASE PROCESSORS</p><strong>ARM</strong><span>32-bit and 64-bit ARM</span></div><div><p class="eyebrow">GOOGLE PLAY SERVICES</p><strong>Not required</strong><span>No separate media server either</span></div></section>
      <section class="section wrap"><h2>Where TorNest <em>fits.</em></h2><div class="table-scroll"><table><caption>Platform scope for the Android release APK</caption><thead><tr><th scope="col">Platform</th><th scope="col">What to check</th></tr></thead><tbody>
      <tr><th scope="row">Android-based Fire TV</th><td>Primary target. Verify the device runs Android-based Fire OS and meets the minimum version.</td></tr>
      <tr><th scope="row">Android TV &amp; Google TV</th><td>Intended targets on compatible ARM hardware. Individual models still need playback testing.</td></tr>
      <tr><th scope="row">Android TV boxes</th><td>Google Play Services is optional. Installation permissions, hardware decoding, and storage still matter.</td></tr>
      <tr><th scope="row">Android emulators</th><td>x86 and x86_64 libraries are included in debug builds, not the release APK.</td></tr>
      <tr><th scope="row">Other TV operating systems</th><td>The APK cannot run directly on Roku OS, Samsung Tizen, LG webOS, Apple tvOS, or Amazon Vega OS.</td></tr>
      </tbody></table></div><p class="muted">These requirements describe the app's build targets. They are not a promise of verified playback on every listed device or model.</p></section>
      <section class="how-section"><div class="wrap split"><div><p class="eyebrow">MORE THAN AN OS VERSION</p><h2>Playback depends<br>on the <em>whole setup.</em></h2></div><div><p>Your TV needs to decode the selected video's codecs, enough free storage for the cache, and a network connection that can reach peers. A catalogue title can appear even if its source is unavailable.</p><p>Older devices and high-bitrate sources may need a different available source or playback settings. Report compatibility with the exact device model, OS version, and app release.</p>${button('Installation guide', 'install.html')}</div></div></section>`
  },
  {
    file: 'troubleshooting.html', label: 'Help', title: 'TorNest help — playback, buffering, installation & updates',
    description: 'Troubleshoot TorNest playback, buffering, APK installation, and updates. Find the device information and diagnostic logs needed for a useful bug report.',
    body: `<section class="page-hero wrap">${sectionLabel('BACK TO WATCHING', 'HELP & TROUBLESHOOTING')}<h1>Let’s get<br><em>unstuck.</em></h1><p class="intro">A few checks for installation, playback, and updates. If the problem persists, help us reproduce it.</p></section>
      <section class="wrap help-content">
      ${faq('A title appears, but it will not play.', '<p>Catalogue metadata and playable video are separate. A source can have no reachable peers, lack a usable magnet, or contain video your TV cannot decode.</p><p>Check your connection, confirm sharing is enabled, and try another available source. If the failure persists, include the title or episode when reporting it.</p>')}
      ${faq('Playback takes a long time to start or keeps buffering.', '<p>Peer availability, network throughput, video bitrate, and device decoding can all affect playback. Check the TV’s connection and free storage, then try another available source.</p><p>Seeking into a part that has not downloaded can require more buffering. A listing in the catalogue does not guarantee a healthy swarm.</p>')}
      ${faq('The APK will not install.', '<p>Check the <a href="compatibility.html">Android version and ARM requirements</a>. Allow installation from the installer you are using, if your TV supports it. Make sure you downloaded the APK rather than the source-code ZIP.</p><p>If replacing an existing installation, check the release notes for signing or migration requirements before uninstalling.</p>')}
      ${faq('An update is rejected.', '<p>Android updates require a compatible package signed with the same signing key. Check the release notes and the version you already have installed.</p><p>Back up watch data from Settings before changing installations. Uninstalling removes local data; do not use it as your first troubleshooting step.</p>')}
      ${faq('Will catalogue updates update the app too?', '<p>No. A catalogue update changes movie and show metadata and source references. To update the app, install the appropriate APK from <a href="'+releases+'">GitHub releases</a>.</p>')}
      ${faq('Where are my watch progress and saved titles?', '<p>TorNest stores Continue Watching, resume positions, and My List on your device. Settings includes manual watch-data backup and restore. There is no account-based cloud sync in this viewing flow.</p>')}
      </section><section class="section wrap split"><div><p class="eyebrow">STILL NEED HELP?</p><h2>A good report<br>makes a <em>difference.</em></h2></div><div><p>Search existing issues before opening a new one. For a bug report, include:</p><ul class="report-list"><li>Device model and Android or Fire OS version</li><li>TorNest version and affected title or episode</li><li>Steps to reproduce, expected behavior, and what happened</li><li>Relevant diagnostic logs exported from Settings</li></ul><p class="small muted">Review logs before posting. Remove IP addresses, credentials, and other private information.</p>${button('Open GitHub issues', `${repo}/issues`)}</div></section>`
  }
];

function layout(page) {
  const canonical = new URL(page.file === 'index.html' ? '' : page.file, base).href;
  const social = new URL('assets/social.png', base).href;
  const schema = page.file === 'index.html' ? {
    '@context': 'https://schema.org', '@type': 'SoftwareApplication', name: 'TorNest',
    description: page.description, operatingSystem: 'Android 5.1 or newer', applicationCategory: 'MultimediaApplication',
    url: base.href, downloadUrl: releases, sameAs: repo
  } : { '@context': 'https://schema.org', '@type': 'WebPage', name: page.title, description: page.description, url: canonical };
  return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${escape(page.title)}</title><meta name="description" content="${escape(page.description)}"><meta name="theme-color" content="#0b0c10">
<link rel="canonical" href="${canonical}"><link rel="icon" type="image/png" href="assets/icon.png"><link rel="stylesheet" href="assets/style.css">
<meta property="og:type" content="website"><meta property="og:site_name" content="TorNest"><meta property="og:title" content="${escape(page.title)}"><meta property="og:description" content="${escape(page.description)}"><meta property="og:url" content="${canonical}"><meta property="og:image" content="${social}"><meta property="og:image:alt" content="TorNest — Your library. Your living room.">
<meta name="twitter:card" content="summary_large_image"><meta name="twitter:title" content="${escape(page.title)}"><meta name="twitter:description" content="${escape(page.description)}"><meta name="twitter:image" content="${social}">
<script type="application/ld+json">${JSON.stringify(schema).replaceAll('<', '\\u003c')}</script><script src="assets/site.js" defer></script></head>
<body><a class="skip-link" href="#main">Skip to content</a><header class="site-header wrap"><a class="brand" href="index.html" aria-label="TorNest home"><span class="brand-mark" aria-hidden="true">▶</span> TorNest<span class="brand-period">.</span></a><nav aria-label="Main navigation">${pages.map(p => `<a href="${p.file}"${p.file === page.file ? ' aria-current="page"' : ''}>${p.label}</a>`).join('')}</nav><a class="header-github" href="${repo}">GitHub ${arrow}</a></header>
<main id="main">${page.body}</main>
<footer class="site-footer"><div class="wrap footer-top"><a class="brand" href="index.html">TorNest<span class="brand-period">.</span></a><p>Your library, everywhere.</p><a href="${repo}/blob/main/CONTRIBUTING.md">Contribute ${arrow}</a><a href="${releases}">Releases ${arrow}</a><a href="${repo}">GitHub ${arrow}</a></div><div class="wrap footer-bottom"><p>Peer-to-peer playback can upload data and makes your network IP visible to peers. Use sources you have permission to download and share.</p><span>BUILT FOR YOUR TV.</span></div></footer></body></html>\n`;
}

// Only this generated directory is replaced; source and repository files are untouched.
await rm(out, { recursive: true, force: true });
await mkdir(path.join(out, 'assets'), { recursive: true });
for (const page of pages) await writeFile(path.join(out, page.file), layout(page));
for (const file of ['style.css', 'site.js', 'social.png', 'ui-home.png', 'ui-cards.png', 'ui-search.png']) await copyFile(path.join(root, 'assets', file), path.join(out, 'assets', file));
await copyFile(path.join(root, '../app/src/main/res/mipmap-xxxhdpi/ic_launcher.png'), path.join(out, 'assets/icon.png'));
await writeFile(path.join(out, '.nojekyll'), '');
await writeFile(path.join(out, 'sitemap.xml'), `<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">${pages.map(p => `<url><loc>${new URL(p.file === 'index.html' ? '' : p.file, base).href}</loc></url>`).join('')}</urlset>\n`);
await writeFile(path.join(out, '404.html'), `<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Page not found — TorNest</title><meta name="robots" content="noindex"><link rel="stylesheet" href="${base.pathname}assets/style.css"></head><body><main class="page-hero wrap"><p class="eyebrow">404 / PAGE NOT FOUND</p><h1>Let’s get you<br><em>back home.</em></h1><p>This address does not match a page on the TorNest website.</p>${button('TorNest home', base.pathname)}</main></body></html>\n`);
console.log(`Built ${pages.length} pages in ${out}`);

import { readFile, readdir, stat } from 'node:fs/promises';
import path from 'node:path';
import assert from 'node:assert/strict';
import { fileURLToPath } from 'node:url';

const root = path.join(path.dirname(fileURLToPath(import.meta.url)), 'dist');
const css = await readFile(path.join(root, 'assets/style.css'), 'utf8');
assert(css.trimStart().startsWith(':root{color-scheme:dark;'), 'Dark palette must start with a valid root rule');
const pages = (await readdir(root)).filter(file => file.endsWith('.html'));
const titles = new Set();
for (const file of pages) {
  const html = await readFile(path.join(root, file), 'utf8');
  const title = html.match(/<title>(.*?)<\/title>/)?.[1];
  assert(title && !titles.has(title), `${file}: missing or duplicate title`);
  titles.add(title);
  assert.equal((html.match(/<h1[ >]/g) || []).length, 1, `${file}: expected one H1`);
  assert(html.includes('name="color-scheme" content="dark"'), `${file}: missing dark color scheme`);
  assert(html.includes('lang="en"'), `${file}: missing document language`);
  const ids = [...html.matchAll(/\bid="([^"]+)"/g)].map(match => match[1]);
  assert.equal(ids.length, new Set(ids).size, `${file}: duplicate element IDs`);
  for (const match of html.matchAll(/(?:href|src)="([^"]+)"/g)) {
    const url = match[1];
    if (/^https?:/.test(url)) continue;
    const [pathname, fragment] = url.split('#');
    let target = (pathname || file).split('?')[0];
    if (target.startsWith('/TorNest/')) target = target.slice('/TorNest/'.length) || 'index.html';
    assert(!target.startsWith('/'), `${file}: unexpected absolute project link ${url}`);
    const location = path.join(root, target);
    assert((await stat(location)).isFile(), `${file}: missing asset ${url}`);
    if (fragment) assert((await readFile(location, 'utf8')).includes(`id="${fragment}"`), `${file}: missing anchor ${url}`);
  }
  if (file !== '404.html') {
    assert(html.includes('name="description"'), `${file}: missing description`);
    assert(html.includes('rel="canonical"'), `${file}: missing canonical`);
    const schema = JSON.parse(html.match(/<script type="application\/ld\+json">(.*?)<\/script>/s)[1]);
    assert(schema.url.startsWith('https://'), `${file}: invalid schema URL`);
    assert((await readFile(path.join(root, 'sitemap.xml'), 'utf8')).includes(schema.url), `${file}: page absent from sitemap`);
    assert.equal((html.match(/aria-current="page"/g) || []).length, 1, `${file}: navigation must identify current page`);
  }
}
console.log(`Checked ${pages.length} HTML pages: titles, metadata, structured data, local links, anchors, assets, sitemap, and navigation.`);

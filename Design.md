# Design.md — RentNest Visual Design System
### Direction: Editorial, Ellipsus-inspired — "a home is the next chapter"

---

## 1. Design Thesis

Ellipsus sells the *craft of writing* through elegant serif typography, a cinematic dark hero, and hand-drawn editorial marks. RentNest borrows that same editorial confidence but reframes the metaphor: **finding a home is the start of a new chapter, not a transaction.**

Where Ellipsus uses paper cranes and sketched books, RentNest uses sketched keys, floor-plan outlines, and hand-drawn circles/underlines around the details that matter (rent, locality, "verified" badges) — as if someone annotated the listing themselves. The result should feel like a thoughtfully designed literary magazine that happens to help you find a flat, not a generic real-estate template of blue buttons and stock photography.

**Signature element:** on the property detail page, the "Verified" badge and the rent figure are hand-circled with an animated SVG stroke that draws itself in on scroll/load — the one moment of warmth and craft that recurs across the product.

---

## 2. Color Palette

| Token | Hex | Use |
|---|---|---|
| `--ink` | `#15130F` | Dark hero background, primary headings on light sections |
| `--parchment` | `#F5F0E4` | Primary light background (search, dashboards, cards) |
| `--paper-white` | `#FBF8F1` | Card surfaces, elevated panels on parchment |
| `--clay` | `#B8562F` | Primary accent — CTAs, active states, price highlights |
| `--sage` | `#5F6B4E` | Secondary accent — success states, "Verified" / "Approved" badges |
| `--charcoal` | `#2A2620` | Body text on light backgrounds |
| `--fog` | `#8A8272` | Muted text, captions, placeholder copy |
| `--gold-thread` | `#C9A24A` | Hand-drawn annotation strokes, dividers, hover underlines |

Dark hero sections use `--ink` background with `--parchment`-toned headline text and `--gold-thread` for annotation strokes — a warm glow rather than a cold, techy dark mode.

## 3. Typography

| Role | Typeface | Notes |
|---|---|---|
| Display (H1/hero) | **Fraunces** (variable serif) | High-contrast, slightly eccentric curves at large sizes — used deliberately, only for hero headlines and page titles. |
| Body | **Inter** | Clean, highly legible sans for paragraphs, forms, and UI copy. |
| Annotation / handwritten accents | **Caveat** | Used *sparingly*: circling a badge, a small "new!" flag, a margin note on empty states — never for body copy or long text. |
| Data / numeric (rent, stats) | **Inter** (tabular figures) | Dashboards and price displays use tabular nums for alignment. |

### Type Scale

| Token | Size | Weight | Usage |
|---|---|---|---|
| `--text-hero` | 64px / 1.05 | Fraunces 500 | Landing hero headline |
| `--text-h1` | 40px / 1.1 | Fraunces 500 | Page titles |
| `--text-h2` | 28px / 1.2 | Fraunces 500 | Section headers |
| `--text-h3` | 20px / 1.3 | Inter 600 | Card titles, dashboard labels |
| `--text-body` | 16px / 1.6 | Inter 400 | Paragraphs, form labels |
| `--text-caption` | 13px / 1.4 | Inter 500 | Meta info, timestamps |
| `--text-annotation` | 22px / 1 | Caveat 500 | Hand-marked accents only |

## 4. Layout Concept

```
┌─────────────────────────────────────────────┐
│  INK HERO (dark)                             │
│  "Every rental is the start of a chapter."   │
│  [big serif headline]   [sketched key motif] │
│  (search bar sits low in hero, glowing edge) │
└─────────────────────────────────────────────┘
┌─────────────────────────────────────────────┐
│  PARCHMENT SECTION                           │
│  Filter rail (left)  |  Property grid (right)│
│  cards on --paper-white, generous whitespace │
└─────────────────────────────────────────────┘
┌─────────────────────────────────────────────┐
│  PROPERTY DETAIL                             │
│  Gallery (large) — Title (Fraunces)          │
│  Rent [hand-circled in gold-thread]          │
│  "Verified" badge [hand-circled]             │
│  Enquiry form — quiet, single column         │
└─────────────────────────────────────────────┘
```

- **Whitespace discipline:** minimum 32px vertical rhythm between sections on desktop; content max-width 1120px, centered, so long lines of body text stay readable (Ellipsus's editorial feel depends on generous margins, not density).
- **Cards:** `--paper-white` surface, 1px `--fog`-at-10%-opacity border, no heavy drop shadows — a soft 1–2px offset shadow only, keeping the flat, print-like quality.
- **Grid:** 12-column responsive grid; property cards flow 3-up desktop, 2-up tablet, 1-up mobile.

## 5. Illustrative Motifs

Hand-drawn SVG line-art, single-weight stroke in `--gold-thread` or `--charcoal`, used as ambient decoration (never as functional icons — those stay simple/geometric for clarity):

- A sketched key and keyhole near the hero search bar.
- A loose floor-plan outline as a background watermark on empty-state illustrations ("No properties match your filters yet").
- A hand-drawn circle animating around the rent price and the "Verified" badge on the property detail page (the signature element).
- A soft underline stroke beneath the active nav item, drawn rather than a flat rectangle.

Keep these *sparse* — one motif per screen at most. The goal is warmth, not clutter.

## 6. Motion

- **Hero load:** headline fades/rises in first, then the sketched key motif "draws itself" (stroke-dashoffset animation), then the search bar settles into place. One orchestrated sequence, not scattered effects.
- **Signature circle:** the hand-drawn circle around price/verified badge draws in via SVG stroke animation the first time the property detail page is viewed (triggered once, not looping).
- **Hover:** property cards lift 2px with a slightly warmer shadow; nav links get a hand-drawn underline stroke that draws in on hover.
- **Respect `prefers-reduced-motion`:** all drawing/entrance animations degrade to a simple opacity fade.

## 7. Component Notes

| Component | Styling |
|---|---|
| Primary button | `--clay` background, `--paper-white` text, Inter 600, 8px radius, no gradient |
| Secondary button | Transparent, `--charcoal` border + text, fills `--parchment` on hover |
| Badge — Verified | `--sage` text on `--sage`-at-12%-opacity background, small Caveat "✓ verified" tag optional |
| Badge — Pending/Status | `--fog` text on `--parchment`, neutral until resolved |
| Input fields | `--paper-white` background, `--fog` border, `--clay` focus ring, Inter body text |
| Filter panel | Sticky on desktop, collapsible drawer on mobile, `--parchment` background distinct from card grid |
| Dashboard stat card | Large Inter tabular number, small caption label beneath, thin `--gold-thread` top border as a quiet accent |

## 8. Accessibility Floor

- All text meets WCAG AA contrast against its background (`--charcoal` on `--parchment`/`--paper-white`, `--parchment` on `--ink`).
- Visible focus rings (`--clay`, 2px) on every interactive element — never `outline: none` without a replacement.
- Hand-drawn/annotation elements are decorative only; never the sole carrier of meaning (e.g., "Verified" always has the text label, the circle is a bonus, not a substitute).
- Reduced-motion users get instant, static states instead of draw-in animations.

## 9. What to Avoid

- No generic SaaS blue (`#2563EB`-style) anywhere — it breaks the editorial tone.
- No stock photography of smiling people shaking hands over a "sold" sign — use the line-art motifs and real property photos only.
- No dense, bordered-table dashboards — keep dashboard stat cards airy and typographic, in keeping with the rest of the site.
- Don't let the handwritten font (Caveat) appear in body copy, forms, or anywhere text needs to be quickly scanned — it's an accent, not a reading font.

---
name: Forest Folio
colors:
  surface: '#f9f9ff'
  surface-dim: '#d3daef'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f3ff'
  surface-container: '#e9edff'
  surface-container-high: '#e1e8fd'
  surface-container-highest: '#dce2f7'
  on-surface: '#141b2b'
  on-surface-variant: '#404940'
  inverse-surface: '#293040'
  inverse-on-surface: '#edf0ff'
  outline: '#707a6f'
  outline-variant: '#bfc9bd'
  surface-tint: '#1f6c3a'
  primary: '#004c22'
  on-primary: '#ffffff'
  primary-container: '#166534'
  on-primary-container: '#93e0a2'
  inverse-primary: '#8bd79b'
  secondary: '#904d00'
  on-secondary: '#ffffff'
  secondary-container: '#fe932c'
  on-secondary-container: '#663500'
  tertiary: '#004b32'
  on-tertiary: '#ffffff'
  tertiary-container: '#006545'
  on-tertiary-container: '#70e4b1'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#a6f4b5'
  primary-fixed-dim: '#8bd79b'
  on-primary-fixed: '#00210b'
  on-primary-fixed-variant: '#005226'
  secondary-fixed: '#ffdcc3'
  secondary-fixed-dim: '#ffb77d'
  on-secondary-fixed: '#2f1500'
  on-secondary-fixed-variant: '#6e3900'
  tertiary-fixed: '#85f8c4'
  tertiary-fixed-dim: '#68dba9'
  on-tertiary-fixed: '#002114'
  on-tertiary-fixed-variant: '#005137'
  background: '#f9f9ff'
  on-background: '#141b2b'
  surface-variant: '#dce2f7'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.015em
  headline-xl:
    fontFamily: Plus Jakarta Sans
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.015em
  headline-xl-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.005em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-md:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.025em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-desktop: 1.5rem
  margin: 1rem
  margin-tablet: 1.5rem
  margin-desktop: 2rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system establishes a dual-mode visual language tailored for both an engaging literary storefront and an ultra-dense, pragmatic administrative dashboard. The aesthetic bridges classic bibliophilia with structured modern software design, instilling authority, reliability, and tactile calm.

The personality balances two distinct domains:
- **Customer Storefront:** Serene, editorial, trustworthy, and inviting. Evokes the atmosphere of a curated independent bookstore with clear categorizations, generous breathing room, and structured presentation.
- **Admin Operations:** Highly structured, utilitarian, and dependable. Designed for high data density, effortless inventory control, ledger management, and swift bulk interactions.

The visual style is **Corporate / Modern** elevated by subtle literary warmth. It uses structured neo-grotesque geometry, flat surfaces grounded by crisp hairline dividers (`#E5E7EB`), soft 4px (`roundedness: 1`) corners, and natural ambient shadows rather than exaggerated layering.

## Colors

The palette draws on heritage book cloth, aged paper neutrals, and crisp functional indicators.

### Functional Allocation
- **Primary (`#166534` - Deep Forest Green):** Brand anchor. Used for primary storefront buttons, top-level navigation, active table selections, and primary structural accents. Communicates longevity and stability.
- **Secondary (`#D97706` - Warm Amber/Gold):** High-intent conversion trigger. Reserved for calls-to-action such as "Buy Now," promotional flash tags, pre-order badges, and financial alerts requiring operator attention.
- **Tertiary (`#059669` - Leaf Emerald):** Positive feedback and active indicators. Used for in-stock statuses, successful checkout receipts, live transaction syncs, and positive revenue trajectories.
- **Neutral (`#111827` - Deep Charcoal):** Primary text and sharp structural silhouettes. Softened off-black avoids visual fatigue while maintaining optimal contrast across high-density tables.

### Surfaces & Backgrounds
- **App Canvas (`#F9FAFB`):** Standard off-white base layer across administrative and public viewports.
- **Subtle Surface Container (`#F3F4F6`):** Secondary container layer for sidebars, table headers, filter toolbars, and inactive toggle tracks.
- **Card Surface (`#FFFFFF`):** Base canvas for product listings, data tables, and modal dialogs.
- **Hairline Borders (`#E5E7EB`):** Defined 1px separation lines maintaining legibility without heavy optical weight.

## Typography

The typographic hierarchy couples geometric clarity with functional density:

- **Headings (Plus Jakarta Sans):** Brings clean, modern geometric structure with humanist warmth to page headers, collection titles, and dashboard KPI summaries.
- **Body & Data Displays (Inter):** Maximizes vertical rhythm, tabular alignment, and effortless scanning in dense inventory tables, form fields, and long descriptions.
- **Editorial Accents:** Storefront curation labels, author signatures, and highlighted book summaries can leverage italicized editorial treatments to evoke classic literary publications without compromising screen legibility.
- **Numbers & Metrics:** Tabular figures (`tnum`) must be enforced for all table cells, price outputs, and SKU numbers to ensure strict vertical column alignment across rows.

## Layout & Spacing

The layout model utilizes a standard 12-column fluid grid system with distinct operational rules for public vs. administrative views:

### Breakpoints & Canvas Bounds
- **Mobile (<640px):** 4-column layout, `margin: 1rem`, `gutter: 1rem`. Tables collapse into linear key-value cards.
- **Tablet (640px - 1024px):** 8-column layout, `margin-tablet: 1.5rem`, `gutter: 1rem`. Secondary sidebar collapses into a slide-over drawer.
- **Desktop (>1024px):** 12-column layout, `margin-desktop: 2rem`, `gutter-desktop: 1.5rem`. Storefront content spans a maximum container width of `1280px` centered.
- **Admin Viewport (>1440px):** Expands to a full-width dashboard container (`max-width: 1600px`) to support horizontal tabular expansion without nested clipping.

### Component Spacing Discipline
- `space-xs` (4px): Inner badge padding, micro-icon margins, compact indicator gaps.
- `space-sm` (8px): Form input inner vertical padding, table row inline spacing, button text-to-icon gaps.
- `space-md` (16px): Card internal padding, form row gaps, list item spacing.
- `space-lg` (24px): Sectional card headers, storefront grid cell gaps, modal margins.
- `space-xl` (32px): Layout section transitions, dashboard metric widget blocks.

## Elevation & Depth

This system avoids excessive floating drop shadows in favor of **structural containment and low-contrast outlines**:

- **Level 0 (Flat):** Used for main backgrounds (`#F9FAFB`), standard table header rows (`#F3F4F6`), and disabled components. Border: `1px solid #E5E7EB`.
- **Level 1 (Default Cards & Surfaces):** Used for book cards, table containers, and filter toolbars. Subtle ambient shadow: `0 1px 3px 0 rgba(17, 24, 39, 0.05), 0 1px 2px -1px rgba(17, 24, 39, 0.05)` paired with a `1px solid #E5E7EB` border.
- **Level 2 (Hover & Active Panels):** Used for interactive card states, book items on mouseover, and contextual popovers. Shadow: `0 4px 6px -1px rgba(17, 24, 39, 0.07), 0 2px 4px -2px rgba(17, 24, 39, 0.05)`.
- **Level 3 (Overlays & Dialogs):** Modals, search drawers, and flyout sheets. Shadow: `0 20px 25px -5px rgba(17, 24, 39, 0.1), 0 8px 10px -6px rgba(17, 24, 39, 0.05)`.
- **Layering Principle:** High contrast borders maintain clarity across white-on-off-white transitions. Tinted shadows are strictly keyed to the neutral (`#111827`), preserving natural daylight tones.

## Shapes

The design system uses a **Soft (`1`)** shape language.

- **Base Radius (0.25rem / 4px):** Standard inputs, buttons, checkboxes, chips, table row hover selections, and operational badges.
- **Medium Radius (`rounded-lg`, 0.5rem / 8px):** Cards, modal dialogs, book cover mockups, and panel surfaces.
- **Large Radius (`rounded-xl`, 0.75rem / 12px):** Toast notifications, floating alert containers, and customer showcase hero blocks.
- **Geometric Strictness:** Clean, low-radius curvature creates an orderly, publication-grade feel. Fully rounded pills are strictly limited to toggle switches and compact numeric notification badges.

## Components

### Buttons
- **Primary:** Solid `#166534`, text `#FFFFFF`, 4px border radius. Hover: `#14532D`. Active: `#052e16`. Focus: 2px offset ring with `#166534`.
- **Secondary Accent:** Solid `#D97706`, text `#FFFFFF`. Hover: `#B45309`. Reserved for checkout and urgent actions.
- **Outline / Neutral:** Background `#FFFFFF`, border `1px solid #E5E7EB`, text `#111827`. Hover: `#F3F4F6`.
- **Ghost:** Background transparent, text `#166534`. Hover: `#F0FDF4`.

### Cards & Book Displays
- **Storefront Book Card:** Surface `#FFFFFF`, border `1px solid #E5E7EB`, radius 8px (`rounded-lg`), elevation Level 1. Features an embedded aspect-ratio container (2:3) for book jackets with a subtle inward inset shadow (`inset 0 0 0 1px rgba(0,0,0,0.05)`).
- **Admin Summary Card:** Surface `#FFFFFF`, border `1px solid #E5E7EB`, padding `space-md`. Displays micro-KPI titles in `label-sm` muted uppercase with primary numeric metrics in `headline-md`.

### Data Tables
- **Header:** Background `#F3F4F6`, text `#4B5563`, height 40px, font `label-sm`, uppercase tracking.
- **Rows:** Height 48px (dense) or 56px (standard), background `#FFFFFF`, bottom border `1px solid #E5E7EB`. Hover: `#F9FAFB`.
- **Numeric Alignment:** All currency, inventory counts, and dates align strictly right using tabular numbers (`font-variant-numeric: tabular-nums`).

### Form Inputs & Controls
- **Text Inputs:** Height 38px, padding `space-xs space-sm`, border `1px solid #D1D5DB`, radius 4px. Focus: border `#166534`, 1px box-shadow `#166534`.
- **Checkboxes & Radios:** 16px square/circle, border `1px solid #D1D5DB`, radius 4px (checkbox). Checked state: `#166534` with white checkmark glyph.
- **Toggle Switches:** Width 36px, height 20px, pill-radius. Track inactive: `#D1D5DB`; track active: `#166534`. Thumb: 16px white circle, 2px transition.

### Badges & Chips
- **In Stock:** Background `#ECFDF5`, text `#065F46`, border `1px solid #A7F3D0`.
- **Low Stock / Warning:** Background `#FFFBEB`, text `#92400E`, border `1px solid #FDE68A`.
- **Out of Stock / Critical:** Background `#FEF2F2`, text `#991B1B`, border `1px solid #FECACA`.
- **Category Filter Chip:** Border `1px solid #E5E7EB`, background `#FFFFFF`, radius 4px. Active state: background `#166534`, text `#FFFFFF`.
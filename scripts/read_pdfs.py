#!/usr/bin/env python3
"""
Estrae testo da tutti i PDF di una directory.

Uso:
  python3 scripts/read_pdfs.py --root Materiale --out extracted_pdf_text
  python3 scripts/read_pdfs.py --root . --out extracted_pdf_text --ocr

Dipendenze consigliate:
  pip install pymupdf pillow pytesseract

OCR:
  L'opzione --ocr usa pytesseract solo se il binario `tesseract` è installato nel sistema.
  Se non è disponibile, lo script continua con la sola estrazione del testo incorporato.
"""
from __future__ import annotations

import argparse
import json
import re
import shutil
import sys
from pathlib import Path

try:
    import fitz  # PyMuPDF
except Exception as exc:  # pragma: no cover
    print("Errore: PyMuPDF non installato. Installa con: pip install pymupdf", file=sys.stderr)
    raise


def safe_name(path: Path, root: Path) -> str:
    rel = path.relative_to(root).as_posix()
    rel = re.sub(r"[^A-Za-z0-9._/-]+", "_", rel)
    return rel.replace("/", "__") + ".txt"


def extract_with_pymupdf(pdf_path: Path) -> tuple[str, list[dict]]:
    chunks: list[str] = []
    pages_meta: list[dict] = []
    with fitz.open(pdf_path) as doc:
        for i, page in enumerate(doc, start=1):
            text = page.get_text("text") or ""
            pages_meta.append({"page": i, "chars": len(text), "method": "text"})
            chunks.append(f"\n\n===== PAGE {i} =====\n{text}")
    return "".join(chunks).strip(), pages_meta


def ocr_sparse_pages(pdf_path: Path, existing_meta: list[dict], min_chars: int = 20, dpi: int = 180) -> tuple[str, list[dict]]:
    """OCR solo sulle pagine con poco/nulla testo. Richiede tesseract + pytesseract + pillow."""
    if shutil.which("tesseract") is None:
        return "", [{"warning": "tesseract binary non disponibile; OCR saltato"}]
    try:
        import pytesseract
        from PIL import Image  # noqa: F401
    except Exception as exc:
        return "", [{"warning": f"pytesseract/Pillow non disponibili; OCR saltato: {exc}"}]

    ocr_chunks: list[str] = []
    ocr_meta: list[dict] = []
    with fitz.open(pdf_path) as doc:
        for idx, page_meta in enumerate(existing_meta):
            if page_meta.get("chars", 0) >= min_chars:
                continue
            page = doc[idx]
            pix = page.get_pixmap(matrix=fitz.Matrix(dpi / 72, dpi / 72), alpha=False)
            img = Image.frombytes("RGB", [pix.width, pix.height], pix.samples)
            text = pytesseract.image_to_string(img, lang="ita+eng")
            ocr_meta.append({"page": idx + 1, "chars": len(text), "method": "ocr"})
            ocr_chunks.append(f"\n\n===== PAGE {idx + 1} OCR =====\n{text}")
    return "".join(ocr_chunks).strip(), ocr_meta


def main() -> int:
    parser = argparse.ArgumentParser(description="Estrae testo da tutti i PDF trovati sotto --root")
    parser.add_argument("--root", default=".", help="Directory da scandire")
    parser.add_argument("--out", default="extracted_pdf_text", help="Directory output")
    parser.add_argument("--ocr", action="store_true", help="Prova OCR sulle pagine senza testo")
    parser.add_argument("--min-chars", type=int, default=20, help="Soglia caratteri per considerare una pagina vuota")
    args = parser.parse_args()

    root = Path(args.root).resolve()
    out = Path(args.out).resolve()
    out.mkdir(parents=True, exist_ok=True)
    pdfs = sorted(root.rglob("*.pdf"))
    manifest = []

    for pdf in pdfs:
        try:
            text, meta = extract_with_pymupdf(pdf)
            ocr_text = ""
            ocr_meta = []
            if args.ocr:
                ocr_text, ocr_meta = ocr_sparse_pages(pdf, meta, min_chars=args.min_chars)
            target = out / safe_name(pdf, root)
            target.parent.mkdir(parents=True, exist_ok=True)
            body = f"SOURCE: {pdf}\nPAGES: {len(meta)}\n\n{text}"
            if ocr_text:
                body += "\n\n\n===== OCR EXTRA =====\n" + ocr_text
            target.write_text(body, encoding="utf-8", errors="replace")
            manifest.append({
                "pdf": str(pdf.relative_to(root)),
                "pages": len(meta),
                "chars": len(text),
                "output": str(target.relative_to(out)),
                "ocr": ocr_meta,
            })
            print(f"OK {pdf} -> {target} ({len(text)} chars)")
        except Exception as exc:
            manifest.append({"pdf": str(pdf), "error": repr(exc)})
            print(f"ERROR {pdf}: {exc}", file=sys.stderr)

    (out / "manifest.json").write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"\nEstratti {len(pdfs)} PDF. Manifest: {out / 'manifest.json'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

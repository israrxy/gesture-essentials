#!/usr/bin/env bash
# Hard project rule: no source file may exceed 500 lines.
set -euo pipefail

LIMIT=500
failed=0

while IFS= read -r -d '' file; do
  lines=$(wc -l < "$file" | tr -d ' ')
  if [ "$lines" -gt "$LIMIT" ]; then
    echo "FAIL  $file has $lines lines (limit $LIMIT)"
    failed=1
  fi
done < <(find app/src -type f \( -name '*.kt' -o -name '*.xml' \) -print0)

if [ "$failed" -ne 0 ]; then
  exit 1
fi

echo "OK    every source file is within $LIMIT lines"

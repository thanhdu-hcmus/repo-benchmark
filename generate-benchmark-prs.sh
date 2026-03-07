#!/bin/bash

set -e

BASE_BRANCH="main"
PATCH_DIR="benchmark/patches"
PATCH_FILE=""
TOTAL_PRS=15

# parse options
while getopts "p:f:" opt; do
  case $opt in
    p) PATCH_DIR="$OPTARG" ;;
    f) PATCH_FILE="$OPTARG" ;;
    *) echo "Usage: $0 [-p patch_directory] [-f patch_file]"
       exit 1 ;;
  esac
done

echo "Starting benchmark PR generation..."
echo "Base branch: $BASE_BRANCH"

create_pr () {

  INDEX=$1
  PATCH=$2
  BRANCH="benchmark-pr-$INDEX"

  echo "-----------------------------------"
  echo "Processing patch $PATCH"

  git checkout $BASE_BRANCH
  git pull origin $BASE_BRANCH

  # delete local branch
  if git show-ref --verify --quiet refs/heads/$BRANCH; then
      git branch -D $BRANCH
  fi

  git checkout -b $BRANCH

  # skip nếu patch không tồn tại
  if [ ! -f "$PATCH" ]; then
      echo "Patch $PATCH not found, skipping..."
      return
  fi

  # skip nếu patch rỗng
  if [ ! -s "$PATCH" ]; then
      echo "Patch $PATCH is empty, skipping..."
      return
  fi

  # close PR nếu tồn tại
  EXISTING_PR=$(gh pr list \
      --head "$BRANCH" \
      --base "$BASE_BRANCH" \
      --state open \
      --json number \
      --jq '.[0].number')

  if [ -n "$EXISTING_PR" ]; then
      echo "Closing existing PR #$EXISTING_PR"
      gh pr close "$EXISTING_PR"
  fi

  # delete remote branch nếu tồn tại
  git push origin --delete $BRANCH 2>/dev/null || true

  # apply patch
  git apply "$PATCH"

  FILES=$(git apply --numstat "$PATCH" | awk '{print $3}')

  if [ -z "$FILES" ]; then
      echo "No files changed in patch, skipping..."
      return
  fi

  git add $FILES

  git commit -m "Benchmark PR $INDEX"

  git push -f origin $BRANCH

  gh pr create \
      --title "Benchmark PR $INDEX" \
      --body "Benchmark pull request $INDEX for AI code review evaluation." \
      --base $BASE_BRANCH \
      --head $BRANCH

  echo "PR created for $BRANCH"
}

# nếu chạy 1 patch cụ thể
if [ -n "$PATCH_FILE" ]; then

  INDEX=$(basename "$PATCH_FILE" | grep -o '[0-9]\+')

  create_pr "$INDEX" "$PATCH_FILE"

  exit 0
fi

# chạy toàn bộ patch
for i in $(seq -w 1 $TOTAL_PRS)
do
  PATCH="$PATCH_DIR/pr$i.patch"

  create_pr "$i" "$PATCH"
done

echo "All PRs processed!"
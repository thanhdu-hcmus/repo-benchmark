#!/bin/bash

for i in $(seq -w 1 15)
do
  BRANCH="benchmark-pr-$i"

  git push origin --delete $BRANCH || true
done
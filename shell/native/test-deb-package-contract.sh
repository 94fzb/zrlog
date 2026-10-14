#!/usr/bin/env bash
set -euo pipefail
archive=${1:?Usage: test-deb-package-contract.sh package.deb}
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
listing=$(dpkg-deb --contents "$archive")
for path in usr/bin/zrlog etc/init.d/zrlog; do
  printf '%s\n' "$listing" | rg -q "^-rwxr-xr-x +root/root .* \\./${path}$"
done
for unit in zrlog.service zrlog-upgrade@.service zrlog-upgrade@.path; do
  printf '%s\n' "$listing" | rg -q "^-rw-r--r-- +root/root .* \\./lib/systemd/system/${unit//./\\.}$"
done
dpkg-deb --extract "$archive" "$work/data"
dpkg-deb --control "$archive" "$work/control"
for script in postinst prerm postrm; do
  [[ -x "$work/control/$script" ]]
  bash -n "$work/control/$script"
done
bash -n "$work/data/etc/init.d/zrlog"
rg -q 'upgrade --channel=%i --non-interactive --json' "$work/data/lib/systemd/system/zrlog-upgrade@.service"
rg -q 'PathExists=/var/lib/zrlog/upgrade-request/%i.request' "$work/data/lib/systemd/system/zrlog-upgrade@.path"
rg -q 'install -d -o root -g root -m 0700 /var/cache/zrlog-updater' "$work/control/postinst"
printf 'deb executable, systemd units, ownership and maintainer scripts passed\n'

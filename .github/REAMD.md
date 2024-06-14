```yaml
      - name: Cache node modules admin
        uses: actions/cache@v3
        with:
          path: admin-web/src/main/frontend/node_modules
          key: ${{ runner.os }}-node-admin-${{ hashFiles('admin-web/src/main/frontend/yarn.lock') }}
          restore-keys: |
            ${{ runner.os }}-node-admin-
      - name: Cache node modules blog
        uses: actions/cache@v3
        with:
          path: blog-web/src/main/frontend/node_modules
          key: ${{ runner.os }}-node-blog-${{ hashFiles('blog-web/src/main/frontend/yarn.lock') }}
          restore-keys: |
            ${{ runner.os }}-node-blog-
```
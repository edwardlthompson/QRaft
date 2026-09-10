# Shape packs

Drop a JSON file here and open a PR. Schema:

```json
{
  "id": "my-pack",
  "name": "Human label",
  "moduleShapes": ["SQUARE", "ROUNDED", "DOT", "DIAMOND", "PILL_H", "PILL_V", "BLOB"],
  "finderShapes": ["SQUARE", "ROUNDED", "CIRCLE", "HEX", "RING"]
}
```

Optional SVGs go in `examples/android/app/src/main/assets/icons/`. Packs are read-only assets; they never fetch the network.

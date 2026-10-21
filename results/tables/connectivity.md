# Topological ordering and SCC

| Graph | V | E | Topo, ms | Topo ops | DAG ordered | Cycle detected | SCC, ms | SCC ops | SCC count | Largest SCC |
|---|---|---|---|---|---|---|---|---|---|---|
| Small | 10 | 30 | 0.027 | 60 | yes | yes | 0.013 | 30 | 1 | 10 |
| Medium | 100 | 500 | 0.073 | 1000 | yes | yes | 0.069 | 500 | 1 | 100 |
| Large | 1000 | 5000 | 0.535 | 10000 | yes | yes | 0.242 | 5000 | 14 | 987 |

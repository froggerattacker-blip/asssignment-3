# DSU: optimized vs naive

| Graph | V | E | find calls | union calls | hops (compressed + rank) | hops (naive) | speedup in hops |
|---|---|---|---|---|---|---|---|
| Small | 10 | 30 | 60 | 30 | 43 | 216 | 5.02 |
| Medium | 100 | 500 | 1000 | 500 | 890 | 42221 | 47.44 |
| Large | 1000 | 5000 | 10000 | 5000 | 8985 | 4227217 | 470.47 |

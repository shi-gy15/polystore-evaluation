# peking-opera-benchmark

## Cross-model Analytical Workloads

Specifications for task $T_1$ to $T_6$ are written in ArangoDB Query Language (AQL) and OrientDB SQL (OSQL) formats respectively in `./experiments/polystore-evaluation/`.

Evaluation results are summarized in `./experiments/polystore-evaluation/`. The mean latency (in milliseconds) of each query is calculated as the mean value of five identical executions, with query cache disabled.

## Hybrid Multi-model Transactional Workloads

Java codes for obtaining the transactional workloads: `./hybrid-workload`, including both write-heavy and read-heavy workloads.

| Category    | Write operation percentage | Read operation percentage | Number of operations ($\times 10^6$) |
| ----------- | -------------------------- | ------------------------- | ------------------------------------ |
| Write-heavy | 50%                        | 50%                       | 1.5                                  |
| Read-heavy  | 10%                        | 90%                       | 7.5                                  |


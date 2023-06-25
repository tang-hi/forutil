## Rewrite ForUtil

Vectorized Code in `SimdPack.java`.

Current Result
Benchmark                              Mode  Cnt          Score          Error  Units
VectorizeBenchmark.decode1            thrpt   15   58293945.447 ±  1524549.587  ops/s
VectorizeBenchmark.decode2            thrpt   15   55598229.538 ±  4516920.135  ops/s
VectorizeBenchmark.decode3            thrpt   15   57163871.965 ±  1566246.490  ops/s
VectorizeBenchmark.decode4            thrpt   15   55128874.528 ±  4752397.170  ops/s
VectorizeBenchmark.decode5            thrpt   15   53822335.729 ±  4599217.489  ops/s
VectorizeBenchmark.decode6            thrpt   15   48155246.120 ±  7519360.551  ops/s
VectorizeBenchmark.decode7            thrpt   15   50253799.192 ±   820075.648  ops/s
VectorizeBenchmark.decode8            thrpt   15   68849728.856 ±  1818468.973  ops/s
VectorizeBenchmark.encode1            thrpt   15   33998510.772 ±  2924618.992  ops/s
VectorizeBenchmark.encode2            thrpt   15   43238190.552 ±   810373.966  ops/s
VectorizeBenchmark.encode3            thrpt   15   36613553.485 ±   483115.838  ops/s
VectorizeBenchmark.encode4            thrpt   15   45675726.831 ±  1081153.655  ops/s
VectorizeBenchmark.encode5            thrpt   15   33591855.278 ±  1084009.112  ops/s
VectorizeBenchmark.encode6            thrpt   15   36110726.127 ±   767075.709  ops/s
VectorizeBenchmark.encode7            thrpt   15   34754339.379 ±   275025.123  ops/s
VectorizeBenchmark.encode8            thrpt   15   55075742.358 ±   991165.320  ops/s
VectorizeBenchmark.vectorizedDecode1  thrpt   15   43878020.796 ±  7148545.623  ops/s
VectorizeBenchmark.vectorizedDecode2  thrpt   15  103091446.773 ± 44115190.011  ops/s
VectorizeBenchmark.vectorizedDecode3  thrpt   15   83168059.373 ± 24930903.852  ops/s
VectorizeBenchmark.vectorizedDecode4  thrpt   15   63156089.355 ± 15039408.293  ops/s
VectorizeBenchmark.vectorizedDecode5  thrpt   15   96567546.695 ± 37142784.493  ops/s
VectorizeBenchmark.vectorizedDecode6  thrpt   15   73897063.180 ± 11549757.437  ops/s
VectorizeBenchmark.vectorizedDecode7  thrpt   15   79716185.567 ± 29990852.039  ops/s
VectorizeBenchmark.vectorizedDecode8  thrpt   15   92621676.617 ± 29702056.667  ops/s
VectorizeBenchmark.vectorizedEncode1  thrpt   15   51140300.852 ±   139758.385  ops/s
VectorizeBenchmark.vectorizedEncode2  thrpt   15   82646100.574 ±  1289600.954  ops/s
VectorizeBenchmark.vectorizedEncode3  thrpt   15   88124485.953 ±   742170.198  ops/s
VectorizeBenchmark.vectorizedEncode4  thrpt   15   91029285.467 ±  5594858.437  ops/s
VectorizeBenchmark.vectorizedEncode5  thrpt   15   96843051.648 ±  8024430.836  ops/s
VectorizeBenchmark.vectorizedEncode6  thrpt   15   98596724.128 ± 10068466.227  ops/s
VectorizeBenchmark.vectorizedEncode7  thrpt   15   85885746.715 ±  6031740.563  ops/s
VectorizeBenchmark.vectorizedEncode8  thrpt   15  117139889.194 ±  8721517.095  ops/s
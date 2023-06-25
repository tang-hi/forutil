# Rewrite ForUtil

Vectorized Code in `SimdPack.java`.

Test File is `life/tangdh/ForUtilVectorizedTest.java`

Benchmark is `life/tangdh/VectorizeBenchmark.java`

Java version >= 20
IDEA PLUGIN 'JMH Java Microbenchmark Harness' to run benchmark in idea
Don't forget to add vm option `--add-modules jdk.incubator.vector`

## Current Result

## UNSAME  FORMAT

| Benchmark                   | Mode | Cnt         | Score          | Error         | Units |
|-----------------------------|------|-------------|----------------|---------------|-------|
| VectorizeBenchmark.decode1  | thrpt | 15 | 58293945.447 | ± 1524549.587 | ops/s |
| VectorizeBenchmark.decode2  | thrpt | 15 | 55598229.538 | ± 4516920.135 | ops/s |
| VectorizeBenchmark.decode3  | thrpt | 15 | 57163871.965 | ± 1566246.490 | ops/s |
| VectorizeBenchmark.decode4  | thrpt | 15 | 55128874.528 | ± 4752397.170 | ops/s |
| VectorizeBenchmark.decode5  | thrpt | 15 | 53822335.729 | ± 4599217.489 | ops/s |
| VectorizeBenchmark.decode6  | thrpt | 15 | 48155246.120 | ± 7519360.551 | ops/s |
| VectorizeBenchmark.decode7  | thrpt | 15 | 50253799.192 | ± 820075.648   | ops/s |
| VectorizeBenchmark.decode8  | thrpt | 15 | 68849728.856 | ± 1818468.973 | ops/s |
| VectorizeBenchmark.encode1  | thrpt | 15 | 33998510.772 | ± 2924618.992 | ops/s |
| VectorizeBenchmark.encode2  | thrpt | 15 | 43238190.552 | ± 810373.966   | ops/s |
| VectorizeBenchmark.encode3  | thrpt | 15 | 36613553.485 | ± 483115.838   | ops/s |
| VectorizeBenchmark.encode4  | thrpt | 15 | 45675726.831 | ± 1081153.655 | ops/s |
| VectorizeBenchmark.encode5  | thrpt | 15 | 33591855.278 | ± 1084009.112 | ops/s |
| VectorizeBenchmark.encode6  | thrpt | 15 | 36110726.127 | ± 767075.709   | ops/s |
| VectorizeBenchmark.encode7  | thrpt | 15 | 34754339.379 | ± 275025.123   | ops/s |
| VectorizeBenchmark.encode8  | thrpt | 15 | 55075742.358 | ± 991165.320   | ops/s |
| VectorizeBenchmark.vectorizedDecode1  | thrpt | 15 | 43878020.796 | ± 7148545.623 | ops/s |
| VectorizeBenchmark.vectorizedDecode2  | thrpt | 15 | 103091446.773 | ± 44115190.011 | ops/s |
| VectorizeBenchmark.vectorizedDecode3  | thrpt | 15 | 83168059.373 | ± 24930903.852 | ops/s |
| VectorizeBenchmark.vectorizedDecode4  | thrpt | 15 | 63156089.355 | ± 15039408.293 | ops/s |
| VectorizeBenchmark.vectorizedDecode5  | thrpt | 15 | 96567546.695 | ± 37142784.493 | ops/s |
| VectorizeBenchmark.vectorizedDecode6  | thrpt | 15 | 73897063.180 | ± 11549757.437 | ops/s |
| VectorizeBenchmark.vectorizedDecode7  | thrpt | 15 | 79716185.567 | ± 29990852.039 | ops/s |
| VectorizeBenchmark.vectorizedDecode8  | thrpt | 15 | 92621676.617 | ± 29702056.667 | ops/s |
| VectorizeBenchmark.vectorizedEncode1  | thrpt | 15 | 51140300.852 | ± 139758.385 | ops/s |
| VectorizeBenchmark.vectorizedEncode2  | thrpt | 15 | 82646100.574 | ± 1289600.954 | ops/s |
| VectorizeBenchmark.vectorizedEncode3  | thrpt | 15 | 88124485.953 | ± 742170.198 | ops/s |
| VectorizeBenchmark.vectorizedEncode4  | thrpt | 15 | 91029285.467 | ± 5594858.437 | ops/s |
| VectorizeBenchmark.vectorizedEncode5  | thrpt | 15 | 96843051.648 | ± 8024430.836 | ops/s |
| VectorizeBenchmark.vectorizedEncode6  | thrpt | 15 | 98596724.128 | ± 10068466.227 | ops/s |
| VectorizeBenchmark.vectorizedEncode7  | thrpt | 15 | 85885746.715 | ± 6031740.563 | ops/s |
| VectorizeBenchmark.vectorizedEncode8  | thrpt | 15 | 117139889.194 | ± 8721517.095 | ops/s |

## SAME FORMAT

| Benchmark                 | Mode | Cnt | Score (ops/s) | Error (ops/s) |
|---------------------------|------|-----|---------------|---------------|
| Encode1                   | thrpt|  15 | 30.81M        | 7.76M         |
| vectorizedEncode1         | thrpt|  15 | 52.11M        | 9.15M         |
| Encode2                   | thrpt|  15 | 44.97M        | 972.53K       |
| vectorizedEncode2         | thrpt|  15 | 84.76M        | 11.56M        |
| Encode3                   | thrpt|  15 | 39.16M        | 389.41K       |
| vectorizedEncode3         | thrpt|  15 | 49.50M        | 319.99K       |
| Encode4                   | thrpt|  15 | 49.35M        | 895.58K       |
| vectorizedEncode4         | thrpt|  15 | 84.05M        | 1.10M         |
| Encode5                   | thrpt|  15 | 35.39M        | 1.76M         |
| vectorizedEncode5         | thrpt|  15 | 39.75M        | 2.49M         |
| Encode6                   | thrpt|  15 | 38.26M        | 1.16M         |
| vectorizedEncode6         | thrpt|  15 | 52.07M        | 2.29M         |
| Encode7                   | thrpt|  15 | 37.20M        | 556.95K       |
| vectorizedEncode7         | thrpt|  15 | 48.99M        | 1.34M         |
| Encode8                   | thrpt|  15 | 59.05M        | 1.74M         |
| vectorizedEncode8         | thrpt|  15 | 95.48M        | 7.30M         |
| Encode9                   | thrpt|  15 | 24.64M        | 135.16K       |
| vectorizedEncode9         | thrpt|  15 | 27.45M        | 1.29M         |
| Encode10                  | thrpt|  15 | 24.94M        | 358.51K       |
| vectorizedEncode10        | thrpt|  15 | 27.22M        | 2.40M         |
| Encode11                  | thrpt|  15 | 25.11M        | 819.30K       |
| vectorizedEncode11        | thrpt|  15 | 23.89M        | 1.28M         |
| Encode12                  | thrpt|  15 | 29.45M        | 490.67K       |
| vectorizedEncode12        | thrpt|  15 | 41.90M        | 2.30M         |
| Encode13                  | thrpt|  15 | 24.38M        | 226.59K       |
| vectorizedEncode13        | thrpt|  15 | 24.83M        | 1.39M         |
| Encode14                  | thrpt|  15 | 27.44M        | 282.45K       |
| vectorizedEncode14        | thrpt|  15 | 36.28M        | 3.62M         |
| Encode15                  | thrpt|  15 | 27.64M        | 504.77K       |
| vectorizedEncode15        | thrpt|  15 | 39.15M        | 2.88M         |
| Encode16                  | thrpt|  15 | 55.31M        | 4.87M         |
| vectorizedEncode16        | thrpt|  15 | 84.89M        | 7.14M         |
| Encode17                  | thrpt|  15 | 16.27M        | 152.66K       |
| vectorizedEncode17        | thrpt|  15 | 15.04M        | 740.80K       |
| Encode18                  | thrpt|  15 | 15.45M        | 69.44K        |
| vectorizedEncode18        | thrpt|  15 | 15.49M        | 654.85K       |
| Encode19                  | thrpt|  15 | 12.67M        | 119.34K       |
| vectorizedEncode19        | thrpt|  15 | 13.17M        | 539.82K       |
| Encode20                  | thrpt|  15 | 15.90M        | 184.34K       |
| vectorizedEncode20        | thrpt|  15 | 16.03M        | 1.29M         |
| Encode21                  | thrpt|  15 | 15.95M        | 87.33K        |
| vectorizedEncode21        | thrpt|  15 | 15.01M        | 274.69K       |
| Encode22                  | thrpt|  15 | 16.04M        | 127.17K       |
| vectorizedEncode22        | thrpt|  15 | 15.34M        | 404.39K       |
| Encode23                  | thrpt|  15 | 15.62M        | 180.99K       |
| vectorizedEncode23        | thrpt|  15 | 15.11M        | 615.33K       |
| Encode24                  | thrpt|  15 | 17.79M        | 673.54K       |
| vectorizedEncode24        | thrpt|  15 | 22.53M        | 2.13M         |
| Encode25                  | thrpt|  15 | 15.42M        | 203.42K       |
| vectorizedEncode25        | thrpt|  15 | 13.45M        | 1.34M         |
| Encode26                  | thrpt|  15 | 15.20M        | 187.19K       |
| vectorizedEncode26        | thrpt|  15 | 14.91M        | 402.92K       |
| Encode27                  | thrpt|  15 | 14.98M        | 81.71K        |
| vectorizedEncode27        | thrpt|  15 | 14.71M        | 740.17K       |
| Encode28                  | thrpt|  15 | 17.84M        | 166.66K       |
| vectorizedEncode28        | thrpt|  15 | 20.75M        | 836.14K       |
| Encode29                  | thrpt|  15 | 14.66M        | 177.74K       |
| vectorizedEncode29        | thrpt|  15 | 13.89M        | 392.35        |
| Encode30                  | thrpt|  15 | 17.43M        | 462.43K       |
| vectorizedEncode30        | thrpt|  15 | 23.42M        | 2.45M         |
| Encode31                  | thrpt|  15 | 18.49M        | 280.37K       |
| vectorizedEncode31        | thrpt|  15 | 26.03M        | 1.40M         |
| Encode32                  | thrpt|  15 | 50.39M        | 1.99M         |
| vectorizedEncode32        | thrpt|  15 | 38.64M        | 51.39M        |

package life.tangdh;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 10, timeUnit =  TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 10, timeUnit =  TimeUnit.MILLISECONDS)
public class Benchmark {
    @org.openjdk.jmh.annotations.Benchmark
    public void measureName(Blackhole bh) {
        bh.consume("hello");
    }

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void someMethod() {

    }

}

package life.tangdh;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 100, timeUnit =  TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 1000, timeUnit =  TimeUnit.MILLISECONDS)
public class Benchmark {

    private static final long[] input = {-1032544580768537905L, -3300798846836653719L, -7395019361832156024L, -2019494021879452897L, 4556637544238854828L, 5203699597321105524L, 2831455363119497015L, 1674357659478346887L, -5347069813042583259L, -6081205712506211821L, 2670726235658945592L, -6920089801376038192L, -4367194139058480904L, 3131898888928418072L, -8672782346117627653L, 4590622280264533966L, -6895423382377807002L, -8623234233189649285L, 3832108463042753971L, -1505237019340427335L, 621835050658899124L, -8430353752432644140L, -1129065909693249066L, -5779397875668889171L, 5126424518786519210L, -7294956009746005023L, -2309666340886187871L, -159247102206558165L, -7705356562301182185L, -1076001937439705338L, 1618623337653067154L, 5426365686554038678L, -1474574448802273206L, -4303303187455627832L, -1629579201553200916L, 6404531310964144311L, 7272101577146700186L, -4182731354938471599L, -4709798308486183882L, 5995003848346955066L, 3842127469019392480L, 8983693306606789104L, 3656452829115031228L, -5828986767601769425L, 1358381900404168184L, 6095500999078327190L, 2309324567094090253L, -2043238373535115127L, -6948486624247100092L, -4823486821842959783L, 2053168095991361147L, 1519327921328481186L, 586289422622205480L, 5551887830609817844L, 8692703313352941470L, 6580017472570271157L, -7813564845931007345L, -5531377261889424571L, -6347789480132960047L, -8280997033639494045L, 5885293546862849124L, 3024585370716540622L, 4563871711452711348L, 6979022886619878703L, 8004040597540893210L, 4790991665289931574L, 7293043256484977320L, 4860111622854488578L, -661617485566700763L, 2163773793986802373L, -6816586600633659050L, 8902697583859787175L, -936677667048706945L, -7576493847285103893L, 8886495939954265385L, 1211410288885465680L, -1222821392358877916L, 7943105777321618617L, 4020044087250224079L, 8375627621302772751L, -2933605754361128298L, 5753450153772959078L, 6208971689915794875L, 1650544170449799518L, 1395973595189513561L, 6794698028575295179L, 2424675780705473190L, -910554329106973800L, -9134302974860812936L, 548031251870711784L, -3995761031775437302L, -8276047595833800835L, -6281576558611757313L, 1432458888758793565L, -1090353970924477530L, 1246785717904885170L, 1807759758340820606L, 4290537523060052465L, -5862769868626859011L, 1289737671523005247L, 6948142285713657229L, 1572790784760661775L, 1564194418676506838L, -3615285696250701297L, 8932579494092066626L, -2637199038341792444L, 6774328747373220285L, 1545200528711387507L, -1568062834354766660L, 5500600458461246659L, -5295467161741162367L, -6642380461883367777L, -4098079012393936484L, 272973356400752097L, 8090498640542846388L, 3722187287485013292L, 8658897422592708743L, -6406507851149839976L, 2341333367144037678L, -5897376417205528683L, 8759899497886338981L, -6257664914411179512L, -5384706275542488437L, -7971420488175507526L, -3634173352431225563L, -3577807114006390030L, 4511238704262469032L, 1357151485363664735L};

    private static final long[] input2;

    private static final long[] collapse;
    private static final long[] collapse2;

    private static final ForUtil forUtil = new ForUtil();

    private static final ForUtilVectorized forUtilVectorized = new ForUtilVectorized();

    static {
        Random random = new Random();
        collapse = new long[128];
        collapse2 = new long[128];
        input2 = new long[128];
        for (int i = 0; i < 128; i++) {
            collapse[i] = random.nextLong(1 << 8 - 1);
            collapse2[i] = collapse[i];
            input2[i] = input[i];
        }

    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode1(Blackhole bh) throws IOException {
        forUtil.encode(input, 1, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode1(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 1, input);
    }



    @org.openjdk.jmh.annotations.Benchmark
    public void encode2(Blackhole bh) throws IOException {
        forUtil.encode(input, 2, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode2(Blackhole bh) throws IOException {
//        var forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(input2, 7, input2);
        forUtilVectorized.encode(input, 2, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void encode3(Blackhole bh) throws IOException {
        forUtil.encode(input, 3, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode3(Blackhole bh) throws IOException {
//        var forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(input2, 7, input2);
        forUtilVectorized.encode(input, 3, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void encode5(Blackhole bh) throws IOException {
        forUtil.encode(input, 5, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode5(Blackhole bh) throws IOException {
//        var forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(input2, 7, input2);
        forUtilVectorized.encode(input, 5, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void encode6(Blackhole bh) throws IOException {
        forUtil.encode(input, 6, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode6(Blackhole bh) throws IOException {
//        var forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(input2, 7, input2);
        forUtilVectorized.encode(input, 6, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void encode7(Blackhole bh) throws IOException {
        forUtil.encode(input, 7, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode7(Blackhole bh) throws IOException {
//        var forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(input2, 7, input2);
        forUtilVectorized.encode(input, 7, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void encode4(Blackhole bh) throws IOException {
        forUtil.encode(input, 4, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode4(Blackhole bh) throws IOException {
//        var forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(input2, 7, input2);
        forUtilVectorized.encode(input, 4, input);
    }



    @org.openjdk.jmh.annotations.Benchmark
    public void encode9(Blackhole bh) throws IOException {
        forUtil.encode(input, 9, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode9(Blackhole bh) throws IOException {
//        var forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(input2, 7, input2);
        forUtilVectorized.encode(input, 9, input);
    }



    @org.openjdk.jmh.annotations.Benchmark
    public void encode8(Blackhole bh) throws IOException {
        forUtil.encode(input, 8, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode8(Blackhole bh) throws IOException {
//        var forUtilVectorized = new ForUtilVectorized();
//        forUtilVectorized.encode(input2, 7, input2);
        forUtilVectorized.encode(input, 8, input);
    }



    @org.openjdk.jmh.annotations.Benchmark
    public void encode10(Blackhole bh) throws IOException {
        forUtil.encode(input, 10, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode10(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 10, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode11(Blackhole bh) throws IOException {
        forUtil.encode(input, 11, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode11(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 11, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode12(Blackhole bh) throws IOException {
        forUtil.encode(input, 12, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode12(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 12, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode13(Blackhole bh) throws IOException {
        forUtil.encode(input, 13, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode13(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 13, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode14(Blackhole bh) throws IOException {
        forUtil.encode(input, 14, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode14(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 14, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode15(Blackhole bh) throws IOException {
        forUtil.encode(input, 15, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode15(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 15, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode16(Blackhole bh) throws IOException {
        forUtil.encode(input, 16, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode16(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 16, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode17(Blackhole bh) throws IOException {
        forUtil.encode(input, 17, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode17(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 17, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode18(Blackhole bh) throws IOException {
        forUtil.encode(input, 18, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode18(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 18, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode19(Blackhole bh) throws IOException {
        forUtil.encode(input, 19, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode19(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 19, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode20(Blackhole bh) throws IOException {
        forUtil.encode(input, 20, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode20(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 20, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode21(Blackhole bh) throws IOException {
        forUtil.encode(input, 21, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode21(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 21, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode22(Blackhole bh) throws IOException {
        forUtil.encode(input, 22, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode22(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 22, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode23(Blackhole bh) throws IOException {
        forUtil.encode(input, 23, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode23(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 23, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode24(Blackhole bh) throws IOException {
        forUtil.encode(input, 24, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode24(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 24, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode25(Blackhole bh) throws IOException {
        forUtil.encode(input, 25, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode25(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 25, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode26(Blackhole bh) throws IOException {
        forUtil.encode(input, 26, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode26(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 26, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode27(Blackhole bh) throws IOException {
        forUtil.encode(input, 27, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode27(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 27, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode28(Blackhole bh) throws IOException {
        forUtil.encode(input, 28, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode28(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 28, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode29(Blackhole bh) throws IOException {
        forUtil.encode(input, 29, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode29(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 29, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode30(Blackhole bh) throws IOException {
        forUtil.encode(input, 30, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode30(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 30, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode31(Blackhole bh) throws IOException {
        forUtil.encode(input, 31, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode31(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 31, input);
    }
    @org.openjdk.jmh.annotations.Benchmark
    public void encode32(Blackhole bh) throws IOException {
        forUtil.encode(input, 32, input);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void vectorizedEncode32(Blackhole bh) throws IOException {
        forUtilVectorized.encode(input, 32, input);
    }





}

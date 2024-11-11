package be.johanaerens.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AssetHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AssetHistory getAssetHistorySample1() {
        return new AssetHistory().id(1L);
    }

    public static AssetHistory getAssetHistorySample2() {
        return new AssetHistory().id(2L);
    }

    public static AssetHistory getAssetHistoryRandomSampleGenerator() {
        return new AssetHistory().id(longCount.incrementAndGet());
    }
}

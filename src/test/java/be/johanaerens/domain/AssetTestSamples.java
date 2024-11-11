package be.johanaerens.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Asset getAssetSample1() {
        return new Asset().id(1L).number("number1").brand("brand1").model("model1").serialNumber("serialNumber1").comments("comments1");
    }

    public static Asset getAssetSample2() {
        return new Asset().id(2L).number("number2").brand("brand2").model("model2").serialNumber("serialNumber2").comments("comments2");
    }

    public static Asset getAssetRandomSampleGenerator() {
        return new Asset()
            .id(longCount.incrementAndGet())
            .number(UUID.randomUUID().toString())
            .brand(UUID.randomUUID().toString())
            .model(UUID.randomUUID().toString())
            .serialNumber(UUID.randomUUID().toString())
            .comments(UUID.randomUUID().toString());
    }
}

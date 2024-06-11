package org.example;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComplexTaskGenerator {
    public static List<Cloudlet> createComplexCloudlets(int numberOfCloudlets) {
        List<Cloudlet> cloudletList = new ArrayList<>(numberOfCloudlets);
        Random rand = new Random();

        for (int i = 0; i < numberOfCloudlets; i++) {
            long length = 7000 + rand.nextInt(10000); // Random length between 3000 and 8000
            long fileSize = 1000 + rand.nextInt(3000); // Random file size between 300 and 1300
            long outputSize = 3000 + rand.nextInt(3000); // Random output size between 300 and 1300
            int pesNumber = 1 + rand.nextInt(4); // Random PEs between 1 and 4

            Cloudlet cloudlet = new CloudletSimple(length, pesNumber)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModelCpu(new UtilizationModelFull())
                    .setUtilizationModelRam(new UtilizationModelFull())
                    .setUtilizationModelBw(new UtilizationModelFull());

            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }
}

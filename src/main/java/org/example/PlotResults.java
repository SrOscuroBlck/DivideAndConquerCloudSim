package org.example;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;
import org.knowm.xchart.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlotResults {

    public static void plotTimes(List<Cloudlet> regularCloudlets, List<Cloudlet> optimizedCloudlets) {
        List<Double> regularTimes = regularCloudlets.stream()
                .map(Cloudlet::getFinishTime)
                .collect(Collectors.toList());

        List<Double> optimizedTimes = optimizedCloudlets.stream()
                .map(Cloudlet::getFinishTime)
                .collect(Collectors.toList());

        if (regularTimes.isEmpty() || optimizedTimes.isEmpty()) {
            System.err.println("Error: One of the times lists is empty.");
            return;
        }

        XYChart chart = new XYChartBuilder().width(800).height(600).title("Cloudlet Finish Times")
                .xAxisTitle("Cloudlet").yAxisTitle("Finish Time (s)").build();

        chart.addSeries("Regular", IntStream.range(0, regularTimes.size()).boxed().collect(Collectors.toList()), regularTimes);
        chart.addSeries("Optimized", IntStream.range(0, optimizedTimes.size()).boxed().collect(Collectors.toList()), optimizedTimes);

        new SwingWrapper<>(chart).displayChart();
    }

    public static void plotCosts(List<Cloudlet> regularCloudlets, List<Cloudlet> optimizedCloudlets) {
        double regularCosts = calculateTotalCost(regularCloudlets);
        double optimizedCosts = calculateTotalCost(optimizedCloudlets);

        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Cloudlet Cost Comparison")
                .xAxisTitle("Approach").yAxisTitle("Total Cost ($)").build();

        chart.addSeries("Costs", List.of("Regular", "Optimized"), List.of(regularCosts, optimizedCosts));

        new SwingWrapper<>(chart).displayChart();
    }

    private static double calculateTotalCost(List<Cloudlet> cloudlets) {
        double totalCost = 0.0;
        double cpuCostPerSecond = 0.02 / 3600; // $0.02 per core-hour, converted to per second
        double ramCostPerGBPerSecond = 0.01 / 3600; // $0.01 per GB-hour, converted to per second
        double storageCostPerGB = 0.001; // $0.001 per GB
        double bandwidthCostPerGB = 0.005; // $0.005 per GB

        for (Cloudlet cloudlet : cloudlets) {
            Vm vm = cloudlet.getVm();
            if (vm == null) {
                continue; // If the cloudlet is not assigned to a VM, skip it.
            }

            double finishTime = cloudlet.getFinishTime();
            double execStartTime = cloudlet.getStartTime();
            double duration = finishTime - execStartTime;

            double cpuUtilization = cloudlet.getUtilizationOfCpu(finishTime);
            double ramUtilization = cloudlet.getUtilizationOfRam(finishTime);
            double bwUtilization = cloudlet.getUtilizationOfBw(finishTime);

            double cloudletCpuCost = duration * cpuUtilization * cpuCostPerSecond * vm.getPesNumber();
            double cloudletRamCost = duration * ramUtilization * vm.getRam().getCapacity() / 1024 * ramCostPerGBPerSecond;
            double cloudletStorageCost = cloudlet.getFileSize() / 1024.0 * storageCostPerGB;
            double cloudletBandwidthCost = duration * bwUtilization * vm.getBw().getCapacity() / 1024.0 * bandwidthCostPerGB;

            totalCost += cloudletCpuCost + cloudletRamCost + cloudletStorageCost + cloudletBandwidthCost;
        }
        return totalCost;
    }
}
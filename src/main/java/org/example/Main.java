package org.example;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private DatacenterBrokerSimple broker;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        // Initialize CloudSim
        CloudSimPlus simulation1 = new CloudSimPlus();
        CloudSimPlus simulation2 = new CloudSimPlus();

        // Create Datacenter
        DatacenterSimple datacenter1 = CloudSimConfig.createDatacenter(simulation1);
        DatacenterSimple datacenter2 = CloudSimConfig.createDatacenter(simulation2);

        // Create VMs for each simulation separately
        List<Vm> vmList = CloudSimConfig.createSinglePowerfulVM(); // Single powerful VM for regular approach
        List<Vm> vmList2 = CloudSimConfig.createMultipleVMs(); // Multiple VMs for optimized approach
        System.out.println("Created Single VM for simulation 1: " + vmList);
        System.out.println("Created Multiple VMs for simulation 2: " + vmList2);

        // Create Cloudlets for each simulation separately
        List<Cloudlet> cloudletList1 = ComplexTaskGenerator.createComplexCloudlets(10);
        List<Cloudlet> cloudletList2 = ComplexTaskGenerator.createComplexCloudlets(10);
        System.out.println("Created Cloudlets for simulation 1: " + cloudletList1);
        System.out.println("Created Cloudlets for simulation 2: " + cloudletList2);

        // Run the simulation with the regular approach
        List<Cloudlet> regularCloudlets = runSimulation(simulation1, datacenter1, vmList, cloudletList1, false);
        System.out.println("Regular Cloudlets: " + regularCloudlets);

        // Run the simulation with the optimized approach
        List<Cloudlet> optimizedCloudlets = runSimulation(simulation2, datacenter2, vmList2, cloudletList2, true);
        System.out.println("Optimized Cloudlets: " + optimizedCloudlets);

        // Ensure cloudlet lists are not empty before plotting
        if (regularCloudlets.isEmpty() || optimizedCloudlets.isEmpty()) {
            System.err.println("Error: One of the cloudlet lists is empty.");
            return;
        }

        // Plot the results
        PlotResults.plotTimes(regularCloudlets, optimizedCloudlets);
        PlotResults.plotCosts(regularCloudlets, optimizedCloudlets);
    }

    private List<Cloudlet> runSimulation(CloudSimPlus simulation, DatacenterSimple datacenter, List<Vm> vmList, List<Cloudlet> cloudletList, boolean isOptimized) {
        // Create Broker
        broker = new DatacenterBrokerSimple(simulation);

        // Submit VMs to the broker
        broker.submitVmList(vmList);

        // Schedule Cloudlet submission based on isOptimized flag
        if (isOptimized) {
            // Submit cloudlets using Divide and Conquer method with delays
            DivideAndConquer.divideTasksAndDistribute(vmList, cloudletList, 0, cloudletList.size() - 1);
            for (Cloudlet cloudlet : cloudletList) {
                broker.submitCloudletList(List.of(cloudlet));
            }
        } else {
            int delay = 0;
            for (Cloudlet cloudlet : cloudletList) {
                cloudlet.setVm(vmList.get(0)); // Bind each cloudlet to the single VM
                broker.submitCloudletList(List.of(cloudlet), delay);
                // Delay it the time it takes to execute the previous cloudlet
                delay += (int) ((cloudlet.getLength() / vmList.get(0).getMips()) + 1);
            }
        }

        // Add a listener to dynamically create and submit Cloudlets
        simulation.addOnClockTickListener(this::createDynamicCloudlet);

        // Start the simulation
        simulation.start();

        // Collect and return the list of finished cloudlets
        List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();

        // Output results
        new CloudletsTableBuilder(finishedCloudlets).build();
        return finishedCloudlets;
    }

    private void createDynamicCloudlet(EventInfo evt) {
        if ((int) evt.getTime() == 100) {
            List<Cloudlet> newCloudletList = new ArrayList<>();
            System.out.printf("\n# Dynamically creating 2 Cloudlets at time %.2f\n", evt.getTime());
            Cloudlet cloudlet1 = new CloudletSimple(1000, 2);
            newCloudletList.add(cloudlet1);
            Cloudlet cloudlet2 = new CloudletSimple(1000, 2);
            newCloudletList.add(cloudlet2);
            broker.submitCloudletList(newCloudletList);
        }
    }
}

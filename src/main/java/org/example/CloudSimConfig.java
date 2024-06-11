package org.example;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.provisioners.ResourceProvisionerSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

public class CloudSimConfig {
    public static DatacenterSimple createDatacenter(CloudSimPlus simulation) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < 4; i++) { // Increased the number of hosts to 4
            hostList.add(createHost());
        }
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private static Host createHost() {
        final long mips = 100000; // Reduced MIPS
        final int ram = 128000; // 128 GB
        final long storage = 10000000; // 1 TB
        final long bw = 150000; // 15 GBps

        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < 8; i++) { // 8 PEs
            peList.add(new PeSimple(mips));
        }

        return new HostSimple(ram, bw, storage, peList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    public static List<Vm> createSinglePowerfulVM() {
        List<Vm> vmList = new ArrayList<>();
        Vm vm = new VmSimple(10000, 8)
                .setRam(40000).setBw(5000).setSize(10000)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        vmList.add(vm);
        return vmList;
    }

    public static List<Vm> createMultipleVMs() {
        List<Vm> vmList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Vm vm = new VmSimple(5000, 4)
                    .setRam(4000)
                    .setBw(5000)
                    .setSize(10000)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        return vmList;
    }
}

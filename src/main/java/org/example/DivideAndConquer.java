package org.example;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

import java.util.List;

public class DivideAndConquer {
    public static void divideTasksAndDistribute(List<Vm> vmList, List<Cloudlet> cloudletList, int start, int end) {
        if (start <= end) {
            if (end - start + 1 <= vmList.size()) {
                for (int i = start; i <= end; i++) {
                    Cloudlet cloudlet = cloudletList.get(i);
                    Vm vm = vmList.get(i % vmList.size());
                    cloudlet.setVm(vm);
                }
            } else {
                int mid = (start + end) / 2;
                divideTasksAndDistribute(vmList, cloudletList, start, mid);
                divideTasksAndDistribute(vmList, cloudletList, mid + 1, end);
            }
        }
    }
}

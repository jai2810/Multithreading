package org.example;

import java.util.ArrayList;
import java.util.List;

public class UnisexBathRoomDemonstration {
    public static void main(String[] args) throws InterruptedException {
        UnisexBathrooms.runTest();
    }
}

class UnisexBathrooms{
    int totalCount = 0;
    Employee.Sex lastEnteredSex;

    public static void runTest() throws InterruptedException {

        final UnisexBathrooms unisexBathroom = new UnisexBathrooms();

        Thread female1 = new Thread(new Runnable() {
            public void run() {
                try {
                    unisexBathroom.enterBathroom(new Employee(Employee.Sex.F));
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread male1 = new Thread(new Runnable() {
            public void run() {
                try {
                    unisexBathroom.enterBathroom(new Employee(Employee.Sex.M));
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread male2 = new Thread(new Runnable() {
            public void run() {
                try {
                    unisexBathroom.enterBathroom(new Employee(Employee.Sex.M));
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread male3 = new Thread(new Runnable() {
            public void run() {
                try {
                    unisexBathroom.enterBathroom(new Employee(Employee.Sex.M));
                } catch (InterruptedException ie) {

                }
            }
        });

        Thread male4 = new Thread(new Runnable() {
            public void run() {
                try {
                    unisexBathroom.enterBathroom(new Employee(Employee.Sex.M));
                } catch (InterruptedException ie) {

                }
            }
        });

        female1.start();
        male1.start();
        male2.start();
        male3.start();
        male4.start();

        female1.join();
        male1.join();
        male2.join();
        male3.join();
        male4.join();

    }

    void useBathroom(String name) throws InterruptedException {
        System.out.println(name + " using bathroom. Current employees in bathroom = " + totalCount);
        Thread.sleep(10000);
        System.out.println(name + " done using bathroom");
    }
    public void enterBathroom(Employee employee) throws InterruptedException {
        while (totalCount == 3) {
            wait();
        }
        synchronized (this) {
            if (lastEnteredSex == null) {
                lastEnteredSex = employee.sex;
                totalCount++;
                System.out.println("Employee with sex: " + employee.sex + " is entered...");
                System.out.println("Current employees in bathroom = " + totalCount);
                Thread.sleep(5000);
                exitBathroom();
            } else if (lastEnteredSex.equals(employee.sex)) {
                totalCount++;
                System.out.println("Employee with sex: " + employee.sex + " is entered...");
                System.out.println("Current employees in bathroom = " + totalCount);
                Thread.sleep(5000);
                exitBathroom();
            } else {
                wait();
            }
        }


    }

    public void exitBathroom() throws InterruptedException {
        //Thread.sleep(3000);
        synchronized (this) {
            totalCount--;
            if (totalCount == 0) {
                lastEnteredSex = null;
            }
            notifyAll();
        }
    }

    static class Employee{
        enum Sex {
            M,
            F
        }

        Sex sex;

        public Employee(Sex sex) {
            this.sex = sex;
        }
    }
}



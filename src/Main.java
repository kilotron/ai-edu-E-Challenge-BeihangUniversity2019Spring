import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String [] args) {
        Scanner in = new Scanner(System.in);
        int totalNumberOfDoors = 0;
        int numberOfDoorsToOpen = 0;
        do {
            System.out.print("门的个数（>=3）：");
            totalNumberOfDoors = in.nextInt();
        } while (totalNumberOfDoors < 3);
        do {
            System.out.print("打开无奖品的门的个数（<" + (totalNumberOfDoors - 1) + "）：");
            numberOfDoorsToOpen = in.nextInt();
        } while (numberOfDoorsToOpen >= totalNumberOfDoors - 1);
        in.close();
        System.out.println("不改变选择的情况下，获奖的比例：" + 
            String.format("%.3f", probability(false, totalNumberOfDoors, numberOfDoorsToOpen)));
        System.out.println("改变选择的情况下，获奖的比例：" + 
            String.format("%.3f", probability(true, totalNumberOfDoors, numberOfDoorsToOpen)));
    }

    public static double probability(boolean willSwitchSelection, int totalNumberOfDoors,
            int numberOfDoorsToOpen) {
        int total = 1000000;
        int numberWin = 0;
        Doors doors = new Doors(totalNumberOfDoors);
        Contestant contestant = new Contestant(willSwitchSelection, doors);
        for (int i = 0; i < total; i++) {
            doors.reset();
            contestant.selectADoor();
            doors.openDoorWithoutPrize(numberOfDoorsToOpen);
            contestant.switchSelection();
            if (contestant.win()) {
                numberWin++;
            }
        }
        return (double)numberWin / total;
    }
}

class Door {
    private boolean prizeBehind;
    public boolean isSelected;
    public boolean isOpened;

    public Door(boolean prizeBehind) {
        this.prizeBehind = prizeBehind;
        this.isSelected = false;
        this.isOpened = false;
    }

    public boolean withPrizeBehind() {
        return this.prizeBehind;
    }
}

class Doors {
    private Door[] doors;
    private Random rand;
    private int numberOfDoors;

    /**
     * @param numberOfDoors is greater than or equal to 3.
     */
    public Doors(int numberOfDoors) {
        this.doors = new Door[numberOfDoors];
        this.rand = new Random();
        this.numberOfDoors = numberOfDoors;
        doors[0] = new Door(true);      // 第0扇门有后有奖品，其他门后没有
        for (int i = 1; i < numberOfDoors; i++) {
            doors[i] = new Door(false);
        }
    }

    /**
     * 重新开始抽奖前调用，重置门的状态。
     */
    public void reset() {
        for (int i = 0; i < this.numberOfDoors; i++) {
            doors[i].isOpened = false;
            doors[i].isSelected = false;
        }
    }

    /**
     * @return 随机选择一扇未打开的门。
     */
    public Door selectARandomDoor() {
        while (true) {
            Door door = doors[rand.nextInt(numberOfDoors)];
            if (!door.isSelected && !door.isOpened) {
                door.isSelected = true;
                return door;
            }
        }
    }

    /**
     * 取消选择notThisDoor，并选择另一扇未打开的门。
     * @return 除了参数notThisDoor之外的随机一扇未打开的门。
     */
    public Door selectARandomDoor(Door notThisDoor) {
        notThisDoor.isSelected = false;
        while (true) {
            Door door = doors[rand.nextInt(numberOfDoors)];
            if (!door.isSelected && door != notThisDoor && !door.isOpened) {
                door.isSelected = true;
                return door;
            }
        }
    }

    /**
     * 打开k扇未被选择且没有奖品的门。
     */
    public void openDoorWithoutPrize(int k) {
        int numberOfOpenedDoors = 0;
        for (int i = 1; i < this.numberOfDoors && numberOfOpenedDoors < k; i++) {
            if (!doors[i].isSelected) {
                doors[i].isOpened = true;
                numberOfOpenedDoors++;
            }
        }
    }
}

class Contestant {
    private boolean willSwitchSelection;
    private Door selectedDoor;
    private Doors doors;

    public Contestant(boolean willSwitchSelection, Doors doors) {
        this.willSwitchSelection = willSwitchSelection;
        this.doors = doors;
    }

    public void selectADoor() {
        this.selectedDoor = doors.selectARandomDoor();
    }

    public void switchSelection() {
        if (this.willSwitchSelection) {
            this.selectedDoor = doors.selectARandomDoor(this.selectedDoor);
        }
    }

    public boolean win() {
        return this.selectedDoor.withPrizeBehind();
    }

}
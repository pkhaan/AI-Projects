import java.util.*;

public class SimulatedAnnealing {

    // Initial temperature
    public static double T = 800;

    // Temperature of termination
    static final double Tmin = 0.0001;

    // Decrease in temperature 
    static final double alpha = 0.97;

    // Rate at which a neighboring solution survives
    // Used for speeding up the calculations
    static final double NEIGHBOR_SURVIVAL_RATE = 0.1;


    public SimulatedAnnealing() {
    }

    public static void saAlgorithm() {

        // Generates random initial candidate solution
        Solution currentSol = initRandomSol();

        Random rand = new Random();

        while (T > Tmin) {
            System.out.println("T=" + T + " CURRENT SOLUTION COST: " + currentSol.cost);

            ArrayList<Solution> neighbors = neighbors(currentSol);
            while (neighbors.size() == 0) neighbors = neighbors(currentSol);

            // Pick random neighbor
            Solution randomNeighbor = neighbors.get(rand.nextInt(neighbors.size()));

            // Annealing process
            int dE = randomNeighbor.cost - currentSol.cost;
            if (dE < 0) {
                currentSol = randomNeighbor;
            } else if (Math.pow(Math.E, -dE / T) < rand.nextDouble()) {
                currentSol = randomNeighbor;

            }

            T *= alpha; // Decrease temperature by factor alpha
        }

        //Returns minimum value based on optimization
        System.out.println("\nResult value after Simulated Annealing: " + currentSol.cost + "\n");
        Main.writeTimetable(currentSol.map);
        System.out.println("Result schedule saved in file: out/schedule.csv");
    }

    // Given current configuration, returns "neighboring" configuration
    // Finds a task with many conflicts and returns solutions with all possible changes to its slot
    public static ArrayList<Solution> neighbors(Solution currentSol) {

        // Slight change to the current solution
        // to avoid getting stuck in local minimas

        HashMap<Task, Slot> curMap = currentSol.map;
        Random r = new Random();
        // pick a random task from a list of the ones with the k most conflicts
        Task randomTask = null;

        List<Map.Entry<Task, Integer>> tasksWithMostConflicts = new ArrayList<>(currentSol.tasksInConflict.entrySet());
        tasksWithMostConflicts.sort(Map.Entry.comparingByValue());
        Collections.reverse(tasksWithMostConflicts);
        int kConflicts = 10;
        randomTask = tasksWithMostConflicts.get(r.nextInt(kConflicts)).getKey();

        ArrayList<Solution> nbrs = new ArrayList<>();
        ArrayList<Slot> posibSlots = new ArrayList<>();
        for (Slot slot : Main.slots) {
            if (randomTask.canBeAsngdToSlot(slot)) {
                if (r.nextDouble() > NEIGHBOR_SURVIVAL_RATE) continue;
                HashMap<Task, Slot> newMap = new HashMap<>(curMap);
                newMap.put(randomTask, slot);
                Solution newSol = new Solution(newMap);
                nbrs.add(newSol);
            }
        }

        return nbrs;
    }

    // Generates random schedule solution
    public static Solution initRandomSol() {
        // Map of task-to-slot matches
        HashMap<Task, Slot> map = new HashMap<>();
        // initialize random matches
        Random r = new Random();
        for (Task task : Main.tasks) {
            ArrayList<Slot> posibSlots = new ArrayList<>();
            for (Slot slot : Main.slots) {
                if (task.canBeAsngdToSlot(slot)) {
                    posibSlots.add(slot);
                }
            }

            Slot randomSlot = Main.slots.get(r.nextInt(posibSlots.size()));
            map.put(task, randomSlot);
        }
        return new Solution(map);
    }


}
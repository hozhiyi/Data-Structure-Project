package MyCLI;

import java.time.Duration;
import java.time.Instant;

public class ProgressBar implements Runnable {
    private static final String FULL_BLOCK = "\u2588";
    private static final String LIGHT_SHADE = "\u2591";

    private final int barLength;
    private final int duration;
    private final StringBuilder sb = new StringBuilder();
    private volatile boolean stop;
    private volatile boolean timeUp;

    public ProgressBar(int barLength, int duration) {
        this.barLength = barLength;
        this.duration = duration;
        this.stop = false;
        this.timeUp = false;
        resetTimer();
    }

    public void setTimeUp(boolean timeUp) {
        this.timeUp = timeUp;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {  // to be invoked by Thread.start()
        /*
        long startTime = System.nanoTime();
        long endTime = startTime + (duration * 1_000_000_000L);
        while (System.nanoTime() <= endTime) {
            try {
                showTimer(System.nanoTime(), startTime, endTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
        Instant startTime = Instant.now();
        Instant endTime = startTime.plusSeconds(duration);

        while (Duration.between(startTime, Instant.now()).getSeconds() <= 60 && !timeUp && !stop) {
            try {
                showTimer(Instant.now(), startTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Something went wrong!");
            }
        }
        if (timeUp) {
            System.out.println(" (Searching is forced to stop!)");
        }
        else {
            System.out.println();
        }
    }


    /**
     * Fill progress bar with LIGHT_SHADE \u2591
     */
    public void resetTimer() {
        sb.append(LIGHT_SHADE.repeat(Math.max(0, barLength)));
    }


    /**
     * Display the progress bar with count up timer
     *
     * @param current Current time
     * @param start Start time
     */
    public void showTimer(Instant current, Instant start) throws InterruptedException {
        // Calculate number of FULL_BLOCK '\u2588' required to fill the portion of progress bar
        Duration temp = Duration.between(start, current);
        int blockCount = (int) (barLength * temp.getSeconds() / duration);

        // Replace the portion of progress bar with FULL_BLOCK
        String bar = sb.substring(0, blockCount).replace(LIGHT_SHADE, FULL_BLOCK) + sb.substring(blockCount);

        Thread.sleep(1000);
        System.out.printf("\rTime elapsed: |%s| %d s", bar, temp.getSeconds());   // print with carriage return '\r'
    }

    // Timer using System.nanoTime()
    /*
    public void showTimer(long current, long start, long end) throws InterruptedException {
        // Calculate number of FULL_BLOCK required to fill the portion of progress bar
        int blockCount = (int) (barLength * (current - start) / (end - start));

        // Replace the portion of progress bar with FULL_BLOCK
        String bar = sb.substring(0, blockCount).replace(LIGHT_SHADE, FULL_BLOCK) + sb.substring(blockCount);

        Thread.sleep(1000);
        int time = (int) ((current - start) / 1_000_000_000);
        System.out.printf("Time elapsed: |%s| %d s\r", bar, time);

    }
    */


}
